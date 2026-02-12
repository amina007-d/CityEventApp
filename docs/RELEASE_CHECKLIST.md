# Release Checklist (CityEventProject)

> Goal: verify core flows before creating a signed Release build and submitting.

## A) Install & Launch
1. **Clean install**: uninstall app → install fresh APK/AAB → app opens without crash.
2. **First launch**: splash/start screen renders, no blank screen.
3. **Permissions** (if any): app behaves correctly if user denies permissions.
4. **Rotation / configuration change**: rotate screen on Feed and Details (no crash, UI state is reasonable).

## B) Authentication (Firebase Auth)
5. **Register**: create a new account with valid email/password → success → navigates to Feed.
6. **Register validation**: invalid email / weak password shows correct error message.
7. **Login**: existing user logs in → navigates to Feed.
8. **Login error state**: wrong password shows friendly error, no crash.
9. **Logout**: logout from Profile → returns to Auth screen and clears user state.

## C) Feed (Paging + Filters)
10. **Initial feed load**: events appear, loading indicator shown during fetch.
11. **Pagination**: scroll down → next page loads (append) without duplicates.
12. **Pagination error**: simulate poor network → append error state is shown + retry works.
13. **Search / filter** (if present): apply keyword/category/city filters → results update.
14. **Date filter**: startDateTime defaults to "now" if not set → past events not shown.

## D) Details Screen
15. **Open details**: tap event card → details screen shows correct title/date/venue/address/image.
16. **Open external link**: event URL opens in browser (or shows error if missing).

## E) Favorites (Realtime Database)
17. **Add to favorites**: tap heart → saved in Firebase and UI updates.
18. **Remove favorite**: tap again → removed from Firebase and UI updates.
19. **Favorites on Profile**: favorites list loads and matches Feed favorites.

## F) Realtime Feature (Comments)
20. **Add comment**: submit comment → appears immediately in list.
21. **Realtime update**: comment added from another device/account appears without refresh.
22. **Comment validation**: too short/empty comment blocked with message.

## G) Offline Mode
23. **Offline feed**: turn off internet → Feed shows cached events (first page) + no crash.
24. **Offline details**: open a cached event details offline → shows cached content or a clear message.
25. **Offline favorites/comments**: try to favorite/comment offline → show clear error state (no crash).

## H) Error States & Recovery
26. **Missing API key**: if TM_API_KEY is empty → show clear error and avoid crash loop.
27. **Retry button**: manual retry works on refresh and append (Paging).
28. **Transient errors**: basic automatic retry triggers for network/5xx/429 (if implemented).
29. **Release logging**: debug/verbose logs are disabled in Release build.

## I) Performance & Stability
30. **Scrolling performance**: feed scroll is smooth; images load without UI jank.
31. **No crashes**: run through checklist once with Crashlytics enabled (no new crashes).
