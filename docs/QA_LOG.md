# QA Log (Self-testing)

> This log documents issues found during manual testing and how they were fixed.
> Each entry includes steps, expected vs actual, and the fix summary.

---

## Issue 01 — Past events appeared in "All" feed
- **Area:** Feed / API query (Ticketmaster)
- **Severity:** Medium
- **Steps to reproduce:**
    1. Open Feed with default filters (no start date selected).
    2. Observe the list.
- **Expected:** Only upcoming events are shown by default.
- **Actual:** Past/old events sometimes appeared.
- **Root cause:** `startDateTime` was not defaulting to "now" for API request.
- **Fix:** Set `startDateTime = (startDateTime ?: nowIsoUtc())` in `EventsPagingSource`.
- **Verification:** Offline/online tests show only upcoming events by default.

---

## Issue 02 — Offline mode showed empty list without explanation
- **Area:** Feed / error state
- **Severity:** Medium
- **Steps to reproduce:**
    1. Launch app and load Feed once (to cache data).
    2. Turn off internet.
    3. Reopen app / refresh feed.
- **Expected:** Cached events are shown OR user sees a clear offline message.
- **Actual:** Sometimes an empty list appeared with no context.
- **Root cause:** NetworkError path returned empty page for non-first pages and UI did not show an offline hint.
- **Fix:** Ensure first page uses cached events (`dao.observeAll().first()`) and add UI error/offline hint.
- **Verification:** With internet off, Feed shows cached items (first page) and clear message if cache is empty.

---

## Issue 03 — Favorites not updating immediately on Profile
- **Area:** Firebase Favorites / Profile screen
- **Severity:** Medium
- **Steps to reproduce:**
    1. Open Feed, favorite an event.
    2. Open Profile.
- **Expected:** Favorites list contains the event immediately.
- **Actual:** Favorites list sometimes required reopening Profile.
- **Root cause:** Profile screen was not observing the favorites Flow continuously (or mapping was delayed).
- **Fix:** Observe favorites via Flow/State in Profile VM and update UI reactively.
- **Verification:** Favorites appear instantly after tapping heart.

---

## Issue 04 — Comments displayed as raw JSON-like text
- **Area:** Comments UI formatting
- **Severity:** Low/Medium
- **Steps to reproduce:**
    1. Open event details.
    2. Open comments section.
- **Expected:** Comment shows author + message with clean formatting.
- **Actual:** Comment content looked unformatted / JSON-like.
- **Root cause:** UI was rendering object/string representation instead of fields.
- **Fix:** Render `comment.text`, `comment.displayName`, and formatted date instead of raw object.
- **Verification:** Comments now show properly formatted text.

---

## Issue 05 — Network failures had no consistent retry
- **Area:** Paging / error recovery
- **Severity:** Medium
- **Steps to reproduce:**
    1. Start scrolling Feed on unstable connection.
    2. Trigger a network failure (toggle internet).
- **Expected:** Show an error state with a Retry button; transient failures retry automatically.
- **Actual:** Error surfaced but recovery was inconsistent; no clear retry path in UI.
- **Root cause:** UI did not consistently use Paging `loadState` to show Retry; no auto-retry for transient errors.
- **Fix:**
    - Add manual retry button using `lazyPagingItems.retry()`
    - Add basic automatic retry for transient failures (IOException / 429 / 5xx) where applicable
- **Verification:** On failures, Retry button appears and works; transient failures retry a few times automatically.

---

## Issue 06 — Verbose logs present in Release builds
- **Area:** Logging / Release build configuration
- **Severity:** Low
- **Steps to reproduce:**
    1. Build Release variant.
    2. Run and check Logcat.
- **Expected:** No verbose/debug logs in Release.
- **Actual:** Debug network/body logs were visible.
- **Root cause:** HttpLoggingInterceptor enabled for all build types; no centralized logger gating.
- **Fix:** Add `AppLogger` and disable network logging in Release (`Level.NONE`), keep debug logs only in Debug.
- **Verification:** Release build shows only warning/error logs; no verbose network bodies.
