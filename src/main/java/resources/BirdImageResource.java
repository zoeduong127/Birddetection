package resources;

import dao.BirdImageDao;
/*
import dao.VisitDao;
*/
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import model.BirdImage;
import model.ImageCollection;
import model.Visit;
import org.glassfish.jaxb.core.v2.model.core.ID;

import javax.print.attribute.standard.Media;
import java.awt.image.BufferedImage;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;


public class BirdImageResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    public BirdImageResource(UriInfo uriInfo, Request request) {
        this.uriInfo = uriInfo;
        this.request = request;
    }


    @GET
    @Path("/visits/{visit_id}")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("loggedIn")
    public Response getVisitImagesById(@PathParam("visit_id") int visit_id) {
        try {
            Visit visitImages = BirdImageDao.instance.getVisitImagesById(visit_id);

            if (visitImages == null) {
                return Response.ok().entity("{}").build();
            } else {
                return Response.ok().entity(visitImages).build();
            }
        } catch (SQLException e) {
            System.out.println("SQLException occurred: " + e);
            return Response.serverError().build();
        }
    }

    // This method doesn't get the actual image of the bird.
    // It gets the Visit object for that bird.
    @GET
    @Path("/birds/{bird_id}")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("loggedIn")
    public Response getBirdImageById(@PathParam("bird_id") int bird_id) {
        try {
            Visit visitResult = BirdImageDao.instance.getBirdImageById(bird_id);
            return Response.ok().entity(visitResult).build();
        } catch (SQLException e) {
            System.err.println("SQLException occurred: " + e);
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/birds/delete/{bird_id}")
    @RolesAllowed("loggedIn")
    public Response deleteBirdImageById(@PathParam("bird_id") int bird_id)
    {
        try {
            boolean success = BirdImageDao.instance.deleteBirdImageById(bird_id);
            if (success) {
                return Response.ok().entity(bird_id + "deleted").build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            System.err.println("SQLException occurred: " + e);
            return Response.serverError().build();
        }
    }


    @PUT
    @Path("/birds")
    @RolesAllowed("loggedIn")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addBirdImage(BirdImage birdImage) {
        try {
            boolean success = BirdImageDao.instance.addBirdImage(birdImage);
            if (success) {
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            System.err.println("SQLException occurred: " + e);
        }
        return Response.serverError().build();
    }

//Modified for a specific Bird ID
    @PUT
    @Path("/birds/archive")
    @RolesAllowed("loggedIn")
    @Consumes({MediaType.APPLICATION_JSON})
    public Response sendToArchive(@QueryParam("ID") int id) {
        try {
            boolean success = BirdImageDao.instance.sendToArchive(id);
            if (success) {
                return Response.ok().entity("Archived Image").build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            System.err.println("SQLException occurred: " + e);
        }
        return Response.serverError().build();
    }


//    @GET
//    @Path("/birds/{bird_id}/image")
//    @Produces("image/jpeg")
//    @RolesAllowed("loggedIn")
//    public Response retrieveImage(@PathParam("bird_id") int bird_id) {
//        String path = images_path + "\\main\\bird" + bird_id + ".jpeg";
//        File imageFile = new File(path);
//        if (imageFile.exists()) {
//            return Response.ok(imageFile).build();
//        } else {
//            return Response.status(Response.Status.NOT_FOUND).build();
//        }
//    }

    @GET
    @Path("/filter/date")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("loggedIn")
    public Response filterBirdImagesByStartAndEndDate(@QueryParam("startDate") String startDate, @QueryParam("endDate") String endDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedStartDate = dateFormat.parse(startDate);
            java.util.Date parsedEndDate = dateFormat.parse(endDate);

            java.sql.Date sqlStartDate = new java.sql.Date(parsedStartDate.getTime());
            java.sql.Date sqlEndDate = new java.sql.Date(parsedEndDate.getTime());

            ImageCollection responseContent = BirdImageDao.instance.getBirdsByStartEndDate(sqlStartDate, sqlEndDate);
            return Response.ok().entity(responseContent).build();
        } catch (SQLException e) {
            System.out.println("SQLException occurred: " + e);
            return Response.serverError().build();
        } catch (ParseException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Invalid date format").build();
        }
    }

    @GET
    @Path("/filter/species")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("loggedIn")
    public Response filterBirdImagesBySpecies(@QueryParam("species") String species) {
        try {
            ImageCollection images = BirdImageDao.instance.getBirdImagesBySpecies(species);
            return Response.ok().entity(images).build();
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
            ImageCollection images = BirdImageDao.instance.getAllBirdImages();
            return Response.ok().entity(images).build();
        } catch (SQLException e) {
            System.err.println("SQLException occurred: " + e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/filter/recent")
    @Produces({MediaType.APPLICATION_JSON})
    //\RolesAllowed("loggedIn")
    public Response getRecentBirdImages(@QueryParam("limit") int limit) {
        try {
            ImageCollection images = BirdImageDao.instance.getRecentBirdImages(limit);
            return Response.ok().entity(images).build();
        } catch (SQLException e) {
            System.err.println("SQLException occurred: " + e);
            return Response.serverError().build();
        }
    }

    @GET
    @Path("/filter/allspecies")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("loggedIn")
    public Response getAllUniqueSpeciesNames() {
        try {
            ArrayList<String> species = (ArrayList<String>) BirdImageDao.instance.getAllUniqueSpeciesNames();
            return Response.ok().entity(species).build();
        } catch (SQLException e) {
            System.out.println("SQLException occurred: " + e);
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/visits")
    @Consumes({MediaType.APPLICATION_JSON})
    @RolesAllowed("loggedIn")
    public Response addVisit(Visit visit) {
        try {
            if (BirdImageDao.instance.addVisit(visit)) {
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return Response.serverError().build();
        }
    }

    //TODO: Test if it works on my own laptop since connection isn't working right now.
    @GET
    @Path("/visits")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("loggedIn")
    public Response getAllVisits() {
        try {
            double confidence = BirdImageDao.instance.getConfidence();
            if (confidence != -1) return Response.ok().entity(confidence).build();
            else {

                return Response.serverError().build();
            }
        } catch (SQLException e) {
            System.out.println("SQLException occurred: " + e);
            return Response.serverError().entity("An error occurred: " + e.getMessage()).build();
        }
    }


    /*@GET
    @Path("/visits/speciesAMonth")
    @Produces({MediaType.APPLICATION_JSON})
    @RolesAllowed("loggedIn")
    public Response getAllSpeciesPerMonth() {
        try {
            Map<String, Date> species = (Map<String, Date>) BirdImageDao.instance.getAllVisitsInAMonth();
            return Response.ok().entity(species).build();
        } catch (SQLException e) {
            System.out.println("SQLException occurred: " + e);
            return Response.serverError().build();
        }
    }*/
}
