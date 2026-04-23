package trustmesh.authz

import future.keywords.in

default allow := false

# Allow if the risk score is below the threshold and identity is verified
allow {
    input.identity_verified == true
    input.risk_score < 70
}

# Explicitly deny if the risk score is extreme
deny {
    input.risk_score >= 85
}
