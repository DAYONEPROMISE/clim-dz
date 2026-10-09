package com.climdz.app

/** Room heat-load inputs. Surface in m2, height in meters. */
enum class Exposure { SHADED, AVERAGE, SUNNY }
enum class Insulation { POOR, AVERAGE, GOOD }

/**
 * BTU sizing rule (documented in README):
 * base = surface * 340 BTU/m2, scaled by height/2.6, then
 * exposure x0.90 / x1.00 / x1.12, insulation x1.15 / x1.00 / x0.90,
 * +600 BTU per occupant beyond 2, +4000 kitchen, +10% top floor.
 */
object SizingEngine {
    val STANDARD_SIZES = listOf(9000, 12000, 18000, 24000, 30000, 36000)

    fun computeBtu(
        surfaceM2: Double,
        heightM: Double,
        exposure: Exposure,
        insulation: Insulation,
        occupants: Int,
        isKitchen: Boolean,
        topFloor: Boolean
    ): Int {
        val surf = surfaceM2.coerceIn(1.0, 1000.0)
        val h = heightM.coerceIn(2.0, 8.0)
        var btu = surf * 340.0 * (h / 2.6)
        btu *= when (exposure) {
            Exposure.SHADED -> 0.90
            Exposure.AVERAGE -> 1.00
            Exposure.SUNNY -> 1.12
        }
        btu *= when (insulation) {
            Insulation.POOR -> 1.15
            Insulation.AVERAGE -> 1.00
            Insulation.GOOD -> 0.90
        }
        val extra = (occupants - 2).coerceAtLeast(0)
        btu += extra * 600.0
        if (isKitchen) btu += 4000.0
        if (topFloor) btu *= 1.10
        return (kotlin.math.round(btu / 10.0) * 10).toInt()
    }

    fun recommendedSize(btuNeeded: Int): Int =
        STANDARD_SIZES.firstOrNull { it >= btuNeeded } ?: STANDARD_SIZES.last()

    fun kilowatts(btu: Int): Double = btu / 3412.0
}
