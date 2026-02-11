# CityEventProject (Android / Kotlin)

A production-like **City Events** app built for the **Native Mobile Development (iOS/Android) Endterm Project** (Track B: Android Kotlin).
The app demonstrates MVVM + Repository, Retrofit networking, Room offline cache, Firebase Auth, and Firebase Realtime Database (comments).

## Features (mapped to rubric)
- **5+ screens**: Feed, Details, Search/Filter, Create/Edit (comment editor), Profile/Settings
- **Deep navigation**: Feed → Details → Comments
- **External API (JSON) with pagination + debounced search**: Ticketmaster Discovery API (page-based)
- **Offline-first**: Room caches events; app is usable offline for cached content
- **Auth**: Firebase Authentication (email/password)
- **User data**: Favorites are stored per-user in Firebase Realtime Database
- **Realtime feature**: Comments per event with realtime updates (CRUD)
- **Images**: Coil
- **DI**: Hilt
- **Concurrency**: Coroutines + Flow
- **Testing**: 5+ unit tests for business logic

## Setup
### 1) Ticketmaster API key
Create `local.properties` in the project root and add:

```properties
TM_API_KEY=YOUR_TICKETMASTER_KEY
```

> Do not commit secrets.

### 2) Firebase
- Create a Firebase project
- Enable **Email/Password** in Authentication
- Create **Realtime Database**
- Download `google-services.json` and place it here: `app/google-services.json`

#### Realtime Database suggested structure
```
users/{uid}/favorites/{eventId}: true
comments/{eventId}/{commentId}: { uid, displayName, text, createdAt }
```

### 3) Run
- Open in Android Studio
- Sync Gradle
- Run the `app` configuration on an emulator/device

## Notes
- Offline policy: when online, refresh remote pages and upsert into Room; when offline, read from Room.
- Duplicate prevention: `EventEntity` uses `id` as primary key, upsert uses REPLACE.

## License
For educational use.
