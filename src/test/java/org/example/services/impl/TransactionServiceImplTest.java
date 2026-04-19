package org.example.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.example.config.database.DatabaseSetup;
import org.example.domain.enums.AccountType;
import org.example.domain.enums.CategoryType;
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.AbstractTransaction;
import org.example.domain.models.AccountBank;
import org.example.domain.models.Category;
import org.example.domain.models.CreditCard;
import org.example.repositories.AccountRepository;
import org.example.repositories.AccountRepositoryH2Impl;
import org.example.repositories.CategoryRepository;
import org.example.repositories.CategoryRepositoryH2Impl;
import org.example.repositories.CreditCardRepository;
import org.example.repositories.CreditCardRepositoryH2Impl;
import org.example.repositories.TransactionRepository;
import org.example.repositories.TransactionRepositoryH2Impl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionServiceImplTest {

  private TransactionServiceImpl transactionService;
  private TransactionRepository transactionRepository;

  private AccountServiceImpl accountService;
  private AccountRepository accountRepository;
  private CategoryRepository categoryRepository;
  private CreditCardRepository creditCardRepository;

  @BeforeEach
  void setUp() {
    DatabaseSetup.dropDB();
    DatabaseSetup.initialize();
    DatabaseSetup.clearDB(); // Limpa o banco e insere a Categoria 1 (Transferência)

    // Inicializando todos os repositórios necessários
    accountRepository = new AccountRepositoryH2Impl();
    categoryRepository = new CategoryRepositoryH2Impl();
    creditCardRepository = new CreditCardRepositoryH2Impl(accountRepository);
    transactionRepository = new TransactionRepositoryH2Impl(
      accountRepository,
      creditCardRepository,
      categoryRepository
    );

    // Inicializando os serviços
    accountService = new AccountServiceImpl(accountRepository);
    transactionService = new TransactionServiceImpl(
      transactionRepository,
      accountRepository,
      creditCardRepository,
      categoryRepository,
      accountService
    );
  }

  // ==========================================
  // MÉTODOS AUXILIARES (Para preparar o cenário)
  // ==========================================

  private Integer criarContaComSaldo(String nome, BigDecimal saldoInicial) {
    accountService.create(nome, AccountType.BANCO);
    Integer accountId = accountRepository.findAll().get(0).getId();
    if (saldoInicial.compareTo(BigDecimal.ZERO) > 0) {
      accountService.deposit(accountId, saldoInicial);
    }
    return accountId;
  }

  private Integer criarCategoriaDespesa() {
    Category cat = new Category("Supermercado", CategoryType.DESPESA);
    categoryRepository.save(cat);
    return cat.getId();
  }

  private Integer criarCartaoCredito(Integer accountId) {
    AccountBank bank = (AccountBank) accountRepository
      .findById(accountId)
      .get();
    CreditCard card = new CreditCard(
      "Cartão Visa",
      new BigDecimal("5000.00"),
      BigDecimal.ZERO,
      15,
      20,
      bank
    );
    creditCardRepository.save(card);
    return card.getId();
  }

  // ==========================================
  // TESTES DO MÉTODO: create()
  // ==========================================

  @Test
  void shouldCreatePendingTransactionWithoutChangingBalance() {
    Integer accountId = criarContaComSaldo(
      "Conta Principal",
      new BigDecimal("1000.00")
    );
    Integer categoryId = criarCategoriaDespesa();

    // Cria uma SAÍDA pendente de 200
    transactionService.create(
      new BigDecimal("200.00"),
      "Compra Pendente",
      new Date(),
      TransactionStatus.PENDENTE,
      categoryId,
      TransactionType.SAIDA,
      accountId
    );

    List<AbstractTransaction> transacoes = transactionRepository.findAll();
    assertEquals(1, transacoes.size());
    assertEquals(TransactionStatus.PENDENTE, transacoes.get(0).getStatus());

    // O saldo DEVE continuar 1000, pois está pendente
    AbstractAccount conta = accountRepository.findById(accountId).get();
    assertEquals(0, new BigDecimal("1000.00").compareTo(conta.getBalance()));
  }

  @Test
  void shouldCreateEffectiveOutputTransactionAndReduceBalance() {
    Integer accountId = criarContaComSaldo(
      "Conta Principal",
      new BigDecimal("1000.00")
    );
    Integer categoryId = criarCategoriaDespesa();

    // Cria uma SAÍDA efetivada de 300
    transactionService.create(
      new BigDecimal("300.00"),
      "Compra Efetivada",
      new Date(),
      TransactionStatus.EFETIVADA,
      categoryId,
      TransactionType.SAIDA,
      accountId
    );

    // O saldo DEVE cair para 700
    AbstractAccount conta = accountRepository.findById(accountId).get();
    assertEquals(
      0,
      new BigDecimal("700.00").compareTo(conta.getBalance()),
      "O saldo devia ter sido reduzido pela despesa efetivada."
    );
  }

  // ==========================================
  // TESTES DO MÉTODO: createCreditCardTransaction()
  // ==========================================

  @Test
  void shouldCreateCreditCardTransactionSuccessfully() {
    Integer accountId = criarContaComSaldo("Conta Principal", BigDecimal.ZERO);
    Integer categoryId = criarCategoriaDespesa();
    Integer cardId = criarCartaoCredito(accountId);

    transactionService.createCreditCardTransaction(
      new BigDecimal("550.00"),
      "TV Nova",
      new Date(),
      categoryId,
      accountId,
      cardId
    );

    List<AbstractTransaction> transacoes = transactionRepository.findAll();
    assertEquals(1, transacoes.size());
    assertEquals("TV Nova", transacoes.get(0).getDescription());
    assertEquals(TransactionType.SAIDA, transacoes.get(0).getType());
    assertEquals(TransactionStatus.PENDENTE, transacoes.get(0).getStatus());
  }

  // ==========================================
  // TESTES DO MÉTODO: changeStatus() e Efeitos Colaterais
  // ==========================================

  @Test
  void shouldApplyBalanceWhenChangingFromPendingToEffective() {
    Integer accountId = criarContaComSaldo("Conta", new BigDecimal("500.00"));
    Integer categoryId = criarCategoriaDespesa();

    // 1. Nasce pendente (Saldo continua 500)
    AbstractTransaction t = transactionService.create(
      new BigDecimal("100.00"),
      "Teste",
      new Date(),
      TransactionStatus.PENDENTE,
      categoryId,
      TransactionType.SAIDA,
      accountId
    );

    // 2. Muda para efetivada
    transactionService.changeStatus(t.getId(), TransactionStatus.EFETIVADA);

    // 3. Verifica se subtraiu os 100
    AbstractAccount contaAtualizada = accountRepository
      .findById(accountId)
      .get();
    assertEquals(
      0,
      new BigDecimal("400.00").compareTo(contaAtualizada.getBalance())
    );
  }

  @Test
  void shouldRevertBalanceWhenChangingFromEffectiveToPending() {
    Integer accountId = criarContaComSaldo("Conta", new BigDecimal("500.00"));
    Integer categoryId = criarCategoriaDespesa();

    // 1. Nasce efetivada de Entrada de 200 (Saldo vai para 700)
    AbstractTransaction t = transactionService.create(
      new BigDecimal("200.00"),
      "Salário",
      new Date(),
      TransactionStatus.EFETIVADA,
      categoryId,
      TransactionType.ENTRADA,
      accountId
    );

    // 2. Cancela/Volta para pendente
    transactionService.changeStatus(t.getId(), TransactionStatus.PENDENTE);

    // 3. Verifica se devolveu os 200 (Saldo tem que voltar para 500)
    AbstractAccount contaAtualizada = accountRepository
      .findById(accountId)
      .get();
    assertEquals(
      0,
      new BigDecimal("500.00").compareTo(contaAtualizada.getBalance()),
      "O saldo devia ter retornado ao valor original."
    );
  }

  // ==========================================
  // TESTES DO MÉTODO: changeValue()
  // ==========================================

  @Test
  void shouldAdjustBalanceCorrectlyWhenChangingValueOfEffectiveTransaction() {
    Integer accountId = criarContaComSaldo("Conta", new BigDecimal("1000.00"));
    Integer categoryId = criarCategoriaDespesa();

    // Nasce efetivada (-200). Saldo fica 800.
    AbstractTransaction t = transactionService.create(
      new BigDecimal("200.00"),
      "Conta de Luz",
      new Date(),
      TransactionStatus.EFETIVADA,
      categoryId,
      TransactionType.SAIDA,
      accountId
    );

    // Ops, a conta era de 250! (O seu código vai estornar 200 e tirar 250). Saldo deve ir para 750.
    transactionService.changeValue(t.getId(), new BigDecimal("250.00"));

    AbstractAccount contaAtualizada = accountRepository
      .findById(accountId)
      .get();
    assertEquals(
      0,
      new BigDecimal("750.00").compareTo(contaAtualizada.getBalance()),
      "A matemática do changeValue está errada."
    );
  }

  // ==========================================
  // TESTES DO MÉTODO: remove()
  // ==========================================

  @Test
  void shouldRevertBalanceBeforeRemovingEffectiveTransaction() {
    Integer accountId = criarContaComSaldo("Conta", new BigDecimal("1000.00"));
    Integer categoryId = criarCategoriaDespesa();

    // Gasta 300 efetivado. Saldo fica 700.
    AbstractTransaction t = transactionService.create(
      new BigDecimal("300.00"),
      "Gasto Errado",
      new Date(),
      TransactionStatus.EFETIVADA,
      categoryId,
      TransactionType.SAIDA,
      accountId
    );

    // Remove a transação do banco
    transactionService.remove(t.getId());

    // Verifica se o dinheiro voltou pra conta (Deve ser 1000 de novo)
    AbstractAccount contaAtualizada = accountRepository
      .findById(accountId)
      .get();
    assertEquals(
      0,
      new BigDecimal("1000.00").compareTo(contaAtualizada.getBalance()),
      "O remove não estornou o dinheiro antes de apagar!"
    );

    // Verifica se apagou do banco
    assertTrue(
      transactionRepository.findById(t.getId()).isEmpty(),
      "A transação não foi apagada."
    );
  }
}
