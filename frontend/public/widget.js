(function() {
    // Widget configuration
    const config = {
        apiUrl: 'http://localhost:8080/aiAssistant/chat',
        position: 'bottom-right'
    };

    // Create widget styles
    const styles = `
        .usm-chat-widget {
            position: fixed;
            bottom: 20px;
            right: 20px;
            z-index: 10000;
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
        }

        .usm-chat-toggle {
            width: 64px;
            height: 64px;
            border-radius: 50%;
            background: #0a392c;
            border: none;
            color: white;
            cursor: pointer;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            transition: all 0.3s ease;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 24px;
            z-index: 10000;
        }

        .usm-chat-toggle:hover {
            transform: scale(1.1);
            box-shadow: 0 6px 16px rgba(0,0,0,0.2);
        }

        .usm-chat-container {
            position: fixed;
            bottom: 100px;
            right: 20px;
            width: 450px;
            height: 600px;
            background: white;
            border-radius: 12px;
            box-shadow: 0 8px 32px rgba(0,0,0,0.15);
            display: none;
            flex-direction: column;
            overflow: hidden;
            z-index: 10000;
            border: 1px solid #e1e5e9;
        }

        .usm-chat-header {
            background: #0a392c;
            color: white;
            padding: 16px;
            display: flex;
            align-items: center;
            justify-content: space-between;
            border-radius: 12px 12px 0 0;
            min-height: 60px;
            box-sizing: border-box;
        }

        .usm-chat-header-content {
            display: flex;
            align-items: center;
            gap: 12px;
            flex: 1;
        }

        .usm-chat-avatar {
            position: relative;
            flex-shrink: 0;
        }

        .usm-chat-avatar-circle {
            width: 24px;
            height: 24px;
            border-radius: 50%;
            background: rgba(255,255,255,0.2);
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .usm-chat-avatar-dot {
            width: 12px;
            height: 12px;
            border-radius: 50%;
            background: white;
            animation: pulse 2s infinite;
        }

        .usm-chat-avatar-ping {
            position: absolute;
            top: -4px;
            right: -4px;
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: #00d084;
            animation: ping 1s infinite;
        }

        .usm-chat-title {
            margin: 0;
            font-size: 18px;
            font-weight: 600;
            display: flex;
            align-items: center;
            color: white;
            flex: 1;
            line-height: 1;
        }

        .usm-chat-badge {
            margin-left: 8px;
            font-size: 12px;
            background: rgba(255,255,255,0.2);
            padding: 2px 8px;
            border-radius: 12px;
            color: white;
            flex-shrink: 0;
            line-height: 1;
        }

        .usm-chat-close {
            background: none;
            border: none;
            color: white;
            cursor: pointer;
            font-size: 24px;
            font-weight: bold;
            padding: 0;
            width: 24px;
            height: 24px;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: color 0.2s;
            flex-shrink: 0;
            margin-left: 16px;
        }

        .usm-chat-close:hover {
            color: #e5e7eb;
        }

        .usm-chat-messages {
            flex: 1;
            overflow-y: auto;
            padding: 16px;
            background: #f8f9fa;
            display: flex;
            flex-direction: column;
            gap: 16px;
        }

        .usm-chat-message {
            display: flex;
        }

        .usm-chat-message.user {
            justify-content: flex-end;
        }

        .usm-chat-message.assistant {
            justify-content: flex-start;
        }

        .usm-chat-message-content {
            max-width: 85%;
            padding: 16px;
            border-radius: 12px;
            font-size: 16px;
            line-height: 1.5;
        }

        .usm-chat-message.user .usm-chat-message-content {
            background: #0a392c;
            color: white;
            text-align: right;
        }

        .usm-chat-message.assistant .usm-chat-message-content {
            background: white;
            color: #333;
            border: 1px solid #e1e5e9;
            text-align: left;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }

        .usm-chat-message.assistant .usm-chat-message-content p {
            margin: 0 0 8px 0;
            text-align: left;
        }

        .usm-chat-message.assistant .usm-chat-message-content ul,
        .usm-chat-message.assistant .usm-chat-message-content ol {
            margin: 8px 0;
            padding-left: 20px;
            text-align: left;
        }

        .usm-chat-message.assistant .usm-chat-message-content li {
            margin-bottom: 4px;
            text-align: left;
        }

        .usm-chat-message.assistant .usm-chat-message-content a {
            color: #0a392c;
            text-decoration: underline;
        }

        .usm-chat-message.assistant .usm-chat-message-content strong {
            font-weight: 600;
        }

        .usm-chat-message.assistant .usm-chat-message-content em {
            font-style: italic;
        }

        .usm-chat-input-container {
            padding: 16px;
            border-top: 1px solid #e1e5e9;
            background: white;
        }

        .usm-chat-input-form {
            display: flex;
            gap: 12px;
        }

        .usm-chat-input {
            flex: 1;
            padding: 12px 16px;
            border: 1px solid #e1e5e9;
            border-radius: 8px;
            font-size: 16px;
            outline: none;
        }

        .usm-chat-input:focus {
            border-color: #0a392c;
            box-shadow: 0 0 0 1px #0a392c;
        }

        .usm-chat-send {
            background: #0a392c;
            color: white;
            border: none;
            border-radius: 8px;
            padding: 12px 16px;
            cursor: pointer;
            display: flex;
            align-items: center;
            justify-content: center;
            transition: background-color 0.2s;
        }

        .usm-chat-send:hover {
            background: #0c4334;
        }

        .usm-chat-send:disabled {
            opacity: 0.5;
            cursor: not-allowed;
        }

        .usm-chat-loading {
            display: flex;
            justify-content: flex-start;
        }

        .usm-chat-loading-content {
            background: white;
            color: #333;
            border-radius: 12px;
            padding: 16px;
            border: 1px solid #e1e5e9;
            box-shadow: 0 1px 3px rgba(0,0,0,0.1);
        }

        .usm-chat-loading-dots {
            display: flex;
            align-items: center;
            gap: 8px;
        }

        .usm-chat-loading-dot {
            width: 8px;
            height: 8px;
            background: #0a392c;
            border-radius: 50%;
            animation: bounce 1.4s infinite ease-in-out;
        }

        .usm-chat-loading-dot:nth-child(1) { animation-delay: -0.32s; }
        .usm-chat-loading-dot:nth-child(2) { animation-delay: -0.16s; }

        @keyframes bounce {
            0%, 80%, 100% { transform: scale(0); }
            40% { transform: scale(1); }
        }

        @keyframes pulse {
            0%, 100% { opacity: 1; }
            50% { opacity: 0.5; }
        }

        @keyframes ping {
            75%, 100% {
                transform: scale(2);
                opacity: 0;
            }
        }
    `;

    // Inject styles
    const styleSheet = document.createElement('style');
    styleSheet.textContent = styles;
    document.head.appendChild(styleSheet);

    // Create widget HTML
    const widgetHTML = `
        <div class="usm-chat-widget">
            <button class="usm-chat-toggle" id="usm-chat-toggle">
                💬
            </button>
            <div class="usm-chat-container" id="usm-chat-container">
                <div class="usm-chat-header">
                    <div class="usm-chat-header-content">
                        <div class="usm-chat-avatar">
                            <div class="usm-chat-avatar-circle">
                                <div class="usm-chat-avatar-dot"></div>
                            </div>
                            <div class="usm-chat-avatar-ping"></div>
                        </div>
                        <h3 class="usm-chat-title">
                            USM Assistant
                            <span class="usm-chat-badge">AI</span>
                        </h3>
                    </div>
                    <button class="usm-chat-close" id="usm-chat-close">×</button>
                </div>
                <div class="usm-chat-messages" id="usm-chat-messages">
                </div>
                <div class="usm-chat-input-container">
                    <form class="usm-chat-input-form" id="usm-chat-form">
                        <input 
                            type="text" 
                            class="usm-chat-input" 
                            placeholder="Scrieți mesajul..."
                            id="usm-chat-input"
                        >
                        <button type="submit" class="usm-chat-send" id="usm-chat-send">
                            <svg width="20" height="20" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M14 5l7 7m0 0l-7 7m7-7H3"></path>
                            </svg>
                        </button>
                    </form>
                </div>
            </div>
        </div>
    `;

    // Inject widget HTML
    document.body.insertAdjacentHTML('beforeend', widgetHTML);

    // Widget functionality
    const widget = {
        messages: [
            { content: 'Salut! Sunt asistentul USM. Cum te pot ajuta?', role: 'assistant' }
        ],
        isOpen: false,
        isLoading: false,

        toggle() {
            const container = document.getElementById('usm-chat-container');
            if (this.isOpen) {
                container.style.display = 'none';
                this.isOpen = false;
            } else {
                container.style.display = 'flex';
                this.isOpen = true;
                this.renderMessages();
                document.getElementById('usm-chat-input').focus();
            }
        },

        close() {
            const container = document.getElementById('usm-chat-container');
            container.style.display = 'none';
            this.isOpen = false;
        },

        addMessage(content, role) {
            this.messages.push({ content, role });
            this.renderMessages();
        },

        renderMessages() {
            const messagesContainer = document.getElementById('usm-chat-messages');
            messagesContainer.innerHTML = '';
            
            this.messages.forEach(msg => {
                const messageDiv = document.createElement('div');
                messageDiv.className = `usm-chat-message ${msg.role}`;
                
                const contentDiv = document.createElement('div');
                contentDiv.className = 'usm-chat-message-content';
                
                if (msg.role === 'assistant') {
                    contentDiv.innerHTML = this.parseMarkdown(msg.content);
                } else {
                    contentDiv.textContent = msg.content;
                }
                
                messageDiv.appendChild(contentDiv);
                messagesContainer.appendChild(messageDiv);
            });
            
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        },

        parseMarkdown(text) {
            if (!text) return '';
            
            return text
                // Bold
                .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
                // Italic
                .replace(/\*(.*?)\*/g, '<em>$1</em>')
                // Links
                .replace(/\[([^\]]+)\]\(([^)]+)\)/g, '<a href="$2" target="_blank" rel="noopener noreferrer" style="color: #0a392c; text-decoration: underline;">$1</a>')
                // Lists
                .replace(/^\d+\. (.*$)/gim, '<li>$1</li>')
                .replace(/^- (.*$)/gim, '<li>$1</li>')
                // Wrap lists
                .replace(/(<li.*<\/li>)/gs, function(match) {
                    const items = match.split('</li>').filter(item => item.trim());
                    if (items.length > 0) {
                        const firstItem = items[0];
                        if (firstItem.includes('1.')) {
                            return '<ol style="margin: 8px 0; padding-left: 20px; text-align: left;">' + match + '</ol>';
                        } else {
                            return '<ul style="margin: 8px 0; padding-left: 20px; text-align: left;">' + match + '</ul>';
                        }
                    }
                    return match;
                })
                // Paragraphs
                .replace(/\n\n/g, '</p><p style="margin: 0 0 8px 0; text-align: left;">')
                .replace(/^(.+)$/gm, '<p style="margin: 0 0 8px 0; text-align: left;">$1</p>')
                // Clean up empty paragraphs
                .replace(/<p style="margin: 0 0 8px 0; text-align: left;"><\/p>/g, '')
                .replace(/<p style="margin: 0 0 8px 0; text-align: left;">\s*<\/p>/g, '')
                // Line breaks
                .replace(/\n/g, '<br>');
        },

        showLoading() {
            const messagesContainer = document.getElementById('usm-chat-messages');
            const loadingDiv = document.createElement('div');
            loadingDiv.className = 'usm-chat-loading';
            loadingDiv.innerHTML = `
                <div class="usm-chat-loading-content">
                    <div class="usm-chat-loading-dots">
                        <div class="usm-chat-loading-dot"></div>
                        <div class="usm-chat-loading-dot"></div>
                        <div class="usm-chat-loading-dot"></div>
                    </div>
                </div>
            `;
            messagesContainer.appendChild(loadingDiv);
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        },

        hideLoading() {
            const loadingElement = document.querySelector('.usm-chat-loading');
            if (loadingElement) {
                loadingElement.remove();
            }
        },

        async sendMessage(event) {
            event.preventDefault();
            const input = document.getElementById('usm-chat-input');
            const sendButton = document.getElementById('usm-chat-send');
            
            if (!input) return;
            
            const message = input.value.trim();
            
            if (!message || this.isLoading) return;

            // Add user message
            this.addMessage(message, 'user');
            input.value = '';
            this.isLoading = true;
            sendButton.disabled = true;
            this.showLoading();

            try {
                const response = await fetch(config.apiUrl, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify({
                        userQuery: message,
                        chatHistory: this.messages.map(msg => ({
                            content: msg.content,
                            isUser: msg.role === 'user'
                        }))
                    })
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const data = await response.json();
                this.hideLoading();
                this.addMessage(data.chatResponse, 'assistant');
            } catch (error) {
                console.error('Error sending message:', error);
                this.hideLoading();
                this.addMessage('Sorry, there was an error processing your request.', 'assistant');
            } finally {
                this.isLoading = false;
                sendButton.disabled = false;
            }
        }
    };

    // Event listeners
    document.getElementById('usm-chat-toggle').addEventListener('click', () => widget.toggle());
    document.getElementById('usm-chat-close').addEventListener('click', () => widget.close());
    document.getElementById('usm-chat-form').addEventListener('submit', (e) => widget.sendMessage(e));

    // Initialize widget
    widget.renderMessages();

    // Expose widget API
    window.USMChatWidget = {
        toggle: () => widget.toggle(),
        close: () => widget.close(),
        isOpen: () => widget.isOpen
    };
})(); 