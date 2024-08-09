package com.swissre.employee;

import com.swissre.employee.model.Employee;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import static com.swissre.employee.utility.EmployeeManagementUtility.*;

@SpringBootApplication
@Slf4j
public class EmployeeApplication implements CommandLineRunner {

	@Value("classpath:employee.csv")
	private Resource resource;

	public static void main(String[] args) throws IOException {
		SpringApplication.run(EmployeeApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info("Processing of csv file started");
		List<Employee> employees = readEmployeesFromCSV(resource);
		Map<Integer, List<Employee>> orgStructure = buildOrgStructure(employees);
		performSalaryCalculation(employees, orgStructure);
		performReportingLines(employees, orgStructure);
		log.info("Processing of csv file finished");
	}

}
