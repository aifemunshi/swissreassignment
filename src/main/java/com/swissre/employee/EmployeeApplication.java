package com.swissre.employee;

import com.swissre.employee.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import static com.swissre.employee.utility.EmployeeManagementUtility.*;

@SpringBootApplication
@Slf4j
public class EmployeeApplication implements CommandLineRunner {

	public static void main(String[] args) throws IOException {
		SpringApplication.run(EmployeeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Processing of csv file started");
		String csvFile = "E://employee//employee//src//main//resources//employee.csv";
		List<Employee> employees = readEmployeesFromCSV(csvFile);
		Map<Integer, List<Employee>> orgStructure = buildOrgStructure(employees);
		performSalaryCalculation(employees, orgStructure);
		performReportingLines(employees, orgStructure);
		log.info("Processing of csv file finished");
	}

}
