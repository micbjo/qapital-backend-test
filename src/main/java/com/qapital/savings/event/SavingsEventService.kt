package com.qapital.savings.event

import com.qapital.savings.event.SavingsEvent.EventName
import com.qapital.savings.rule.SavingsRule
import java.math.BigDecimal

interface SavingsEventService {
    fun createSavingsEvent(
        savingsRule: SavingsRule,
        savingsGoalId: Long,
        triggerId: Long,
        eventName: EventName,
        amount: BigDecimal
    ): SavingsEvent
}