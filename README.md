# Media Player Shuffler
This is a pet project that allowed me to learn more about the Spring framework. Current refactoring it to apply clean principles.

In this public repo, it has been modified to be a podcast player.

Frontend is in JS and thymeleaf, it is far from best practices. I want to change to React at some point, but my focus on the backend

[![Gradle Build](https://img.shields.io/badge/build-gradle-blue.svg)](https://gradle.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3-green.svg)](https://spring.io/projects/spring-boot)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

A Spring Boot based podcast player with advanced directory navigation and playback features. This project serves as a learning platform for me to learn Spring framework and clean architecture principles.

## 📋 Table of Contents
- [Features](#features)
- [Usage](#usage)
- [Architecture](#architecture)
- [Tech Stack](#tech-stack)
- [Development](#development)
- [Roadmap](#roadmap)
- [Environment Configuration](#environment-configuration)
- [Installation](#installation)


## ✨ Features

### 🎵 Feed Navigation and Playback
* Navigates through hierarchical directory structures
* Plays feeds (audio content) in sequential order as users navigate directories
* Supports both random and ordered playback modes

### 💾 File System Integration
* Interfaces with a NAS or local file system to access media content
* Maps directory structures to navigable feeds
* Handles path formatting for cross-platform compatibility

### 📁 Content Organization
* Calendar-based content organization
* Directory-based feed organization
* Logo management for feeds
* Bookmark functionality for tracks

### ▶️ Track Selection & Playback
* Sequential playback through directory navigation
* Random track selection with preferences (specific days/times)
* Bookmarked track retrieval
* Parent-child relationship tracking for navigation history

## 🔧 Tech Stack
* `Java 17` - Core programming language
* `Spring Boot 3` - Application framework
* `Gradle` - Build and dependency management
* `Thymeleaf` - Server-side template engine
* `JavaScript` - Frontend functionality
* `Docker` - Containerization


### Key Services
The NasConnectorServiceFileSystem is a core service that handles file system interactions with the following capabilities:
* Listing and navigating directories
* Retrieving files and feeds
* Managing track selection (sequential)
* Calendar view integration
* Logo management
* Error handling for file operations

## 🛣️ Roadmap
- [ ] Implement clean architecture patterns
- [ ] Migrate frontend to React
- [ ] Add comprehensive test coverage

## ⚙️ Environment Configuration

The application requires several configuration properties in `src/main/resources/application.properties`:

```properties
# File System Configuration
# File share directory on the current server
file.share.root=<<FILE_SHARE_ROOT>>
# Windows path
file.share.windows.root=<<FILE_SHARE_WINDOWS_ROOT>>
file.slash=<<FILE_SLASH>>

# Server Configuration
server.port=8443
server.ssl.key-store=classpath:your-keystore.p12
server.ssl.key-store-password=<<KEYSTORE_KEY>>

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://<<DB_USERNAME>>:<<DB_PASSWORD>>@<<MONGODB_HOST>>:27017/?tls=true
spring.data.mongodb.database=radioDB
```

### Required Environment Variables
* `DB_USERNAME` - MongoDB username
* `DB_PASSWORD` - MongoDB password
* `KEYSTORE_KEY` - SSL keystore password
* `MONGODB_HOST` - MongoDB server hostname
* `FILE_SHARE_ROOT` - Root path for Linux/Mac file system (e.g., /mnt/media/podcasts/)
* `FILE_SLASH` - The current slash for the current server (e.g. `/` for Linux/Mac or `\\` for windows)
* `FILE_SHARE_WINDOWS_ROOT` - Root path for Windows file system (e.g., P:\\podcasts\\)

## 🚀 Installation

```bash
# Clone the repository
git clone https://github.com/yourusername/media-player-shuffler.git

# Navigate to project directory
cd media-player-shuffler

# Build with Gradle
./gradlew build

# Run the application
./gradlew bootRun
```
