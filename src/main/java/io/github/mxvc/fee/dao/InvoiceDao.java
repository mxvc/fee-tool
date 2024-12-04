package io.github.mxvc.fee.dao;

import io.github.mxvc.fee.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceDao extends JpaRepository<Invoice, Integer> {


    long countByCodeAndNumber(String code, String number);

}

