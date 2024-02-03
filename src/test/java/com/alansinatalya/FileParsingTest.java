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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileParsingTest {
    private final ClassLoader cl = FileParsingTest.class.getClassLoader();

    @Test
    void zipParsingTest() throws Exception {
        try (
                InputStream resource = cl.getResourceAsStream("test.zip");
                ZipInputStream zis = new ZipInputStream(resource)
        ) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains("attention.pdf")) {
                    PDF pdf = new PDF(zis);
                    assertEquals("All her hard work paid off and she passed the exam. She is happy", pdf.text.trim());
                } else if (entry.getName().contains("bug.csv")) {
                    CSVReader csvContent = new CSVReader(new InputStreamReader(zis));
                    List<String[]> content = csvContent.readAll();
                    assertArrayEquals(
                            new String[]{"severity", "major"}, content.get(0)
                    );
                    assertArrayEquals(
                            new String[]{"priority", "minor"}, content.get(1)
                    );
                } else if (entry.getName().contains("cart.xls")) {
                    XLS xls = new XLS(zis);
                    assertEquals(
                            "fruit",
                            xls.excel.getSheet("cart1")
                                    .getRow(0)
                                    .getCell(0)
                                    .getStringCellValue()
                    );
                    assertEquals(
                            "orange",
                            xls.excel.getSheet("cart1")
                                    .getRow(0)
                                    .getCell(1)
                                    .getStringCellValue()
                    );
                    assertEquals(
                            "vegetable",
                            xls.excel.getSheet("cart1")
                                    .getRow(1)
                                    .getCell(0)
                                    .getStringCellValue()
                    );
                    assertEquals(
                            "cucumber",
                            xls.excel.getSheet("cart1")
                                    .getRow(1)
                                    .getCell(1)
                                    .getStringCellValue()
                    );
                }
            }
        }
    }

    @Test
    void jsonParsingTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("pet.json");
             Reader reader = new InputStreamReader(is)) {
            ObjectMapper objectMapper = new ObjectMapper();
            Pet pet = objectMapper.readValue(reader, Pet.class);

            assertEquals("cat", pet.type);
            assertEquals("zosya", pet.name);
            assertEquals(1, pet.age);
            assertArrayEquals(new String[]{"meow", "purr", "growl"}, pet.sound.toArray());
        }
    }
}
