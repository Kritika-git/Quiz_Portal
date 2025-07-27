package com.cmpdi.project.service;

import java.io.IOException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cmpdi.project.model.MasterEmployee;
import com.cmpdi.project.repository.MasterEmployeeRepository;

@Service
public class MasterEmployeeService {

    @Autowired
    private MasterEmployeeRepository repository;

    private final DataFormatter dataFormatter = new DataFormatter();

    public void importFromExcel(MultipartFile file) throws IOException {
        List<MasterEmployee> employees = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                MasterEmployee emp = new MasterEmployee();
                emp.setEmployeeId(getCellAsString(row.getCell(0)));
                emp.setName(getCellAsString(row.getCell(1)));
                emp.setDesignation(getCellAsString(row.getCell(2)));
                emp.setDepartment(getCellAsString(row.getCell(3)));
                emp.setEmail(getCellAsString(row.getCell(4)));
                emp.setPhone(getCellAsString(row.getCell(5)));
                emp.setCategory(getCellAsString(row.getCell(6)));
                emp.setGender(getCellAsString(row.getCell(7)));
                Cell dobCell = row.getCell(8);
                if (dobCell != null && DateUtil.isCellDateFormatted(dobCell)) {
                    Date dob = dobCell.getDateCellValue();
                    emp.setDob(dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                }
                String isAdminStr = getCellAsString(row.getCell(9)); // Assuming isAdmin is in column J (index 9)
                boolean isAdmin = "true".equalsIgnoreCase(isAdminStr.trim()) || "1".equals(isAdminStr.trim());
                emp.setAdmin(isAdmin);
;
                employees.add(emp);
            }
        }

        repository.deleteAll(); 
        repository.saveAll(employees);
    }

    private String getCellAsString(Cell cell) {
        if (cell == null) return "";
        return dataFormatter.formatCellValue(cell).trim();
    }

    public List<MasterEmployee> getAllEmployees() {
        return repository.findAll();
    }
}
