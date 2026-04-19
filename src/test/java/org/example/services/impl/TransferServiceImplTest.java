package org.example.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import org.example.config.database.DatabaseSetup;
import org.example.domain.enums.AccountType;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.Transfer;
import org.example.repositories.AccountRepository;
import org.example.repositories.AccountRepositoryH2Impl;
import org.example.repositories.CategoryRepository;
import org.example.repositories.CategoryRepositoryH2Impl;
import org.example.repositories.CreditCardRepository;
import org.example.repositories.CreditCardRepositoryH2Impl;
import org.example.repositories.TransactionRepository;
import org.example.repositories.TransactionRepositoryH2Impl;
import org.example.repositories.TransferRepository;
import org.example.repositories.TransferRepositoryH2Impl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransferServiceImplTest {

  private TransferServiceImpl transferService;
  private TransferRepository transferRepository;

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
    DatabaseSetup.clearDB(); // Limpa o banco e recria a categoria de transferência

    // 1. Inicializa todos os repositórios
    accountRepository = new AccountRepositoryH2Impl();
    categoryRepository = new CategoryRepositoryH2Impl();
    creditCardRepository = new CreditCardRepositoryH2Impl(accountRepository);
    transactionRepository = new TransactionRepositoryH2Impl(
      accountRepository,
      creditCardRepository,
      categoryRepository
    );
    transferRepository = new TransferRepositoryH2Impl(transactionRepository);

    // 2. Inicializa todos os serviços injetando as dependências
    accountService = new AccountServiceImpl(accountRepository);
    transactionService = new TransactionServiceImpl(
      transactionRepository,
      accountRepository,
      creditCardRepository,
      categoryRepository,
      accountService
    );
    transferService = new TransferServiceImpl(
      transferRepository,
      transactionService
    );
  }

  // ==========================================
  // MÉTODOS AUXILIARES
  // ==========================================

  private AbstractAccount criarContaComSaldo(
    String nome,
    BigDecimal saldoInicial
  ) {
    accountService.create(nome, AccountType.BANCO);
    List<AbstractAccount> contas = accountRepository.findAll();
    AbstractAccount contaSalva = contas.get(contas.size() - 1); // Pega a última criada

    if (saldoInicial.compareTo(BigDecimal.ZERO) > 0) {
      accountService.deposit(contaSalva.getId(), saldoInicial);
      // Atualiza o objeto com o novo saldo
      contaSalva = accountRepository.findById(contaSalva.getId()).get();
    }
    return contaSalva;
  }

  // ==========================================
  // TESTES DO MÉTODO: create()
  // ==========================================

  @Test
  void shouldCreateTransferAndMoveBalancesSuccessfully() {
    // 1. Arrange: Cria conta de Origem (com 500) e conta de Destino (com 0)
    AbstractAccount contaOrigem = criarContaComSaldo(
      "Conta Corrente",
      new BigDecimal("500.00")
    );
    AbstractAccount contaDestino = criarContaComSaldo(
      "Poupança",
      BigDecimal.ZERO
    );

    Date dataHoje = new Date(System.currentTimeMillis());
    BigDecimal valorTransferencia = new BigDecimal("150.00");

    // 2. Act: Executa a transferência
    transferService.create(
      valorTransferencia,
      dataHoje,
      contaOrigem,
      contaDestino
    );

    // 3. Assert: Verifica a transferência no banco
    List<Transfer> transferencias = transferRepository.findAll();
    assertEquals(
      1,
      transferencias.size(),
      "Deve existir 1 transferência salva no banco."
    );

    // Verifica se as transações de Entrada e Saída foram amarradas corretamente
    assertNotNull(transferencias.get(0).getInputTransaction());
    assertNotNull(transferencias.get(0).getOutputTransaction());

    // 4. A Prova de Fogo (Efeito Colateral): Verifica os saldos das contas!
    AbstractAccount origemAtualizada = accountRepository
      .findById(contaOrigem.getId())
      .get();
    AbstractAccount destinoAtualizada = accountRepository
      .findById(contaDestino.getId())
      .get();

    assertEquals(
      0,
      new BigDecimal("350.00").compareTo(origemAtualizada.getBalance()),
      "Conta origem devia ter 350."
    );
    assertEquals(
      0,
      new BigDecimal("150.00").compareTo(destinoAtualizada.getBalance()),
      "Conta destino devia ter 150."
    );
  }

  // ==========================================
  // TESTES DOS MÉTODOS: listAll() e remove()
  // ==========================================

  @Test
  void shouldListAllTransfers() {
    assertEquals(0, transferService.listAll().size());

    AbstractAccount contaA = criarContaComSaldo(
      "Conta A",
      new BigDecimal("100.00")
    );
    AbstractAccount contaB = criarContaComSaldo("Conta B", BigDecimal.ZERO);

    transferService.create(
      new BigDecimal("50.00"),
      new Date(System.currentTimeMillis()),
      contaA,
      contaB
    );

    assertEquals(1, transferService.listAll().size());
  }

  @Test
  void shouldRemoveTransferAndRevertBalancesSuccessfully() {
    // 1. Arrange
    AbstractAccount contaOrigem = criarContaComSaldo(
      "Origem",
      new BigDecimal("1000.00")
    );
    AbstractAccount contaDestino = criarContaComSaldo(
      "Destino",
      BigDecimal.ZERO
    );

    transferService.create(
      new BigDecimal("200.00"),
      new Date(System.currentTimeMillis()),
      contaOrigem,
      contaDestino
    );

    Integer transferId = transferRepository.findAll().get(0).getId();

    // 2. Act: Deleta a transferência
    transferService.remove(transferId);

    // 3. Assert: Verifica se a transferência sumiu
    assertEquals(
      0,
      transferService.listAll().size(),
      "A transferência devia ser deletada."
    );

    // 4. Assert: Verifica se o dinheiro foi estornado para as contas originais
    AbstractAccount origemEstornada = accountRepository
      .findById(contaOrigem.getId())
      .get();
    AbstractAccount destinoEstornada = accountRepository
      .findById(contaDestino.getId())
      .get();

    assertEquals(
      0,
      new BigDecimal("1000.00").compareTo(origemEstornada.getBalance()),
      "O dinheiro não voltou para a conta de origem!"
    );
    assertEquals(
      0,
      BigDecimal.ZERO.compareTo(destinoEstornada.getBalance()),
      "O dinheiro não foi retirado da conta de destino!"
    );
  }

  @Test
  void shouldThrowExceptionWhenRemovingInvalidTransfer() {
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      transferService.remove(999);
    });
    assertEquals(
      "Transferencia não encontrada para o ID informado.",
      exception.getMessage()
    );
  }
}
