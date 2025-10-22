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
                // InputStream inputStream = resourcePatternResolver.getResource("file:" + absoluteFilePath).getInputStream();
                XWPFDocument doc = new XWPFDocument(inputStream);
                XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        } catch (Exception e) {
            // In a real application, you'd want to handle this exception more gracefully
            throw new RuntimeException("Failed to read DOCX file", e);
        }
    }
}
