package dio.designpatterns.chain.app.impl;

import dio.designpatterns.chain.app.BaseHandler;
import dio.designpatterns.chain.domain.CustomerBudget;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ManagerBudgetHandler extends BaseHandler {

  @Override
  public CustomerBudget handle(CustomerBudget customerBudget) {
    if (customerBudget.getTotalValue().compareTo(new java.math.BigDecimal("5000")) < 0) {
      customerBudget.setApproved(true);
      log.info("Budget approved by Manager: {}", customerBudget.getTotalValue());
      return customerBudget;
    } else if (nextHandler != null) {
      return nextHandler.handle(customerBudget);
    } else {
      log.warn("Budget rejected by all handlers: {}", customerBudget.getTotalValue());
      return customerBudget;
    }
  }
}
