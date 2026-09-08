# ⏱️ TimeThread

A full-stack, real-time group study workspace and scheduling application built with Java and Spring Boot.

## Overview
TimeThread is designed to optimize academic productivity through peer-to-peer networking, synchronized group scheduling, and habit tracking. The platform features secure direct messaging, an RPG-style gamification engine, and algorithmic schedule detection to instantly find optimal study times for groups.

## Key Features
* **Real-Time Workspace:** Instant messaging and live Online/Offline presence indicators powered by STOMP/SockJS WebSockets.
* **Smart Scheduling:** FullCalendar integration with dynamic overlap detection for group study sessions.
* **RPG Gamification:** A retro, Minecraft-inspired leveling system tied to a daily habit tracking engine.
* **Secure Connections:** Encrypted peer-to-peer direct messaging for private 1-on-1 communication.
* **Dynamic Profiles:** Secure multipart file uploading for custom profile avatars.

## Tech Stack
* **Backend:** Java 17, Spring Boot, Spring Security, WebSockets
* **Frontend:** HTML5, Thymeleaf, CSS3, Bootstrap 5, JavaScript
* **Database:** H2 (Local Development) / PostgreSQL (Production)

##  Running Locally
1. Clone the repository: `git clone https://github.com/yourusername/TimeThread.git`
2. Open the project in IntelliJ IDEA or your preferred IDE.
3. Allow Maven to download the required dependencies.
4. Run `TimeThreadApplication.java`.
5. Access the application at `http://localhost:8080`.

---
*Developed by Tanishq Chauhan*