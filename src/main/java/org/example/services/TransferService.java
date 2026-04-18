package org.example.services;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.Transfer;

public interface TransferService {
  void create(
    BigDecimal value,
    Date date,
    AbstractAccount accOutput,
    AbstractAccount accInput
  );

  List<Transfer> listAll();

  void remove(Integer transferId);
}
