'use client';

import { useState } from 'react';
import { ethers } from 'ethers';

export default function Home() {
  const [account, setAccount] = useState<string | null>(null);
  const [isConnecting, setIsConnecting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const connectWallet = async () => {
    setIsConnecting(true);
    setError(null);
    try {
      if (typeof window.ethereum !== 'undefined') {
        const provider = new ethers.BrowserProvider(window.ethereum);
        const accounts = await provider.send('eth_requestAccounts', []);
        if (accounts.length > 0) {
          setAccount(accounts[0]);
        }
      } else {
        setError('MetaMask or a Web3 wallet is not installed.');
      }
    } catch (err: any) {
      setError(err.message || 'Failed to connect wallet');
    } finally {
      setIsConnecting(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-950 text-white font-[family-name:var(--font-geist-sans)]">
      <nav className="border-b border-gray-800 bg-gray-900/50 backdrop-blur-md px-6 py-4 flex justify-between items-center sticky top-0 z-50">
        <h1 className="text-xl font-bold tracking-tight bg-gradient-to-r from-blue-400 to-emerald-400 bg-clip-text text-transparent">
          TrustMesh
        </h1>
        {account ? (
          <div className="flex items-center gap-3 bg-gray-800 border border-gray-700 rounded-full px-4 py-2 text-sm">
            <div className="w-2 h-2 rounded-full bg-emerald-500 animate-pulse"></div>
            {account.slice(0, 6)}...{account.slice(-4)}
          </div>
        ) : (
          <button
            onClick={connectWallet}
            disabled={isConnecting}
            className="bg-blue-600 hover:bg-blue-500 transition-colors text-white px-5 py-2 rounded-lg text-sm font-medium disabled:opacity-50"
          >
            {isConnecting ? 'Connecting...' : 'Connect Wallet'}
          </button>
        )}
      </nav>

      <main className="max-w-6xl mx-auto px-6 py-12">
        <div className="text-center space-y-6 mb-16">
          <h2 className="text-4xl md:text-5xl font-extrabold tracking-tight">
            Decentralized Zero-Trust Security
          </h2>
          <p className="text-gray-400 max-w-2xl mx-auto text-lg">
            Verify identity on-chain. Neutralize threats at the edge. TrustMesh creates a mathematically proven secure channel for your microservices.
          </p>
        </div>

        {error && (
          <div className="bg-red-500/10 border border-red-500/50 text-red-400 px-4 py-3 rounded-lg mb-8">
            {error}
          </div>
        )}

        <div className="grid md:grid-cols-2 gap-8">
          <div className="bg-gray-900/80 border border-gray-800 rounded-2xl p-8 hover:border-gray-700 transition duration-300">
            <div className="w-12 h-12 bg-blue-500/20 rounded-xl flex items-center justify-center mb-6">
              <svg className="w-6 h-6 text-blue-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
              </svg>
            </div>
            <h3 className="text-xl font-bold mb-3">Identity Status</h3>
            {account ? (
               <div>
                  <p className="text-gray-400 text-sm mb-4">Your connection is secured. Your address is currently registered as a Decentralized Identifier (DID).</p>
                  <div className="bg-gray-950 rounded-lg p-4 border border-gray-800">
                    <p className="text-sm"><span className="text-gray-500">DID: </span> did:trustmesh:{account}</p>
                    <p className="text-sm mt-2"><span className="text-gray-500">Status: </span> <span className="text-emerald-400">Verified</span></p>
                  </div>
               </div>
            ) : (
              <p className="text-gray-400">Connect your wallet to verify your identity and view your active verifiable credentials.</p>
            )}
          </div>

          <div className="bg-gray-900/80 border border-gray-800 rounded-2xl p-8 hover:border-gray-700 transition duration-300">
             <div className="w-12 h-12 bg-purple-500/20 rounded-xl flex items-center justify-center mb-6">
              <svg className="w-6 h-6 text-purple-400" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
            <h3 className="text-xl font-bold mb-3">AI Risk Engine</h3>
            <p className="text-gray-400 mb-6">Monitoring network telemetry for anomalous sequences and unexpected geolocation footprints.</p>
            
            <div className="space-y-4">
               <div>
                 <div className="flex justify-between text-sm mb-1">
                   <span>Overall Risk Score</span>
                   <span className="text-emerald-400">12 / 100</span>
                 </div>
                 <div className="h-2 bg-gray-800 rounded-full overflow-hidden">
                   <div className="h-full bg-emerald-500" style={{ width: '12%' }}></div>
                 </div>
               </div>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
}
