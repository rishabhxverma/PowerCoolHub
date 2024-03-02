# PowerCool Hub
By: Rishabh Verma(301460799), Trevor Mclean(301605526), Akkarachai (Aki) Wangcharoensap (301415712), Alex Menzies (301563620), Mahdi Beigahmadi (301570853)
Client Info: 

PowerCool company.
Website: www.powercool.ca
Address: 205-8475 Ontario St.
Vancouver BC V5X 3E8
Phone: 604-715-7478

# Abstract
PowerCool Hub is a browser-based application that digitalizes the logistics for the company PowerCool (HVAC deliveries and services). It provides an online calendar that the managers can edit to slot in jobs for clients. The managers can then assign the employees daily tasks. After the employee accepts the assignment, they will be provided directions from their current location to the client’s location with an integrated Google Maps API. After finishing the job, the employee will have to press a “job finished” button which notes their current location in the database. The hours the job took will then be sent to a CSV file along with the employees’ name and the current date. The managers will then be able to generate a weekly/monthly report of what jobs the employees took and what hours they worked. While this app does contain features specific to PowerCool, it will be built in such a way that any similar company can use it with some minor adjustments in the code. 
A Bit About PowerCool
Power Cool Electrical and Refrigeration Mech. LTD. They offer air conditioning and refrigeration solutions for both residential and commercial purposes, focusing on providing cost-effective options. The company is owned by three brothers, who manage customer outreach, booking service calls, and other managerial duties. They are assisted by one of the manager’s sons, Nitin Lakha, who described some issues they face in their day-to-day. 

PowerCool has eight service technicians, a tight group of friends turned coworkers, who cooperate to install, service, and diagnose problems at various sites in the lower mainland. 
A typical workday includes all employees and managers meeting at their warehouse at 8 am and dispatching groups of employees, using company vehicles, to various sites for servicing/installation. Managers will make sales calls for potential clients while on-site and may leave as they require. Once employees are finished at the site, they drive their company vehicle back to their warehouse and drive their own car home.
Problems
Currently, many of the administrative duties performed at PowerCool are outdated and will not scale well as the company continues to grow. For example, recording employee's hours is done by sending a text of the hours they worked. This process is tedious and occasionally taken advantage of, with employees inflating their work hours. PowerCool has also been booking clients using paper, leading to inaccurate client records and an inefficient way to reach out to past clients. Also, on rare occasions, the company loses track of commitments, garnering a negative review.

PowerCool is looking for a centralized system to help improve these general administrative, and operational duties. This will help the company improve the accuracy of the employee's pay, the efficiency of reaching out to past clients, and provide an overall smoother working experience for the entire company.

# Competitive Analysis
Homebase is a paid or free competing product that provides some solutions to the existing problems the client is facing. However, the competitor does not provide any solution to manage customers using a Customer Relationship Management (CRM) tool. The ability to keep track of customers' requests and satisfaction is our client’s highest priority. We believe that our software solution will provide much better value than our competitor's. Additionally, our product will integrate seamlessly with the client’s requirements given their current existing workflow.

# Solutions
Our software solution allows the client to access employees' monthly data through an authenticated dashboard. It will have two types of users, employees and service managers. To ease the access, they will have the option to scan an assigned QR code that logs them in after entering a personal PIN. 

Once logged in, the service managers will have the ability to book a customer appointment and assign employees to the request. In addition, the service managers can view weekly/monthly reports of employees' work history and hours. Lastly, the service manager will be able to view customers' requests, feedback, or any disputes through a CRM solution using our application. This page will also need to work on both PC and mobile, as the managers sometimes book clients while out on jobs. 

The employee page will have a list of their jobs for the day, each showing some info about the job and who else is assigned to it. After making a choice, they are directed to another page for the job with directions using a Google Maps API. After completing it, the employee will have to press a “job finished” button which stores their hours worked in the database and calls the Maps API to get their current location, ensuring honest and accurate work hours are tracked. 

The project requires extensive research on which existing open-source frontend and backend technologies needed for the project. Each member will be working collaboratively to build a similar UI/UX language throughout the project. We believe that developing a standard/QR code login system, a scheduling and assignment system, a CRM solution, a Google Maps API integration, and a report generator is a sufficient workload for a group of five members.

# Sample User Stories
Onboarding a new customer
As a Service Manager, when a new or existing customer wants to request a service, I should be able to let the customer fill out a web form that is then stored in the database. To test the functionality, I should be able to view customer’s web form data through the web app.
Technician clocking out
As a Technician, at the end of the shift, I should be able to clock out to accurately store the worked hours in the database. To ensure the correctness of the functionality, by the end of 9-5 work days, 8 hours should be kept and displayed on my dashboard.
Monthly Report
As a Service Manager, at the end of each month, I should be able to access employees' monthly data such as total worked hours, employees' work logs and so on through a dashboard portal. This ability will allow me to dispute any incorrect amount of employees’ worked hours, view customers' complaints/concerns, and effectively inform me of how the business is navigating.



