package dio.designpatterns.chain.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da classe CustomerBudget")
class CustomerBudgetTest {

  private CustomerBudget customerBudget;

  @BeforeEach
  void setUp() {
    customerBudget = new CustomerBudget(BigDecimal.valueOf(10000));
  }

  @Test
  @DisplayName("Deve criar um orçamento com valor inicial")
  void testConstructorWithValue() {
    BigDecimal expectedValue = BigDecimal.valueOf(50000);
    CustomerBudget budget = new CustomerBudget(expectedValue);

    assertEquals(expectedValue, budget.getTotalValue());
  }

  @Test
  @DisplayName("Deve iniciar com approved como false")
  void testInitialApprovedStatus() {
    assertFalse(customerBudget.isApproved());
  }

  @Test
  @DisplayName("Deve alterar o status de aprovação")
  void testSetApproved() {
    customerBudget.setApproved(true);

    assertTrue(customerBudget.isApproved());
  }

  @Test
  @DisplayName("Deve permitir alternância de status de aprovação")
  void testToggleApprovedStatus() {
    assertFalse(customerBudget.isApproved());
    
    customerBudget.setApproved(true);
    assertTrue(customerBudget.isApproved());
    
    customerBudget.setApproved(false);
    assertFalse(customerBudget.isApproved());
  }

  @Test
  @DisplayName("Deve manter o valor total após alterações")
  void testTotalValueImmutable() {
    BigDecimal originalValue = customerBudget.getTotalValue();
    customerBudget.setApproved(true);

    assertEquals(originalValue, customerBudget.getTotalValue());
  }

  @ParameterizedTest
  @DisplayName("Deve criar orçamentos com valores diferentes")
  @ValueSource(longs = { 1000, 10000, 100000, 1000000 })
  void testCustomerBudgetWithDifferentValues(long value) {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(value));

    assertEquals(BigDecimal.valueOf(value), budget.getTotalValue());
    assertFalse(budget.isApproved());
  }

  @Test
  @DisplayName("Deve aceitar zero como valor válido")
  void testZeroValue() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.ZERO);

    assertEquals(BigDecimal.ZERO, budget.getTotalValue());
  }

  @Test
  @DisplayName("Deve aceitar valores negativos (para teste de negócios)")
  void testNegativeValue() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(-5000));

    assertEquals(BigDecimal.valueOf(-5000), budget.getTotalValue());
  }
}
