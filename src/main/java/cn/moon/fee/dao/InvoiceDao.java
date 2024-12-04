package cn.moon.fee.dao;

import cn.moon.fee.entity.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InvoiceDao extends JpaRepository<Invoice, Integer> {


    long countByCodeAndNumber(String code, String number);

}

