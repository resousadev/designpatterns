package dio.designpatterns.chain.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CustomerBudget {
  private BigDecimal totalValue;
  public boolean approved = false;

  public CustomerBudget(BigDecimal totalValue) {
    this.totalValue = totalValue;
  }

}
