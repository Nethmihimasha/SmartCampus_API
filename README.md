# SmartCampusAPI

JAX-RS coursework implementation for `5COSC022W` using only module-allowed technologies:
- Java EE / JAX-RS (`javax.ws.rs`)
- Jersey 2.32
- In-memory collections only (`ConcurrentHashMap`, `ArrayList`)
- Apache Tomcat deployment (WAR)

This project implements a RESTful "Smart Campus" API for room management, sensor management, and historical readings, including nested resources, filtered retrieval, exception mapping, and request/response logging.

## API Overview

### Data Models
- `Room`: `id`, `name`, `capacity`, `sensorIds`
- `Sensor`: `id`, `type`, `status`, `currentValue`, `roomId`
- `SensorReading`: `id`, `timestamp`, `value`

### Base URLs
- App root: `http://localhost:8080/SmartCampusAPI`
- Versioned API root: `http://localhost:8080/SmartCampusAPI/api/v1`

### Core Endpoints
- `GET /api/v1/` discovery metadata
- `GET /api/v1/rooms`
- `POST /api/v1/rooms`
- `GET /api/v1/rooms/{roomId}`
- `DELETE /api/v1/rooms/{roomId}`
- `GET /api/v1/sensors`
- `POST /api/v1/sensors`
- `GET /api/v1/sensors/{sensorId}`
- `GET /api/v1/sensors?type=CO2`
- `GET /api/v1/sensors/{sensorId}/readings`
- `POST /api/v1/sensors/{sensorId}/readings`

## Build and Run Instructions

### Option A: NetBeans + Tomcat (as used in coursework labs)
1. Open **NetBeans**.
2. Register **Apache Tomcat 9.x** in Services.
3. Open `SmartCampusAPI` as a Maven web project.
4. Right-click project -> **Clean and Build**.
5. Right-click project -> **Run** with server set to Tomcat.
6. Verify deployment by opening:
   - `http://localhost:8080/SmartCampusAPI/api/v1/`

### Option B: Maven package then deploy WAR manually
1. From project root, run:
   - `mvn clean package`
2. Deploy generated WAR from `target/` to Tomcat `webapps/`.
3. Start Tomcat and verify:
   - `http://localhost:8080/SmartCampusAPI/api/v1/`

## Coursework Compliance Notes
- Uses `javax.ws.rs.core.Application` with `@ApplicationPath("/api/v1")`.
- Uses in-memory data structures only (no SQL/NoSQL database).
- Uses JAX-RS only (no Spring Boot).
- Includes specific exception mappers for `409`, `422`, `403` and a global `500` mapper.
- Includes JAX-RS request/response logging filter.

## Sample curl Commands (for README requirement)

1) Discovery endpoint
```bash
curl -i http://localhost:8080/SmartCampusAPI/api/v1/
```

2) Create a room
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"LIB-301\",\"name\":\"Library Quiet Study\",\"capacity\":100}"
```

3) Get all rooms
```bash
curl -i http://localhost:8080/SmartCampusAPI/api/v1/rooms
```

4) Create a sensor linked to a valid room
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"CO2-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":410.5,\"roomId\":\"LIB-301\"}"
```

5) Filter sensors by type
```bash
curl -i "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2"
```

6) Add a sensor reading
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"timestamp\":1713770400000,\"value\":612.8}"
```

7) Trigger 422 by using unknown roomId
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"CO2-404\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":399.0,\"roomId\":\"MISSING-ROOM\"}"
```

8) Trigger 403 by posting reading to MAINTENANCE sensor
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"CO2-M-01\",\"type\":\"CO2\",\"status\":\"MAINTENANCE\",\"currentValue\":380.0,\"roomId\":\"LIB-301\"}"

curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-M-01/readings \
  -H "Content-Type: application/json" \
  -d "{\"value\":500}"
```

9) Trigger 409 by deleting room with assigned sensors
```bash
curl -i -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

