SmartPark — AI Vehicle Parking Management System

An AI-powered, full-stack vehicle parking management system that uses real-time computer vision to detect license plates and automatically manages vehicle entry, exit, fee calculation, and unauthorized vehicle alerts.

Overview

SmartPark combines a Java Spring Boot REST API backend with a Python OpenCV + EasyOCR computer vision pipeline to create an end-to-end automated parking management solution — from live plate detection to billing.

Features


Real-time license plate detection using a live webcam feed
Automatic vehicle entry and exit logging
Dynamic parking fee calculation based on duration (Rs. 10/hour, proportional billing)
Live parking slot occupancy tracking
Unauthorized vehicle detection and alerting
On-screen bill display with one-click PDF generation
Live HTML dashboard with auto-refresh
Manual entry/exit override for testing and demo purposes


Architecture

Webcam → Python (OpenCV + EasyOCR) → REST API → Java Spring Boot
                                                       |
                                                       v
                                                  MySQL Database
                                                       |
                                                       v
                                                 HTML Dashboard

Tech Stack

Backend: Java 17, Spring Boot 3.2, Spring JDBC, MySQL 8, Maven
Computer Vision: Python, OpenCV, EasyOCR
PDF Generation: iText
Frontend: HTML, CSS, JavaScript (Fetch API)
Tools: IntelliJ IDEA, VS Code, Postman, Git

Project Structure

smartpark/
├── src/main/java/com/smartpark/
│   ├── SmartparkApplication.java
│   ├── controller/      # REST API endpoints
│   ├── service/          # Business logic, fee calculation
│   ├── repository/       # JDBC database access
│   └── model/            # Data models
├── src/main/resources/
│   ├── application.properties.example
│   └── static/index.html # Dashboard
├── python/
│   └── plate_detector.py # Live plate detection
└── README.md

API Endpoints

MethodEndpointDescriptionPOST/api/vehicle/entryRecord vehicle entry (from camera or manual)POST/api/vehicle/exitRecord vehicle exit, calculate feeGET/api/dashboard/liveCurrently parked vehiclesGET/api/dashboard/logsRecent activity logsGET/api/dashboard/slotsParking slot statusesGET/api/dashboard/alertsUnauthorized vehicle alertsGET/api/bill/pdf/{id}Download PDF bill

Setup and Installation

Prerequisites


JDK 17+
Maven
MySQL 8
Python 3.10+


Database Setup


Create the database and tables using the SQL script in /sql/schema.sql
Copy application.properties.example to application.properties and add your MySQL credentials


Run the Backend

bashmvn spring-boot:run

The dashboard will be available at http://localhost:8080/index.html

Run the Plate Detector

bashcd python
pip install opencv-python easyocr requests numpy
python plate_detector.py

How It Works


The Python script captures live video from a webcam and uses EasyOCR to detect text resembling a license plate
Detected plates are sent via HTTP POST to the Java backend
The backend checks if the vehicle is authorized, assigns a parking slot, and logs the entry
On exit, the system calculates the parking duration and fee, and generates a bill
The dashboard displays live updates of slot occupancy, parked vehicles, and alerts


Future Improvements


Cloud deployment for remote dashboard access
SMS/email notifications for unauthorized vehicles
Multi-camera support for multiple entry/exit points
Mobile app for ticket scanning


Author

Bhaventhan R
B.E. Electronics and Communication Engineering, Sathyabama Institute of Science and Technology
LinkedIn | GitHub
