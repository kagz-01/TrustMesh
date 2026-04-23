// SPDX-License-Identifier: MIT
pragma solidity ^0.8.20;

/**
 * @title TrustMeshIdentity
 * @dev A simplified Decentralized Identity (DID) registry based on ERC-725/SSI principles.
 */
contract TrustMeshIdentity {
    
    struct Claim {
        uint256 topic;
        uint256 scheme;
        address issuer;
        bytes signature;
        bytes data;
        string uri;
    }

    // Mapping from Identity Owner to an array of claims about them
    mapping(address => Claim[]) public claims;
    
    // Mapping from Identity Owner to active status
    mapping(address => bool) public isIdentityRegistered;

    event IdentityRegistered(address indexed owner);
    event ClaimAdded(address indexed owner, uint256 indexed topic, address indexed issuer);

    /**
     * @dev Register a new identity. Any Ethereum address can register its own identity.
     */
    function registerIdentity() public {
        require(!isIdentityRegistered[msg.sender], "Identity already registered");
        isIdentityRegistered[msg.sender] = true;
        emit IdentityRegistered(msg.sender);
    }

    /**
     * @dev Add a verifiable claim to an identity.
     * In a real SSI architecture, this is off-chain (EIP-712), but here we register on-chain for the MVP proof.
     */
    function addClaim(
        address _subject,
        uint256 _topic,
        uint256 _scheme,
        bytes memory _signature,
        bytes memory _data,
        string memory _uri
    ) public {
        require(isIdentityRegistered[_subject], "Subject identity not registered");
        
        Claim memory newClaim = Claim({
            topic: _topic,
            scheme: _scheme,
            issuer: msg.sender,
            signature: _signature,
            data: _data,
            uri: _uri
        });

        claims[_subject].push(newClaim);
        emit ClaimAdded(_subject, _topic, msg.sender);
    }

    /**
     * @dev Get total claims for a subject.
     */
    function getClaimCount(address _subject) public view returns (uint256) {
        return claims[_subject].length;
    }
    
    /**
     * @dev Simple verification helper for the platform.
     */
    function hasKYCClaim(address _subject, address _trustedIssuer, uint256 _kycTopic) public view returns (bool) {
        for (uint256 i = 0; i < claims[_subject].length; i++) {
            if (claims[_subject][i].issuer == _trustedIssuer && claims[_subject][i].topic == _kycTopic) {
                return true;
            }
        }
        return false;
    }
}
