package com.employee.service;

import com.employee.model.Employee;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmployeeService {
    // The Map stores employees by their unique ID and LinkedHashMap keeps insertion order.
    private final Map<Integer, Employee> employees = new LinkedHashMap<>();

    public Employee addEmployee(Employee employee) {
        employees.put(employee.getId(), employee);
        return employee;
    }

    public List<Employee> getAllEmployees() {
        return new ArrayList<>(employees.values());
    }

    public Employee getEmployeeById(int id) {
        return employees.get(id);
    }

    public Employee updateEmployee(int id, Employee employee) {
        employee.setId(id);
        employees.put(id, employee);
        return employee;
    }

    public boolean deleteEmployee(int id) {
        return employees.remove(id) != null;
    }

    public boolean exists(int id) {
        return employees.containsKey(id);
    }
}
