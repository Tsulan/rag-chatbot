import React from 'react';
import Chat from './components/Chat';

function App() {
  return (
    <div className="min-h-screen bg-gray-100">
      <div className="container mx-auto px-4 py-8">
        <h1 className="text-3xl font-bold text-center mb-8 text-gray-800">
          RAG Chatbot
        </h1>
        <Chat />
      </div>
    </div>
  );
}

export default App; 