package io.github.mxvc.fee.service;

import io.github.mxvc.fee.dao.InvoiceDao;
import io.github.mxvc.fee.entity.Invoice;
import io.github.mxvc.fee.service.parser.PdfParser;
import io.github.mxvc.fee.service.parser.QrcodeParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class InvoiceService  {


    @Resource
    InvoiceDao invoiceDao;







    @Transactional
    public void saveUpload(byte[] bytes, String fileName, String owner) throws Exception {
        Invoice invoice = new Invoice();
        invoice.setName(fileName);
        invoice.setContent(bytes);
        QrcodeParser.parse(bytes, invoice);
        PdfParser.parse(bytes, invoice);

        invoice.setOwner(owner);

        long count = invoiceDao.countByCodeAndNumber(invoice.getCode(), invoice.getNumber());
        Assert.state(count == 0, String.format("发票%s已存在，不能重复上传", invoice.getName()));
        invoice = invoiceDao.save(invoice);
    }

    public List<Invoice> findAll(Invoice param) {
        return invoiceDao.findAll(Example.of(param));
    }

    public void deleteById(Integer id) {
        invoiceDao.deleteById(id);
    }

    public Invoice findOne(Invoice invoice) {
        return invoiceDao.findById(invoice.getId()).orElseThrow();
    }

    public void save(Invoice e) {
        invoiceDao.save(e);
    }

    public List<Invoice> findAllById(List<Integer> ids) {
        return invoiceDao.findAllById(ids);
    }


}

