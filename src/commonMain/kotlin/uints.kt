@OptIn(ExperimentalUnsignedTypes::class)
fun String.toBigIntViaUInts(): BigInt {

    // returns carry
    fun UIntArray.multiplyAndAdd(index: Int, multiplier: ULong, addition: ULong): ULong {
        val sum = multiplier * this[index] + addition
        this[index] = sum.toUInt()
        return sum shr 32
    }

    fun UIntArray.multiplyAndAdd(chunk: UInt) {
        var carry = 0UL

        var i = size - 1
        while (i >= 0) carry = multiplyAndAdd(i--, CHUNK_MULTIPLIER_ULONG, carry)

        check(carry == 0UL) { "carry.1=$carry" }

        carry = multiplyAndAdd(size - 1, 1UL, chunk.toULong())

        i = size - 2
        while (i >= 0) carry = multiplyAndAdd(i--, 1UL, carry)

        check(carry == 0UL) { "carry.2=$carry" }
    }

    require(isNotEmpty()) { "String is empty" }

    val indexOfMinus = lastIndexOf('-')
    val indexOfPlus = lastIndexOf('+')

    var stringIndex: Int
    val sign: Int
    when {
        indexOfMinus >= 0 -> {
            check(indexOfMinus == 0 && indexOfPlus < 0) { "embedded sign" }
            stringIndex = 1
            sign = -1
        }

        indexOfPlus >= 0 -> {
            check(indexOfPlus == 0) { "embedded sign" }
            stringIndex = 1
            sign = 1
        }

        else -> {
            stringIndex = 0
            sign = 1
        }
    }

    require(stringIndex != length) { "String contains only `sign`" }

    // remove leading zeros
    while (this[stringIndex] == '0' && stringIndex < length - 1) stringIndex += 1

    if (stringIndex == length) return BigInt.ZERO

    val numberOfDigits = length - stringIndex
    val firstChunkSize = when (val value = numberOfDigits % CHUNK_SIZE) {
        0 -> CHUNK_SIZE
        else -> value
    }

    val magnitude = UIntArray(numberOfDigits / CHUNK_SIZE + 1)
    magnitude[magnitude.size - 1] = substring(stringIndex, stringIndex + firstChunkSize).toUInt()
    stringIndex += firstChunkSize
    while (stringIndex != length) {
        magnitude.multiplyAndAdd(substring(stringIndex, stringIndex + CHUNK_SIZE).toUInt())
        stringIndex += CHUNK_SIZE
    }
    val result = magnitude.removeLeadingZeros()
    if (result.isEmpty()) return BigInt.ZERO
    return BigInt(sign, result)
}

@OptIn(ExperimentalUnsignedTypes::class)
private fun UIntArray.removeLeadingZeros(): UIntArray {
    return when (val index = indexOfFirst { it != 0U }) {
        -1 -> EmptyUIntArray
        0 -> this
        else -> copyOfRange(index, size)
    }
}

@OptIn(ExperimentalUnsignedTypes::class)
private val EmptyUIntArray: UIntArray = uintArrayOf()
