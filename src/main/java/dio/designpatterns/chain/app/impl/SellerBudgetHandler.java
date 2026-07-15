package dio.designpatterns.chain.app.impl;

import dio.designpatterns.chain.app.BaseHandler;
import dio.designpatterns.chain.domain.CustomerBudget;

import java.math.BigDecimal;

public class SellerBudgetHandler extends BaseHandler {

  private BaseHandler nextHandler;

  @Override
  public BaseHandler setNextHandler(BaseHandler nextHandler) {
    this.nextHandler = nextHandler;
    return nextHandler;
  }

  @Override
  public CustomerBudget handle(CustomerBudget customerBudget) {
    if (customerBudget.getTotalValue().compareTo(new BigDecimal("1000")) < 0) {
      customerBudget.setApproved(true);
      return customerBudget;
    } else if (nextHandler != null) {
      return nextHandler.handle(customerBudget);
    } else {
      return customerBudget;
    }
  }
}
