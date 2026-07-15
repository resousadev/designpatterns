package dio.designpatterns.chain.app;

import dio.designpatterns.chain.domain.CustomerBudget;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Testes da classe BaseHandler")
class BaseHandlerTest {

  private ConcreteHandler handler1;
  private ConcreteHandler handler2;
  private ConcreteHandler handler3;

  @BeforeEach
  void setUp() {
    handler1 = new ConcreteHandler("Handler1");
    handler2 = new ConcreteHandler("Handler2");
    handler3 = new ConcreteHandler("Handler3");
  }

  @Test
  @DisplayName("Deve encadear handlers corretamente")
  void testSetNextHandler() {
    handler1.setNextHandler(handler2).setNextHandler(handler3);

    assertNotNull(handler1.getNextHandler());
    assertEquals(handler2, handler1.getNextHandler());
    assertEquals(handler3, handler2.getNextHandler());
    assertNull(handler3.getNextHandler());
  }

  @Test
  @DisplayName("Deve retornar o próximo handler ao encadear")
  void testSetNextHandlerReturnValue() {
    BaseHandler result = handler1.setNextHandler(handler2);

    assertEquals(handler2, result);
  }

  @Test
  @DisplayName("Deve permitir encadear múltiplos handlers em uma linha")
  void testFluentChaining() {
    assertDoesNotThrow(() -> {
      handler1.setNextHandler(handler2).setNextHandler(handler3);
    });
  }

  @Test
  @DisplayName("Deve processar orçamento na cadeia de responsabilidade")
  void testHandleInChain() {
    handler1.setNextHandler(handler2).setNextHandler(handler3);
    
    CustomerBudget budget = new CustomerBudget(BigDecimal.valueOf(5000));
    CustomerBudget result = handler1.handle(budget);

    assertNotNull(result);
    assertEquals(budget, result);
  }

  // Classe concreta para testes
  private static class ConcreteHandler extends BaseHandler {
    private final String name;

    ConcreteHandler(String name) {
      this.name = name;
    }

    @Override
    public CustomerBudget handle(CustomerBudget customerBudget) {
      if (nextHandler != null) {
        return nextHandler.handle(customerBudget);
      }
      return customerBudget;
    }

    public BaseHandler getNextHandler() {
      return nextHandler;
    }
  }
}