10) Trigger 500 global mapper demo (unexpected null body)
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d "null"
```

## Report Answers (Questions from Coursework Specification)

### Part 1.1 - Resource lifecycle and in-memory synchronization
JAX-RS resource classes are request-scoped by default in common runtimes, meaning a new instance can be created per incoming request. However, shared in-memory collections live at application scope and are accessed by multiple request threads concurrently. Therefore, mutable shared state must be stored in thread-safe containers (`ConcurrentHashMap`) and not in per-request instance fields. This avoids race conditions and inconsistent writes when concurrent requests create/update/delete rooms, sensors, and readings.

### Part 1.2 - Why hypermedia (HATEOAS) matters
Hypermedia makes the API self-discoverable. A client can start at `GET /api/v1/` and navigate links/resources without hardcoding every URI. Compared with static documentation only, this reduces coupling, improves evolvability, and lowers maintenance effort for client applications when routes change.

### Part 2.1 - IDs vs full objects for room lists
Returning IDs only minimizes payload size and network cost, but requires extra client requests to retrieve room details. Returning full objects increases response size but reduces client round-trips and simplifies UI logic. For this coursework, returning full room objects is appropriate because facilities users need immediate metadata (`name`, `capacity`, `sensorIds`) and the dataset is in-memory and moderate.

### Part 2.2 - DELETE idempotency
The delete operation is idempotent by final state. If a deletable room exists, the first `DELETE` removes it. Repeating the same `DELETE` request returns `404` because the target is already absent, but no additional state change occurs. The resource remains deleted after any number of repeated calls.

### Part 3.1 - @Consumes and media-type mismatch
`@Consumes(MediaType.APPLICATION_JSON)` enforces the expected request media type. If a client sends `text/plain` or `application/xml`, JAX-RS cannot select a compatible message body reader for that method and responds with `415 Unsupported Media Type`. This protects endpoint contracts and prevents ambiguous parsing behavior.

### Part 3.2 - QueryParam vs path segment for filtering
Filtering is a query concern on a collection, not a resource identity concern. `GET /sensors?type=CO2` keeps `/sensors` as the collection and applies optional criteria cleanly. Query parameters compose naturally (`?type=CO2&status=ACTIVE`) and avoid path proliferation such as `/sensors/type/CO2/...`. Therefore query parameters are generally superior for searching/filtering semantics.

### Part 4.1 - Sub-resource locator benefits
Using a sub-resource locator (`/sensors/{sensorId}/readings`) separates parent sensor operations from reading-history operations. This improves cohesion, keeps classes smaller, and simplifies testing and maintenance. In larger APIs, this pattern scales better than putting all nested endpoints into one large controller/resource class.

### Part 4.2 - Historical data management and consistency side effect
`SensorReadingResource` implements:
- `GET /sensors/{sensorId}/readings` for history retrieval
- `POST /sensors/{sensorId}/readings` for appending new events

On successful reading creation, the parent `Sensor.currentValue` is immediately updated to the new reading value. This ensures consistency between historical stream data and the summary/current-state view returned by sensor endpoints.

### Part 5.1 - Resource conflict (409)
When deleting a room that still has assigned sensors, the request is syntactically valid but violates current server state constraints. Returning `409 Conflict` is appropriate. A dedicated `RoomNotEmptyException` plus mapper provides a stable JSON error contract instead of default/plain server errors.

### Part 5.2 - Why 422 is better than 404 for missing linked payload data
`422 Unprocessable Entity` is semantically accurate when the target endpoint exists and payload is valid JSON, but a referenced dependency inside the payload is invalid (e.g., unknown `roomId`). `404 Not Found` usually refers to the requested URI target not existing, which is not the case here.

### Part 5.3 - State constraint (403)
If a sensor is in `MAINTENANCE`, posting a new reading is intentionally disallowed by business policy. `403 Forbidden` correctly communicates that the server understood the request but refuses to execute it due to operational state constraints.

### Part 5.4 - Global safety net (500) and cybersecurity risks
A catch-all `ExceptionMapper<Throwable>` prevents raw stack traces/default error pages from being exposed to API consumers and returns sanitized `500` JSON responses. Exposed stack traces can leak internal package names, class design, framework versions, and file paths, enabling targeted exploit development, dependency fingerprinting, and reconnaissance.

### Part 5.5 - Why filters are better for cross-cutting logging
JAX-RS filters centralize cross-cutting concerns such as request/response logging. This gives consistent coverage across all endpoints, reduces duplicated `Logger.info()` calls in resource methods, and preserves separation of concerns. Business logic remains focused on domain behavior while observability remains centralized and maintainable.
