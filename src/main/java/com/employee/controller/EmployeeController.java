package com.employee.controller;

import com.employee.model.Employee;
import com.employee.service.EmployeeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<?> addEmployee(@RequestBody(required = false) Employee employee) {
        String validationMessage = validateEmployee(employee, true);
        if (validationMessage != null) {
            return badRequest(validationMessage);
        }
        if (employeeService.exists(employee.getId())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(message("Employee ID already exists"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeService.addEmployee(employee));
    }

    @GetMapping
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEmployeeById(@PathVariable int id) {
        if (id <= 0) {
            return badRequest("Employee ID must be greater than zero");
        }
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return notFound("Employee not found");
        }
        return ResponseEntity.ok(employee);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEmployee(@PathVariable int id,
                                             @RequestBody(required = false) Map<String, Object> requestBody) {
        if (id <= 0) {
            return badRequest("Employee ID must be greater than zero");
        }
        if (!employeeService.exists(id)) {
            return notFound("Employee not found");
        }
        Employee employee = employeeFromUpdateBody(id, requestBody);
        String validationMessage = validateEmployee(employee, false);
        if (validationMessage != null) {
            return badRequest(validationMessage);
        }
        return ResponseEntity.ok(employeeService.updateEmployee(id, employee));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable int id) {
        if (id <= 0) {
            return badRequest("Employee ID must be greater than zero");
        }
        if (!employeeService.deleteEmployee(id)) {
            return notFound("Employee not found");
        }
        return ResponseEntity.ok(message("Employee deleted successfully"));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidJson() {
        return badRequest("Request body must contain valid employee JSON");
    }

    private String validateEmployee(Employee employee, boolean validateId) {
        if (employee == null) {
            return "Employee data is required";
        }
        if (validateId && employee.getId() <= 0) {
            return "Employee ID must be greater than zero";
        }
        if (employee.getName() == null || employee.getName().isBlank()) {
            return "Employee name cannot be blank";
        }
        if (employee.getDepartment() == null || employee.getDepartment().isBlank()) {
            return "Employee department cannot be blank";
        }
        if (!Double.isFinite(employee.getSalary()) || employee.getSalary() <= 0) {
            return "Employee salary must be greater than zero and finite";
        }
        return null;
    }

    private Employee employeeFromUpdateBody(int id, Map<String, Object> requestBody) {
        if (requestBody == null) {
            return null;
        }
        String name = requestBody.get("name") instanceof String
                ? (String) requestBody.get("name") : null;
        String department = requestBody.get("department") instanceof String
                ? (String) requestBody.get("department") : null;
        double salary = requestBody.get("salary") instanceof Number
                ? ((Number) requestBody.get("salary")).doubleValue() : Double.NaN;
        // The path variable is the source of truth; a body ID is deliberately ignored.
        return new Employee(id, name, department, salary);
    }

    private ResponseEntity<Map<String, String>> badRequest(String message) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message(message));
    }

    private ResponseEntity<Map<String, String>> notFound(String message) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message(message));
    }

    private Map<String, String> message(String message) {
        return Map.of("message", message);
    }
}
