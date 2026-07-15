package dio.designpatterns.chain.app;

import dio.designpatterns.chain.domain.CustomerBudget;

public abstract class BaseHandler {

  protected BaseHandler nextHandler;

  public BaseHandler setNextHandler(BaseHandler nextHandler) {
    this.nextHandler = nextHandler;
    return nextHandler;
  }

  public abstract CustomerBudget handle(CustomerBudget customerBudget);

}
