package dio.designpatterns.chain.app.impl;

import dio.designpatterns.chain.app.BaseHandler;
import dio.designpatterns.chain.domain.CustomerBudget;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
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
      log.info("Budget approved by Seller: {}", customerBudget.getTotalValue());
      return customerBudget;
    } else if (nextHandler != null) {
      return nextHandler.handle(customerBudget);
    } else {
      log.warn("Budget rejected by all handlers: {}", customerBudget.getTotalValue());
      return customerBudget;
    }
  }
}
