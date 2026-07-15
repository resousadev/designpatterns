package dio.designpatterns;

import dio.designpatterns.chain.app.BaseHandler;
import dio.designpatterns.chain.app.impl.CeoBudgetHandler;
import dio.designpatterns.chain.app.impl.DirectorBudgetHandler;
import dio.designpatterns.chain.app.impl.ManagerBudgetHandler;
import dio.designpatterns.chain.app.impl.SellerBudgetHandler;
import dio.designpatterns.chain.domain.CustomerBudget;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;

@SpringBootApplication
public class DesignpatternsApplication {

  public static void main(String[] args) {
    SpringApplication.run(DesignpatternsApplication.class, args);

    CustomerBudget customerBudget = new CustomerBudget(new BigDecimal("1500"));
    BaseHandler sellerHandler = new SellerBudgetHandler();
//    sellerHandler.setNextHandler(new ManagerBudgetHandler());
    sellerHandler.setNextHandler(new DirectorBudgetHandler());
//    sellerHandler.setNextHandler(new CeoBudgetHandler());
    sellerHandler.handle(customerBudget);
  }

}
