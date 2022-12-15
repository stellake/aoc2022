package common

private fun Int?.valueIsBetween(min: Int, max: Int): Boolean {
    return this != null && this >= min && this <= max
}
