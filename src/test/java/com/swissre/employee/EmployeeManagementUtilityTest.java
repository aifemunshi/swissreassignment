package com.swissre.employee;

import com.swissre.employee.model.Employee;
import com.swissre.employee.utility.EmployeeManagementUtility;
import org.junit.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;
import static org.junit.Assert.*;

/**
 * Test class
 */

public class EmployeeManagementUtilityTest {

    @Test
    public void testCalculateDepth() {
        Employee ceo = new Employee(123, "Joe", "Doe", 60000, null);
        Employee manager1 = new Employee(124, "Martin", "Chekov", 45000, 123);
        Employee manager2 = new Employee(125, "Bob", "Ronstad", 47000, 123);
        Employee emp1 = new Employee(300, "Alice", "Hasacat", 50000, 124);
        Employee emp2 = new Employee(305, "Brett", "Hardleaf", 34000, 300);
        List<Employee> employees = Arrays.asList(ceo, manager1, manager2, emp1, emp2);
        Map<Integer, List<Employee>> orgStructure = EmployeeManagementUtility.buildOrgStructure(employees);
        assertEquals(0, EmployeeManagementUtility.calculateDepth(123, orgStructure, new HashMap<>()));
        assertEquals(1, EmployeeManagementUtility.calculateDepth(124, orgStructure, new HashMap<>()));
        assertEquals(1, EmployeeManagementUtility.calculateDepth(125, orgStructure, new HashMap<>()));
        assertEquals(2, EmployeeManagementUtility.calculateDepth(300, orgStructure, new HashMap<>()));
        assertEquals(3, EmployeeManagementUtility.calculateDepth(305, orgStructure, new HashMap<>()));
    }

    @Test
    public void testBuildOrgStructure_ValidInput() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1, "John", "Doe", 50000, 2));
        employees.add(new Employee(2, "Jane", "Smith", 60000, null));
        employees.add(new Employee(3, "Jim", "Beam", 55000, 2));
        Map<Integer, List<Employee>> expectedOrgStructure = new HashMap<>();
        expectedOrgStructure.put(2, List.of(employees.get(0), employees.get(2)));
        Map<Integer, List<Employee>> actualOrgStructure = EmployeeManagementUtility.buildOrgStructure(employees);
        assertEquals(expectedOrgStructure, actualOrgStructure);
    }

    @Test
    public void testBuildOrgStructure_EmptyList() {
        List<Employee> employees = Collections.emptyList();
        Map<Integer, List<Employee>> actualOrgStructure = EmployeeManagementUtility.buildOrgStructure(employees);
        assertTrue(actualOrgStructure.isEmpty());
    }

    @Test
    public void testBuildOrgStructure_NoManagerId() {
        List<Employee> employees = new ArrayList<>();
        employees.add(new Employee(1, "John", "Doe", 50000, null));
        employees.add(new Employee(2, "Jane", "Smith", 60000, null));
        Map<Integer, List<Employee>> actualOrgStructure = EmployeeManagementUtility.buildOrgStructure(employees);
        assertTrue(actualOrgStructure.isEmpty());
    }

    @Test
    public void testBuildOrgStructure_WithNullEmployees() {
        List<Employee> employees = new ArrayList<>();
        employees.add(null);
        employees.add(new Employee(1, "John", "Doe", 50000, 2));
        employees.add(null);
        employees.add(new Employee(2, "Jane", "Smith", 60000, null));
        Map<Integer, List<Employee>> expectedOrgStructure = new HashMap<>();
        expectedOrgStructure.put(2, List.of(employees.get(1)));
        Map<Integer, List<Employee>> actualOrgStructure = EmployeeManagementUtility.buildOrgStructure(employees);
        assertEquals(expectedOrgStructure, actualOrgStructure);
    }

    @Test
    public void testPerformSalaryCalculation_ManagerBelowRange() {
        List<Employee> employees = Arrays.asList(
                new Employee(1, "John", "Doe", 40000, null), // Manager with low salary
                new Employee(2, "Jane", "Smith", 50000, 1),
                new Employee(3, "Jim", "Beam", 60000, 1)
        );
        Map<Integer, List<Employee>> orgStructure = new HashMap<>();
        orgStructure.put(1, Arrays.asList(
                employees.get(1),
                employees.get(2)
        ));
        EmployeeManagementUtility.performSalaryCalculation(employees, orgStructure);
        assertNotNull(employees);
    }


    @Test
    public void testPerformReportingLines_ReportingLineExceedsLimit() {
        List<Employee> employees = new ArrayList<>();
        Employee emp1 = new Employee(1, "John", "Doe", 50000, null);
        Employee emp2 = new Employee(2, "Jane", "Smith", 60000, 1);
        Employee emp3 = new Employee(3, "Jim", "Beam", 55000, 2);
        Employee emp4 = new Employee(4, "Jake", "Blues", 65000, 3);
        Employee emp5 = new Employee(5, "Jill", "Green", 70000, 4);
        Employee emp6 = new Employee(6, "Joe", "Black", 75000, 5);
        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);
        employees.add(emp4);
        employees.add(emp5);
        employees.add(emp6);
        Map<Integer, List<Employee>> orgStructure = new HashMap<>();
        orgStructure.put(1, Arrays.asList(emp2));
        orgStructure.put(2, Arrays.asList(emp3));
        orgStructure.put(3, Arrays.asList(emp4));
        orgStructure.put(4, Arrays.asList(emp5));
        orgStructure.put(5, Arrays.asList(emp6));
        EmployeeManagementUtility.performReportingLines(employees, orgStructure);
        assertNotNull(employees);
    }

}
