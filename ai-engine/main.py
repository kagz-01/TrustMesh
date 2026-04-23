from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
import pickle
import numpy as np
import os

app = FastAPI(title="TrustMesh AI Risk Engine")

# Load model at startup
MODEL_PATH = "models/isolation_forest.pkl"
model = None

@app.on_event("startup")
async def load_model():
    global model
    if os.path.exists(MODEL_PATH):
        with open(MODEL_PATH, "rb") as f:
            model = pickle.load(f)
        print("Model loaded successfully.")
    else:
        print("WARNING: Model file not found. Ensure train.py has run.")

class RequestMetadata(BaseModel):
    latency_ms: float
    request_volume_per_min: float
    geo_distance_km: float

@app.post("/predict")
async def detect_anomaly(metadata: RequestMetadata):
    if model is None:
        raise HTTPException(status_code=503, detail="Model is not loaded")
        
    try:
        # Features: [latency_ms, request_volume_per_min, geo_distance_km]
        input_data = np.array([[
            metadata.latency_ms, 
            metadata.request_volume_per_min, 
            metadata.geo_distance_km
        ]])
        
        # Output: -1 for anomalies, 1 for normal data
        prediction = model.predict(input_data)
        score = model.decision_function(input_data)[0]
        
        # Convert Scikit-Learn score to a 0-100 risk score
        # IsolationForest decision_function: < 0 is anomaly, > 0 is normal.
        # We want risk: 0 is completely normal, 100 is highly anomalous.
        # Shift and scale the score (-0.5 to 0.5 usually)
        normalized_risk = (0.5 - float(score)) * 100
        risk_score = max(0.0, min(100.0, normalized_risk))
        
        return {
            "is_anomaly": bool(prediction[0] == -1),
            "risk_score": round(risk_score, 2),
            "raw_decision_score": float(score)
        }
    except Exception as e:
        raise HTTPException(status_code=400, detail=str(e))
