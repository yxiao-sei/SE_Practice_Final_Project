package com.cloume.ecnu.sei.app.utils;

//import com.itextpdf.text.Element;
//import com.itextpdf.text.pdf.*;
//import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
//import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class WordConverterPDFUtil {

//
//    /**
//     * pdf加水印
//     * @param inputFiles
//     * @param outputFiles
//     * @param watermarkName
//     */
//    public static void addWatermark(String inputFiles,String outputFiles,String watermarkName){
//        try {
//            PdfReader reader = new PdfReader(inputFiles);
//            PdfStamper stamper = new PdfStamper(reader, new FileOutputStream(outputFiles));
//            PdfContentByte content;
//            PdfGState gs1 = new PdfGState();
//            gs1.setFillOpacity(0.5f);
//            // 使用支持中文的字体
//            BaseFont bf = BaseFont.createFont("simsun.ttc,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
//            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
//                content = stamper.getOverContent(i);
//                content.setGState(gs1);
//                content.beginText();
//                // 设置字体和大小
//                content.setFontAndSize(bf, 60); // 增大字体大小到50
//                content.showTextAligned(Element.ALIGN_CENTER, watermarkName, 290.5f, 421, 45);
//                content.endText();
//            }
//            stamper.close();
//            reader.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    /**
//     * word转pdf
//     * @param inputFiles
//     * @param outputFiles
//     */
//    public static void wordConverterPDF(String inputFiles,String outputFiles){
//        try {
//            FileInputStream fileInputStream = new FileInputStream(inputFiles);
//            XWPFDocument xwpfDocument = new XWPFDocument(fileInputStream);
//            PdfOptions pdfOptions = PdfOptions.create();
//            FileOutputStream fileOutputStream = new FileOutputStream(outputFiles);
//            PdfConverter.getInstance().convert(xwpfDocument, fileOutputStream, pdfOptions);
//            fileInputStream.close();
//            fileOutputStream.close();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }



}
