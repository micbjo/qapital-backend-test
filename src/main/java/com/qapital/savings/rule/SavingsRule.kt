package com.qapital.savings.rule

import com.qapital.*
import com.qapital.bankdata.transaction.Transaction

typealias SavingsGoalId = Id

/**
 * The domain object for a Savings Rule.
 */
sealed class SavingsRule(
    val id: Id?,
    val userId: Id,
    amount: Amount,
    savingsGoalIds: List<SavingsGoalId>,
    val status: Status
) {
    val amount: Amount = amount.abs().enforceScale()
    val savingsGoalIds = savingsGoalIds.toMutableSet();
    val savingsGoalIdList
        get() = savingsGoalIds.toList()
    abstract val ruleType: RuleType

    protected abstract fun calculateSavingsAmount(transaction: Transaction): Amount

    fun applyToTransaction(transaction: Transaction): Map<SavingsGoalId, Amount> {
        if (transaction.amount.compareTo(0.0) < 0) {
            val totalSavingsAmount = calculateSavingsAmount(transaction)
            if (totalSavingsAmount.isPositive())
                if (savingsGoalIds.size == 1)
                    return mapOf(savingsGoalIds.first() to totalSavingsAmount)
                else if (savingsGoalIds.size > 1)
                    return splitSavingsAcrossGoals(totalSavingsAmount)
        }
        return mapOf();
    }

    private fun splitSavingsAcrossGoals(totalSavingsAmount: Amount): Map<SavingsGoalId, Amount> {
        val dividedSavingsAmount = totalSavingsAmount / savingsGoalIds.size.toBigDecimal()
        val lastSavingsAmount = totalSavingsAmount - (dividedSavingsAmount * (savingsGoalIds.size - 1).toBigDecimal())

        return savingsGoalIdList.run { subList(0, lastIndex) }
            .associateWith { dividedSavingsAmount }
            .toMutableMap().apply {
                put(savingsGoalIds.last(), lastSavingsAmount)
            }
    }

    enum class Status {
        active, deleted, paused
    }

    enum class RuleType {
        guiltypleasure, roundup
    }
}

class RoundupRule(
    userId: Id,
    amount: Amount,
    savingsGoalIds: List<Id>,
    id: Id? = null,
    status: Status = Status.active
) : SavingsRule(id, userId, amount, savingsGoalIds, status) {

    constructor(
        userId: Id,
        amount: Number,
        savingsGoalIds: List<Id>,
        id: Id? = null,
        status: Status = Status.active
    ) : this(userId, amountOf(amount), savingsGoalIds, id, status)

    override val ruleType = RuleType.roundup

    override fun calculateSavingsAmount(transaction: Transaction): Amount =
        amount - amountOf(transaction.amount).abs().remainder(amount)
}

class GuiltyPleasureRule(
    userId: Id,
    amount: Amount,
    savingsGoalIds: List<Id>,
    val placeDescription: String,
    id: Id? = null,
    status: Status = Status.active
) : SavingsRule(id, userId, amount, savingsGoalIds, status) {

    constructor(
        userId: Id,
        amount: Number,
        savingsGoalIds: List<Id>,
        placeDescription: String,
        id: Id? = null,
        status: Status = Status.active
    ) : this(userId, amountOf(amount), savingsGoalIds, placeDescription, id, status)

    override val ruleType = RuleType.guiltypleasure

    override fun calculateSavingsAmount(transaction: Transaction): Amount =
        if (transaction.description == placeDescription) amount else Amount.ZERO
}
