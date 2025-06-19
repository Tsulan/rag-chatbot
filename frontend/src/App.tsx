import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import Chat from './components/Chat';
import ChatWidget from './components/ChatWidget';
import WidgetPage from './components/WidgetPage';
import WidgetPageEmbed from './components/WidgetPageEmbed';

function MainLayout({ children }: { children: React.ReactNode }) {
  return (
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
        {children}
      </div>
    </div>
  );
}

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/widget-embed" element={<WidgetPageEmbed />} />
        <Route path="/widget" element={<WidgetPage />} />
        <Route
          path="*"
          element={
            <MainLayout>
              <Routes>
                <Route path="/" element={<Chat />} />
              </Routes>
            </MainLayout>
          }
        />
      </Routes>
    </Router>
  );
}

export default App; 