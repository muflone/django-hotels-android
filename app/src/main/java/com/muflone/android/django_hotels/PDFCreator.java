package com.muflone.android.django_hotels;

import android.graphics.pdf.PdfDocument;

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
import java.util.Locale;

public class PDFCreator {
    private Document document = null;
    public Rectangle pageSize = PageSize.A4;
    public String title = null;
    public String subject = null;
    public String author = null;
    public String creator = null;
    public String keywords = null;

    public PDFCreator() {
        // Empty method
    }

    private void create() {
        this.document = new Document();
    }

    private void open() {
        this.document.open();
        this.document.setPageSize(this.pageSize);
        // Add title
        if (this.title != null) {
            this.document.addTitle(this.title);
        }
        // Add subject
        if (this.subject != null) {
            this.document.addSubject(this.subject);
        }
        // Add author
        if (this.author != null) {
            this.document.addAuthor(this.author);
        }
        // Add creator
        if (this.creator != null) {
            this.document.addCreator(this.creator);
        }
        // Add keywords
        if (this.keywords != null) {
            this.document.addKeywords(this.keywords);
        }
        this.document.addCreationDate();
    }

    private void close() {
        this.document.close();
        this.document = null;
    }

    public boolean htmlToPDF(String content, String destionationPath) throws DocumentException {
        // Save the HTML content to a PDFCreator file
        boolean result = false;
        File filePath = new File(destionationPath);
        try {
            OutputStream destinationStream = new FileOutputStream(filePath);
            this.create();
            PdfWriter writer = PdfWriter.getInstance(this.document, destinationStream);
            this.open();
            InputStream inputStream = new ByteArrayInputStream(content.getBytes());
            XMLWorkerHelper.getInstance().parseXHtml(writer, this.document, inputStream);
            // Replace HTML title
            if (this.title != null) {
                this.document.addTitle(this.title);
            }
            this.close();
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
