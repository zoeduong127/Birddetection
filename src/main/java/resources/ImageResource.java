package resources;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

@Path("/images")
public class ImageResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @RolesAllowed("loggedIn")
    @Path("/main")
    public BirdImageResource linkToBirdImageResource() {
        return new BirdImageResource(uriInfo, request);
    }

    @RolesAllowed("loggedIn")
    @Path("/archive")
    public ArchivedImageResource ArchivedImageResource() {
        return new ArchivedImageResource(uriInfo, request);
    }
}
