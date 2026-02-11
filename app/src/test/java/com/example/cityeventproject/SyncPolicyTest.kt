package com.example.cityeventproject

import com.example.cityeventproject.data.local.entities.EventEntity
import com.example.cityeventproject.domain.logic.SyncPolicy
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class SyncPolicyTest {
    @Test fun merge_prefers_newer_remote() {
        val cached = EventEntity("1","Old","2026-02-11",null,null,null,null,null,null,null, lastUpdatedEpochMs = 100)
        val remote = cached.copy(name = "New", lastUpdatedEpochMs = 200)
        val merged = SyncPolicy.merge(cached, remote)
        assertThat(merged.name).isEqualTo("New")
    }

    @Test fun merge_keeps_cached_if_newer() {
        val cached = EventEntity("1","Cached","2026-02-11",null,null,null,null,null,null,null, lastUpdatedEpochMs = 300)
        val remote = cached.copy(name = "Remote", lastUpdatedEpochMs = 100)
        val merged = SyncPolicy.merge(cached, remote)
        assertThat(merged.name).isEqualTo("Cached")
    }
}
