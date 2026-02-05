package nl.stokpop.kotlin.collections

import java.util.Date

class ImmutableCollections {

    enum class MyEnum {
        ONE, TWO, THREE
    }

    fun testMap() {
        val date1 = Date(2001, 1, 1)
        val date2 = Date(2002, 2, 2)
        val date3 = Date(2003, 3, 3)

        val immutableMap = mutableMapOf(
            setOf(MyEnum.ONE, MyEnum.TWO) to setOf(date1, date2),
            setOf(MyEnum.TWO, MyEnum.ONE) to setOf(date2, date1),
            setOf(MyEnum.THREE, MyEnum.ONE) to setOf(date3, date1)
        )

        println("Notice below that setOf and mapOf in Kotlin use memory-inefficient LinkedHashSet and LinkedHashMap implementations by default.")
        println("Set type: ${immutableMap.keys.first().javaClass}")
        println("Map type: ${immutableMap.javaClass}")
        println("Notice below that sets with same elements in different order are considered equal keys: so only two entries are actually stored in the map, not three!")
        println("Original map: $immutableMap")

        println("--- Immutable set with *mutable* elements")
        val immutableSet = setOf(date1, date2)
        val immutableList = listOf(date1, date2)
        println("Set contains date2: ${immutableSet.contains(date2)}")
        println("List contains date2: ${immutableList.contains(date2)}")
        println("Mutate date2 that is inside the immutable set and list, note this changes its hashCode!")
        println("Change day from 2 to 15 for date2")
        date2.date = 15
        println("Immutable set: $immutableSet")
        println("Immutable list: $immutableList")
        val checkDate = Date(2002, 2, 15) // create new Date object equal to mutated date2
        println("Check date equals mutated date2: ${checkDate == date2}")
        println("Set contains checkDate: ${immutableSet.contains(checkDate)}")
        println("List contains checkDate: ${immutableList.contains(checkDate)}")
        println("Notice below that the set cannot find its own mutated element anymore!! This is because its hashCode has changed due to mutation.")
        immutableSet.forEach { println("Set element: $it contained in set: ${immutableSet.contains(it)}") }
        immutableList.forEach { println("List element: $it contained in list: ${immutableList.contains(it)}") }

    }
}

fun main() {
    ImmutableCollections().testMap()
}