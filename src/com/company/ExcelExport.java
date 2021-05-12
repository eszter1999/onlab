package com.company;

import com.company.data.Groups;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ExcelExport {

    private static String[] columns = {"Group", "Lesson", "Teacher", "Room", "Time"};
    Class[] classes;
    Timetable timetable;

    public ExcelExport(Timetable timetable) throws IOException {
        this.timetable = timetable;
        this.classes = timetable.getClasses();
        export();
    }

    private void export() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        File f = new File("out/production/GA-timetable_sort");
        if (f.exists()) f.delete();

        Groups[] groups = timetable.getGroupsAsArray();
        for (Groups group : groups) {
            ArrayList<Class> groupClasses = getClasses(timetable, group.getId());
            Collections.sort(groupClasses,
                    new TimeslotSorter());

            Sheet sheet = workbook.createSheet(group.getName());

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerFont.setColor(IndexedColors.RED.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            Row headerRow = sheet.createRow(0);

            for (int j = 0; j < columns.length; j++) {
                Cell cell = headerRow.createCell(j);
                cell.setCellValue(columns[j]);
                cell.setCellStyle(headerCellStyle);
            }

            int rowNum = 1;

            for (Class bestClass : groupClasses) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0)
                        .setCellValue(timetable.getGroup(bestClass.getGroupId()).getName());

                row.createCell(1)
                        .setCellValue(timetable.getLesson(bestClass.getLessonId()).getName());

                row.createCell(2)
                        .setCellValue(timetable.getTeacher(bestClass.getTeacherId()).getName());

                row.createCell(3)
                        .setCellValue(timetable.getRoom(bestClass.getRoomId()).getName());

                row.createCell(4)
                        .setCellValue(timetable.getTimeslot(bestClass.getTimeslotId()).getTime());
            }

            for (int j = 0; j < columns.length; j++) {
                sheet.autoSizeColumn(j);
            }
        }


        FileOutputStream fileOut = new FileOutputStream("classes.xlsx");
        workbook.write(fileOut);
        fileOut.close();

        workbook.close();
    }


    private static ArrayList getClasses(Timetable timetable, int group) {
        ArrayList<Class> classes = new ArrayList<>();
        Class[] cl = timetable.getClasses();
        for (Class aClass : cl) {
            if (aClass.getGroupId() == group)
                classes.add(aClass);
        }
        return classes;
    }
}

class TimeslotSorter implements Comparator<Class> {
    @Override
    public int compare(Class cl1, Class cl2) {
        return cl1.getTimeslotId() - cl2.getTimeslotId();
    }
}

