# RAG Chatbot Frontend

This is the frontend for the RAG Chatbot project, built with **React**, **TypeScript**, and **Tailwind CSS**.  
It provides both a full-page chat interface and an embeddable chat widget for integration into any website.

---

## Table of Contents

- [Overview](#overview)
- [Project Structure](#project-structure)
- [Main Features](#main-features)
- [How It Works](#how-it-works)
- [Key Components](#key-components)
  - [Chat (`src/components/Chat.tsx`)](#chat-srccomponentschattsx)
  - [Widget (`src/components/ChatWidget.tsx`)](#widget-srccomponentschatwidgettsx)
- [Widget Embedding](#widget-embedding)
- [Development](#development)
- [Customization](#customization)

---

## Overview

- **Modern UI**: Responsive, branded chat interface using Tailwind CSS.
- **Markdown Support**: Assistant responses support Markdown (links, lists, tables, code, etc.).
- **Widget**: Easily embeddable chat widget for any website, using the same React logic as the main chat.
- **Robust Error Handling**: User-friendly error messages and stable UI.
- **Backend Integration**: Communicates with a Spring Boot backend via REST API.

---

## Project Structure

```
frontend/
├── public/
│   ├── index.html
│   ├── widget.js           # Script for embedding the chat widget
│   └── ...
├── src/
│   ├── components/
│   │   ├── Chat.tsx        # Main chat logic
│   │   ├── ChatWidget.tsx  # Widget logic
│   │   ├── WidgetPage.tsx
│   │   └── WidgetPageEmbed.tsx
│   ├── App.tsx
│   ├── index.tsx
│   └── index.css
├── tailwind.config.js
├── package.json
└── ...
```

---

## Main Features

- **Chat with AI Assistant**: User-friendly interface for interacting with the AI.
- **Markdown Rendering**: Assistant messages are rendered with full Markdown support.
- **Chat History**: Maintains conversation context for better answers.
- **Widget Mode**: Floating chat widget can be embedded on any site via a single script.
- **Branding**: Colors and styles match university branding.

---

## How It Works

1. **User sends a message** via the chat interface or widget.
2. **Frontend sends a POST request** to the backend with the current message and chat history.
3. **Backend responds** with an assistant message (Markdown supported).
4. **Frontend displays the response** in the chat, rendering Markdown as formatted text.
5. **Widget** can be opened/closed and is fully isolated from the host site.

---

## Key Components

### Chat (`src/components/Chat.tsx`)

This is the main chat interface for the application.

**Core Logic:**
- Maintains a list of messages (`user` and `assistant` roles).
- Handles user input, sending messages, and displaying responses.
- Sends chat history and the current user query to the backend (`/aiAssistant/chat`).
- Renders assistant responses using `react-markdown` and `remark-gfm` for full Markdown support (links, tables, lists, code blocks, etc.).
- UI is styled with Tailwind CSS, matching the university's branding.
- Includes a header with an animated icon, a reset button, and a loading indicator.
- User messages are right-aligned; assistant messages are left-aligned.

**Error Handling:**
- If the backend is unreachable or returns an error, a friendly error message is shown in the chat.

---

### Widget (`src/components/ChatWidget.tsx`)

This is the embeddable chat widget, designed to be loaded on any website.

**Core Logic:**
- Uses the same message handling and Markdown rendering as the main chat.
- Starts with a default assistant greeting.
- Can be toggled open/closed via a floating button.
- When open, displays a chat window with the same look and feel as the main chat, but in a compact, widget-friendly format.
- All assistant messages are strictly left-aligned for consistency, regardless of the host site's styles (enforced via injected CSS).
- Includes a reset button and a close button in the header.

**Robustness:**
- The widget is fully isolated from the host page (rendered in an iframe).
- Handles errors gracefully, ensuring the chat window never disappears unexpectedly.

---

## Widget Embedding

To embed the chat widget on any website, simply include the following script tag in your HTML:

```html
<script src="http://localhost:3000/widget.js"></script>
```

or insert in browser console: 

```javascript
fetch('http://localhost:3000/widget.js').then(r => r.text()).then(code => eval(code))
```
- This script injects an iframe into the page, loading the widget from `/widget-embed`.
- The widget is fully self-contained and will not interfere with the host site's styles or scripts.

---

## Development

**Install dependencies:**
```bash
npm install
```

**Start the development server:**
```bash
npm start
```
- The app will be available at [http://localhost:3000](http://localhost:3000).

**Build for production:**
```bash
npm run build
```

---

## Customization

- **Branding**: Colors and styles can be easily changed via Tailwind classes in the components.
- **API Endpoint**: Update the backend URL in `Chat.tsx` and `ChatWidget.tsx` if needed.
- **Widget Size**: Adjust the width/height in `public/widget.js` and `ChatWidget.tsx` for your needs.
- **Markdown Rendering**: Extend or customize Markdown support by editing the `components` object in the chat components.

---
