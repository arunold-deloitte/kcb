package com.namak.services;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
public class DocumentService {

    public String readDocxFile(String fileName) {
        try (InputStream inputStream = new ClassPathResource(fileName).getInputStream();
             XWPFDocument doc = new XWPFDocument(inputStream);
             XWPFWordExtractor extractor = new XWPFWordExtractor(doc)) {
            return extractor.getText();
        } catch (Exception e) {
            // In a real application, you'd want to handle this exception more gracefully
            throw new RuntimeException("Failed to read DOCX file", e);
        }
    }
}
