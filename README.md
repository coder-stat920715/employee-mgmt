# Enterprise Employee & Designation Management Application

[![Java 11](https://img.shields.io/badge/Java-11-blue.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-2.7.18-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![GWT](https://img.shields.io/badge/GWT-2.10.0-orange.svg)](http://www.gwtproject.org/)
[![Database](https://img.shields.io/badge/Database-Oracle%2021c-red.svg)](https://www.oracle.com/database/)
[![Build](https://img.shields.io/badge/Build-Maven-blueviolet.svg)](https://maven.apache.org/)

An enterprise-grade Employee & Designation Management system built using **Java 11**, **Spring Boot 2.7.18**, **Google Web Toolkit (GWT 2.10.0)**, and **Oracle Database**. This application showcases seamless integration between a Java-based Single Page Application (SPA) frontend compiled to highly optimized JavaScript and a robust Spring Boot backend operating with Spring Data JPA and HikariCP connection pooling.

**GitHub Repository:** [https://github.com/coder-stat920715/employee-mgmt](https://github.com/coder-stat920715/employee-mgmt)

---

## Table of Contents
1. [Executive Overview & Business Value](#1-executive-overview--business-value)
2. [Deep Dive: Google Web Toolkit (GWT) Concept & Enterprise Realities](#2-deep-dive-google-web-toolkit-gwt-concept--enterprise-realities)
   - [Why GWT Was Introduced (Historical Context & Problem Statement)](#why-gwt-was-introduced)
   - [How GWT Works (Compilation Pipeline & RPC Protocol)](#how-gwt-works)
   - [Real-Life Enterprise Use Cases of GWT](#real-life-enterprise-use-cases-of-gwt)
   - [Modern Relevance & Legacy Ecosystem Maintenance](#modern-relevance--legacy-ecosystem-maintenance)
3. [End-to-End Architectural Workflow](#3-end-to-end-architectural-workflow)
4. [System Architecture: The C4 Model (Four C's of Abstraction)](#4-system-architecture-the-c4-model-four-cs-of-abstraction)
   - [Level 1: System Context Diagram](#level-1-system-context-diagram)
   - [Level 2: Container Diagram](#level-2-container-diagram)
   - [Level 3: Component Diagram](#level-3-component-diagram)
   - [Level 4: Code & Class Execution Architecture](#level-4-code--class-execution-architecture)
5. [Deep-Dive Subsystem Specifications (6-Parameter Framework)](#5-deep-dive-subsystem-specifications-6-parameter-framework)
   - [Subsystem 1: GWT Client SPA (Browser Container)](#subsystem-1-gwt-client-spa-browser-container)
   - [Subsystem 2: GWT-RPC Servlet & Spring Integration Layer](#subsystem-2-gwt-rpc-servlet--spring-integration-layer)
   - [Subsystem 3: Spring Data JPA Persistence Layer](#subsystem-3-spring-data-jpa-persistence-layer)
   - [Subsystem 4: Oracle Database Subsystem](#subsystem-4-oracle-database-subsystem)
6. [Database Schema & Sequence Specifications](#6-database-schema--sequence-specifications)
7. [Project Structure & File Layout](#7-project-structure--file-layout)
8. [Setup, Build, and Deployment Guide](#8-setup-build-and-deployment-guide)

---

## 1. Executive Overview & Business Value

The **Employee & Designation Management Application** provides HR administrators and corporate personnel with a centralized, real-time portal for creating, updating, viewing, and deleting employee designations and salary metrics. 

### Key Capabilities
- **End-to-End Single-Language Stack:** Built completely in Java from client UI to database access layers, eliminating context-switching between JavaScript frameworks and backend Java code.
- **Real-Time Data Synchronization:** Automatic background polling every $15\text{ seconds}$ ensures that updates made by concurrent HR administrators are immediately visible without hard page reloads.
- **Embedded RPC Servlet Spring Integration:** Custom Spring `ServletRegistrationBean` mapping bridges GWT’s asynchronous RPC servlet framework into Spring Boot's dependency injection container, enabling direct `@Autowired` repository injection inside RPC endpoints.
- **Class-Path RPC Serialization Policy Resolver:** Custom GWT `SerializationPolicyLoader` override resolves `.gwt.rpc` policies directly from executable Spring Boot JAR classpaths (`/static/...`).

---

## 2. Deep Dive: Google Web Toolkit (GWT) Concept & Enterprise Realities

### Why GWT Was Introduced
In the mid-2000s, building rich, asynchronous web applications (AJAX) introduced severe engineering challenges:
1. **Cross-Browser Incompatibilities:** Browsers (Internet Explorer 6/7, Firefox, Safari) executed JavaScript, rendered DOM elements, and handled XMLHttpRequests with vast inconsistencies.
2. **Lack of Strong Typing & Tooling in JavaScript:** Standard JavaScript lacked static typing, interfaces, compile-time error checking, refactoring tools, and sophisticated IDE integration.
3. **Monolithic Engineering Teams:** Large enterprise teams with deep Java expertise found it inefficient to maintain separate JavaScript codebases without static analysis, unit testing, or shared DTO models.

Google introduced GWT in 2006 to solve these problems by allowing developers to write web UI code in pure, strongly-typed **Java**, which the GWT compiler then transpiled into optimized, cross-browser compatible **JavaScript** and **HTML**.

### How GWT Works

```
+-----------------------------------------------------------------------+
|                            JAVA SOURCE CODE                           |
| src/main/java/com/acme/employee/client/EmployeeApp.java               |
+-----------------------------------------------------------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                             GWT COMPILER                              |
| 1. AST Construction & Java-to-JS Transpilation                        |
| 2. Permutation Matrix Generation (Browser-specific variants)          |
| 3. Dead Code Elimination & Monification/Obfuscation                   |
+-----------------------------------------------------------------------+
                                   |
                                   v
+-----------------------------------------------------------------------+
|                          COMPILED ARTIFACTS                           |
| target/classes/static/employeeapp/                                    |
|   ├── employeeapp.nocache.js  (Bootstrap script)                      |
|   └── [MD5_HASH].cache.html    (Browser permutation code)             |
+-----------------------------------------------------------------------+
```

1. **Java-to-JavaScript Transpilation:** GWT parses Java client source code, validates that only GWT-supported JRE subset classes are used, and outputs static JavaScript assets.
2. **Permutation Compilation:** GWT generates separate JavaScript bundles ("permutations") optimized for specific browser user agents and language locales. The browser downloads *only* the specific JavaScript bundle it needs.
3. **Dead-Code Elimination:** The GWT compiler performs tree-shaking across the entire client dependency graph. Any method or class not directly called is purged, producing ultra-compact JavaScript bundles.
4. **Type-Safe RPC Protocol:** GWT-RPC uses serialized HTTP POST requests between client and server interfaces. Shared Data Transfer Objects (DTOs) implement `java.io.Serializable` or GWT `IsSerializable`, maintaining static type contracts across the wire.

### Real-Life Enterprise Use Cases of GWT
GWT remains heavily deployed in enterprise environments characterized by high complex UI state management and strict Java standards:
- **Google Internal Systems:** Originally used to build **Google AdWords**, **Google AdSense**, **Google Groups**, and **Google Flights**.
- **Financial & Banking Dashboards:** Used in risk analysis, portfolio management, and algorithmic trading platforms (e.g., Charles Schwab, E*TRADE) where complex tabular data grids require strong typing and zero DOM leaks.
- **Telecom & Infrastructure Management:** Enterprise resource planning (ERP) platforms (e.g., SAP NetWeaver UI extensions, Cisco network configuration portals) where hundreds of form fields and complex validation rules are shared across client and server.
- **Healthcare & EHR Systems:** Patient management dashboards where strict data structure schemas must be maintained between backends and frontends.

### Modern Relevance & Legacy Ecosystem Maintenance
While modern web projects often select React, Angular, or Vue with TypeScript, GWT (now maintained by the open-source community under **org.gwtproject**) remains critical for enterprise applications. It offers:
- Zero JavaScript build tooling overhead (no `npm`, `webpack`, or `vite` dependency drift).
- Single repository, single-language project architecture (100% Java).
- High performance for large data-table views (`DataGrid`, `CellTable`) with virtualized rendering.

---

## 3. End-to-End Architectural Workflow

The diagram and step-by-step sequence below detail how a user interaction propagates through the system from the browser down to the database and back.

```
[ Browser User ] ──(1. Click 'Submit')──> [ GWT Client SPA ]
                                                  │
                                          (2. Async RPC Call)
                                                  │
                                                  v
[ Oracle DB ] <──(4. SQL Insert/Update)── [ Spring Boot RPC Servlet ]
     │                                            │
     └──(5. Return Saved Row)─────────────────────┘
                                                  │
                                          (6. Return DTO)
                                                  │
                                                  v
[ Browser UI ] <──(7. Update DataGrid)─── [ GWT Async Callback ]
```

### Detailed Execution Steps

#### Step 1: User Form Interaction (Client UI)
- The user fills in the **Name** and **Salary per hour** fields in `EmployeeApp.java` and clicks **Submit**.
- Client-side validation verifies that:
  - `Name` is non-empty and length $\le 100\text{ chars}$.
  - `Salary per hour` parses to a valid positive floating-point number $\ge 0.00$.

#### Step 2: Asynchronous RPC Dispatch
- `EmployeeApp.java` instantiates an `EmployeeDTO` object containing `(id, name, salaryPerHour)`.
- The client invokes `service.save(dto, new AsyncCallback<EmployeeDTO>() {...})`.
- The GWT RPC infrastructure serializes the `EmployeeDTO` into GWT's string-based RPC wire format and submits an HTTP POST request to `/employeeapp/employeeService`.

#### Step 3: Spring Servlet Dispatch & Security Context
- Spring Boot’s `ServletRegistrationBean` routes `/employeeapp/employeeService` to the `EmployeeServiceImpl` bean.
- `EmployeeServiceImpl` inherits from `RemoteServiceServlet`. It invokes its overridden `doGetSerializationPolicy()` to load the `.gwt.rpc` file from the JAR's `classpath:/static/employeeapp/` folder.
- GWT deserializes the incoming payload into a server-side `EmployeeDTO`.

#### Step 4: Business Logic & JPA Persistence
- `EmployeeServiceImpl` performs server-side input validation.
- If `dto.getId()` is `null` (new entry), it fetches the next ID from the Oracle sequence:
  $$\text{ID} = \text{DESIGNATION\_SEQ.NEXTVAL}$$
- The servlet converts `double salary` to a `BigDecimal` with half-up rounding ($2$ decimal places).
- `EmployeeRepository.save(new Employee(id, name, salary))` issues an `INSERT` or `UPDATE` query via Hibernate/JPA over a HikariCP database connection.

#### Step 5: Database Commit & DTO Response
- Oracle DB executes the statement and returns the persisted record.
- `EmployeeServiceImpl` converts the `Employee` entity back into an `EmployeeDTO` and returns it.
- GWT RPC serializes the response back over HTTP $200\text{ OK}$.

#### Step 6: Client UI Update
- The `AsyncCallback.onSuccess(EmployeeDTO saved)` method fires in the browser.
- The UI status label displays a success notification (`"Saved/Updated [Name]."`).
- `loadData(false)` is invoked to update the `ListDataProvider<EmployeeDTO>`, causing the `DataGrid` to automatically refresh its rows.

---

## 4. System Architecture: The C4 Model (Four C's of Abstraction)

### Level 1: System Context Diagram
The System Context diagram shows the high-level boundary of the Employee Management System and its interaction with HR Administrators and external enterprise systems.

```
+-------------------------------------------------------------------+
|                        HR Administrator                           |
|       (Manages employee designations, compensation metrics)       |
+-------------------------------------------------------------------+
                                  │
                                  │ Uses HTTPS / Web Browser
                                  v
+-------------------------------------------------------------------+
|             EMPLOYEE MANAGEMENT SYSTEM (GWT + Spring Boot)        |
|  - Provides real-time designation creation, modification, deletion |
|  - Persists data to enterprise Oracle DB                           |
+-------------------------------------------------------------------+
                                  │
                                  │ JDBC Thin Protocol (TCP 1521)
                                  v
+-------------------------------------------------------------------+
|                      ORACLE DATABASE 19c/21c                      |
|           (Stores DESIGNATIONS schema, sequences, tables)         |
+-------------------------------------------------------------------+
```

### Level 2: Container Diagram
The Container diagram explodes the system into its high-level runnable units.

```
+---------------------------------------------------------------------------------------+
|                                    WEB BROWSER                                        |
|  +---------------------------------------------------------------------------------+  |
|  | Single Page Application (SPA) Container                                         |  |
|  | - Compiled GWT JavaScript Engine (employeeapp.nocache.js)                        |  |
|  | - HTML5 DOM / CSS3 (styles.css)                                               |  |
|  +---------------------------------------------------------------------------------+  |
+---------------------------------------------------------------------------------------+
                                           │
                                           │ GWT-RPC over HTTP (POST)
                                           │ Path: /employeeapp/employeeService
                                           v
+---------------------------------------------------------------------------------------+
|                                SPRING BOOT APP SERVER                                 |
|  +---------------------------------------------------------------------------------+  |
|  | Embedded Tomcat Container (Port 8080)                                           |  |
|  |                                                                                 |  |
|  |  +---------------------------------------------------------------------------+  |  |
|  |  | GWT-RPC Servlet Bridge (GwtRpcConfig & EmployeeServiceImpl)               |  |  |
|  |  +---------------------------------------------------------------------------+  |  |
|  |                                      │                                          |  |
|  |                                      v                                          |  |
|  |  +---------------------------------------------------------------------------+  |  |
|  |  | Spring Data JPA / Hibernate Layer (EmployeeRepository)                    |  |  |
|  |  +---------------------------------------------------------------------------+  |  |
|  |                                      │                                          |  |
|  |                                      v                                          |  |
|  |  +---------------------------------------------------------------------------+  |  |
|  |  | HikariCP Connection Pool (EmployeeHikariPool, max-size: 10)                 |  |  |
|  |  +---------------------------------------------------------------------------+  |  |
|  +---------------------------------------------------------------------------------+  |
+---------------------------------------------------------------------------------------+
                                           │
                                           │ SQL / Oracle OJDBC8 Thin Driver
                                           v
+---------------------------------------------------------------------------------------+
|                                ORACLE DATABASE SERVER                                 |
|  +---------------------------------------------------------------------------------+  |
|  | Oracle Database Instance (XE / Enterprise Edition)                              |  |
|  |  - Table: DESIGNATIONS                                                          |  |
|  |  - Sequence: DESIGNATION_SEQ                                                    |  |
|  +---------------------------------------------------------------------------------+  |
+---------------------------------------------------------------------------------------+
```

### Level 3: Component Diagram
The Component diagram illustrates the inner structural components of the Spring Boot application container.

```
+---------------------------------------------------------------------------------------+
|                             SPRING BOOT APPLICATION CONTAINER                         |
|                                                                                       |
|  +-------------------------------------+     +-------------------------------------+  |
|  |          GwtRpcConfig               |     |       EmployeeServiceImpl           |  |
|  |  Configures ServletRegistration     |---> |  - Implements GWT RemoteService     |  |
|  |  bean mapped to RPC URL path        |     |  - Handles RPC serialization        |  |
|  +-------------------------------------+     +-------------------------------------+  |
|                                                                 │                     |
|                                                                 │ @Autowired          |
|                                                                 v                     |
|                                              +-------------------------------------+  |
|                                              |         EmployeeRepository          |  |
|                                              |  - Spring Data JPA Repository       |  |
|                                              |  - Custom Native Query nextId()     |  |
|                                              +-------------------------------------+  |
|                                                                 │                     |
|                                                                 │ Hibernate ORM       |
|                                                                 v                     |
|                                              +-------------------------------------+  |
|                                              |         Employee Domain Entity      |  |
|                                              |  - @Entity mapping for DESIGNATIONS |  |
|                                              +-------------------------------------+  |
+---------------------------------------------------------------------------------------+
```

### Level 4: Code & Class Execution Architecture

```
com.acme.employee
├── client/                     # Transpiled to JS by GWT Compiler
│   └── EmployeeApp.java        # EntryPoint, DataGrid UI, Event Handlers, Async Callback
├── shared/                     # Shared Java classes (Client & Server)
│   ├── EmployeeDTO.java        # Serializable Data Transfer Object
│   ├── EmployeeService.java    # RemoteService RPC interface contract
│   ├── EmployeeServiceAsync.java # Client-side Async RPC counterpart interface
│   └── ServiceException.java   # Custom application exception
├── server/                     # Server-Only Spring/GWT Execution
│   └── EmployeeServiceImpl.java# Spring-managed RemoteServiceServlet implementation
├── domain/                     # JPA Domain Entities
│   └── Employee.java           # JPA Entity (@Table "DESIGNATIONS")
├── repository/                 # Data Access Layer
│   └── EmployeeRepository.java # Spring Data JPA repository interface
└── config/                     # Infrastructure Configuration
    └── GwtRpcConfig.java       # ServletRegistrationBean bean setup for GWT RPC
```

---

## 5. Deep-Dive Subsystem Specifications (6-Parameter Framework)

### Subsystem 1: GWT Client SPA (Browser Container)

#### 1. Primary Purpose & Key Responsibilities
- Renders the interactive Single Page Application within the user's browser without requiring page refreshes.
- Manages client-side state, form inputs, dynamic CSS formatting, input validation, and row-level edit/delete events.
- Executes background polling timers every $15\text{ seconds}$ to fetch system updates asynchronously.

#### 2. Measuring Success (KPIs)
- **First Contentful Paint (FCP):** $< 1.2\text{ seconds}$.
- **DOM Rendering Performance:** Maintains $60\text{ FPS}$ during table scrolling and row selection.
- **Client Heap Consumption:** $< 40\text{ MB}$ footprint in V8/Gecko browser engines.

#### 3. Data Flow, Response Times & Failure Impact
- **Data Flow:** User Input Event $\rightarrow$ DTO Instantiation $\rightarrow$ Async RPC Call $\rightarrow$ UI State Mutation.
- **Response Time Target:** $< 100\text{ ms}$ UI update latency post-RPC response.
- **Failure Impact:** If the browser loses network connectivity, status messages inform the user (`"Cannot reach the server. Check your connection."`). The client degrades gracefully without losing typed data in active inputs.

#### 4. Technologies, Advantages & Limitations
- **Technologies:** Java 11 (GWT Subset), GWT `DataGrid`, `ListDataProvider`, `SafeHtmlBuilder`, HTML5, CSS3.
- **Advantages:** Strongly-typed UI code, zero JavaScript dependency creep, compiled JavaScript optimization (tree-shaking).
- **Limitations:** Requires full GWT Maven compilation step during builds; browser legacy DOM extensions require CSS overrides (`styles.css`).

#### 5. Scalability & Performance Targets
- Optimized for up to $10,000$ client-side rows managed via `ListDataProvider`.
- DataGrid row height styled at $44\text{px}$ via CSS to avoid recalculating table layouts via Java DOM manipulation.

#### 6. Security, Reliability & Compliance
- Automatic HTML escaping via GWT `SafeHtmlBuilder` prevents Cross-Site Scripting (XSS) attacks.
- Input validation sanitizes strings prior to transmitting payload over the wire.

---

### Subsystem 2: GWT-RPC Servlet & Spring Integration Layer

#### 1. Primary Purpose & Key Responsibilities
- Receives HTTP POST GWT-RPC requests at `/employeeapp/employeeService`.
- Integrates GWT's `RemoteServiceServlet` lifecycle into Spring Boot's Application Context.
- Resolves GWT strong-name serialization policy files (`.gwt.rpc`) from Spring Boot executable JAR classpaths.

#### 2. Measuring Success (KPIs)
- **RPC Deserialization Overhead:** $< 5\text{ ms}$ per payload.
- **Error Resolution Rate:** $100\%$ capture of business exceptions mapped to `ServiceException`.

#### 3. Data Flow, Response Times & Failure Impact
- **Data Flow:** HTTP POST Stream $\rightarrow$ `doGetSerializationPolicy` $\rightarrow$ RPC Deserialization $\rightarrow$ Spring Service Execution $\rightarrow$ RPC Serialization $\rightarrow$ HTTP $200\text{ OK}$.
- **Response Time Target:** $< 50\text{ ms}$ average processing time.
- **Failure Impact:** Uncaught server runtime errors return HTTP $500$ or `StatusCodeException`, caught safely by client `AsyncCallback.onFailure()`.

#### 4. Technologies, Advantages & Limitations
- **Technologies:** Spring Web, `ServletRegistrationBean`, GWT `RemoteServiceServlet`, `SerializationPolicyLoader`.
- **Advantages:** Enables full Spring `@Autowired` dependency injection within GWT RPC servlets; solves executable JAR classpath resource path mismatches.
- **Limitations:** Servlet instance is a singleton managed by Spring; must remain stateless.

#### 5. Scalability & Performance Targets
- Concurrent throughput capacity: $> 2,000\text{ requests/sec}$ per application instance.
- Thread management handled via Spring Boot's embedded Tomcat executor pool (default max $200$ threads).

#### 6. Security, Reliability & Compliance
- Strongly-typed GWT RPC whitelist policy enforcement prevents arbitrary object deserialization exploits.
- Custom exception translation prevents raw internal server stack traces from leaking to the browser.

---

### Subsystem 3: Spring Data JPA Persistence Layer

#### 1. Primary Purpose & Key Responsibilities
- Maps domain objects (`Employee.java`) to relational Oracle tables (`DESIGNATIONS`).
- Executes transactional CRUD operations and native sequence queries (`DESIGNATION_SEQ.NEXTVAL`).
- Handles data validation and numeric precision scaling ($2$ decimal places for currency).

#### 2. Measuring Success (KPIs)
- **Database Query Latency:** $< 10\text{ ms}$ for primary key lookups and queries.
- **ORM Overhead:** $< 3\text{ ms}$ conversion overhead between SQL result sets and JPA entities.

#### 3. Data Flow, Response Times & Failure Impact
- **Data Flow:** RPC Servlet $\rightarrow$ `EmployeeRepository` $\rightarrow$ Hibernate Dialect $\rightarrow$ HikariCP JDBC Connection $\rightarrow$ Oracle SQL execution.
- **Response Time Target:** $< 15\text{ ms}$ complete repository method lifecycle.
- **Failure Impact:** Database constraints or connectivity failures trigger Spring `DataAccessException`, translated by `EmployeeServiceImpl` into user-friendly `ServiceException` messages.

#### 4. Technologies, Advantages & Limitations
- **Technologies:** Spring Data JPA, Hibernate 5.6, Java Persistence API (JPA) 2.2, Oracle Dialect (`Oracle12cDialect`).
- **Advantages:** Automated query generation, built-in transaction bounds, automated SQL injection prevention via parameterized queries.
- **Limitations:** JPA sequence generation overhead avoided by implementing custom native `nextId()` database sequence calls.

#### 5. Scalability & Performance Targets
- Scales horizontally across multiple application instances behind a load balancer.
- Read operations sorted by ID (`findAllByOrderByIdAsc`) leverage indexed primary key order.

#### 6. Security, Reliability & Compliance
- Parameterized SQL queries completely prevent SQL Injection (SQLi).
- Strict database dialect configuration prevents cross-schema mutation risks.

---

### Subsystem 4: Oracle Database Subsystem

#### 1. Primary Purpose & Key Responsibilities
- Provides durable, ACID-compliant relational storage for enterprise designation and compensation data.
- Manages sequential primary key generation via Oracle database sequences (`DESIGNATION_SEQ`).

#### 2. Measuring Success (KPIs)
- **Database Uptime:** $99.99\%$ availability SLA.
- **Transaction Commit Time:** $< 5\text{ ms}$.

#### 3. Data Flow, Response Times & Failure Impact
- **Data Flow:** Incoming Oracle Thin OJDBC8 Request $\rightarrow$ Transaction Log (Redo Log) $\rightarrow$ Table Block Write $\rightarrow$ Commit Ack.
- **Response Time Target:** $< 5\text{ ms}$ per read/write transaction.
- **Failure Impact:** DB connection drop triggers HikariCP auto-reconnection retries; active requests fail fast without hanging application threads indefinitely.

#### 4. Technologies, Advantages & Limitations
- **Technologies:** Oracle Database 19c/21c, `ojdbc8` driver, HikariCP pool (`EmployeeHikariPool`).
- **Advantages:** High performance, enterprise reliability, strict transactional concurrency controls.
- **Limitations:** Requires active Oracle DB instance listening on TCP port $1521$.

#### 5. Scalability & Performance Targets
- **HikariCP Configuration:**
  - `maximum-pool-size: 10`
  - `minimum-idle: 2`
  - `connection-timeout: 30000ms` ($30\text{s}$)
  - `max-lifetime: 1800000ms` ($30\text{ mins}$)

#### 6. Security, Reliability & Compliance
- Protected by database authentication credentials stored in `application.yml`.
- `connection-test-query: SELECT 1 FROM DUAL` validates pool connections prior to serving application threads.

---

## 6. Database Schema & Sequence Specifications

Execute the following DDL script in your Oracle database environment (`hr_app` schema) to set up required objects:

```sql
-- 1. Create Sequence for Designation/Employee Primary Keys
CREATE SEQUENCE DESIGNATION_SEQ
    START WITH 101
    INCREMENT BY 1
    NOCACHE
    NOCYCLE;

-- 2. Create Designations Table
CREATE TABLE DESIGNATIONS (
    ID NUMBER(10) NOT NULL,
    NAME VARCHAR2(100) NOT NULL,
    SALARY_PER_HOUR NUMBER(10, 2) NOT NULL,
    CONSTRAINT PK_DESIGNATIONS PRIMARY KEY (ID)
);

-- 3. Insert Initial Benchmark Data
INSERT INTO DESIGNATIONS (ID, NAME, SALARY_PER_HOUR) 
VALUES (DESIGNATION_SEQ.NEXTVAL, 'Software Engineer', 45.50);

INSERT INTO DESIGNATIONS (ID, NAME, SALARY_PER_HOUR) 
VALUES (DESIGNATION_SEQ.NEXTVAL, 'Senior Developer', 65.00);

INSERT INTO DESIGNATIONS (ID, NAME, SALARY_PER_HOUR) 
VALUES (DESIGNATION_SEQ.NEXTVAL, 'Lead Architect', 85.25);

COMMIT;
```

---

## 7. Project Structure & File Layout

```
employee-mgmt/
├── pom.xml                                 # Maven dependencies and GWT plugin build config
├── README.md                               # Enterprise System Documentation
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── acme/
        │           └── employee/
        │               ├── EmployeeApp.gwt.xml # GWT Module XML specification
        │               ├── client/          # GWT Client Source Code (Transpiled to JS)
        │               │   └── EmployeeApp.java
        │               ├── config/          # Spring Infrastructure Configuration
        │               │   └── GwtRpcConfig.java
        │               ├── domain/          # JPA Domain Entities
        │               │   └── Employee.java
        │               ├── repository/      # Spring Data JPA Repositories
        │               │   └── EmployeeRepository.java
        │               ├── server/          # Server Servlets & RPC Services
        │               │   └── EmployeeServiceImpl.java
        │               └── shared/          # Shared DTOs and Remote Interfaces
        │                   ├── EmployeeDTO.java
        │                   ├── EmployeeService.java
        │                   ├── EmployeeServiceAsync.java
        │                   └── ServiceException.java
        └── resources/
            ├── application.yml             # Spring Boot & Database Configuration
            └── static/                     # Static Web Assets & GWT Output
                ├── index.html              # HTML Bootstrap page
                └── styles.css              # Custom UI & GWT DataGrid Stylesheet
```

---

## 8. Setup, Build, and Deployment Guide

### Prerequisites
- **JDK:** Java 11 Development Kit installed and configured on PATH.
- **Maven:** Apache Maven 3.8.x or higher.
- **Database:** Oracle Database 11g/12c/19c/21c (XE or Enterprise Edition) running on `localhost:1521` with SID/Service `XE`.

### Step 1: Database Configuration
Update `src/main/resources/application.yml` with your Oracle database credentials:

```yaml
spring:
  datasource:
    url: jdbc:oracle:thin:@localhost:1521:XE
    username: hr_app
    password: your_secure_password
```

### Step 2: Build the Application
Run the Maven build command from the root folder. This compiles Java classes, executes the GWT plugin to generate client JavaScript into `target/classes/static/employeeapp`, and packages the executable Spring Boot JAR:

```bash
mvn clean install
```

### Step 3: Run the Application
Start the Spring Boot application using Maven or Java CLI:

```bash
# Option A: Run via Spring Boot Maven Plugin
mvn spring-boot:run

# Option B: Run packaged JAR file
java -jar target/employee-mgmt-1.0.0.jar
```

### Step 4: Access the Application
Open your web browser and navigate to:
```
http://localhost:8080/index.html
```

---
*Maintained by standard enterprise Java architecture guidelines.*