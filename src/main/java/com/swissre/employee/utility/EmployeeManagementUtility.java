package com.swissre.employee.utility;

import com.swissre.employee.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for handling all employee details
 */

@Slf4j
@Service
public class EmployeeManagementUtility {

    /**
     *
     * @param filePath
     * @return List<Employee>
     * @throws IOException
     */
    public static List<Employee> readEmployeesFromCSV(String filePath) throws IOException {
        List<Employee> employees = new ArrayList<>();
        String line;
        String cvsSplitBy = ",";

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] employeeData = line.split(cvsSplitBy);
                int id = Integer.parseInt(employeeData[0]);
                String firstName = employeeData[1];
                String lastName = employeeData[2];
                double salary = Double.parseDouble(employeeData[3]);
                Integer managerId = employeeData.length > 4 && !employeeData[4].isEmpty() ? Integer.parseInt(employeeData[4]) : null;

                employees.add(new Employee(id, firstName, lastName, salary, managerId));
            }
        } catch (IOException excp) {
            log.error("Exception occured while reading excel file");
            throw excp;
        }
        return employees;
    }

    /**
     *
     * @param employees
     * @return Map<Integer, List<Employee>>
     */
    public static Map<Integer, List<Employee>> buildOrgStructure(List<Employee> employees) {
        Map<Integer, List<Employee>> orgStructure = new HashMap<>();
        for (Employee emp : employees) {
            if (emp != null && emp.getManagerId() != null) {
                orgStructure.putIfAbsent(emp.getManagerId(), new ArrayList<>());
                orgStructure.get(emp.getManagerId()).add(emp);
            }
        }
        return orgStructure;
    }

    /**
     *
     * @param employees
     * @param orgStructure
     */
    public static void performSalaryCalculation(List<Employee> employees, Map<Integer, List<Employee>> orgStructure) {
        for (Employee manager : employees) {
            if (orgStructure.containsKey(manager.getId())) {
                List<Employee> subordinates = orgStructure.get(manager.getId());
                double avgSalary = subordinates.stream().mapToDouble(e -> e.getSalary()).average().orElse(0);
                double minSalary = avgSalary * 1.2;
                double maxSalary = avgSalary * 1.5;

                if (manager.getSalary() < minSalary) {
                    log.info("Manager {} {} earns less than they should by {} ", manager.getFirstName(), manager.getLastName(), minSalary - manager.getSalary());
                } else if (manager.getSalary() > maxSalary) {
                    log.info("Manager {} {} earns more than they should by {}", manager.getFirstName(), manager.getLastName(), manager.getSalary() - maxSalary);
                }
            }
        }
    }

    /**
     *
     * @param employees
     * @param orgStructure
     */
    public static void performReportingLines(List<Employee> employees, Map<Integer, List<Employee>> orgStructure) {
        Map<Integer, Integer> depthMap = new HashMap<>();
        for (Employee emp : employees) {
            int depth = calculateDepth(emp.getId(), orgStructure, depthMap);
            if (depth > 4) {
                log.info("Employee {}  {} has a reporting line which is too long by {}", emp.getFirstName(), emp.getLastName(), depth - 4);
            }
        }
    }

    /**
     *
     * @param empId
     * @param orgStructure
     * @param depthMap
     * @return int
     */
    public static int calculateDepth(int empId, Map<Integer, List<Employee>> orgStructure, Map<Integer, Integer> depthMap) {
        if (depthMap.containsKey(empId)) {
            return depthMap.get(empId);
        }
        for (Map.Entry<Integer, List<Employee>> entry : orgStructure.entrySet()) {
            for (Employee emp : entry.getValue()) {
                if (emp.getId() == empId) {
                    int depth = 1 + calculateDepth(entry.getKey(), orgStructure, depthMap);
                    depthMap.put(empId, depth);
                    return depth;
                }
            }
        }
        depthMap.put(empId, 0);
        return 0; // CEO or top-level employee
    }
}
