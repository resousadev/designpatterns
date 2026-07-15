package dio.designpatterns.chain.app.impl;

import dio.designpatterns.chain.app.BaseHandler;
import dio.designpatterns.chain.domain.CustomerBudget;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da classe CeoBudgetHandler")
class CeoBudgetHandlerTest {

  private CeoBudgetHandler ceoHandler;
  private MockNextHandler nextHandler;

  @BeforeEach
  void setUp() {
    ceoHandler = new CeoBudgetHandler();
    nextHandler = new MockNextHandler();
  }

  @Test
  @DisplayName("Deve aprovar orçamento pequeno sem limite")
  void testApproveSmallBudget() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(999));

    CustomerBudget result = ceoHandler.handle(budget);

    assertTrue(result.isApproved());
    assertEquals(budget, result);
  }

  @Test
  @DisplayName("Deve aprovar orçamento grande")
  void testApproveLargeBudget() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(100000));

    CustomerBudget result = ceoHandler.handle(budget);

    assertTrue(result.isApproved());
    assertEquals(budget, result);
  }

  @Test
  @DisplayName("Deve aprovar orçamento extremamente grande")
  void testApproveExtremelyLargeBudget() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(999999999));

    CustomerBudget result = ceoHandler.handle(budget);

    assertTrue(result.isApproved());
  }

  @ParameterizedTest
  @DisplayName("Deve aprovar orçamentos de qualquer tamanho")
  @ValueSource(longs = { 1, 100, 1000, 5000, 10000, 50000, 100000, 1000000 })
  void testApproveMultipleBudgets(long value) {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(value));

    CustomerBudget result = ceoHandler.handle(budget);

    assertTrue(result.isApproved(), "Deve aprovar orçamento de " + value);
  }

  @Test
  @DisplayName("Deve ser o último nível - aprova o que Director rejeita")
  void testApproveWhatDirectorRejects() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(50000));

    CustomerBudget result = ceoHandler.handle(budget);

    assertTrue(result.isApproved(), "CEO deve aprovar 50000 (Director rejeitaria)");
  }

  @Test
  @DisplayName("Deve retornar o mesmo objeto do orçamento")
  void testReturnSameObject() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(999999));

    CustomerBudget result = ceoHandler.handle(budget);

    assertSame(budget, result, "Deve retornar o mesmo objeto");
  }

  @Test
  @DisplayName("Deve processar corretamente com decimal points")
  void testBudgetWithDecimalPoints() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(99999.99));

    CustomerBudget result = ceoHandler.handle(budget);

    assertTrue(result.isApproved());
  }

  @Test
  @DisplayName("Deve aprovar orçamento zero")
  void testZeroBudget() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.ZERO);

    CustomerBudget result = ceoHandler.handle(budget);

    assertTrue(result.isApproved());
  }

  @Test
  @DisplayName("Deve aprovar orçamento negativo (caso extremo)")
  void testNegativeBudget() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(-1000));

    CustomerBudget result = ceoHandler.handle(budget);

    assertTrue(result.isApproved());
  }

  @Test
  @DisplayName("Não deve chamar próximo handler (é o topo da cadeia)")
  void testDoesNotCallNextHandler() {
    ceoHandler.setNextHandler(nextHandler);
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(999999));

    ceoHandler.handle(budget);

    assertFalse(nextHandler.wasCalled(), "CEO não deve chamar próximo handler");
  }

  @Test
  @DisplayName("Deve retornar o próximo handler ao encadear")
  void testSetNextHandlerReturnValue() {
    BaseHandler result = ceoHandler.setNextHandler(nextHandler);

    assertEquals(nextHandler, result);
  }

  @Test
  @DisplayName("Deve encadear múltiplos handlers (mesmo que não os chame)")
  void testFluentChainingMultipleHandlers() {
    MockNextHandler handler2 = new MockNextHandler();
    MockNextHandler handler3 = new MockNextHandler();

    assertDoesNotThrow(() -> {
      ceoHandler.setNextHandler(handler2).setNextHandler(handler3);
    });
  }

  @Test
  @DisplayName("Deve ser o topo da hierarquia - aprova tudo")
  void testIsTopOfHierarchy() {
    long[] testValues = { 
        1, 999, 1000, 5000, 10000, 50000, 100000, 1000000, 999999999 
    };

    for (long value : testValues) {
      CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(value));
      CustomerBudget result = ceoHandler.handle(budget);
      
      assertTrue(result.isApproved(), 
          "CEO deve aprovar qualquer valor: " + value);
    }
  }

  @Test
  @DisplayName("Deve aprovar múltiplas vezes sem alteração de estado")
  void testMultipleApprovals() {
    CustomerBudget budget1 = new CustomerBudget(BigDecimal.valueOf(100));
    CustomerBudget budget2 = new CustomerBudget(BigDecimal.valueOf(1000000));

    CustomerBudget result1 = ceoHandler.handle(budget1);
    CustomerBudget result2 = ceoHandler.handle(budget2);

    assertTrue(result1.isApproved());
    assertTrue(result2.isApproved());
  }

  @Test
  @DisplayName("Deve alterar o status de aprovação corretamente")
  void testChangeApprovalStatus() {
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(50000));
    assertFalse(budget.isApproved(), "Deve iniciar como não aprovado");

    CustomerBudget result = ceoHandler.handle(budget);

    assertTrue(result.isApproved(), "Deve estar aprovado após handle");
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
