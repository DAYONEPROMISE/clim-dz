package com.climdz.app

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SizingEngineTest {

    @Test
    fun standard20m2() {
        // 20 * 340 * (2.8/2.6) = ~7323
        val btu = SizingEngine.computeBtu(20.0, 2.8, Exposure.AVERAGE, Insulation.AVERAGE, 2, false, false)
        assertTrue("expected ~7323, got $btu", btu in 7200..7450)
        assertEquals(9000, SizingEngine.recommendedSize(btu))
    }

    @Test
    fun sunnyPoorTopFloorIncreases() {
        val base = SizingEngine.computeBtu(20.0, 2.8, Exposure.AVERAGE, Insulation.AVERAGE, 2, false, false)
        val harsh = SizingEngine.computeBtu(20.0, 2.8, Exposure.SUNNY, Insulation.POOR, 2, false, true)
        assertTrue("harsh=$harsh base=$base", harsh > base * 1.3)
    }

    @Test
    fun shadedGoodDecreases() {
        val base = SizingEngine.computeBtu(20.0, 2.8, Exposure.AVERAGE, Insulation.AVERAGE, 2, false, false)
        val easy = SizingEngine.computeBtu(20.0, 2.8, Exposure.SHADED, Insulation.GOOD, 2, false, false)
        assertTrue("easy=$easy base=$base", easy < base)
    }

    @Test
    fun kitchenAdds4000() {
        val a = SizingEngine.computeBtu(15.0, 2.6, Exposure.AVERAGE, Insulation.AVERAGE, 2, false, false)
        val b = SizingEngine.computeBtu(15.0, 2.6, Exposure.AVERAGE, Insulation.AVERAGE, 2, true, false)
        assertEquals(4000, b - a)
    }

    @Test
    fun extraOccupantAdds600() {
        val a = SizingEngine.computeBtu(15.0, 2.6, Exposure.AVERAGE, Insulation.AVERAGE, 2, false, false)
        val b = SizingEngine.computeBtu(15.0, 2.6, Exposure.AVERAGE, Insulation.AVERAGE, 4, false, false)
        assertEquals(1200, b - a)
    }

    @Test
    fun thresholds() {
        assertEquals(9000, SizingEngine.recommendedSize(8500))
        assertEquals(12000, SizingEngine.recommendedSize(9001))
        assertEquals(18000, SizingEngine.recommendedSize(15000))
        assertEquals(24000, SizingEngine.recommendedSize(24000))
        assertEquals(36000, SizingEngine.recommendedSize(99999))
    }

    @Test
    fun kilowatts() {
        assertEquals(3.52, SizingEngine.kilowatts(12000), 0.01)
    }

    @Test
    fun ceilingHeightScales() {
        val low = SizingEngine.computeBtu(20.0, 2.6, Exposure.AVERAGE, Insulation.AVERAGE, 2, false, false)
        val high = SizingEngine.computeBtu(20.0, 3.9, Exposure.AVERAGE, Insulation.AVERAGE, 2, false, false)
        assertEquals(1.5, high.toDouble() / low.toDouble(), 0.01)
    }
}
