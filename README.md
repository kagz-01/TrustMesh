# TrustMesh: Decentralized Zero-Trust Identity Platform

TrustMesh is an advanced, multi-layered security ecosystem that merges **Decentralized Identity (DID)**, **AI-driven Risk Scoring**, and **Zero Trust Policy Enforcement**. It provides a robust framework for managing access to sensitive resources based on cryptographic identity and real-time behavioral analysis.

## 🌟 The "W"s of TrustMesh

*   **What**: A Decentralized Zero-Trust Identity and Risk Management Platform. It acts as an intelligent gatekeeper for your infrastructure.
*   **Why**: Traditional access control relies on static credentials. TrustMesh introduces a dynamic, context-aware security posture where every request is evaluated for risk and verified against an immutable blockchain identity.
*   **Who**: Designed for security-conscious enterprises, decentralized autonomous organizations (DAOs), and developers building the next generation of secure web applications.
*   **When**: Developed as a state-of-the-art MVP in 2026, incorporating the latest in AI and Blockchain standards.
*   **Where**: Engineered to run anywhere from localized Docker environments to distributed cloud-native clusters (AWS/GCP/Azure).

## 🚀 Technology Stack

TrustMesh utilizes a high-performance, polyglot microservices architecture:

*   **API Gateway (PEP)**: Built with **Spring Boot (Kotlin)** and **Spring Cloud Gateway**. It serves as the Policy Enforcement Point, intercepting every request.
*   **AI Risk Engine**: Powered by **FastAPI (Python)** and **Scikit-Learn**. It uses an *Isolation Forest* model to detect anomalies in request patterns (latency, volume, geography) in real-time.
*   **Policy Engine (PDP)**: **Open Policy Agent (OPA)** handles authorization decisions using **Rego** policies, ensuring that security logic is decoupled from application code.
*   **Blockchain Identity**: **Solidity** smart contracts (developed with **Hardhat**) provide a tamper-proof registry for decentralized identifiers and verifiable claims on the **Sepolia** testnet.
*   **Management Dashboard**: A premium **Next.js (TypeScript)** application styled with **Tailwind CSS**. It integrates **Ethers.js** for direct Web3 wallet interaction (MetaMask/Exodus).
*   **Orchestration**: Fully containerized using **Docker** and **Docker Compose** for seamless deployment and scaling.

## 🛠 Prerequisites

*   **Docker** & Docker Compose
*   **Node.js** (v20+) & npm (for local UI/Blockchain development)
*   **Java 17** (for local Gateway development)
*   **Python 3.11+** (for local AI development)
*   A **Web3 Wallet** (MetaMask, Exodus, etc.)

## 💻 Getting Started Locally

1.  **Clone and Launch**:
    ```bash
    docker compose up -d --build
    ```
    *This builds and starts the Gateway, AI Engine, OPA, and Management UI.*

2.  **Access the Dashboard**:
    Navigate to `http://localhost:3000`. Connect your Web3 wallet to simulate identity verification.

3.  **Test Zero-Trust Interception**:
    Send a test request through the gateway:
    ```bash
    curl -H "X-User-ID: did:example:user123" http://localhost:8080/api/test
    ```
    Observe the logs to see the AI engine calculating risk and OPA making the final authorization decision.

## 🔒 Security & Performance

TrustMesh is built with security first. All native binaries and sensitive `node_modules` are gitignored to prevent credential leaks (Secret Scanning resolved). The system uses high-performance reactive patterns to ensure minimal latency in the security pipeline.

---
© 2026 TrustMesh Team. Built for the future of secure decentralized systems.
