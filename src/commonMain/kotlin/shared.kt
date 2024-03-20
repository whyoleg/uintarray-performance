@OptIn(ExperimentalUnsignedTypes::class)
class BigInt(
    val sign: Int,
    val magnitude: UIntArray,
) {
    companion object {
        val ZERO: BigInt = BigInt(0, uintArrayOf(0U))
    }
}

// Int.MAX_VALUE is 10 digits, so 9 can fit full number
const val CHUNK_SIZE: Int = 9

// 9 zeros
const val CHUNK_MULTIPLIER_LONG: Long = 1_000_000_000L
const val CHUNK_MULTIPLIER_ULONG: ULong = 1_000_000_000UL
