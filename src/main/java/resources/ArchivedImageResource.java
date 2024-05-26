package resources;


import dao.ArchivedImageDao;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.*;
import jakarta.ws.rs.*;

import model.BirdImage;
import model.ImageCollection;
import model.Visit;

import java.sql.SQLException;

public class ArchivedImageResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    public ArchivedImageResource(UriInfo uriInfo, Request request) {
        this.uriInfo = uriInfo;
        this.request = request;
    }

    @PUT
    @RolesAllowed("loggedIn")
    @Path("/birds/gallery")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addArchivedImage(@QueryParam("ID") int id) {
        try {
            boolean success = ArchivedImageDao.instance.addBirdImageToArchive(id);
            if (success) {
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            System.err.println("SQLException occurred: " + e);
            return Response.serverError().build();
        }
    }

    @PUT
    @RolesAllowed("loggedIn")
    @Path("/birds")
    public Response addNewArchivedImage(BirdImage image) throws SQLException {
        try {
            if (ArchivedImageDao.instance.addNewArchivedImage(image)) {
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    @DELETE
    @RolesAllowed("loggedIn")
    @Path("/birds/delete/{bird_id}")
    public Response deleteArchivedImageById(@PathParam("bird_id") int bird_id) {
        try {
            boolean success = ArchivedImageDao.instance.deleteArchivedImageById(bird_id);
            if (success) {
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            System.err.println("SQLException occurred: " + e);
            return Response.serverError().build();
        }
    }


    @GET
    @Path("/filter/allspecies/images")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("loggedIn")
    public Response filterAllBirdImages() {
        try {
            ImageCollection images = ArchivedImageDao.instance.getAllBirdImages();
            return Response.ok().entity(images).build();
        } catch (SQLException e) {
            System.err.println("SQLException occurred: " + e);
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/visits")
    @Consumes({MediaType.APPLICATION_JSON})
    @RolesAllowed("loggedIn")
    public Response addVisit(Visit visit) {
        try {
            if (ArchivedImageDao.instance.addVisit(visit)) {
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }


}