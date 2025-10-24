# PowerCool Hub: Operations & CRM Platform

PowerCool Hub is a full-stack web application built to digitize and streamline the daily operations for PowerCool, a commercial and residential HVAC company.

This platform replaces outdated paper-and-text-message-based systems with a centralized hub for job management, employee scheduling, customer relationship management (CRM), and automated reporting.

### Key Info

* **Live Demo:** ``
* **Project Status:** ``

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

``
``

**Example:**

* **Backend:** Java 17, Spring Boot (Spring Web, Spring Security, Spring Data JPA)
* **Frontend:** Thymeleaf (Server-Side Rendered), HTML5, CSS3, JavaScript (ES6+)
* **Database:** PostgreSQL (or MySQL, H2 for testing)
* **APIs:** Google Maps API (for routing and geolocation)
* **Testing:** JUnit 5, Mockito
* **Build:** Apache Maven

---

## Running the Project Locally

``
``

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
