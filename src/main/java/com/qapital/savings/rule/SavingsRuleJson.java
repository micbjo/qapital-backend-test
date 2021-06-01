package com.qapital.savings.rule;


import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/**
 * JSON representation of a Savings Rule.
 */
public class SavingsRuleJson {

  private Long id;
  private Long userId;
  private String placeDescription;
  private Double amount;
  private List<Long> savingsGoalIds;
  private SavingsRule.RuleType ruleType;
  private SavingsRule.Status status;

  public SavingsRuleJson() {
  }

  public SavingsRuleJson(SavingsRule savingsRule) {
    id = savingsRule.getId();
    userId = savingsRule.getUserId();
    amount = savingsRule.getAmount().doubleValue();
    savingsGoalIds = savingsRule.getSavingsGoalIdList();
    ruleType = savingsRule.getRuleType();
    status = savingsRule.getStatus();
    placeDescription = savingsRule instanceof GuiltyPleasureRule ?
      ((GuiltyPleasureRule) savingsRule).getPlaceDescription() :
      null;
  }

  public void addSavingsGoal(Long savingsGoalId) {
    if (!savingsGoalIds.contains(savingsGoalId)) {
      savingsGoalIds.add(savingsGoalId);
    }
  }

  public void removeSavingsGoal(Long savingsGoalId) {
    savingsGoalIds.remove(savingsGoalId);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public String getPlaceDescription() {
    return placeDescription;
  }

  public void setPlaceDescription(String placeDescription) {
    this.placeDescription = placeDescription;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }

  public List<Long> getSavingsGoalIds() {
    return savingsGoalIds;
  }

  public void setSavingsGoalIds(List<Long> savingsGoalIds) {
    this.savingsGoalIds = savingsGoalIds;
  }

  public SavingsRule.RuleType getRuleType() {
    return ruleType;
  }

  public void setRuleType(SavingsRule.RuleType ruleType) {
    this.ruleType = ruleType;
  }

  public SavingsRule.Status getStatus() {
    return status;
  }

  public void setStatus(SavingsRule.Status status) {
    this.status = status;
  }

  @JsonIgnore
  public boolean isActive() {
    return SavingsRule.Status.active.equals(getStatus());
  }

}
