package common

// inclusive, last allowed
data class Limits(
    val min: Int? = null,
    val max: Int? = null
)

// This can be used if you need data around a coordinate
data class Coordinate(
    val x: Int,
    val y: Int
) {
    override fun equals(other: Any?): Boolean {
        return if (other is Coordinate) {
            other.x == this.x && other.y == this.y
        } else {
            false
        }
    }

    fun getHorizontalCoordinates(limits: Limits? = null): List<Coordinate> {
        return buildList {
            if (limits?.min == null || x - 1 >= limits.min) {
                add(Coordinate(x - 1, y))
            }
            if (limits?.max == null || x + 1 <= limits.max) {
                add(Coordinate(x + 1, y))
            }
        }
    }

    fun getVerticalCoordinates(limits: Limits? = null): List<Coordinate> {
        return buildList {
            if (limits?.min == null || y - 1 >= limits.min) {
                add(Coordinate(x, y - 1))
            }
            if (limits?.max == null || y + 1 <= limits.max) {
                add(Coordinate(x, y + 1))
            }
        }
    }

    fun getNonDiagonalCoordinates(xLimits: Limits? = null, yLimits: Limits? = null): List<Coordinate> {
        return getHorizontalCoordinates(xLimits).plus(getVerticalCoordinates(yLimits))
    }

    fun getDiagonalCoordinates(xLimits: Limits? = null, yLimits: Limits? = null): List<Coordinate> {
        return buildList {
            if (xLimits?.min == null || x - 1 >= xLimits.min) {
                if (yLimits?.min == null || y - 1 >= yLimits.min) {
                    add(Coordinate(x - 1, y - 1))
                }
                if (yLimits?.max == null || y + 1 <= yLimits.max) {
                    add(Coordinate(x - 1, y + 1))
                }
            }
            if (xLimits?.max == null || x + 1 <= xLimits.max) {
                if (yLimits?.min == null || y - 1 >= yLimits.min) {
                    add(Coordinate(x + 1, y - 1))
                }
                if (yLimits?.max == null || y + 1 <= yLimits.max) {
                    add(Coordinate(x + 1, y + 1))
                }
            }
        }
    }

    fun getAllSurroundingCoordinates(xLimits: Limits? = null, yLimits: Limits? = null): List<Coordinate> {
        return getDiagonalCoordinates(xLimits, yLimits).plus(getNonDiagonalCoordinates(xLimits, yLimits))
    }
}

// This set is for the cases where we need to go from one point infinitely in any direction (no repeating of coordinates)
// Can also be achived with the above + limits, but this implementation should be easier at 5am xd
data class CoordinateChange(val x: Int, val y: Int)

fun getHorizontalCoordinateChanges(): List<CoordinateChange> {
    return listOf(CoordinateChange(1, 0), CoordinateChange(-1, 0))
}

fun getVerticalCoordinateChanges(): List<CoordinateChange> {
    return listOf(CoordinateChange(0, 1), CoordinateChange(0, -1))
}

fun getDiagonalCoordinateChanges(): List<CoordinateChange> {
    return listOf(CoordinateChange(1, 1), CoordinateChange(-1, 1), CoordinateChange(1, -1), CoordinateChange(-1, -1))
}
