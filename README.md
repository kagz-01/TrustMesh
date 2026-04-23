# TrustMesh

TrustMesh is an advanced, multi-layered security ecosystem combining **Blockchain Identity**, **AI-driven Risk Scoring**, and **Zero Trust Policy Enforcement**. It is designed to evaluate, verify, and route access to sensitive systems based on decentralized verifiable credentials and dynamic real-time machine learning heuristics.

## 🚀 Architecture

The platform operates on a polyglot microservices architecture, locally orchestrated via Docker Compose:

1. **Spring Cloud Gateway (Java/Kotlin)**
   - Acts as the primary ingress controller and policy enforcement point (PEP).
   - Intercepts requests, queries the AI Engine for a risk score, and delegates final authorization to Open Policy Agent (OPA).

2. **AI Risk Engine (Python/FastAPI)**
   - Utilizes Scikit-Learn's `IsolationForest` to analyze incoming request metadata (e.g., latency, volume, geolocation distance).
   - Generates a normalized `risk_score` (0-100) indicating the likelihood of anomalous or malicious behavior.

3. **Blockchain Identity Registry (Solidity/Hardhat)**
   - A Decentralized Identity (DID) smart contract registry deployed on the Sepolia testnet.
   - Manages self-sovereign identities and verifiable claims, ensuring immutable cryptographic proof of user identity.

4. **Management Dashboard (Next.js/React/Tailwind CSS)**
   - A sleek, modern Web3 interface for administrators and users.
   - Integrates with EIP-1193 compatible wallets (like MetaMask or Exodus) using `ethers.js` for identity verification and real-time monitoring of the Zero Trust pipeline.

5. **Open Policy Agent (OPA)**
   - Acts as the Policy Decision Point (PDP).
   - Evaluates Rego policies against the user's verified identity and the AI-generated risk score to yield a boolean `allow`/`deny` decision.

## 🛠 Prerequisites

- [Docker](https://docs.docker.com/get-docker/) & Docker Compose
- Node.js (v20+) & npm
- JDK 17 (for local Spring Boot development)
- Python 3.11+ (for local AI engine development)
- A Web3 Wallet extension (MetaMask, Exodus, etc.)

## 💻 Getting Started Locally

1. **Start the Platform:**
   Use Docker Compose to build and spin up the entire ecosystem (Gateway, AI Engine, OPA, and UI):
   ```bash
   docker compose up -d --build
   ```

2. **Access the Dashboard:**
   Open your browser and navigate to:
   ```
   http://localhost:3000
   ```
   Connect your Web3 wallet to simulate DID authentication.

3. **Test the Gateway Interception:**
   Send an HTTP request to the Spring Cloud Gateway (`http://localhost:8080/api/test`) passing a simulated Decentralized Identifier in the headers:
   ```bash
   curl -H "X-User-ID: did:example:123" http://localhost:8080/api/test
   ```
   Check the `docker compose` logs to observe the AI Risk Score generation and OPA's authorization decision.

## 🔒 Security Posture

This repository utilizes GitHub Secret Scanning. Note that some binaries installed via standard npm dependencies (like Hardhat's EDR simulated environment) may trigger false positives for API tokens. We maintain a clean git history by explicitly untracking `node_modules`.

## 📜 License
MIT
