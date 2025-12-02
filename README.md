# Movies App

A modern Android application built with Jetpack Compose that displays movies from a remote API. The app features genre filtering, infinite scroll pagination, and offline caching using Room database.

## Features

- 🎬 Browse movies with genre filtering
- 📱 Modern Material Design 3 UI with Jetpack Compose
- 🔄 Infinite scroll pagination
- 💾 Offline caching with Room database
- 🌐 Network calls using Retrofit and OkHttp
- ⚡ Parallel API calls for improved performance
- 🏗️ Clean Architecture with MVVM pattern

## Architecture

The app follows **Clean Architecture** principles with clear separation of concerns:

### Layers

1. **UI Layer** (`ui/`)
   - `MoviesScreen.kt` - Compose UI components
   - `MoviesViewModel.kt` - ViewModel managing UI state

2. **Domain Layer** (`domain/`)
   - `model/` - Domain models (Genre, Movie)
   - `repository/` - Repository interface
   - `usecase/` - Business logic use cases

3. **Data Layer** (`data/`)
   - `remote/` - Retrofit API service and DTOs
   - `local/` - Room database entities and DAO
   - `mapper/` - Data mapping functions
   - `MoviesRepositoryImpl.kt` - Repository implementation

4. **DI Layer** (`di/`)
   - `DataModule.kt` - Hilt dependency injection module

## Technology Stack

### Core Libraries

- **Jetpack Compose** - Modern declarative UI framework
- **Material Design 3** - Material Design components
- **ViewModel** - UI-related data holder
- **StateFlow** - Reactive state management

### Networking

- **Retrofit 2.11.0** - Type-safe HTTP client
- **OkHttp 4.12.0** - HTTP client with interceptors
- **Kotlinx Serialization** - JSON serialization/deserialization
- **Retrofit Kotlinx Serialization Converter** - Retrofit integration

### Database

- **Room 2.6.1** - Local database abstraction
- **Room KTX** - Coroutines support for Room

### Dependency Injection

- **Dagger Hilt 2.52** - Dependency injection framework

### Coroutines

- **Kotlinx Coroutines** - Asynchronous programming

## Project Structure

```
app/src/main/java/com/simple/moviesapp/
├── data/
│   ├── local/
│   │   ├── MoviesDao.kt          # Room DAO interface
│   │   ├── MoviesDatabase.kt     # Room database
│   │   └── MoviesEntities.kt     # Room entities
│   ├── mapper/
│   │   ├── GenreMapper.kt        # Genre mapping functions
│   │   └── MovieMapper.kt        # Movie mapping functions
│   ├── remote/
│   │   ├── dto/
│   │   │   ├── GenreDto.kt       # Genre DTO
│   │   │   └── MovieDto.kt       # Movie DTO
│   │   └── MoviesApiService.kt   # Retrofit API service
│   └── MoviesRepositoryImpl.kt   # Repository implementation
├── di/
│   └── DataModule.kt             # Hilt dependency injection
├── domain/
│   ├── model/
│   │   └── Models.kt              # Domain models
│   ├── repository/
│   │   └── MoviesRepository.kt   # Repository interface
│   └── usecase/
│       ├── GetGenresUseCase.kt   # Get genres use case
│       └── GetMoviesPageUseCase.kt # Get movies use case
├── ui/
│   ├── MoviesScreen.kt            # Compose UI
│   └── MoviesViewModel.kt        # ViewModel
├── MainActivity.kt                # Main activity
└── MoviesApp.kt                   # Application class
```

## Key Implementation Details

### API Integration

- **Base URL**: `https://movies-app-backend.replit.app/`
- **Endpoints**:
  - `GET /api/genres` - Fetch all genres
  - `GET /api/movies` - Fetch movies with pagination and optional genre filter

### Parallel API Calls

The app makes parallel API calls for genres and movies to improve performance:

```kotlin
val genresDeferred = async { getGenres().first() }
val moviesDeferred = async { getMoviesPage(...).first() }
val (genres, movies) = awaitAll(genresDeferred, moviesDeferred)
```

### Offline Caching

- Room database caches genres and movies locally
- App checks local database first before making network requests
- Data is automatically synced when network requests succeed

### Error Handling

- Network errors are caught and displayed to the user
- Loading states are managed properly
- Empty states are handled gracefully

## Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd MoviesAppSubmission
   ```

2. **Open in Android Studio**
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the project directory

3. **Sync Gradle**
   - Android Studio will automatically sync Gradle
   - Wait for dependencies to download

4. **Run the app**
   - Connect an Android device or start an emulator
   - Click "Run" or press `Shift+F10`

## Build Requirements

- **Android Studio**: Latest version
- **JDK**: 11 or higher
- **Min SDK**: 25
- **Target SDK**: 36
- **Compile SDK**: 36

## Dependencies

All dependencies are managed through `gradle/libs.versions.toml`:

- AndroidX Core KTX
- Jetpack Compose BOM
- Material Design 3
- Room Database
- Retrofit & OkHttp
- Dagger Hilt
- Kotlinx Serialization
- Kotlinx Coroutines

## Code Quality

- Clean Architecture principles
- Separation of concerns
- Dependency injection
- Type-safe API calls
- Proper error handling
- Offline-first approach

## Future Improvements

- Unit tests for ViewModel and UseCases
- UI tests for Compose screens
- Image loading with Coil or Glide
- Search functionality
- Movie details screen
- Favorites/bookmark feature
- Pull-to-refresh
- Network connectivity monitoring

## License

This project is for educational purposes.

