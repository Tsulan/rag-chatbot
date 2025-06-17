import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Chat from './components/Chat';
import ChatWidget from './components/ChatWidget';
import WidgetPage from './components/WidgetPage';

function App() {
  return (
    <Router>
      <div className="min-h-screen bg-gray-100">
        <div className="container mx-auto px-4 py-8">
          <div className="flex justify-between items-center mb-8">
            <h1 className="text-3xl font-bold text-gray-800">
              RAG Chatbot
            </h1>
            <nav className="flex space-x-4">
              <Link
                to="/"
                className="px-4 py-2 rounded-lg transition-all duration-200 bg-[#0a392c] text-white hover:bg-[#0c4334]"
              >
                Main Chat
              </Link>
              <Link
                to="/widget"
                className="px-4 py-2 rounded-lg transition-all duration-200 bg-white text-[#0a392c] border border-[#0a392c] hover:bg-[#0a392c] hover:text-white"
              >
                Widget Mode
              </Link>
            </nav>
          </div>
          
          <Routes>
            <Route path="/" element={<Chat />} />
            <Route path="/widget" element={
              <div className="text-center">
                <div className="bg-white rounded-lg shadow-lg border border-gray-100 p-8">
                  <h2 className="text-2xl font-semibold text-gray-800 mb-4">
                    Widget Mode Active
                  </h2>
                  <p className="text-gray-600 mb-6">
                    The chat widget is now active. Look for the chat button in the bottom-right corner of the screen.
                  </p>
                  <div className="inline-block bg-gray-100 rounded-lg p-4">
                    <div className="w-16 h-16 rounded-full bg-[#0a392c] text-white shadow-lg flex items-center justify-center text-2xl mx-auto mb-2">
                      💬
                    </div>
                    <p className="text-sm text-gray-600">Chat button appears here</p>
                  </div>
                </div>
                <ChatWidget />
              </div>
            } />
            <Route path="/widget-embed" element={<WidgetPage />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App; 