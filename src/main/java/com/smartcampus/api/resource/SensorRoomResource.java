package com.smartcampus.api.resource;

import com.smartcampus.api.exception.RoomNotEmptyException;
import com.smartcampus.api.model.Room;
import com.smartcampus.api.store.InMemoryDataStore;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorRoomResource {
    private static final Logger LOGGER = Logger.getLogger(SensorRoomResource.class.getName());

    @GET
    public List<Room> getAllRooms() {
        LOGGER.info("Listing all rooms");
        return new ArrayList<Room>(InMemoryDataStore.rooms().values());
    }

    @POST
    public Response createRoom(Room room, @Context UriInfo uriInfo) {
        if (room == null || room.getId() == null || room.getId().trim().isEmpty()) {
            throw new WebApplicationException("Room id is required", Response.Status.BAD_REQUEST);
        }

        room.setSensorIds(room.getSensorIds() == null ? new ArrayList<String>() : room.getSensorIds());
        InMemoryDataStore.rooms().put(room.getId(), room);
        LOGGER.info("Created room: " + room.getId());

        URI location = uriInfo.getAbsolutePathBuilder().path(room.getId()).build();
        return Response.created(location).entity(room).build();
    }

    @GET
    @Path("/{roomId}")
    public Room getRoomById(@PathParam("roomId") String roomId) {
        Room room = InMemoryDataStore.rooms().get(roomId);
        if (room == null) {
            throw new WebApplicationException("Room not found", Response.Status.NOT_FOUND);
        }
        LOGGER.info("Fetched room: " + roomId);
        return room;
    }

    @DELETE
    @Path("/{roomId}")
    public Response deleteRoom(@PathParam("roomId") String roomId) {
        Map<String, Room> rooms = InMemoryDataStore.rooms();
        Room existing = rooms.get(roomId);
        if (existing == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (existing.getSensorIds() != null && !existing.getSensorIds().isEmpty()) {
            throw new RoomNotEmptyException("Room " + roomId + " still has assigned sensors");
        }

        rooms.remove(roomId);
        LOGGER.info("Deleted room: " + roomId);
        return Response.noContent().build();
    }
}
