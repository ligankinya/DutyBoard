package org.example;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.time.temporal.ChronoUnit.DAYS;

public class DutyTableGenerator {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private final String dutySeparator;
    private final String nameSeparator;

    public DutyTableGenerator() {
        this.dutySeparator = ":";
        this.nameSeparator = ",";
    }

    public String getPrintForm(String dateRange, String[] dutyWithLastNames) {

        String[] dates = dateRange.split("-");
        LocalDate startDate = LocalDate.parse(dates[0], dtf);

        final int dutyPerDay = dutyWithLastNames.length;
        final int dutyDays = (int) DAYS.between(startDate, LocalDate.parse(dates[1], dtf)) + 1;

        String[] dutyNames = new String[dutyPerDay];
        String[][] employeeNames = new String[dutyPerDay][];
        for (int i = 0; i < dutyPerDay; i++) {
            String[] values = dutyWithLastNames[i].split(dutySeparator);
            dutyNames[i] = values[0];
            employeeNames[i] = values[1].split(nameSeparator);
        }

        int j = 0;
        StringBuilder sb = new StringBuilder();
        String[][] packs = generateDutyPacks(employeeNames, dutyDays);
        for (String[] pack : packs) {
            sb.append(dtf.format(startDate.plusDays(j++))).append(':').append(System.lineSeparator());
            for (int i = 0; i < dutyNames.length; i++) {
                sb.append('\t').append(dutyNames[i]).append(": ").append(pack[i]).append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    public String[][] generateDutyPacks(String[][] employeeNamesByDuty, int daysCount) {
        int dutyCount = employeeNamesByDuty.length;
        String[][] res = new String[daysCount][dutyCount];

        List<Queue<Employee>> employeesByDuty = new ArrayList<>(dutyCount);
        for (String[] employeeNames : employeeNamesByDuty) {
            employeesByDuty.add(initQueue(employeeNames));
        }

        for (int i = 0; i < daysCount; i++) {
            List<String> occupied = new ArrayList<>(dutyCount);      //may be Set, but now it's short
            Employee current;
            for (Queue<Employee> employees : employeesByDuty) {
                if ((current = employees.poll()) != null && occupied.contains(current.getLastName())) {
                    Employee temp = current;
                    current = employees.poll();
                    employees.add(temp);
                }
                if (current != null) {
                    occupied.add(current.getLastName());
                    current.incrementDuty();
                    employees.add(current);
                }
            }
            res[i] = occupied.toArray(new String[0]);
        }
        return res;
    }

    private Queue<Employee> initQueue(String[] arr) {
        Queue<Employee> res = new PriorityQueue<>(new EmployeeComparator());
        for (String s : arr) {
            res.add(new Employee(s.trim()));
        }
        return res;
    }

    class Employee {

        private final String lastName;
        private int dutyDays;

        public Employee(String lastName) {
            this.lastName = lastName;
        }

        public void incrementDuty() {
            dutyDays++;
        }

        public int getDutyDays() {
            return dutyDays;
        }

        public String getLastName() {
            return lastName;
        }

        @Override
        public String toString() {
            return "Emp. LastName: " + lastName + " days: " + dutyDays;
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Not all args is set");
        } else {
            //have to be args check format
            String[] employeesNamesByDuty = new String[args.length - 1];
            int j = 0;
            for (int i = 1; i < args.length; i++) {
                employeesNamesByDuty[j++] = args[i];
            }
            DutyTableGenerator dutyTableGenerator = new DutyTableGenerator();
            String dutyTable = dutyTableGenerator.getPrintForm(args[0], employeesNamesByDuty);
            System.out.println(dutyTable);
        }
    }

    /**
     * Order: dutyDays Asc, lastName Asc
     */
    class EmployeeComparator implements Comparator<Employee> {
        @Override
        public int compare(Employee o1, Employee o2) {
            int res = Integer.compare(o1.getDutyDays(), o2.getDutyDays());
            return res == 0 ? o1.getLastName().compareTo(o2.getLastName()) : res;
        }
    }

}
