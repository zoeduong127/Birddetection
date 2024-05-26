package unitTests;

import dao.AccountDao;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.Account;
import model.LoginCredentials;
import model.Token;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.*;
import resources.AccountResource;

import java.sql.SQLException;

@SuppressWarnings("CheckStyle")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AccountResourceTest extends JerseyTest {

    @SuppressWarnings("CheckStyle")
    private final String USERNAME = "testaccount";
    private final String EMAIL = "testaccount@gmail.com";
    private final String PASSWORD = "testaccountpassword";
    private static String authToken;

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        config.register(AccountResource.class);
        return config;
    }

    @BeforeAll
    public void setUp() throws Exception {
        try {
            Token token = AccountDao.instance.getAuthTokenByCredentials(new LoginCredentials(EMAIL, PASSWORD));

            assert token != null;
            authToken = token.getToken();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        super.setUp();
    }
    @BeforeEach
    void init() throws Exception {
        super.setUp();
    }

    @Test
    public void testAddAndDeleteAccount() throws SQLException {
        String testEmail = "testaddaccount@gmail.com";
        Account testAddAccount = new Account();
        testAddAccount.setUsername("testaddaccount");
        testAddAccount.setEmail(testEmail);
        testAddAccount.setPasswordHash("testaddaccountpassword");
        testAddAccount.setTelephone("");

        // Send a POST request with the model object as JSON payload
        Response response = target("/accounts")
                .request()
                .post(Entity.entity(testAddAccount, MediaType.APPLICATION_JSON));

        Assertions.assertEquals(200, response.getStatus());

        response = target("/accounts")
                .request()
                .post(Entity.entity(testAddAccount, MediaType.APPLICATION_JSON));
        Assertions.assertEquals(422, response.getStatus());

        Account acc = AccountDao.instance.getAccountByEmail(testEmail);
        response = target("/accounts/" + acc.getId())
                .request()
                .header("Authorization", authToken)
                .delete();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertEquals(AccountDao.instance.getAccountByEmail(testEmail), null);

    }

    @Test
    public void testLoginAndLogout() {
        LoginCredentials creds = new LoginCredentials(EMAIL, PASSWORD);
        Response response = target("/accounts/login")
                .request()
                .post(Entity.json(creds));
        Assertions.assertEquals(200, response.getStatus());

        String potentialToken = response.readEntity(String.class);
        Assertions.assertNotEquals(null, potentialToken);
        authToken = potentialToken;
        Assertions.assertTrue(authToken.contains("."));
    }


}
