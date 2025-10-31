package com.namak.services;

import java.io.FileInputStream;
import java.io.InputStream;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    public String readDocxFile(String absoluteFilePath) {
        try (InputStream inputStream = new FileInputStream(absoluteFilePath);
                XWPFDocument doc = new XWPFDocument(inputStream);
                XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        } catch (Exception e) {
            System.err.println("Error reading DOCX file: " + absoluteFilePath);
            throw new RuntimeException("Failed to read DOCX file", e);
        }
    }
}
