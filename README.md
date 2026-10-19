# TimeThread - https://timethread-production.up.railway.app/

**A Secure, Real-Time Group Study Workspace and Scheduling Engine**

## Architecture Overview

TimeThread is a full-stack academic productivity platform engineered to optimize collaborative study through real-time peer-to-peer networking, algorithmic schedule synchronization, and strict data security.

Moving beyond standard high-level web frameworks, TimeThread's security layer integrates a custom-built, bare-metal Java cryptographic daemon to ensure zero-knowledge data transmission across all private communications and shared study materials.

## Core Engineering Features

* **Military-Grade Cryptography (E2EE):** Integrates a custom Java proxy daemon utilizing a hybrid cryptographic engine. Executes an RSA-2048 asymmetric key exchange over open TCP sockets to synchronize a master AES-256 session key, ensuring true End-to-End Encryption for direct messaging.
* **Real-Time Asynchronous Workspace:** Powered by STOMP/SockJS WebSockets to handle concurrent, full-duplex live chat and instant Online/Offline presence synchronization without HTTP polling.
* **Algorithmic Schedule Detection:** Utilizes dynamic overlap algorithms tied to a FullCalendar integration to instantly calculate and propose optimal shared study blocks for multi-user groups.
* **RPG Gamification Engine:** Features a custom habit-tracking state machine that calculates daily streaks and awards experience points (XP), visualized through a dynamic leveling system.
* **Production-Grade Cloud Deployment:** Hosted continuously via Railway's infrastructure, utilizing a persistent PostgreSQL database and secure cloud integrations for multipart file processing (avatars).

## Technical Stack

* **Backend:** Java 17, Spring Boot, Spring Security, WebSockets, Java Cryptography Architecture (`javax.crypto`), Raw TCP Sockets (`java.net.Socket`)
* **Frontend:** HTML5, Thymeleaf, CSS3, Bootstrap 5, Vanilla JavaScript
* **Database & Hosting:** PostgreSQL, Railway (Continuous Uptime Deployment)

## Local Installation

1. Clone the repository: `git clone [https://github.com/yourusername/TimeThread.git](https://github.com/yourusername/TimeThread.git)`
2. Open the project directory in your preferred Java IDE (IntelliJ IDEA recommended).
3. Resolve Maven dependencies via `pom.xml`.
4. Configure local environment variables for your PostgreSQL database instance in `application.properties`.
5. Execute `TimeThreadApplication.java`.
6. Access the local build at `http://localhost:8080`.

---

*Engineered by Tanishq Chauhan*
