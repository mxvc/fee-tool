package io.github.mxvc.fee.tool;

import org.apache.pdfbox.multipdf.PDFMergerUtility;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class PdfTool {


    public static void mergePdfs(List<byte[]> list, OutputStream os) throws IOException {
        PDFMergerUtility util = new PDFMergerUtility();


        List<InputStream> streamList = list.stream().map(ByteArrayInputStream::new).collect(Collectors.toList());

        util.addSources(streamList);

        util.setDestinationStream(os);
        util.mergeDocuments();



        for (InputStream inputStream : streamList) {
            inputStream.close();
        }

        os.close();
    }

}
