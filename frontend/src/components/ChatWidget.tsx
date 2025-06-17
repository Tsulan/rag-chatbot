import React, { useState, useRef, useEffect } from 'react';
import axios from 'axios';
import ReactMarkdown from 'react-markdown';
import remarkGfm from 'remark-gfm';
import { Components } from 'react-markdown';

interface Message {
  role: 'user' | 'assistant';
  content: string;
}

interface ChatHistory {
  content: string;
  isUser: boolean;
}

interface CodeProps {
  inline?: boolean;
  className?: string;
  children?: React.ReactNode;
}

const ChatWidget: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([
    { role: 'assistant', content: 'Salut! Sunt asistentul USM. Cum te pot ajuta?' }
  ]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const [isOpen, setIsOpen] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  // Inject custom styles for text alignment
  useEffect(() => {
    const style = document.createElement('style');
    style.textContent = `
      .usm-widget-prose {
        text-align: left !important;
      }
      .usm-widget-prose * {
        text-align: left !important;
      }
      .usm-widget-prose p {
        text-align: left !important;
      }
      .usm-widget-prose ul, .usm-widget-prose ol {
        text-align: left !important;
      }
      .usm-widget-prose li {
        text-align: left !important;
      }
      .usm-widget-prose h1, .usm-widget-prose h2, .usm-widget-prose h3, .usm-widget-prose h4, .usm-widget-prose h5, .usm-widget-prose h6 {
        text-align: left !important;
      }
    `;
    document.head.appendChild(style);

    return () => {
      document.head.removeChild(style);
    };
  }, []);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim() || isLoading) return;

    const userMessage: Message = { role: 'user', content: input };
    setMessages(prev => [...prev, userMessage]);
    setInput('');
    setIsLoading(true);

    try {
      const chatHistory: ChatHistory[] = messages.map(msg => ({
        content: msg.content,
        isUser: msg.role === 'user'
      }));

      const response = await axios.post('http://localhost:8080/aiAssistant/chat', {
        userQuery: input,
        chatHistory: chatHistory
      }, {
        headers: {
          'Content-Type': 'application/json'
        }
      });

      const assistantMessage: Message = {
        role: 'assistant',
        content: response.data.chatResponse
      };
      setMessages(prev => [...prev, assistantMessage]);
    } catch (error) {
      console.error('Error sending message:', error);
      const errorMessage: Message = {
        role: 'assistant',
        content: 'Sorry, there was an error processing your request.'
      };
      setMessages(prev => [...prev, errorMessage]);
    } finally {
      setIsLoading(false);
    }
  };

  const components: Components = {
    a: ({ node, ...props }) => (
      <a {...props} className="text-[#0a392c] hover:text-[#0c4334] underline text-left" target="_blank" rel="noopener noreferrer" />
    ),
    table: ({ node, ...props }) => (
      <div className="overflow-x-auto text-left">
        <table {...props} className="min-w-full divide-y divide-gray-200" />
      </div>
    ),
    th: ({ node, ...props }) => (
      <th {...props} className="px-4 py-2 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider" />
    ),
    td: ({ node, ...props }) => (
      <td {...props} className="px-4 py-2 whitespace-nowrap text-sm text-gray-900 text-left" />
    ),
    code: ({ inline, className, children, ...props }: CodeProps) => (
      inline ? 
        <code {...props} className="bg-gray-100 rounded px-1 py-0.5 text-sm text-left">{children}</code> :
        <code {...props} className="block bg-gray-100 rounded p-2 text-sm overflow-x-auto text-left">{children}</code>
    ),
    pre: ({ node, ...props }) => (
      <pre {...props} className="bg-gray-100 rounded p-2 text-sm overflow-x-auto text-left" />
    ),
    ul: ({ node, ...props }) => (
      <ul {...props} className="list-disc list-inside space-y-1 my-2 text-left" />
    ),
    ol: ({ node, ...props }) => (
      <ol {...props} className="list-decimal list-inside space-y-1 my-2 text-left" />
    ),
    li: ({ node, ...props }) => (
      <li {...props} className="text-gray-800 text-left" />
    ),
    p: ({ node, ...props }) => (
      <p {...props} className="text-left mb-2" />
    ),
    h1: ({ node, ...props }) => (
      <h1 {...props} className="text-left text-2xl font-bold mb-4" />
    ),
    h2: ({ node, ...props }) => (
      <h2 {...props} className="text-left text-xl font-semibold mb-3" />
    ),
    h3: ({ node, ...props }) => (
      <h3 {...props} className="text-left text-lg font-semibold mb-2" />
    )
  };

  return (
    <div className="fixed bottom-5 right-5 z-50">
      {/* Toggle Button */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        className="w-16 h-16 rounded-full bg-[#0a392c] text-white shadow-lg hover:shadow-xl transition-all duration-300 flex items-center justify-center text-2xl hover:scale-110"
      >
        💬
      </button>

      {/* Chat Container */}
      {isOpen && (
        <div className="absolute bottom-20 right-0 w-[450px] h-[600px] bg-white rounded-lg shadow-2xl border border-gray-200 flex flex-col">
          {/* Header */}
          <div className="bg-[#0a392c] text-white p-4 rounded-t-lg flex items-center justify-between">
            <div className="flex items-center space-x-3">
              <div className="relative">
                <div className="w-6 h-6 rounded-full bg-white/20 flex items-center justify-center">
                  <div className="w-3 h-3 rounded-full bg-white animate-pulse"></div>
                </div>
                <div className="absolute -top-1 -right-1 w-2 h-2 rounded-full bg-[#00d084] animate-ping"></div>
              </div>
              <h3 className="text-lg font-semibold flex items-center">
                USM Assistant
                <span className="ml-2 text-xs bg-white/20 px-2 py-0.5 rounded-full">AI</span>
              </h3>
            </div>
            <button
              onClick={() => setIsOpen(false)}
              className="text-white hover:text-gray-200 text-xl font-bold"
            >
              ×
            </button>
          </div>

          {/* Messages */}
          <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50">
            {messages.map((message, index) => (
              <div
                key={index}
                className={`flex ${
                  message.role === 'user' ? 'justify-end' : 'justify-start'
                }`}
              >
                <div
                  className={`max-w-[85%] rounded-lg p-4 text-base ${
                    message.role === 'user'
                      ? 'bg-[#0a392c] text-white text-right'
                      : 'bg-white text-gray-800 border border-gray-200 shadow-sm text-left'
                  }`}
                >
                  {message.role === 'user' ? (
                    message.content
                  ) : (
                    <div className="prose prose-base max-w-none text-left usm-widget-prose">
                      <ReactMarkdown 
                        remarkPlugins={[remarkGfm]}
                        components={components}
                      >
                        {message.content}
                      </ReactMarkdown>
                    </div>
                  )}
                </div>
              </div>
            ))}
            {isLoading && (
              <div className="flex justify-start">
                <div className="bg-white text-gray-800 rounded-lg p-4 border border-gray-200 shadow-sm text-left">
                  <div className="flex items-center space-x-2">
                    <div className="w-2 h-2 bg-[#0a392c] rounded-full animate-bounce"></div>
                    <div className="w-2 h-2 bg-[#0a392c] rounded-full animate-bounce" style={{ animationDelay: '0.2s' }}></div>
                    <div className="w-2 h-2 bg-[#0a392c] rounded-full animate-bounce" style={{ animationDelay: '0.4s' }}></div>
                  </div>
                </div>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* Input Form */}
          <form onSubmit={handleSubmit} className="border-t p-4 bg-white">
            <div className="flex space-x-3">
              <input
                type="text"
                value={input}
                onChange={(e) => setInput(e.target.value)}
                placeholder="Scrieți mesajul..."
                className="flex-1 border border-gray-200 rounded-lg px-4 py-3 text-base focus:outline-none focus:border-[#0a392c] focus:ring-1 focus:ring-[#0a392c] transition-all duration-200"
                disabled={isLoading}
              />
              <button
                type="submit"
                disabled={isLoading}
                className="bg-[#0a392c] text-white px-4 py-3 rounded-lg hover:bg-[#0c4334] focus:outline-none focus:ring-2 focus:ring-[#0a392c] focus:ring-opacity-50 disabled:opacity-50 transition-all duration-200 flex items-center"
              >
                <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 5l7 7m0 0l-7 7m7-7H3" />
                </svg>
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};

export default ChatWidget; 