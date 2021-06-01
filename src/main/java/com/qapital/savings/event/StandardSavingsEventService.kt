package com.qapital.savings.event

import com.qapital.Amount
import com.qapital.savings.event.SavingsEvent.EventName
import com.qapital.savings.rule.SavingsRule
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDate

@Service
class StandardSavingsEventService : SavingsEventService {

    override fun createSavingsEvent(
        savingsRule: SavingsRule,
        savingsGoalId: Long,
        triggerId: Long,
        eventName: EventName,
        amount: Amount
    ): SavingsEvent {

        if (!savingsRule.savingsGoalIds.contains(savingsGoalId))
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Non matching Savings goal id")

        return SavingsEvent(
            savingsRule,
            savingsGoalId,
            triggerId,
            eventName,
            LocalDate.now(),
            amount.toDouble()
        )
    }

}