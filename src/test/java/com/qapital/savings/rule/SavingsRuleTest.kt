package com.qapital.savings.rule

import com.qapital.amountOf
import com.qapital.bankdata.transaction.Transaction
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDate
import java.util.Map

@SpringBootTest
internal class SavingsRuleTest {

    private var nextTransactionId: Long = 1;

    @Test
    fun guiltyPleasure() {
        val savingRule = GuiltyPleasureRule(1, 2.00, listOf(1), "Starbucks")

        assertThat(
            savingRule.applyToTransaction(
                createTransaction(3.55, "Starbucks"))
        ).isEmpty()

        assertThat(
            savingRule.applyToTransaction(
                createTransaction(-3.55, "Starbucks"))
        ).containsExactly(
            Map.entry(1, amountOf(2))
        )

        savingRule.savingsGoalIds.addAll(listOf(2, 3))

        assertThat(
            savingRule.applyToTransaction(
                createTransaction(-3.55, "Starbucks"))
        ).containsExactly(
            Map.entry(1, amountOf(0.67)),
            Map.entry(2, amountOf(0.67)),
            Map.entry(3, amountOf(0.66)),
        )

        savingRule.savingsGoalIds.addAll(listOf(1, 1, 2, 2))

        assertThat(
            savingRule.applyToTransaction(
                createTransaction(-111.111, "Starbucks"))
        ).containsExactly(
            Map.entry(1, amountOf(0.67)),
            Map.entry(2, amountOf(0.67)),
            Map.entry(3, amountOf(0.66)),
        )
    }

    @Test
    fun roundUp() {
        val savingRule = RoundupRule(1, 2.00, listOf(1))

        assertThat(
            savingRule.applyToTransaction(
                createTransaction(3.55, "Starbucks"))
        ).isEmpty()

        assertThat(
            savingRule.applyToTransaction(
                createTransaction(-3.55, "Starbucks"))
        ).containsExactly(
            Map.entry(1, amountOf(0.45))
        )

        assertThat(
            savingRule.applyToTransaction(
                createTransaction(-2.005, "Starbucks"))
        ).containsExactly(
            Map.entry(1, amountOf(2.00))
        )

        assertThat(
            savingRule.applyToTransaction(
                createTransaction(-2.55, "Starbucks"))
        ).containsExactly(
            Map.entry(1, amountOf(1.45))
        )

        savingRule.savingsGoalIds.addAll(listOf(2, 3))

        assertThat(
            savingRule.applyToTransaction(
                createTransaction(-2.55, "Starbucks"))
        ).containsExactly(
            Map.entry(1, amountOf(0.48)),
            Map.entry(2, amountOf(0.48)),
            Map.entry(3, amountOf(0.49)),
        )
    }

    private fun createTransaction(amount: Double, description: String) =
        Transaction(nextTransactionId++, 1, amount, description, LocalDate.now())

}