package com.terricom.mytype

import com.terricom.mytype.data.Foodie
import com.terricom.mytype.data.Goal
import com.terricom.mytype.tools.toDemicalPoint
import com.terricom.mytype.tools.toFloatFormat
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun ifReachGoal_isCorrect(){

        // 食記和目標都是空的
        assertEquals(false, ifReachGoal(
            mutableListOf(
                Foodie(

                )
            ), mutableListOf(Goal(

            ))
        ))

        // 一篇食記，沒有目標
        assertEquals(false, ifReachGoal(
            mutableListOf(
                Foodie(
                    Date(),
                    "",
                    listOf(),
                    5.0f,
                    4.0f,
                    3.0f,
                    2.0f,
                    1.0f,
                    2.0f,
                    listOf(),
                    ""
                )
            ), mutableListOf(Goal(

            ))
        ))

        // 目標大於食記
        assertEquals(false, ifReachGoal(
            mutableListOf(
                Foodie(
                    Date(),
                    "",
                    listOf(),
                    5.0f,
                    4.0f,
                    3.0f,
                    2.0f,
                    1.0f,
                    2.0f,
                    listOf(),
                    ""
                )
            ), mutableListOf(Goal(
                Date(),
                Date(),
                6.0f,
                3.0f,
                0.0f,
                1.0f,
                3.0f,
                5.0f,
                null,
                null,
                30.0f,
                "",
                ""

            ))
        ))



    }

    fun ifReachGoal(items: MutableList<Foodie>, itemsGoal: MutableList<Goal>): Boolean {

        var totalWater = 0f
        var totalOil = 0f
        var totalVegetable = 0f
        var totalProtein = 0f
        var totalFruit = 0f
        var totalCarbon = 0f

        var goalWater = "0.0"
        var goalOil = "0.0"
        var goalVegetable = "0.0"
        var goalFruit = "0.0"
        var goalProtein = "0.0"
        var goalCarbon = "0.0"

        for (today in items){
            totalWater = totalWater.plus(today.water ?: 0f)
            totalOil = totalOil.plus(today.oil ?: 0f)
            totalVegetable = totalVegetable.plus(today.vegetable ?: 0f)
            totalProtein = totalProtein.plus(today.protein ?: 0f)
            totalFruit = totalFruit.plus(today.fruit ?: 0f)
            totalCarbon = totalCarbon.plus(today.carbon ?: 0f)
        }

        itemsGoal[0].timestamp?.let {

            itemsGoal[0].let {
                goalWater = it.water.toDemicalPoint(1)
                goalVegetable = it.vegetable.toDemicalPoint(1)
                goalFruit = it.fruit.toDemicalPoint(1)
                goalCarbon = it.carbon.toDemicalPoint(1)
                goalOil = it.oil.toDemicalPoint(1)
                goalProtein = it.protein.toDemicalPoint(1)
            }
        }

        return goalWater.toFloatFormat() > totalWater && goalOil.toFloatFormat() > totalOil
                && goalVegetable.toFloatFormat() > totalVegetable && goalProtein.toFloatFormat() > totalProtein
                && goalFruit.toFloatFormat() > totalFruit && goalCarbon.toFloatFormat() > totalCarbon

    }
}
