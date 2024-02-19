package com.alansinatalya;

import com.alansinatalya.model.Pet;
import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class FileParsingTest {
    private final ClassLoader cl = FileParsingTest.class.getClassLoader();

    @Test
    void zipParsingPdfTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("test.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            boolean fileExists = false;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains("attention.pdf")) {
                    PDF pdf = new PDF(zis);
                    assertThat(pdf.text.trim()).isEqualTo("All her hard work paid off and she passed the exam. She is happy");
                    fileExists = true;
                    break;
                }
            }
            assertThat(fileExists).withFailMessage("PDF file does not exist in the archive").isEqualTo(true);
        }
    }

    @Test
    void zipParsingCsvTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("test.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            boolean fileExists = false;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains("bug.csv")) {
                    CSVReader csv = new CSVReader(new InputStreamReader(zis));
                    List<String[]> content = csv.readAll();
                    assertThat(content.get(0)).isEqualTo(new String[]{"severity", "major"});
                    assertThat(content.get(1)).isEqualTo(new String[]{"priority", "minor"});
                    fileExists = true;
                    break;
                }
            }
            assertThat(fileExists).withFailMessage("CSV file does not exist in the archive").isEqualTo(true);
        }
    }

    @Test
    void zipParsingXlsTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("test.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            boolean fileExists = false;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains("cart.xls")) {
                    XLS xls = new XLS(zis);
                    assertThat(xls.excel.getSheet("cart1").getRow(0).getCell(0).getStringCellValue()).isEqualTo("fruit");
                    assertThat(xls.excel.getSheet("cart1").getRow(0).getCell(1).getStringCellValue()).isEqualTo("orange");
                    assertThat(xls.excel.getSheet("cart1").getRow(1).getCell(0).getStringCellValue()).isEqualTo("vegetable");
                    assertThat(xls.excel.getSheet("cart1").getRow(1).getCell(1).getStringCellValue()).isEqualTo("cucumber");
                    fileExists = true;
                    break;
                }
            }
            assertThat(fileExists).withFailMessage("XLS file does not exist in the archive").isEqualTo(true);
        }
    }

    @Test
    void jsonParsingTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("pet.json");
             Reader reader = new InputStreamReader(is)) {
            ObjectMapper objectMapper = new ObjectMapper();
            Pet pet = objectMapper.readValue(reader, Pet.class);
            assertThat(pet.getType()).isEqualTo("cat");
            assertThat(pet.getName()).isEqualTo("zosya");
            assertThat(pet.getAge()).isEqualTo(1);
            assertThat(pet.getSound().toArray()).isEqualTo(new String[]{"meow", "purr", "growl"});
        }
    }
}
