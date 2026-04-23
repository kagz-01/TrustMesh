import numpy as np
import pandas as pd
from sklearn.ensemble import IsolationForest
import pickle
import os

def train_baseline_model():
    print("Training baseline Isolation Forest model...")
    
    # Generate synthetic "normal" traffic metadata baseline
    # Features: [latency_ms, request_volume_per_min, geo_distance_km]
    np.random.seed(42)
    normal_data = np.random.normal(loc=[50.0, 10.0, 100.0], scale=[10.0, 3.0, 20.0], size=(1000, 3))
    
    # Add a few outliers for the model to understand the space boundaries
    outlier_data = np.random.normal(loc=[500.0, 200.0, 10000.0], scale=[50.0, 20.0, 100.0], size=(50, 3))
    
    X_train = np.vstack((normal_data, outlier_data))
    
    # Train Isolation Forest
    model = IsolationForest(
        n_estimators=100, 
        max_samples='auto', 
        contamination=0.05, 
        random_state=42
    )
    model.fit(X_train)
    
    # Save the model
    os.makedirs('models', exist_ok=True)
    with open('models/isolation_forest.pkl', 'wb') as f:
        pickle.dump(model, f)
        
    print("Model trained and saved to models/isolation_forest.pkl")

if __name__ == "__main__":
    train_baseline_model()
