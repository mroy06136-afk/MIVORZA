package com.example

import com.example.data.model.VirtualGift
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testMivorzaSeventyThirtyCreatorSplit() {
    // 1 Star = $0.01 USD Baseline
    val dragonGift = VirtualGift("gift_dragon", "Dragon", 3000L, "🐉", "premium")
    val totalUsd = dragonGift.stars * 0.01 // $30.00 USD
    val creatorShare = (totalUsd * 0.70 * 100.0).toLong() / 100.0 // $21.00 USD (70%)
    val adminShare = (totalUsd * 0.30 * 100.0).toLong() / 100.0   // $9.00 USD (30%)

    assertEquals(30.0, totalUsd, 0.001)
    assertEquals(21.0, creatorShare, 0.001)
    assertEquals(9.0, adminShare, 0.001)
    assertEquals(totalUsd, creatorShare + adminShare, 0.001)
  }

  @Test
  fun testTigerGiftSplit() {
    val tiger = VirtualGift("gift_tiger", "Tiger", 1000L, "🐯", "medium")
    val totalUsd = tiger.stars * 0.01 // $10.00 USD
    val creatorShare = (totalUsd * 0.70 * 100.0).toLong() / 100.0 // $7.00 USD
    val adminShare = (totalUsd * 0.30 * 100.0).toLong() / 100.0   // $3.00 USD

    assertEquals(10.0, totalUsd, 0.001)
    assertEquals(7.0, creatorShare, 0.001)
    assertEquals(3.0, adminShare, 0.001)
  }
}
