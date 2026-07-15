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

@DisplayName("Testes da classe SellerBudgetHandler")
class SellerBudgetHandlerTest {

  private SellerBudgetHandler sellerHandler;
  private MockNextHandler nextHandler;

  @BeforeEach
  void setUp() {
    sellerHandler = new SellerBudgetHandler();
    nextHandler = new MockNextHandler();
  }

  @Test
  @DisplayName("Deve aprovar orçamento menor que 1000")
  void testApproveSmallBudget() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(999));

    CustomerBudget result = sellerHandler.handle(budget);

    assertTrue(result.isApproved());
    assertEquals(budget, result);
  }

  @Test
  @DisplayName("Deve rejeitar orçamento igual ou maior que 1000 e passar para próximo handler")
  void testRejectLargeBudgetAndPassToNext() {
    sellerHandler.setNextHandler(nextHandler);
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(1500));

    CustomerBudget result = sellerHandler.handle(budget);

    assertTrue(nextHandler.wasCalled());
    assertEquals(budget, result);
  }

  @Test
  @DisplayName("Deve retornar orçamento rejeitado se não houver próximo handler")
  void testRejectBudgetWithoutNextHandler() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(2000));

    CustomerBudget result = sellerHandler.handle(budget);

    assertFalse(result.isApproved());
    assertEquals(budget, result);
  }

  @ParameterizedTest
  @DisplayName("Deve aprovar orçamentos menores que 1000")
  @ValueSource(longs = { 1, 100, 500, 750, 999 })
  void testApproveMultipleSmallBudgets(long value) {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(value));

    CustomerBudget result = sellerHandler.handle(budget);

    assertTrue(result.isApproved(), "Deve aprovar orçamento de " + value);
  }

  @ParameterizedTest
  @DisplayName("Deve rejeitar orçamentos >= 1000 sem próximo handler")
  @ValueSource(longs = { 1000, 1001, 5000, 10000, 100000 })
  void testRejectMultipleLargeBudgets(long value) {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(value));

    CustomerBudget result = sellerHandler.handle(budget);

    assertFalse(result.isApproved(), "Deve rejeitar orçamento de " + value);
  }

  @ParameterizedTest
  @DisplayName("Deve passar para próximo handler quando orçamento >= 1000")
  @CsvSource({
    "1000, true",
    "5000, true",
    "100000, true"
  })
  void testPassToNextHandlerForLargeBudgets(long value, boolean shouldCall) {
    sellerHandler.setNextHandler(nextHandler);
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(value));

    sellerHandler.handle(budget);

    assertEquals(shouldCall, nextHandler.wasCalled());
  }

  @Test
  @DisplayName("Deve manter o objeto CustomerBudget aprovado")
  void testApprovedBudgetReturnsSameObject() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(500));

    CustomerBudget result = sellerHandler.handle(budget);

    assertSame(budget, result, "Deve retornar o mesmo objeto do orçamento");
  }

  @Test
  @DisplayName("Deve retornar o próximo handler ao encadear")
  void testSetNextHandlerReturnValue() {
    BaseHandler result = sellerHandler.setNextHandler(nextHandler);

    assertEquals(nextHandler, result);
  }

  @Test
  @DisplayName("Deve encadear múltiplos handlers fluentemente")
  void testFluentChainingMultipleHandlers() {
    MockNextHandler handler2 = new MockNextHandler();
    MockNextHandler handler3 = new MockNextHandler();

    assertDoesNotThrow(() -> {
      sellerHandler.setNextHandler(handler2).setNextHandler(handler3);
    });
  }

  @Test
  @DisplayName("Deve processar corretamente com decimal points")
  void testBudgetWithDecimalPoints() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(999.99));

    CustomerBudget result = sellerHandler.handle(budget);

    assertTrue(result.isApproved());
  }

  @Test
  @DisplayName("Deve rejeitar orçamento com decimal acima de 1000")
  void testBudgetWithDecimalAboveThreshold() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(1000.01));

    CustomerBudget result = sellerHandler.handle(budget);

    assertFalse(result.isApproved());
  }

  @Test
  @DisplayName("Deve aprovar orçamento zero")
  void testZeroBudget() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.ZERO);

    CustomerBudget result = sellerHandler.handle(budget);

    assertTrue(result.isApproved());
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
