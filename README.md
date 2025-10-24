# PowerCool Hub: Operations & CRM Platform

PowerCool Hub is a full-stack web application built to digitize and streamline the daily operations for PowerCool, a commercial and residential HVAC company.

This platform replaces outdated paper-and-text-message-based systems with a centralized hub for job management, employee scheduling, customer relationship management (CRM), and automated reporting.

### Key Info

* **Live Demo:** ``
* **Project Status:** `V1.0 (Stable Release)`

---

## The Business Challenge

PowerCool, a growing HVAC and refrigeration company, was facing significant operational bottlenecks:

* **Manual Scheduling:** Job booking was done on paper, leading to scheduling conflicts and lost client records.
* **Inefficient Dispatch:** Managers and technicians met at a central warehouse every morning just to receive daily assignments.
* **Inaccurate Time-Tracking:** Employee hours were reported via text message, a tedious process prone to manual errors and "hour inflation."
* **No CRM:** Customer history and service requests were not tracked, resulting in missed opportunities and an inefficient way to manage client relationships.

## The Solution: PowerCool Hub

PowerCool Hub is a custom-built, role-based platform that solves these problems by providing a single source of truth for all company operations.

### Core Features

* **Role-Based Access Control:**
    * **Manager Portal:** Full-featured dashboard to create/update jobs, assign technicians, manage client data, and view reports.
    * **Employee (Technician) Portal:** Mobile-friendly view to see assigned daily jobs, access client information, and manage job status.

* **Dynamic Job Calendar:**
    * A full-service calendar allows managers to schedule jobs and assign multiple technicians.
    * Employees receive real-time updates on their personal job list.

* **Integrated CRM:**
    * Managers can create, retrieve, update, and delete customer records.
    * All jobs are linked to a customer, building a complete service history.

* **Smart Employee Tracking & Routing:**
    * Employees can "clock in" and "clock out" of specific jobs.
    * Integrates with the **Google Maps API** to provide technicians with one-click directions to the job site.
    * On "clock out," the system logs the employee's location to verify they are at the job site, ensuring accurate hour-tracking.

* **Automated Reporting:**
    * Managers can generate weekly or monthly reports of all completed jobs.
    * Data includes technician hours, job details, and client info, which can be exported to CSV for payroll and analysis.

---

## Tech Stack

* **Backend:** **Java 17** & **Spring Boot 3.2.3**
    * **Spring Web:** For building the RESTful APIs and web controllers.
    * **Spring Data JPA:** For object-relational mapping (ORM) to interact with the database.
* **Frontend (View):** **Thymeleaf**
    * A modern server-side Java template engine used to render dynamic HTML.
* **Database:**
    * **PostgreSQL:** The primary production-grade relational database.
    * **H2 Database:** An in-memory database used for testing and local development.
* **Security:** **Spring Security (Crypto)**
    * Used for securely hashing and verifying user passwords (e.g., using BCrypt).
* **Data Validation:** **Jakarta Validation (Hibernate Validator)**
    * Used for server-side validation of data models and request bodies (e.g., `@NotBlank`, `@Email`).
* **Testing:** **JUnit 4** & **Spring Boot Test**
    * For unit and integration testing of the application.
* **Utilities:**
    * **Apache Commons CSV:** For generating and parsing CSV files (used for reports).
    * **Spring Mail:** For integrating email-sending capabilities.
* **Build Tool:** **Apache Maven**
    * For managing project dependencies and building the application.

**Example:**

* **Backend:** Java 17, Spring Boot (Spring Web, Spring Security, Spring Data JPA)
* **Frontend:** Thymeleaf (Server-Side Rendered), HTML5, CSS3, JavaScript (ES6+)
* **Database:** PostgreSQL (or MySQL, H2 for testing)
* **APIs:** Google Maps API (for routing and geolocation)
* **Testing:** JUnit 5, Mockito
* **Build:** Apache Maven

---

## Running the Project Locally

This project is fully containerized using Docker and Docker Compose. This is the simplest way to run the application and its database with a single command.

### Prerequisites

* [Docker](https://www.docker.com/products/docker-desktop/) must be installed and running on your machine.

### 1. Create Environment File

Before you can run the app, you must provide your secret keys.

1.  In the root folder of the project, create a new file named `.env`
    * (This file is automatically ignored by `.gitignore` and should **never** be committed to GitHub.)

2.  Copy and paste the following into the `.env` file, replacing the `...` with your actual secret keys:

    ```bash
    # A secure password for the PostgreSQL database
    DATABASE_PASSWORD=...

    # The Google App Password for the powercool205@gmail.com account
    powercool205_PASSWORD=...

    # Your Google Cloud API Key (for Google Maps)
    GOOGLE_API_KEY=...
    ```

### 2. Build and Run

With your `.env` file created, open a terminal in the project's root directory and run a single command:

```bash
# This will build the Java app's image and start both the app and database containers
docker-compose up --build

**Example:**

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/rishabhxverma/PowerCoolHub.git](https://github.com/rishabhxverma/PowerCoolHub.git)
    cd PowerCoolHub
    ```

2.  **Set up Environment Variables:**
    * Create an `application.properties` file in `src/main/resources/`.
    * Add the following properties (you can use your own local DB):
        ```properties
        # Example for PostgreSQL
        spring.datasource.url=jdbc:postgresql://localhost:5432/powercool
        spring.datasource.username=postgres
        spring.datasource.password=your_db_password

        # Google Maps API Key
        google.maps.api.key=YOUR_GOOGLE_MAPS_API_KEY
        ```

3.  **Build and Run the Application:**
    ```bash
    # Using Maven
    mvn spring-boot:run
    ```

4.  **Access the application:**
    Open your browser and navigate to `http://localhost:8080`.

---

## Acknowledgements

This project was developed as part of a collaborative effort by:

* Rishabh Verma
* Trevor Mclean
* Akkarachai (Aki) Wangcharoensap
* Alex Menzies
* Mahdi Beigahmadi
