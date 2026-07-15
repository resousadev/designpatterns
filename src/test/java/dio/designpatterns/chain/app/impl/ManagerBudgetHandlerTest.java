package dio.designpatterns.chain.app.impl;

import dio.designpatterns.chain.app.BaseHandler;
import dio.designpatterns.chain.domain.CustomerBudget;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da classe ManagerBudgetHandler")
class ManagerBudgetHandlerTest {

  private ManagerBudgetHandler managerHandler;
  private MockNextHandler nextHandler;

  @BeforeEach
  void setUp() {
    managerHandler = new ManagerBudgetHandler();
    nextHandler = new MockNextHandler();
  }

  @Test
  @DisplayName("Deve aprovar orçamento menor que 5000")
  void testApproveSmallBudget() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(4999));

    CustomerBudget result = managerHandler.handle(budget);

    assertTrue(result.isApproved());
    assertEquals(budget, result);
  }

  @Test
  @DisplayName("Deve rejeitar orçamento igual ou maior que 5000 e passar para próximo handler")
  void testRejectLargeBudgetAndPassToNext() {
    managerHandler.setNextHandler(nextHandler);
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(5500));

    CustomerBudget result = managerHandler.handle(budget);

    assertTrue(nextHandler.wasCalled());
    assertEquals(budget, result);
  }

  @Test
  @DisplayName("Deve retornar orçamento rejeitado se não houver próximo handler")
  void testRejectBudgetWithoutNextHandler() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(10000));

    CustomerBudget result = managerHandler.handle(budget);

    assertFalse(result.isApproved());
    assertEquals(budget, result);
  }

  @ParameterizedTest
  @DisplayName("Deve aprovar orçamentos menores que 5000")
  @ValueSource(longs = { 1, 1000, 2000, 3000, 4000, 4999 })
  void testApproveMultipleSmallBudgets(long value) {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(value));

    CustomerBudget result = managerHandler.handle(budget);

    assertTrue(result.isApproved(), "Deve aprovar orçamento de " + value);
  }

  @ParameterizedTest
  @DisplayName("Deve rejeitar orçamentos >= 5000 sem próximo handler")
  @ValueSource(longs = { 5000, 5001, 10000, 50000, 100000 })
  void testRejectMultipleLargeBudgets(long value) {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(value));

    CustomerBudget result = managerHandler.handle(budget);

    assertFalse(result.isApproved(), "Deve rejeitar orçamento de " + value);
  }

  @ParameterizedTest
  @DisplayName("Deve passar para próximo handler quando orçamento >= 5000")
  @CsvSource({
    "5000, true",
    "10000, true",
    "100000, true"
  })
  void testPassToNextHandlerForLargeBudgets(long value, boolean shouldCall) {
    managerHandler.setNextHandler(nextHandler);
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(value));

    managerHandler.handle(budget);

    assertEquals(shouldCall, nextHandler.wasCalled());
  }

  @Test
  @DisplayName("Deve manter o objeto CustomerBudget aprovado")
  void testApprovedBudgetReturnsSameObject() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(2000));

    CustomerBudget result = managerHandler.handle(budget);

    assertSame(budget, result, "Deve retornar o mesmo objeto do orçamento");
  }

  @Test
  @DisplayName("Deve retornar o próximo handler ao encadear")
  void testSetNextHandlerReturnValue() {
    BaseHandler result = managerHandler.setNextHandler(nextHandler);

    assertEquals(nextHandler, result);
  }

  @Test
  @DisplayName("Deve encadear múltiplos handlers fluentemente")
  void testFluentChainingMultipleHandlers() {
    MockNextHandler handler2 = new MockNextHandler();
    MockNextHandler handler3 = new MockNextHandler();

    assertDoesNotThrow(() -> {
      managerHandler.setNextHandler(handler2).setNextHandler(handler3);
    });
  }

  @Test
  @DisplayName("Deve processar corretamente com decimal points")
  void testBudgetWithDecimalPoints() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(4999.99));

    CustomerBudget result = managerHandler.handle(budget);

    assertTrue(result.isApproved());
  }

  @Test
  @DisplayName("Deve rejeitar orçamento com decimal acima de 5000")
  void testBudgetWithDecimalAboveThreshold() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(5000.01));

    CustomerBudget result = managerHandler.handle(budget);

    assertFalse(result.isApproved());
  }

  @Test
  @DisplayName("Deve aprovar orçamento zero")
  void testZeroBudget() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.ZERO);

    CustomerBudget result = managerHandler.handle(budget);

    assertTrue(result.isApproved());
  }

  @Test
  @DisplayName("Deve aceitar exatamente 4999 (limite inferior)")
  void testExactlyAtLowerBoundary() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(4999));

    CustomerBudget result = managerHandler.handle(budget);

    assertTrue(result.isApproved(), "Deve aprovar valores menores que 5000");
  }

  @Test
  @DisplayName("Deve rejeitar exatamente 5000 (limite superior)")
  void testExactlyAtUpperBoundary() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(5000));

    CustomerBudget result = managerHandler.handle(budget);

    assertFalse(result.isApproved(), "Deve rejeitar valores >= 5000");
  }

  @Test
  @DisplayName("Deve validar o threshold correto diferente do Seller (5000 vs 1000)")
  void testThresholdDifferenceFromSeller() {
    // Manager aprova até 5000, Seller aprova até 1000
    CustomerBudget budgetFor2000 = new CustomerBudget(BigDecimal.valueOf(2000));

    CustomerBudget result = managerHandler.handle(budgetFor2000);

    assertTrue(result.isApproved(), "Manager deve aprovar 2000 (Seller não aprovaria)");
  }

  @Test
  @DisplayName("Deve retornar o mesmo objeto quando rejeitado e sem próximo handler")
  void testReturnSameObjectWhenRejected() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(50000));

    CustomerBudget result = managerHandler.handle(budget);

    assertSame(budget, result, "Deve retornar o mesmo objeto mesmo quando rejeitado");
  }

  // Mock handler para testes
  private static class MockNextHandler extends BaseHandler {
    private boolean called = false;

    @Override
    public CustomerBudget handle(CustomerBudget customerBudget) {
      called = true;
      return customerBudget;
    }

    public boolean wasCalled() {
      return called;
    }
  }
}
