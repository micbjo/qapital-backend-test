package com.qapital.savings.rule;

import com.qapital.bankdata.transaction.Transaction;
import com.qapital.bankdata.transaction.TransactionsService;
import com.qapital.savings.event.SavingsEvent;
import com.qapital.savings.event.SavingsEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.qapital.savings.event.SavingsEvent.EventName.rule_application;

@Service
public class StandardSavingsRulesService implements SavingsRulesService {

  private final TransactionsService transactionsService;
  private final SavingsEventService savingsEventService;

  @Autowired
  public StandardSavingsRulesService(TransactionsService transactionsService, SavingsEventService savingsEventService) {
    this.transactionsService = transactionsService;
    this.savingsEventService = savingsEventService;
  }

  @Override
  public List<SavingsRule> activeRulesForUser(Long userId) {

    var guiltyPleasureRule = new GuiltyPleasureRule(
      userId, BigDecimal.valueOf(3.00d), List.of(1L, 2L), "Starbucks", 1L, SavingsRule.Status.active
    );
    var roundupRule = new RoundupRule(
      userId, BigDecimal.valueOf(2.00d), List.of(1L), 2L, SavingsRule.Status.active
    );

    return List.of(guiltyPleasureRule, roundupRule);
  }

  @Override
  public List<SavingsEvent> executeRule(SavingsRule savingsRule) {

    List<Transaction> latestTransactionsForUser = transactionsService.latestTransactionsForUser(savingsRule.getUserId());

    return latestTransactionsForUser
      .stream()
      .filter(transaction -> transaction
        .getAmount()
        .compareTo(0.0d) < 0)
      .flatMap(transaction -> applyRuleToTransaction(savingsRule, transaction)
        .stream())
      .collect(Collectors.toList());
  }

  private List<SavingsEvent> applyRuleToTransaction(SavingsRule savingsRule, Transaction transaction) {

    return savingsRule.applyToTransaction(transaction).entrySet().stream()
      .map(entry -> savingsEventService.createSavingsEvent(
        savingsRule, entry.getKey(), transaction.getId(), rule_application, entry.getValue()))
      .collect(Collectors.toList());
  }

}
