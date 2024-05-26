package security;

import io.jsonwebtoken.Claims;
import jakarta.annotation.Priority;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ResourceInfo;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;


@Provider
@Priority(2)
public class AuthorizationFilter implements ContainerRequestFilter {


    @Context
    private ResourceInfo resourceInfo;


    @Override
    public void filter(ContainerRequestContext requestContext) {
        // Retrieve the required roles from the resource's annotations
        RolesAllowed annotation = resourceInfo.getResourceMethod().getAnnotation(RolesAllowed.class);

        if (annotation == null) {
            return;
        }

        // Retrieve the authentication token from the request headers
        String token = requestContext.getHeaderString("Authorization");

        if (token == null || token.equals("")) {
            System.out.println("Access denied");
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
            return;
        }

        if (!verifyToken(token)) {
            System.out.println("Access denied");
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        }
    }

    private boolean verifyToken(String token) {
        Claims claims = TokenManager.decodeTokens(token);
        return claims != null;
    }
}