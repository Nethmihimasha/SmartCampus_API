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

## Sample curl Commands
1. Discovery endpoint
```bash
curl -i http://localhost:8080/SmartCampusAPI/api/v1/
```

2. Create a room
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/rooms \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"LIB-301\",\"name\":\"Library Quiet Study\",\"capacity\":100}"
```

3. Create a sensor linked to a valid room
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors \
  -H "Content-Type: application/json" \
  -d "{\"id\":\"CO2-001\",\"type\":\"CO2\",\"status\":\"ACTIVE\",\"currentValue\":410.5,\"roomId\":\"LIB-301\"}"
```

4. Filter sensors by type
```bash
curl -i "http://localhost:8080/SmartCampusAPI/api/v1/sensors?type=CO2"
```

5. Add a sensor reading
```bash
curl -i -X POST http://localhost:8080/SmartCampusAPI/api/v1/sensors/CO2-001/readings \
  -H "Content-Type: application/json" \
  -d "{\"timestamp\":1713770400000,\"value\":612.8}"
```

6. Trigger 409 conflict by deleting a room with assigned sensors
```bash
curl -i -X DELETE http://localhost:8080/SmartCampusAPI/api/v1/rooms/LIB-301
```

## Report Answers

### Part 1.1 - Resource lifecycle and in-memory synchronization
JAX-RS resource classes are typically request-scoped by default (a new instance can be created for each request), but shared in-memory data structures still exist at application level and are concurrently accessed by many request threads. For this reason, this implementation stores data in application-level `ConcurrentHashMap` containers and avoids request-instance fields for mutable shared state. This prevents race conditions and data loss under concurrent calls.

### Part 1.2 - Why hypermedia (HATEOAS) matters
Hypermedia makes the API self-discoverable: clients can navigate from a known entry point (`GET /api/v1`) to resource links without hardcoding all routes. Compared with static docs, this reduces client coupling and allows server-side URI evolution while preserving client interoperability.

### Part 2.1 - IDs vs full objects for room lists
Returning only IDs reduces payload size and bandwidth usage, which can help large collections. Returning full objects reduces client round-trips because clients immediately get all metadata. For this coursework, full objects are returned because managers need immediate room context (name/capacity/sensors) and the dataset is in-memory and moderate.

### Part 2.2 - DELETE idempotency
The delete endpoint is idempotent. If a room exists and has no sensors, the first `DELETE` removes it. Repeating the same `DELETE` does not create additional state changes; it returns `404` because the target is already absent. The final server state remains the same after repeated calls.

### Part 3.1 - @Consumes and media-type mismatch
The sensor creation endpoint uses `@Consumes(MediaType.APPLICATION_JSON)`. If a client sends `text/plain` or `application/xml`, JAX-RS cannot match a compatible message body reader for that method and returns `415 Unsupported Media Type`. This enforces strict payload contract compliance.

### Part 3.2 - QueryParam vs path segment for filtering
Filtering is a collection-query concern, not a resource identity concern. `GET /sensors?type=CO2` keeps `/sensors` as the collection resource and applies optional criteria cleanly, supports combining filters naturally, and avoids route explosion. Path-style filters are less flexible for search semantics.

### Part 4.1 - Sub-resource locator benefits
Sub-resource locator (`/sensors/{sensorId}/readings`) separates parent sensor logic from reading-history logic, making classes smaller and easier to maintain/test. This scales better than placing all nested path logic in one large controller.

### Part 5.2 - Why 422 is better than 404 for missing linked payload data
`422 Unprocessable Entity` is semantically accurate when the request document is valid JSON but contains invalid domain references (for example, unknown `roomId` inside a sensor payload). A plain `404` usually refers to the request URI target itself not existing, not a nested payload dependency failure.

### Part 5.4 - Stack-trace exposure cybersecurity risk
Exposed stack traces leak internal package names, class design, file paths, and framework/library behavior. Attackers can use this information for targeted exploit development, dependency fingerprinting, endpoint probing, and identifying weak trust boundaries. This API uses sanitized JSON error bodies and logs internals server-side only.
