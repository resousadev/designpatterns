package dio.designpatterns.chain.app.impl;

import dio.designpatterns.chain.app.BaseHandler;
import dio.designpatterns.chain.domain.CustomerBudget;

public class CeoBudgetHandler extends BaseHandler {

  @Override
  public CustomerBudget handle(CustomerBudget customerBudget) {
    customerBudget.setApproved(true);
    return customerBudget;
  }
}
