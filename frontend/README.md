# DGCA Flight Logbook - Desktop Frontend (JavaFX)

A clean, minimal JavaFX desktop frontend for the DGCA Flight Logbook Spring Boot REST API.

---

## 1. Prerequisites

- **Java JDK 17+** (JDK 17 to JDK 26 compatible)
- **Maven** or the bundled Maven wrapper (`mvnw.cmd` / `mvnw`)
- **MySQL database** running at `localhost:3306` with schema `dgca_logbook` (or configured in `Code/src/main/resources/application.properties`)

---

## 2. How to Start the Backend

1. Open a terminal in the backend directory (`Code/`):
   ```bash
   cd Code
   ```
2. Start the Spring Boot application using Maven:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
   (On Linux/macOS: `./mvnw spring-boot:run`)
3. The API will start on **`http://localhost:8080`**.

---

## 3. How to Run the Frontend

1. Open a terminal in the `frontend/` directory:
   ```bash
   cd frontend
   ```
2. Compile and run the JavaFX client:
   ```powershell
   .\mvnw.cmd javafx:run
   ```
   (Or if Maven is on your system PATH: `mvn javafx:run`)

---

## 4. Features & Screens

1. **Authentication (Login & Register)**:
   - Login with username and password.
   - Toggle to reveal registration fields (`fullName`, `email`, `licenseNumber`, `role`).
   - Role-based automatic routing upon successful login:
     - `STUDENT` &rarr; Student Portal
     - `CFI` &rarr; Instructor Certification Portal
     - `ADMIN` &rarr; Administrator Fleet Portal

2. **Student Portal (`student.fxml`)**:
   - **My Logs Tab**: TableView of the student's logged flights with status filtering (`ALL`, `PENDING`, `CERTIFIED`, `REJECTED`). Edit button enabled for `PENDING` logs.
   - **Add / Edit Log Tab**: Form with date picker, departure/arrival ICAO, block off/on times with **live auto-calculated block time** (handling midnight crossings, +1440 min), aircraft dropdown, instructor dropdown, and hour breakdown. Client-side validation enforces DGCA rules:
     - `PIC + SIC + Dual == totalFlightTime`
     - `Day + Night == totalFlightTime`
     - `Instrument <= totalFlightTime`
   - **CA-39 Report Tab**: Summary of grand total flight hours, breakdown by aircraft and engine types matching DGCA CA-39 submission specifications.

3. **CFI Portal (`cfi.fxml`)**:
   - TableView showing pending logs assigned to the authenticated CFI instructor.
   - **Certify**: Locks the flight record with DGCA signature.
   - **Reject**: Prompts for a rejection reason in a dialog and records the status.

4. **Admin Portal (`admin.fxml`)**:
   - Form to register new aircraft (`registrationTail`, `typeDesignator`, `engineType`).
   - TableView of registered fleet.

---

## 5. Backend Changes Summary

As requested in Step 1 of the prompt:
1. **`AuthResponse`**:
   - Added `fullName` to `AuthResponse` constructor and getters/setters alongside `token`, `userId`, `username`, and `role`.
   - Updated `AuthService.login()` and `AuthController.register()` to include `fullName`.
2. **`UserController` & `UserRepository`**:
   - Added `findByRole(Role role)` to `UserRepository`.
   - Added `UserController` with endpoint `GET /api/users/instructors` (authenticated, any role) returning `{ id, fullName }` for CFI dropdowns.
3. **Security Refinements (Ownership Enforcement)**:
   - `FlightLogController`: Enforced principal check — students can only create, view, or update logs matching their own `userId`. `ADMIN` and `CFI` remain unrestricted.
   - `CertificationController`: Enforced that authenticated CFIs can only view and certify/reject pending logs where `instructorId` matches their own user ID. `ADMIN` is unrestricted.
   - `ReportController`: Enforced that students can only view their own CA-39 report (`studentId == principal.id`).
4. **Build & Compiler Fixes**:
   - Upgraded `maven-compiler-plugin` to `3.13.0` with `<release>17</release>` to support modern JDKs (JDK 21 - JDK 26).
   - Removed unused Lombok compiler processor that conflicted with JDK 26 (the project's entities and DTOs already use standard explicit getters/setters/builders).
