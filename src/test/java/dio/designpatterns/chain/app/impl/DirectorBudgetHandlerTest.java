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

@DisplayName("Testes da classe DirectorBudgetHandler")
class DirectorBudgetHandlerTest {

  private DirectorBudgetHandler directorHandler;
  private MockNextHandler nextHandler;

  @BeforeEach
  void setUp() {
    directorHandler = new DirectorBudgetHandler();
    nextHandler = new MockNextHandler();
  }

  @Test
  @DisplayName("Deve aprovar orçamento menor que 10000")
  void testApproveSmallBudget() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(9999));

    CustomerBudget result = directorHandler.handle(budget);

    assertTrue(result.isApproved());
    assertEquals(budget, result);
  }

  @Test
  @DisplayName("Deve rejeitar orçamento igual ou maior que 10000 e passar para próximo handler")
  void testRejectLargeBudgetAndPassToNext() {
    directorHandler.setNextHandler(nextHandler);
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(15000));

    CustomerBudget result = directorHandler.handle(budget);

    assertTrue(nextHandler.wasCalled());
    assertEquals(budget, result);
  }

  @Test
  @DisplayName("Deve retornar orçamento rejeitado se não houver próximo handler")
  void testRejectBudgetWithoutNextHandler() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(50000));

    CustomerBudget result = directorHandler.handle(budget);

    assertFalse(result.isApproved());
    assertEquals(budget, result);
  }

  @ParameterizedTest
  @DisplayName("Deve aprovar orçamentos menores que 10000")
  @ValueSource(longs = { 1, 1000, 5000, 8000, 9000, 9999 })
  void testApproveMultipleSmallBudgets(long value) {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(value));

    CustomerBudget result = directorHandler.handle(budget);

    assertTrue(result.isApproved(), "Deve aprovar orçamento de " + value);
  }

  @ParameterizedTest
  @DisplayName("Deve rejeitar orçamentos >= 10000 sem próximo handler")
  @ValueSource(longs = { 10000, 10001, 50000, 100000, 1000000 })
  void testRejectMultipleLargeBudgets(long value) {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(value));

    CustomerBudget result = directorHandler.handle(budget);

    assertFalse(result.isApproved(), "Deve rejeitar orçamento de " + value);
  }

  @ParameterizedTest
  @DisplayName("Deve passar para próximo handler quando orçamento >= 10000")
  @CsvSource({
    "10000, true",
    "50000, true",
    "1000000, true"
  })
  void testPassToNextHandlerForLargeBudgets(long value, boolean shouldCall) {
    directorHandler.setNextHandler(nextHandler);
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(value));

    directorHandler.handle(budget);

    assertEquals(shouldCall, nextHandler.wasCalled());
  }

  @Test
  @DisplayName("Deve manter o objeto CustomerBudget aprovado")
  void testApprovedBudgetReturnsSameObject() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(5000));

    CustomerBudget result = directorHandler.handle(budget);

    assertSame(budget, result, "Deve retornar o mesmo objeto do orçamento");
  }

  @Test
  @DisplayName("Deve retornar o próximo handler ao encadear")
  void testSetNextHandlerReturnValue() {
    BaseHandler result = directorHandler.setNextHandler(nextHandler);

    assertEquals(nextHandler, result);
  }

  @Test
  @DisplayName("Deve encadear múltiplos handlers fluentemente")
  void testFluentChainingMultipleHandlers() {
    MockNextHandler handler2 = new MockNextHandler();
    MockNextHandler handler3 = new MockNextHandler();

    assertDoesNotThrow(() -> {
      directorHandler.setNextHandler(handler2).setNextHandler(handler3);
    });
  }

  @Test
  @DisplayName("Deve processar corretamente com decimal points")
  void testBudgetWithDecimalPoints() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(9999.99));

    CustomerBudget result = directorHandler.handle(budget);

    assertTrue(result.isApproved());
  }

  @Test
  @DisplayName("Deve rejeitar orçamento com decimal acima de 10000")
  void testBudgetWithDecimalAboveThreshold() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(10000.01));

    CustomerBudget result = directorHandler.handle(budget);

    assertFalse(result.isApproved());
  }

  @Test
  @DisplayName("Deve aprovar orçamento zero")
  void testZeroBudget() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.ZERO);

    CustomerBudget result = directorHandler.handle(budget);

    assertTrue(result.isApproved());
  }

  @Test
  @DisplayName("Deve aceitar exatamente 9999 (limite inferior)")
  void testExactlyAtLowerBoundary() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(9999));

    CustomerBudget result = directorHandler.handle(budget);

    assertTrue(result.isApproved(), "Deve aprovar valores menores que 10000");
  }

  @Test
  @DisplayName("Deve rejeitar exatamente 10000 (limite superior)")
  void testExactlyAtUpperBoundary() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(10000));

    CustomerBudget result = directorHandler.handle(budget);

    assertFalse(result.isApproved(), "Deve rejeitar valores >= 10000");
  }

  @Test
  @DisplayName("Deve validar o threshold correto diferente do Manager (10000 vs 5000)")
  void testThresholdDifferenceFromManager() {
    // Director aprova até 10000, Manager aprova até 5000
    CustomerBudget budgetFor7000 = new CustomerBudget(BigDecimal.valueOf(7000));

    CustomerBudget result = directorHandler.handle(budgetFor7000);

    assertTrue(result.isApproved(), "Director deve aprovar 7000 (Manager não aprovaria)");
  }

  @Test
  @DisplayName("Deve retornar o mesmo objeto quando rejeitado e sem próximo handler")
  void testReturnSameObjectWhenRejected() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(500000));

    CustomerBudget result = directorHandler.handle(budget);

    assertSame(budget, result, "Deve retornar o mesmo objeto mesmo quando rejeitado");
  }

  @Test
  @DisplayName("Deve ser o nível mais alto (aprova maiores valores que Manager)")
  void testHighestLevelThreshold() {
    CustomerBudget budget8000 = new CustomerBudget(BigDecimal.valueOf(8000));
    CustomerBudget budget9999 = new CustomerBudget(BigDecimal.valueOf(9999));

    assertTrue(directorHandler.handle(budget8000).isApproved());
    assertTrue(directorHandler.handle(budget9999).isApproved());
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
