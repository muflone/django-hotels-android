package com.muflone.android.django_hotels;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PDFCreator {
    public Rectangle pageSize;

    public PDFCreator() {
        this.pageSize = PageSize.A4;
    }

    public PDFCreator(Rectangle pageSize) {
        super();
        this.pageSize = pageSize;
    }


    public boolean htmlToPDF(String content, String destionationPath) throws DocumentException {
        // Save the HTML content to a PDFCreator file
        boolean result = false;
        File filePath = new File(destionationPath);
        try {
            OutputStream destinationStream = new FileOutputStream(filePath);
            Document document = new Document();
            document.setPageSize(this.pageSize);
            PdfWriter writer = PdfWriter.getInstance(document, destinationStream);
            document.open();
            InputStream inputStream = new ByteArrayInputStream(content.getBytes());
            XMLWorkerHelper.getInstance().parseXHtml(writer, document, inputStream);
            document.close();
            inputStream.close();
            destinationStream.close();
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
