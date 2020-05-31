package org.example;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class DutyTableGeneratorTest {

    @Test
    public void everyPackIsSet() {
        int packCount = 100;
        DutyTableGenerator dutyTableGenerator = new DutyTableGenerator();
        String[][] dutyTable = dutyTableGenerator.generateDutyPacks(new String[][]{
                new String[]{"Иванов", "Петров", "Сидоров"},
                new String[]{"Иванов", "Кузнецов", "Никифоров", "Борисов"}
        }, packCount);
        for (String[] strings : dutyTable) {
            assertTrue(Stream.of(strings).noneMatch(String::isEmpty));
            assertEquals(strings.length, new HashSet<>(Arrays.asList(strings)).size());
        }
    }

    @Test
    public void getPrintForm() {
        DutyTableGenerator dutyTableGenerator = new DutyTableGenerator();
        String dutyTable = dutyTableGenerator.getPrintForm("20.04.2020-26.04.2020", new String[]{
                "Дежурство по установке: Иванов, Петров, Сидоров",
                "Дежурство по поддержке: Иванов, Кузнецов, Никифоров, Борисов"
        });
        System.out.println(dutyTable);
    }
}
