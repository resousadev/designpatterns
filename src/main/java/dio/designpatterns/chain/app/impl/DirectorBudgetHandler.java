package dio.designpatterns.chain.app.impl;

import dio.designpatterns.chain.app.BaseHandler;
import dio.designpatterns.chain.domain.CustomerBudget;

public class DirectorBudgetHandler extends BaseHandler {

  @Override
  public CustomerBudget handle(CustomerBudget customerBudget) {
    if (customerBudget.getTotalValue().compareTo(new java.math.BigDecimal("10000")) < 0) {
      customerBudget.setApproved(true);
      return customerBudget;
    } else if (nextHandler != null) {
      return nextHandler.handle(customerBudget);
    } else {
      return customerBudget;
    }
  }
}
