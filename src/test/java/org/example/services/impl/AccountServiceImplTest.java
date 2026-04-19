package org.example.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;
import java.util.List;
import org.example.config.database.DatabaseSetup;
import org.example.domain.enums.AccountType;
import org.example.domain.models.AbstractAccount;
import org.example.repositories.AccountRepository;
import org.example.repositories.AccountRepositoryH2Impl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AccountServiceImplTest {

  private AccountServiceImpl accountService;
  private AccountRepository accountRepository;

  @BeforeEach
  void setUp() {
    DatabaseSetup.initialize();
    DatabaseSetup.clearDB();

    accountRepository = new AccountRepositoryH2Impl();
    accountService = new AccountServiceImpl(accountRepository);
  }

  // ==========================================
  // MÉTODOS AUXILIARES PARA OS TESTES
  // ==========================================

  private Integer criarContaPadrao() {
    accountService.create("Conta Teste", AccountType.BANCO);
    return accountRepository.findAll().get(0).getId();
  }

  // ==========================================
  // TESTES DO MÉTODO: create()
  // ==========================================

  @Test
  void shouldCreateBankAccountSuccessfully() {
    accountService.create("Conta Nubank", AccountType.BANCO);

    List<AbstractAccount> contas = accountRepository.findAll();
    assertEquals(1, contas.size(), "Deveria ter 1 conta salva.");
    assertEquals("Conta Nubank", contas.get(0).getAccountName());
    assertEquals(AccountType.BANCO, contas.get(0).getType());
    assertEquals(
      0,
      BigDecimal.ZERO.compareTo(contas.get(0).getBalance()),
      "O saldo inicial deve ser zero."
    );
  }

  @Test
  void shouldCreateWalletAccountSuccessfully() {
    accountService.create("Minha Carteira", AccountType.CARTEIRA);

    List<AbstractAccount> contas = accountRepository.findAll();
    assertEquals(AccountType.CARTEIRA, contas.get(0).getType());
  }

  @Test
  void shouldThrowExceptionWhenCreatingAccountWithEmptyName() {
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      accountService.create("", AccountType.BANCO);
    });
    assertEquals("O nome não pode ser vazio!", exception.getMessage());
  }

  // ==========================================
  // TESTES DO MÉTODO: changeName()
  // ==========================================

  @Test
  void shouldChangeAccountNameSuccessfully() {
    Integer idConta = criarContaPadrao();
    accountService.changeName(idConta, "Nome Atualizado");
    AbstractAccount contaAtualizada = accountRepository.findById(idConta).get();
    assertEquals("Nome Atualizado", contaAtualizada.getAccountName());
  }

  @Test
  void shouldThrowExceptionWhenChangingNameToEmpty() {
    Integer idConta = criarContaPadrao();
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      accountService.changeName(idConta, "   ");
    });
    assertEquals("O nome não pode ser vazio!", exception.getMessage());
  }

  // ==========================================
  // TESTES DO MÉTODO: deposit()
  // ==========================================

  @Test
  void shouldDepositSuccessfully() {
    Integer idConta = criarContaPadrao();
    BigDecimal valorDeposito = new BigDecimal("150.50");

    accountService.deposit(idConta, valorDeposito);

    AbstractAccount conta = accountRepository.findById(idConta).get();
    assertEquals(
      0,
      valorDeposito.compareTo(conta.getBalance()),
      "O saldo deve ser igual ao valor depositado."
    );
  }

  @Test
  void shouldThrowExceptionWhenDepositingNegativeValue() {
    Integer idConta = criarContaPadrao();
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      accountService.deposit(idConta, new BigDecimal("-50.00"));
    });
    assertEquals(
      "O valor do depósito deve ser maior que zero.",
      exception.getMessage()
    );
  }

  // ==========================================
  // TESTES DO MÉTODO: withdraw()
  // ==========================================

  @Test
  void shouldWithdrawSuccessfully() {
    Integer idConta = criarContaPadrao();
    accountService.deposit(idConta, new BigDecimal("200.00"));

    accountService.withdraw(idConta, new BigDecimal("50.00"));

    AbstractAccount conta = accountRepository.findById(idConta).get();
    assertEquals(
      0,
      new BigDecimal("150.00").compareTo(conta.getBalance()),
      "O saldo restante deve ser 150."
    );
  }

  @Test
  void shouldAllowNegativeBalanceWhenWithdrawingMoreThanAvailable() {
    Integer idConta = criarContaPadrao();
    accountService.deposit(idConta, new BigDecimal("100.00")); // Coloca 100 de saldo

    // Saca 150, o saldo deve ficar -50.00 (Sem dar erro!)
    accountService.withdraw(idConta, new BigDecimal("150.00"));

    AbstractAccount conta = accountRepository.findById(idConta).get();
    assertEquals(
      0,
      new BigDecimal("-50.00").compareTo(conta.getBalance()),
      "O saldo deve ficar negativo em -50.00"
    );
  }

  @Test
  void shouldThrowExceptionWhenWithdrawingNegativeOrZeroValue() {
    Integer idConta = criarContaPadrao();

    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      accountService.withdraw(idConta, new BigDecimal("-50.00")); // Tentar sacar um valor negativo
    });

    // Lembre-se de corrigir a palavra "depósito" para "saque" lá no seu AbstractAccount.java para este teste passar!
    assertEquals(
      "O valor do saque deve ser maior que zero.",
      exception.getMessage()
    );
  }

  // ==========================================
  // TESTES DOS MÉTODOS: listAll() e remove()
  // ==========================================

  @Test
  void shouldListAllAccounts() {
    assertEquals(0, accountService.listAll().size());
    accountService.create("Conta 1", AccountType.BANCO);
    accountService.create("Conta 2", AccountType.CARTEIRA);
    assertEquals(2, accountService.listAll().size());
  }

  @Test
  void shouldRemoveAccountSuccessfully() {
    Integer idConta = criarContaPadrao();
    assertEquals(1, accountService.listAll().size());

    accountService.remove(idConta);
    assertEquals(0, accountService.listAll().size());
  }

  @Test
  void shouldThrowExceptionWhenRemovingInvalidId() {
    RuntimeException exception = assertThrows(RuntimeException.class, () -> {
      accountService.remove(-1);
    });
    assertEquals(
      "Conta não encontrada para o ID informado.",
      exception.getMessage()
    );
  }
}
