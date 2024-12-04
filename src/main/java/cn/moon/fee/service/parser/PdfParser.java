package cn.moon.fee.service.parser;

import cn.moon.fee.entity.Invoice;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 主要解析总金额，然后计算税率
 */
@Slf4j
public class PdfParser {


    public static void parse(byte[] f, Invoice invoice) throws Exception {
        String str = readText(f);
        String[] lines = str.split("\n");

        BigDecimal totalAmt = parseTotalAmt(lines);
        Assert.notNull(totalAmt, "totalAmt解析失败");

        invoice.setTotalAmt(totalAmt);

        BigDecimal amt = invoice.getAmt();
        Assert.notNull(amt, "amt应该通过你二维码解析过");


        BigDecimal taxAmt =  totalAmt.subtract(amt);
        invoice.setTaxAmt(taxAmt);

        if (taxAmt.equals(BigDecimal.ZERO)) {
            invoice.setRate(BigDecimal.ZERO); // 免税
            return;
        }

        // 金额 * 税率 = 税额
        BigDecimal rate = taxAmt.divide(amt, RoundingMode.HALF_DOWN);

        // 有可能计算错误，需要再次验证， 比如通过判断数字是否在pdf出现过
        invoice.setRate(rate);


        log.info("解析后发票信息为: {}", invoice);
    }


    /**
     * 价税合计(大写) 贰佰叁拾伍圆整 (小写)¥235.00
     *
     * @param lines
     * @param invoice
     * @return
     */
    private static BigDecimal parseTotalAmt(String[] lines) {
        for (String line : lines) {
            line = line.trim();
            if (line.contains("价税合计")) {
                List<BigDecimal> numbers = parseNumber(line);

                return numbers.get(0);
            }
        }
        return null;
    }


    private static List<BigDecimal> parseNumber(String str) {
        log.info("解析字符串中的数字: {}", str);
        Pattern pattern = Pattern.compile("(\\d+\\.\\d+)");

        Matcher matcher = pattern.matcher(str);
        List<BigDecimal> list = new ArrayList<>();
        while (matcher.find()) {
            String number = matcher.group();
            list.add(new BigDecimal(number));
        }

        log.info("解析结果为：{}", list);

        return list;
    }


    private static String readText(byte[] pdfFile) throws Exception {
        PDDocument doc = null;
        try {
            doc = PDDocument.load(pdfFile);


            PDFTextStripper textStripper = new PDFTextStripper();
            textStripper.setSortByPosition(true);

            return textStripper.getText(doc);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (doc != null) {
                doc.close();
            }
        }
        return null;
    }


}
