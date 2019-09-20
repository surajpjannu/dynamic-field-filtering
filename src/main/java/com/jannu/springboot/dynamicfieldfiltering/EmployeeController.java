package com.jannu.springboot.dynamicfieldfiltering;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class EmployeeController {

    private List<Employee> employees = new ArrayList<>();

    public EmployeeController() {
        employees.add(new Employee("Suraj", "Panduranga", "Jannu", 26, 100000.00));
        employees.add(new Employee("Pramod", "", "Jannu", 29, 300000.00));
        employees.add(new Employee("Mohan", null, "Mari", 49, 200000.00));
        employees.add(new Employee("Abhilash", null, "", 34, 400000.00));
        employees.add(new Employee("Harsha", null, null, 28, 500000.00));
        employees.add(new Employee("Imran", "", "", 26, 800000.00));
    }

    @GetMapping("/employees")
    public List<Employee> employees() {
        return employees;
    }
}
