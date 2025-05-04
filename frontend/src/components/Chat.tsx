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

const Chat: React.FC = () => {
  const [messages, setMessages] = useState<Message[]>([]);
  const [input, setInput] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!input.trim()) return;

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
      <a {...props} className="text-[#0a392c] hover:text-[#0c4334] underline" target="_blank" rel="noopener noreferrer" />
    ),
    table: ({ node, ...props }) => (
      <div className="overflow-x-auto">
        <table {...props} className="min-w-full divide-y divide-gray-200" />
      </div>
    ),
    th: ({ node, ...props }) => (
      <th {...props} className="px-4 py-2 bg-gray-50 text-left text-xs font-medium text-gray-500 uppercase tracking-wider" />
    ),
    td: ({ node, ...props }) => (
      <td {...props} className="px-4 py-2 whitespace-nowrap text-sm text-gray-900" />
    ),
    code: ({ inline, className, children, ...props }: CodeProps) => (
      inline ? 
        <code {...props} className="bg-gray-100 rounded px-1 py-0.5 text-sm">{children}</code> :
        <code {...props} className="block bg-gray-100 rounded p-2 text-sm overflow-x-auto">{children}</code>
    ),
    pre: ({ node, ...props }) => (
      <pre {...props} className="bg-gray-100 rounded p-2 text-sm overflow-x-auto" />
    ),
    ul: ({ node, ...props }) => (
      <ul {...props} className="list-disc list-inside space-y-1 my-2" />
    ),
    ol: ({ node, ...props }) => (
      <ol {...props} className="list-decimal list-inside space-y-1 my-2" />
    ),
    li: ({ node, ...props }) => (
      <li {...props} className="text-gray-800" />
    )
  };

  return (
    <div className="flex flex-col h-[85vh] mt-4 bg-white rounded-lg shadow-lg border border-gray-100">
      <div className="bg-[#0a392c] text-white p-4 rounded-t-lg flex items-center space-x-3">
        <div className="relative">
          <div className="w-8 h-8 rounded-full bg-white/20 flex items-center justify-center">
            <div className="w-4 h-4 rounded-full bg-white animate-pulse"></div>
          </div>
          <div className="absolute -top-1 -right-1 w-3 h-3 rounded-full bg-[#00d084] animate-ping"></div>
        </div>
        <h2 className="text-xl font-semibold flex items-center">
          USM Assistant
          <span className="ml-2 text-sm bg-white/20 px-2 py-0.5 rounded-full">AI</span>
        </h2>
      </div>
      <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50">
        {messages.map((message, index) => (
          <div
            key={index}
            className={`flex ${
              message.role === 'user' ? 'justify-end' : 'justify-start'
            }`}
          >
            <div
              className={`max-w-[70%] rounded-lg p-3 ${
                message.role === 'user'
                  ? 'bg-[#0a392c] text-white'
                  : 'bg-white text-gray-800 border border-gray-200 shadow-sm hover:shadow-md transition-shadow duration-200'
              }`}
            >
              {message.role === 'user' ? (
                message.content
              ) : (
                <div className="prose prose-sm max-w-none">
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
            <div className="bg-white text-gray-800 rounded-lg p-3 border border-gray-200 shadow-sm">
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
      <form onSubmit={handleSubmit} className="border-t p-4 bg-white">
        <div className="flex space-x-4">
          <input
            type="text"
            value={input}
            onChange={(e) => setInput(e.target.value)}
            placeholder="Type your message..."
            className="flex-1 border border-gray-200 rounded-lg px-4 py-2 focus:outline-none focus:border-[#0a392c] focus:ring-1 focus:ring-[#0a392c] transition-all duration-200"
            disabled={isLoading}
          />
          <button
            type="submit"
            disabled={isLoading}
            className="bg-[#0a392c] text-white px-6 py-2 rounded-lg hover:bg-[#0c4334] focus:outline-none focus:ring-2 focus:ring-[#0a392c] focus:ring-opacity-50 disabled:opacity-50 transition-all duration-200 flex items-center space-x-2"
          >
            <span>Send</span>
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M14 5l7 7m0 0l-7 7m7-7H3" />
            </svg>
          </button>
        </div>
      </form>
    </div>
  );
};

export default Chat; 