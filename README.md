# EmployeeManagementAPI

This is a beginner-friendly Spring Boot 4.1.2 REST API for managing employees. It uses an in-memory `LinkedHashMap`, so data resets whenever the application restarts. It does not use MySQL, JDBC, JPA, Lombok, security, or an external database.

## Prerequisites

- JDK 21.
- IntelliJ IDEA.
- Postman for manual API testing.

Maven does not need to be installed globally because the project includes the Maven Wrapper. The only application dependency is `spring-boot-starter-web`.

## MVC-style separation

- **Model (`Employee`)**: stores employee fields and provides constructors, getters, setters, and `toString()`.
- **Service (`EmployeeService`)**: owns the private `LinkedHashMap` and contains the employee business operations.
- **Controller (`EmployeeController`)**: receives HTTP requests, validates input, calls the service, and returns JSON responses with suitable HTTP status codes.

## Why LinkedHashMap?

The map stores each employee using the employee ID as the key, which makes ID lookup and duplicate checking simple. `LinkedHashMap` also preserves insertion order when all employees are returned. The map is private so callers cannot change the collection directly.

## Open and run in IntelliJ IDEA

1. Select **File > Open** in IntelliJ IDEA.
2. Open the `EmployeeManagementAPI` folder that contains `pom.xml`.
3. Wait for IntelliJ to import the Maven project.
4. Set the Project SDK to Java 21 in **File > Project Structure**.
5. Open `src/main/java/com/employee/EmployeeManagementApiApplication.java`.
6. Run the `main` method using the green run icon.
7. The API starts at `http://localhost:8080`.

From IntelliJ's Terminal, the Maven Wrapper commands are:

```text
.\mvnw.cmd clean compile -DskipTests
.\mvnw.cmd spring-boot:run
```

## Endpoints and expected responses

### POST `/employees`

Creates an employee. Required JSON fields are `id`, `name`, `department`, and `salary`.

Request:

```json
{
  "id": 101,
  "name": "Aman",
  "department": "IT",
  "salary": 50000
}
```

Returns `201 Created`. Duplicate IDs return `409 Conflict`. Invalid IDs, blank names/departments, or non-positive/non-finite salaries return `400 Bad Request`.

### GET `/employees`

Returns all employees with `200 OK`. If there are no employees, the response is an empty JSON array: `[]`.

### GET `/employees/{id}`

Returns one employee with `200 OK`. A missing employee returns `404 Not Found`:

```json
{"message":"Employee not found"}
```

### PUT `/employees/{id}`

Updates an existing employee. The path ID is used as the stored ID, so a body ID cannot change it.

Request for employee 101:

```json
{
  "name": "Aman Singh",
  "department": "Engineering",
  "salary": 60000
}
```

Returns `200 OK`, `400 Bad Request` for invalid data, or `404 Not Found` when the path ID does not exist.

### DELETE `/employees/{id}`

Deletes an employee and returns `200 OK`:

```json
{"message":"Employee deleted successfully"}
```

A missing employee returns `404 Not Found` with `{"message":"Employee not found"}`.

## Postman collection

1. Start the application.
2. Open Postman.
3. Select **Import**.
4. Choose `EmployeeManagementAPI.postman_collection.json` from this project folder.
5. Open the imported **Employee Management API** collection.
6. Confirm the `baseUrl` variable is `http://localhost:8080`.
7. Run the requests in the listed order.

The collection contains these requests:

1. **Create Employee 1** — POST `/employees`, Aman, IT, salary 50000 — expected `201`.
2. **Create Employee 2** — POST `/employees`, Simran, HR, salary 45000 — expected `201`.
3. **Duplicate Employee ID** — POST ID 101 again — expected `409`.
4. **Invalid Employee** — POST with salary `-1000` — expected `400`.
5. **Get All Employees** — GET `/employees` — expected `200`.
6. **Get Employee 101** — GET `/employees/101` — expected `200`.
7. **Employee Not Found** — GET `/employees/999` — expected `404`.
8. **Update Employee 101** — PUT `/employees/101` — expected `200`.
9. **Update Missing Employee** — PUT `/employees/999` — expected `404`.
10. **Delete Employee 102** — DELETE `/employees/102` — expected `200`.
11. **Delete Missing Employee** — DELETE `/employees/999` — expected `404`.

## Manual testing order

1. Start the application.
2. Create employees 101 and 102.
3. Confirm GET all returns both employees in insertion order.
4. Repeat ID 101 and confirm `409 Conflict`.
5. Send a negative salary and confirm `400 Bad Request`.
6. Retrieve employee 101 and employee 999.
7. Update employee 101 and confirm the updated JSON.
8. Try updating employee 999 and confirm `404 Not Found`.
9. Delete employee 102 and confirm the success message.
10. Try deleting employee 999 and confirm `404 Not Found`.

## Moodle screenshot checklist

- [ ] IntelliJ project structure showing `pom.xml`, wrapper, Postman collection, and packages.
- [ ] Main application running on port 8080.
- [ ] POST success for employees 101 and 102.
- [ ] Duplicate ID response with status 409.
- [ ] Invalid salary response with status 400.
- [ ] GET all response.
- [ ] GET existing employee response.
- [ ] GET missing employee response with JSON message.
- [ ] PUT success response.
- [ ] PUT missing employee response.
- [ ] DELETE success response.
- [ ] DELETE missing employee response.

Do not claim endpoint success until the request is actually sent and the response is visible.
