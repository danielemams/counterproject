package com.counterproject.rest;

import com.counterproject.dto.DiffDTO;
import com.counterproject.service.DiffService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/diffs")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DiffResource {

    @Inject
    private DiffService diffService;

    @GET
    public List<DiffDTO> findByQueueId(@QueryParam("queueId") Long queueId) {
        if (queueId == null) {
            throw new BadRequestException("queueId is required");
        }
        return diffService.findByQueueId(queueId);
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        DiffDTO diff = diffService.findById(id);
        if (diff == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(diff).build();
    }

    @POST
    public Response create(DiffDTO dto) {
        try {
            DiffDTO created = diffService.create(dto);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, DiffDTO dto) {
        try {
            DiffDTO updated = diffService.update(id, dto);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        diffService.delete(id);
        return Response.noContent().build();
    }
}
