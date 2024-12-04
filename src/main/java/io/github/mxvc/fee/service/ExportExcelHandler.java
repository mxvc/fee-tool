package io.github.mxvc.fee.service;

import io.github.mxvc.fee.entity.Invoice;
import cn.hutool.core.date.DateUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@Component
public class ExportExcelHandler {

    public  void exportExcel(List<Invoice> invoiceList, OutputStream os) throws IOException {
        Workbook wb = new XSSFWorkbook();

        // 百分比样式
        CellStyle percentCellStyle = wb.createCellStyle();//创建单元格格式
        percentCellStyle.setDataFormat(wb.createDataFormat().getFormat("0.00%"));

        Sheet sheet = wb.createSheet();

        {
            String fullHeader = "序号,发票类型,发票代码,发票号,开票日期,税率,价税合计,金额,税额,发票类别,是否认证,张数,录入方式";

            String[] headers = fullHeader.split(",");
            Row row = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                row.createCell(i).setCellValue(headers[i]);
            }
        }


        for (int i = 0; i < invoiceList.size(); i++) {
            Invoice invoice = invoiceList.get(i);
            Row row = sheet.createRow(sheet.getLastRowNum() + 1);
            row.createCell(0).setCellValue(i + 1);
            row.createCell(1).setCellValue(invoice.getTypeLabelCrec());
            row.createCell(2).setCellValue(invoice.getCode());
            row.createCell(3).setCellValue(invoice.getNumber());
            row.createCell(4).setCellValue(DateUtil.formatDate(invoice.getDate()));

            {
                Cell cell = row.createCell(5);
                cell.setCellStyle(percentCellStyle);
                cell.setCellValue(invoice.getRate().doubleValue());
            }



            row.createCell(6).setCellValue(invoice.getTotalAmt().doubleValue()); // 价税合计

            row.createCell(7).setCellValue(invoice.getAmt().doubleValue()); // 金额
            row.createCell(8).setCellValue(invoice.getTaxAmt().doubleValue()); // 税额

            row.createCell(9).setCellValue("进项发票");
            row.createCell(10).setCellValue("否");
            row.createCell(11).setCellValue(1);
            row.createCell(12).setCellValue("手工录入");
        }


        wb.write(os);
        os.close();
        wb.close();
    }
}
