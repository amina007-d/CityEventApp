package com.example.cityeventproject

import com.example.cityeventproject.domain.logic.RecommendationEngine
import com.example.cityeventproject.domain.model.Event
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import java.time.LocalDate

class RecommendationEngineTest {
    @Test fun scores_prefer_same_city() {
        val today = LocalDate.parse("2026-02-11")
        val a = Event("1","A","2026-02-12",null,"Astana",null,null,null,null,null,false)
        val b = Event("2","B","2026-02-12",null,"Almaty",null,null,null,null,null,false)
        val sa = RecommendationEngine.score(a, "Astana", today)
        val sb = RecommendationEngine.score(b, "Astana", today)
        assertThat(sa).isGreaterThan(sb)
    }

    @Test fun sort_recommended_orders_by_score() {
        val today = LocalDate.parse("2026-02-11")
        val fav = Event("1","Fav","2026-03-01",null,"Astana",null,null,null,null,null,true)
        val soon = Event("2","Soon","2026-02-12",null,"Almaty",null,null,null,null,null,false)
        val out = RecommendationEngine.sortRecommended(listOf(fav, soon), "Astana", today)
        assertThat(out.first().id).isEqualTo("2") // date proximity beats fav bonus here
    }
}
