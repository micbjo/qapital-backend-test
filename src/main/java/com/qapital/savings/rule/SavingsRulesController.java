package com.qapital.savings.rule;

import com.qapital.savings.event.SavingsEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/savings/rule")
public class SavingsRulesController {

  private final SavingsRulesService savingsRulesService;

  @Autowired
  public SavingsRulesController(SavingsRulesService savingsRulesService) {
    this.savingsRulesService = savingsRulesService;
  }

  @GetMapping("/active/{userId}")
  public List<SavingsRuleJson> activeRulesForUser(@PathVariable Long userId) {
    return savingsRulesService.activeRulesForUser(userId)
      .stream()
      .map(SavingsRulesController::savingsRuleToJson)
      .collect(Collectors.toList());
  }

  @PostMapping("/executions")
  public List<SavingsEvent> savingsEventsForSavingsRule(@RequestBody SavingsRuleJson savingsRule) {
    return savingsRulesService.executeRule(jsonToSavingsRule(savingsRule));
  }

  static private SavingsRule jsonToSavingsRule(SavingsRuleJson json) {

    checkNotNull(json.getUserId(), "User id");
    checkNotNull(json.getAmount(), "Amount");
    checkNotNull(json.getSavingsGoalIds(), "Savings goals");
    checkNotNull(json.getStatus(), "Status");

    switch (json.getRuleType()) {
      case guiltypleasure:
        return jsonToGuiltyPleasureRule(json);
      case roundup:
        return jsonToRoundUpRule(json);
      default:
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown rule type");
    }
  }

  static private GuiltyPleasureRule jsonToGuiltyPleasureRule(SavingsRuleJson json) {
    return new GuiltyPleasureRule(
      checkNotNull(json.getUserId(), "User id"),
      json.getAmount(),
      json.getSavingsGoalIds(),
      normalizeRequiredString(json.getPlaceDescription(), "Place description"),
      json.getId(),
      json.getStatus()
    );
  }

  static private RoundupRule jsonToRoundUpRule(SavingsRuleJson json) {
    return new RoundupRule(
      checkNotNull(json.getUserId(), "User id"),
      json.getAmount(),
      json.getSavingsGoalIds(),
      json.getId(),
      json.getStatus()
    );
  }

  static private SavingsRuleJson savingsRuleToJson(SavingsRule savingsRule) {
    return new SavingsRuleJson(savingsRule);
  }

  static private <T> T checkNotNull(T value, String propertyName) {
    if (value == null)
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, propertyName + " is required");
    return value;
  }

  static private String normalizeRequiredString(String value, String propertyName) {
    if (value != null) {
      var result = value.trim();
      if (!result.isEmpty())
        return result;
    }
    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, propertyName + " is required");
  }

}
