package nl.stokpop.kotlin.streams

class FollowTheStreams {

    fun filterOrStream() {
        val letters = listOf("abc", "def", "ghi")

        val onlyWithLetterA = letters.asSequence()
            .filter { l -> l.contains("a") }.toList()

        println("found ${onlyWithLetterA}")
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            FollowTheStreams().filterOrStream()
        }
    }

}