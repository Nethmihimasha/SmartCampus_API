# SmartCampusAPI

JAX-RS coursework implementation for `5COSC022W` using only module-taught technologies:
- Java EE / JAX-RS (`javax.ws.rs`)
- Jersey 2.32
- In-memory collections (`ConcurrentHashMap`, `ArrayList`)
- Apache Tomcat deployment (NetBeans-compatible WAR)

## NetBeans + Tomcat Setup
1. Open **NetBeans**.
2. Add **Apache Tomcat 9.x** in the Services panel.
3. Open `SmartCampusAPI` as a Maven web project.
4. Right-click project -> **Clean and Build**.
5. Right-click project -> **Run** (server: Apache Tomcat).

## Base URL
`http://localhost:8080/SmartCampusAPI/api/v1`

## Core Endpoints
- `GET /` discovery metadata
- `GET/POST /rooms`
- `GET/DELETE /rooms/{roomId}`
- `GET/POST /sensors`
- `GET /sensors?type=CO2`
- `GET /sensors/{sensorId}/readings`
- `POST /sensors/{sensorId}/readings`

## Notes
- Uses `javax` imports (no `jakarta`).
- Includes JAX-RS request/response logging filter.
- Includes specific exception mappers (409, 422, 403) and global 500 mapper.
