package org.example.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.example.config.database.DatabaseSetup;
import org.example.domain.enums.AccountType;
import org.example.domain.models.CreditCard;
import org.example.repositories.AccountRepository;
import org.example.repositories.AccountRepositoryH2Impl;
import org.example.repositories.CreditCardRepository;
import org.example.repositories.CreditCardRepositoryH2Impl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CreditCardServiceImplTest {

  private CreditCardServiceImpl creditCardService;
  private CreditCardRepository creditCardRepository;

  private AccountServiceImpl accountService;
  private AccountRepository accountRepository;

  @BeforeEach
  void setUp() {
    DatabaseSetup.dropDB();
    DatabaseSetup.initialize();
    DatabaseSetup.clearDB();

    // Precisamos dos repositórios e serviços de Conta também!
    accountRepository = new AccountRepositoryH2Impl();
    accountService = new AccountServiceImpl(accountRepository);

    creditCardRepository = new CreditCardRepositoryH2Impl(accountRepository);
    creditCardService = new CreditCardServiceImpl(
      creditCardRepository,
      accountRepository
    );
  }

  // ==========================================
  // MÉTODOS AUXILIARES PARA OS TESTES
  // ==========================================

  private Integer criarBancoPadrao() {
    accountService.create("Banco Inter", AccountType.BANCO);
    return accountRepository.findAll().get(0).getId();
  }

  private Integer criarCarteiraPadrao() {
    accountService.create("Cofre de Casa", AccountType.CARTEIRA);
    // Retorna o último ID criado
    int size = accountRepository.findAll().size();
    return accountRepository.findAll().get(size - 1).getId();
  }

  private Integer criarCartaoPadrao(Integer bankId) {
    creditCardService.create(
      "Cartão Black",
      new BigDecimal("5000.00"),
      BigDecimal.ZERO,
      15,
      20,
      bankId
    );
    return creditCardRepository.findAll().get(0).getId();
  }

  // ==========================================
  // TESTES DO MÉTODO: create()
  // ==========================================

  @Test
  void shouldCreateCreditCardSuccessfully() {
    Integer bankId = criarBancoPadrao();

    creditCardService.create(
      "Nubank Ultravioleta",
      new BigDecimal("10000.00"),
      BigDecimal.ZERO,
      5,
      12,
      bankId
    );

    List<CreditCard> cartoes = creditCardRepository.findAll();
    assertEquals(1, cartoes.size());
    assertEquals("Nubank Ultravioleta", cartoes.get(0).getName());
    assertEquals(
      0,
      new BigDecimal("10000.00").compareTo(cartoes.get(0).getLimit())
    );
    assertEquals(5, cartoes.get(0).getClosingDay());
    assertEquals(12, cartoes.get(0).getDueDate());
    assertEquals(bankId, cartoes.get(0).getBank().getId());
  }

  @Test
  void shouldThrowExceptionWhenCreatingCardWithInvalidDay() {
    Integer bankId = criarBancoPadrao();

    // Tenta criar com dia 30 (o limite é 28)
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      creditCardService.create(
        "Cartão Errado",
        new BigDecimal("1000.00"),
        BigDecimal.ZERO,
        30,
        10,
        bankId
      );
    });
    assertEquals(
      "O dia tem que ser um nomero entre 1 e 28",
      exception.getMessage()
    );
  }

  @Test
  void shouldThrowExceptionWhenCreatingCardWithNegativeLimit() {
    Integer bankId = criarBancoPadrao();

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      creditCardService.create(
        "Cartão Negativo",
        new BigDecimal("-100.00"),
        BigDecimal.ZERO,
        10,
        15,
        bankId
      );
    });
    assertEquals("O limite não pode ser negativo.", exception.getMessage());
  }

  @Test
  void shouldThrowExceptionWhenLinkingCardToWalletInsteadOfBank() {
    Integer walletId = criarCarteiraPadrao();

    // A regra de negócio diz que cartão só pode ser vinculado a BANCO
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      creditCardService.create(
        "Cartão da Carteira",
        new BigDecimal("1000.00"),
        BigDecimal.ZERO,
        10,
        15,
        walletId
      );
    });
    assertEquals("A conta informada não é um banco.", exception.getMessage());
  }

  // ==========================================
  // TESTES DOS MÉTODOS: changes... (Alterações)
  // ==========================================

  @Test
  void shouldChangeLimitSuccessfully() {
    Integer bankId = criarBancoPadrao();
    Integer cardId = criarCartaoPadrao(bankId);

    creditCardService.changeLimit(cardId, new BigDecimal("8000.00"));

    CreditCard cartaoAtualizado = creditCardRepository.findById(cardId).get();
    assertEquals(
      0,
      new BigDecimal("8000.00").compareTo(cartaoAtualizado.getLimit())
    );
  }

  @Test
  void shouldChangeDueDateSuccessfully() {
    Integer bankId = criarBancoPadrao();
    Integer cardId = criarCartaoPadrao(bankId);

    creditCardService.changeDueDate(cardId, 25);

    CreditCard cartaoAtualizado = creditCardRepository.findById(cardId).get();
    assertEquals(25, cartaoAtualizado.getDueDate());
  }

  @Test
  void shouldThrowExceptionWhenChangingToInvalidBank() {
    Integer bankId = criarBancoPadrao();
    Integer cardId = criarCartaoPadrao(bankId);
    Integer walletId = criarCarteiraPadrao(); // Cria uma carteira para tentar fazer a troca

    // Tenta transferir o cartão do banco para a carteira
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      creditCardService.changeBank(cardId, walletId);
    });
    assertEquals("A conta informada não é um banco.", exception.getMessage());
  }

  // ==========================================
  // TESTES DOS MÉTODOS: listAll() e remove()
  // ==========================================

  @Test
  void shouldListAllCreditCards() {
    assertEquals(0, creditCardService.listAll().size());

    Integer bankId = criarBancoPadrao();
    creditCardService.create(
      "Cartão 1",
      new BigDecimal("1000.00"),
      BigDecimal.ZERO,
      5,
      10,
      bankId
    );
    creditCardService.create(
      "Cartão 2",
      new BigDecimal("2000.00"),
      BigDecimal.ZERO,
      15,
      20,
      bankId
    );

    assertEquals(2, creditCardService.listAll().size());
  }

  @Test
  void shouldRemoveCreditCardSuccessfully() {
    Integer bankId = criarBancoPadrao();
    Integer cardId = criarCartaoPadrao(bankId);

    assertEquals(1, creditCardService.listAll().size());

    creditCardService.remove(cardId);

    assertEquals(0, creditCardService.listAll().size());
  }
}
