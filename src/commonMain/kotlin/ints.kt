@OptIn(ExperimentalUnsignedTypes::class)
fun String.toBigIntViaInts(): BigInt {

    // returns carry
    fun IntArray.multiplyAndAdd(index: Int, multiplier: Long, addition: Long): Long {
        val sum = multiplier * (this[index].toLong() and 0xffffffffL) + addition
        this[index] = sum.toInt()
        return sum ushr 32
    }

    fun IntArray.multiplyAndAdd(chunk: Int) {
        var carry = 0L

        var i = size - 1
        while (i >= 0) carry = multiplyAndAdd(i--, CHUNK_MULTIPLIER_LONG, carry)

        check(carry == 0L) { "carry.1=$carry" }

        carry = multiplyAndAdd(size - 1, 1L, chunk.toLong())

        i = size - 2
        while (i >= 0) carry = multiplyAndAdd(i--, 1L, carry)

        check(carry == 0L) { "carry.2=$carry" }
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

    val magnitude = IntArray(numberOfDigits / CHUNK_SIZE + 1)
    magnitude[magnitude.size - 1] = substring(stringIndex, stringIndex + firstChunkSize).toInt()
    stringIndex += firstChunkSize
    while (stringIndex != length) {
        magnitude.multiplyAndAdd(substring(stringIndex, stringIndex + CHUNK_SIZE).toInt())
        stringIndex += CHUNK_SIZE
    }
    val result = magnitude.removeLeadingZeros()
    if (result.isEmpty()) return BigInt.ZERO
    return BigInt(sign, result.asUIntArray())
}

private fun IntArray.removeLeadingZeros(): IntArray {
    return when (val index = indexOfFirst { it != 0 }) {
        -1 -> EmptyIntArray
        0 -> this
        else -> copyOfRange(index, size)
    }
}

private val EmptyIntArray: IntArray = intArrayOf()
