package resources;

import dao.AccountDao;
import dao.ResetTokenDao;
import dao.TokenDao;
import emails.Mailer;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlElement;
import model.Account;
import model.LoginCredentials;
import model.ResetToken;
import model.Token;
import security.TokenManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Path("/accounts")
public class AccountResource {
    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    public Response addAccount(Account account) {
        try {
            // Performing input validation on the given account credentials.
            if (AccountDao.instance.accountExists(account)) {
                return Response.status(422)
                        .entity("Username or email is already in use.")
                        .build();
            } else if (account.getUsername() == null || account.getUsername().length() <= 3) {
                return Response.status(422)
                        .entity("Username is invalid or is less than 4 characters long.")
                        .build();
            } else if (account.getEmail() == null || !isEmailValid(account.getEmail())) {
                return Response.status(422)
                        .entity("Email is in an invalid format.")
                        .build();
            } else if (account.getPasswordHash() == null || account.getPasswordHash().length() <= 6) {
                return Response.status(422)
                        .entity("Password is invalid or is less than 7 characters long.")
                        .build();
            }

            String salt = new String(generateSalt(), StandardCharsets.UTF_8);
            account.setSalt(salt);
            String saltedAndHashedPass = AccountDao.instance.hash256(account.getPasswordHash() + salt);
            if (saltedAndHashedPass != null) {
                account.setPasswordHash(saltedAndHashedPass);
            } else {
                return Response.serverError().build();
            }

            boolean success = AccountDao.instance.addAccount(account);
            if (success) {
//                return Response.ok().entity("Account added successfully").build();
                Timestamp expiration = addTime(new Timestamp(System.currentTimeMillis()), 24, Calendar.HOUR);
                String token = TokenManager.generateToken(account.getUsername(), account.getId(), expiration);
                return Response.ok().entity(token).build();

            } else {
                return Response.notModified().entity("Account was not added successfully. Account credentials might be invalid.").build();
            }
        } catch (SQLException e) {
            System.err.println("SQLException e occurred: " + e);
        }
        return Response.serverError().build();
    }


    @POST
    @Path("/logout")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    @RolesAllowed("loggedIn")
    public Response logOut(@HeaderParam("Authorization") String authorizationHeader) {
        try {
            boolean success = TokenDao.instance.deleteToken(authorizationHeader);
            if (success) {
                return Response.ok().entity("Logged out successfully").build();
            } else {
                return Response.notModified().entity("The given account was not logged in.").build();
            }
        } catch (SQLException e) {
            System.err.println("SQLException occurred: " + e);
        }
        return Response.serverError().build();
    }


    @POST
    @Path("/login")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    // TODO: Validate input for XXS protection
    public Response login(LoginCredentials credentials) {
        if (!isEmailValid(credentials.getEmail())) {
            return Response.serverError().build();
        }

        try {
            Account account = AccountDao.instance.getAccountByEmail(credentials.getEmail());
            if (account == null) {
                return Response.serverError().entity("Login credentials are invalid.").build();
            }
            String saltedHashedPass = AccountDao.instance.hash256(credentials.getPassword() + account.getSalt());

            if (!AccountDao.instance.loginCheck(credentials.getEmail(), saltedHashedPass)) {
                return Response.serverError().entity("Login credentials are invalid.").build();
            }

            Timestamp expiration = addTime(new Timestamp(System.currentTimeMillis()), 24, Calendar.HOUR);
            String token = TokenManager.generateToken(account.getUsername(), account.getId(), expiration);

            Token tokenToAdd = new Token();
            tokenToAdd.setAccountId(account.getId());
            tokenToAdd.setToken(token);
            tokenToAdd.setExpiration(expiration);
            tokenToAdd.setCreateDate(new Timestamp(System.currentTimeMillis()));
            tokenToAdd.setUpdatedDate(new Timestamp(System.currentTimeMillis()));

            // Removes existing tokens associated with this account
            TokenDao.instance.removeTokensByAccountId(account.getId());

            if (!TokenDao.instance.addToken(tokenToAdd)) {
                return Response.serverError().entity("Unknown error occurred while adding token.").build();
            }

            return Response.ok().entity(token).build();

        } catch (SQLException e) {
            System.err.println("SQLException occurred: " + e);
            return Response.serverError().build();
        }
    }

    @DELETE
    @Path("/{account_id}")
    @RolesAllowed("loggedIn")
    public Response deleteAccountById(@PathParam("account_id") int account_id) {
        try {
            if (AccountDao.instance.deleteAccountById(account_id)) {
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }
        } catch (SQLException e) {
            System.out.println("SQLException occurred: " + e);
            return Response.serverError().build();
        }
    }

    @POST
    @Path("/password-reset")
    public Response requestPasswordReset(@QueryParam("email") String email) {
        try {
            ResetToken rToken = new ResetToken();
            if (!isEmailValid(email)) {
                return Response.notModified().entity("Email is not valid.").build();
            }

            Account account = AccountDao.instance.getAccountByEmail(email);
            if  (account == null) {
                return Response.serverError().build();
            }

            rToken.setAccountId(account.getId());
            rToken.setToken(UUID.randomUUID().toString());
            rToken.setExpiration(addTime(new Timestamp(System.currentTimeMillis()), 24, Calendar.HOUR));

            ResetTokenDao.instance.removeTokensByAccountId(account.getId());

            if (ResetTokenDao.instance.addToken(rToken)) {
                // TODO: Set reset email link to the page where users can change their password.
                Mailer.sendPasswordResetEmail(email, uriInfo.getBaseUri().getPath());
                return Response.ok().build();
            } else {
                return Response.notModified().build();
            }
        } catch (Exception e) {
            System.out.println("Exception occurred: " + e);
            return Response.serverError().build();
        }
    }

    @PUT
    @Path("/password-reset/{token}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resetPassword(@PathParam("token") String token, LoginCredentials creds) {
        try {
            String newPassword = creds.getPassword();
            if (newPassword == null ||newPassword.length() <= 6) {
                return Response.notModified().entity("Password is invalid or is less than 7 characters long.").build();
            }

            if (ResetTokenDao.instance.resetPassword(token, newPassword)) {
                return Response.ok().build();
            } else {
                return Response.notModified().entity("Token is likely invalid.").build();
            }

        } catch (SQLException e) {
            System.out.println("SQLException occurred: " + e);
            return Response.serverError().build();
        }
    }


    public static boolean isEmailValid(String email) {
        // Define the email pattern using regular expression
        String emailPattern = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailPattern);

        // Match the input email against the pattern
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public byte[] generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        return bytes;
    }


    public static Timestamp addTime(Timestamp timestamp, int amount, int field) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        cal.add(field, amount);
        return new Timestamp(cal.getTimeInMillis());
    }

}