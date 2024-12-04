package io.github.mxvc.fee.service;

import io.github.mxvc.fee.entity.Invoice;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * 合并打印，节约纸张
 * 注意，坐标是右上角那个区， 所以00 标识页面的左下角
 */
@Slf4j
@Component
public class InvoiceMergeHandler {


    public static final PDRectangle A4 = PDRectangle.A4;
    public static final int LINE_WIDTH = 2;


    public void start(List<Invoice> files, OutputStream outputStream) throws IOException {
        PDDocument doc = new PDDocument();
        int i = 0;
        while (i < files.size()) {
            Invoice f1 = files.get(i), f2 = null;
            if (i + 1 < files.size()) {
                f2 = files.get(i + 1);
            }
            drawFile(doc, f1, f2);
            i = i + 2;
        }

        doc.save(outputStream);
    }


    private void drawFile(PDDocument doc, Invoice file1, Invoice file2) throws IOException {
        PDPage page = new PDPage(A4);
        doc.addPage(page);
        PDPageContentStream ps = new PDPageContentStream(doc, page);

        ps.setLineWidth(LINE_WIDTH);
        ps.setStrokingColor(Color.LIGHT_GRAY);

        float x = 0;
        float y = 0;

        // 第一个文件
        y = drawFile(doc, ps, file1, x, y);


        ps.drawLine(0, y, A4.getWidth(), y = y + LINE_WIDTH);


        // 第二个文件
        y = drawFile(doc, ps, file2, x, y);
        ps.drawLine(0, y, A4.getWidth(), y);

        ps.close();
    }

    private float drawFile(PDDocument doc, PDPageContentStream ps, Invoice f, float x, float y) throws IOException {
        if (f == null) {
            return y;
        }
        byte[] imgFile = pdf2img(f);

        // 写入图片
        PDImageXObject pimg = PDImageXObject.createFromByteArray(doc, imgFile, f.getName());

        float width = pimg.getWidth();
        float height = pimg.getHeight();

        float rate = height / width;

        float w = A4.getWidth();
        float h = w * rate;


        ps.drawImage(pimg, x, y, w, h);


        return y + h;
    }

    private byte[] pdf2img(Invoice f) throws IOException {
        PDDocument doc = PDDocument.load(f.getContent());
        PDFRenderer renderer = new PDFRenderer(doc);
        BufferedImage img = renderer.renderImageWithDPI(0, 144);

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        ImageIO.write(img, "png", os);

        doc.close();
        byte[] bytes = os.toByteArray();
        os.close();
        ;
        return bytes;
    }

}
