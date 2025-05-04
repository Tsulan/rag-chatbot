# RAG Chatbot

A Retrieval-Augmented Generation (RAG) chatbot application that uses Azure OpenAI and Azure AI Search to provide context-aware responses based on document content.

## Architecture Overview

### Core Modules
- **Frontend**: React-based web interface
- **Backend**: Spring Boot application
- **Azure AI Services**: OpenAI and Vector Search integration

### Data Flow
1. Document Processing:
   ```
   HTML Document -> Cleaned HTML -> Markdown -> Master Chunks -> Sub-chunks -> Vector Embeddings of Sub-chunks -> Azure Vector Search
   ```

2. User Interaction Flow:
   ```
   User Query -> Query Embedding -> Vector Search -> Relevant Context -> OpenAI Generation -> Response
   ```

## Technical Stack

### Backend
- Java 17
- Spring Boot 3.4.4
- Key Dependencies:
  - Spring Boot Starter Web
  - Azure AI OpenAI (1.0.0-beta.16)
  - Azure Search Documents (11.7.6)
  - Lombok
  - JSoup (1.17.2)
  - Flexmark HTML2MD Converter (0.64.8)

### Frontend
- React
- TypeScript
- Tailwind CSS

## Project Structure

```
rag-chatbot/
├── src/main/java/com/example/rag_chatbot/
│   ├── RagChatbotApplication.java           # Main application class
│   │
│   ├── azureaiclient/                       # Azure OpenAI integration
│   │   ├── chat/
│   │   │   └── AzureAiClient.java          # OpenAI API client implementation
│   │   └── configuration/
│   │       └── AzureAiConfiguration.java    # OpenAI configuration
│   │
│   ├── azureaisearch/                       # Azure Vector Search integration
│   │   ├── config/
│   │   │   └── AzureAiSearchConfig.java    # Vector Search configuration
│   │   ├── controller/
│   │   │   └── IndexController.java        # Index management endpoints
│   │   ├── model/
│   │   │   ├── ChunkedDocument.java        # Document chunking model
│   │   │   ├── MasterChunk.java            # Master chunk model
│   │   │   ├── QueryChunk.java             # Query chunk model for Azure Vector Search
│   │   │   └── SubChunk.java               # Sub-chunk model
│   │   ├── services/
│   │   │   ├── ChunkSplitter.java          # Document chunking service
│   │   │   ├── DocumentsService.java       # Document management service
│   │   │   ├── HtmlMarkdownProcessingService.java  # HTML to Markdown conversion
│   │   │   └── IndexService.java           # Index management service
│   │   └── utils/
│   │       └── VectorDbUtils.java          # Vector database utilities
│   │
│   ├── controllers/                         # REST API endpoints
│   │   └── ChatController.java             # Chat endpoints
│   │
│   ├── models/                             # Data models
│   │   ├── ChatHistory.java                # Chat history model
│   │   ├── ChatRequest.java                # Chat request model
│   │   ├── ChatResponse.java               # Chat response model
│   │   └── RephrasedUserInputResponse.java # Rephrased query model
│   │
│   ├── services/                           # Business logic
│   │   └── ChatService.java                # Core chat service
│   │
│   ├── utils/                              # Utility classes
│   │   └── ChatUtils.java                  # Chat-related utilities
│   │
│   └── exceptions/                         # Custom exceptions
│       └── ChatException.java              # Chat-related exceptions
│
└── frontend/                # React frontend application
```

## Key Components

### Document Processing
- `ChunkSplitter`: Splits documents into master chunks and sub-chunks
- `HtmlMarkdownProcessingService`: Converts HTML to Markdown
- `DocumentsService`: Handles document upload and retrieval from Azure Vector Search

### Chat Processing
- `ChatService`: Core service handling chat interactions
- `AzureAiClient`: Manages OpenAI API interactions
- `VectorDbUtils`: Utilities for vector search operations

## API Endpoints

### Chat
- `POST /aiAssistant/chat`: Process user queries and return responses

### Index Management
- `POST /indexes/create`: Create a new search index
- `POST /indexes/upload-html-document`: Upload and process HTML documents

## Data Flow Example

### Document Processing
1. HTML document is uploaded
2. Converted to Markdown
3. Split into chunks:
   - Master chunks (2 per document)
   - Sub-chunks (2 per master chunk)
4. Generate embeddings for sub-chunks
5. Upload to Azure Vector Search

### Chat Flow
1. User query is received
2. Query is embedded using OpenAI
3. Vector search finds relevant documents
4. Context is combined with query
5. OpenAI generates response
6. Response is returned to user

## Environment Setup

1. Set required environment variables:
   - `AZURE_AI_API_KEY`
   - `AZURE_AI_SEARCH_ADMIN_KEY`
   - `AZURE_AI_SEARCH_QUERY_KEY`

2. Configure application.properties with your Azure endpoints

3. Build and run:
   ```bash
   # Backend
   mvn clean install
   mvn spring-boot:run

   # Frontend
   cd frontend
   npm install
   npm start
   ```

## Architecture Patterns

- **Layered Architecture**:
  - Controllers (API Layer)
  - Services (Business Logic)
  - Repositories (Data Access)

- **DTO Pattern**:
  - Request/Response DTOs for API communication
  - Model classes for internal data representation

- **Dependency Injection**:
  - Spring Boot's component-based architecture
  - Constructor injection for dependencies

## Security

- API keys stored as environment variables
- CORS configuration for frontend access
- Token limit validation for user queries 