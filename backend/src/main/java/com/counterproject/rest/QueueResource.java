package com.counterproject.rest;

import com.counterproject.dto.LinkTemplateRequest;
import com.counterproject.dto.MetricsDTO;
import com.counterproject.dto.QueueDTO;
import com.counterproject.service.MetricsService;
import com.counterproject.service.QueueService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.LocalDate;
import java.util.List;

@Path("/queues")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class QueueResource {

    @Inject
    private QueueService queueService;

    @Inject
    private MetricsService metricsService;

    @GET
    public List<QueueDTO> findAll() {
        return queueService.findAll();
    }

    @GET
    @Path("/{id}")
    public Response findById(@PathParam("id") Long id) {
        QueueDTO queue = queueService.findById(id);
        if (queue == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        return Response.ok(queue).build();
    }

    @POST
    public Response create(QueueDTO dto) {
        try {
            QueueDTO created = queueService.create(dto);
            return Response.status(Response.Status.CREATED).entity(created).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") Long id, QueueDTO dto) {
        try {
            QueueDTO updated = queueService.update(id, dto);
            return Response.ok(updated).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        queueService.delete(id);
        return Response.noContent().build();
    }

    @POST
    @Path("/{id}/clear-diffs")
    public Response clearAllDiffs(@PathParam("id") Long id) {
        try {
            queueService.clearAllDiffs(id);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @POST
    @Path("/{id}/link-template")
    public Response linkTemplate(@PathParam("id") Long id, LinkTemplateRequest request) {
        try {
            queueService.linkTemplate(id, request);
            return Response.noContent().build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @GET
    @Path("/{id}/metrics")
    public Response getMetrics(@PathParam("id") Long id, @QueryParam("dtRif") String dtRifStr) {
        try {
            LocalDate dtRif = dtRifStr != null ? LocalDate.parse(dtRifStr) : null;
            MetricsDTO metrics = metricsService.calculateMetrics(id, dtRif);
            return Response.ok(metrics).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
