package io.github.mxvc.fee.service.parser;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.extra.qrcode.BufferedImageLuminanceSource;
import io.github.mxvc.fee.entity.Invoice;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.common.HybridBinarizer;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDResources;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class QrcodeParser {


    public  static Invoice parse(byte[] file, Invoice invoice) throws Exception {
        List<BufferedImage> imageList = findQrCode(file);

        String text = null;
        for (BufferedImage image : imageList) {
            try {
                text = parseQrcodeText(image);
            } catch (Exception e) {
                System.out.println("解析二维码失败，试一试下个对象" + e.getMessage());
            }
            if (text != null) {
                break;
            }

        }
        Assert.state(text != null,"解析发票二维码失败");


        String[] arr = text.split(",");

        System.out.println("二维码数据数目:" + arr.length);


        invoice.setType(arr[1]); //发票类型
        invoice.setCode(arr[2]); //发票代码
        invoice.setNumber(arr[3]); // 发票号码
        invoice.setAmt(new BigDecimal(arr[4])); // 发票金额
        invoice.setDate(DateUtil.parse(arr[5], "yyyyMMdd")); //开票日期
        invoice.setValidateCode(arr[6]); // 校验码

        return invoice;
    }


    /**
     * 将PDF文件转换成多张图片
     *
     * @param pdfFile PDF源文件
     * @return 图片字节数组列表
     */
    private static   List<BufferedImage> findQrCode(byte[] pdfFile) throws Exception {
        List<BufferedImage> list = new ArrayList<>();
        PDDocument doc = null;
        try {
            doc = PDDocument.load(pdfFile);
            PDPage page = doc.getPage(0); // 发票只取第一页
            PDResources resources = page.getResources();
            for (COSName name : resources.getXObjectNames()) {
                if (resources.isImageXObject(name)) {
                    PDImageXObject obj = (PDImageXObject) resources.getXObject(name);
                    BufferedImage image = obj.getImage();

                    // ImageIO.write(image, "png", new File("temp.png"));

                    list.add(image);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (doc != null) {
                doc.close();
            }
        }


        return list;
    }

    private static   String parseQrcodeText(BufferedImage img) throws NotFoundException {
        MultiFormatReader formatReader = new MultiFormatReader();
        //读取指定的二维码文件
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(img)));

        com.google.zxing.Result result = formatReader.decode(binaryBitmap, null);
        //输出相关的二维码信息
        System.out.println("解析结果：" + result.toString());
        System.out.println("二维码格式类型：" + result.getBarcodeFormat());
        System.out.println("二维码文本内容：" + result.getText());
        return result.getText();
    }


}
