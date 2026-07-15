package dio.designpatterns.chain.app.impl;

import dio.designpatterns.chain.app.BaseHandler;
import dio.designpatterns.chain.domain.CustomerBudget;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CeoBudgetHandler extends BaseHandler {

  @Override
  public CustomerBudget handle(CustomerBudget customerBudget) {
    customerBudget.setApproved(true);
    log.info("Budget approved by CEO: {}", customerBudget.getTotalValue());
    return customerBudget;
  }
}
