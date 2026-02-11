package com.example.cityeventproject.domain.logic

import com.example.cityeventproject.data.local.entities.EventEntity

/**
 * Merge policy:
 * - Primary key is remote id (prevents duplicates)
 * - If remote has a newer fetch time, replace cached values.
 * - If cached is newer (should not happen for remote events), keep cached.
 */
object SyncPolicy {
    fun merge(cached: EventEntity?, remote: EventEntity): EventEntity {
        return if (cached == null) remote
        else if (remote.lastUpdatedEpochMs >= cached.lastUpdatedEpochMs) remote
        else cached
    }
}
