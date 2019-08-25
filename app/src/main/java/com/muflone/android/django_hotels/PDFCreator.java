/*
 *     Project: Django Hotels Android
 * Description: The Android client companion app for Django Hotels
 *     Website: http://www.muflone.com/django-hotels-android/
 *      Author: Fabio Castelli (Muflone) <muflone@muflone.com>
 *   Copyright: 2018-2019 Fabio Castelli
 *     License: GPL-3+
 * Source code: https://github.com/muflone/django-hotels-android
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
        this.document = new Document(this.pageSize);
    }

    private void open() {
        this.document.open();
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

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean htmlToPDF(String content, String destinationPath) throws DocumentException {
        // Save the HTML content to a PDFCreator file
        boolean result = false;
        File filePath = new File(destinationPath);
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
