package unitTests;

import dao.AccountDao;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.BirdImage;
import model.LoginCredentials;
import model.Token;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.*;
import resources.ArchivedImageResource;
import resources.BirdImageResource;
import resources.ImageResource;

import java.sql.Date;
import java.sql.SQLException;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ArchivedImageResourceTest extends JerseyTest {

    private final String USERNAME = "testaccount";
    private final String EMAIL = "testaccount@gmail.com";
    private final String PASSWORD = "testaccountpassword";
    private static String authToken;

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        config.register(ArchivedImageResource.class);
        config.register(ImageResource.class);
        return config;
    }

    @BeforeAll
    public void setUp() {
        try {
            Token token = AccountDao.instance.getAuthTokenByCredentials(new LoginCredentials(EMAIL, PASSWORD));

            assert token != null;
            authToken = token.getToken();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @BeforeEach
    void init() throws Exception {
        super.setUp();
    }

    @Test
    public void addArchivedImageTest() {
        BirdImage image = new BirdImage();
        image.setVisitId();
        image.setDate(new Date(System.currentTimeMillis()));
        image.setImageId(0);
        Response response = target("/images/archive/birds")
                .request()
                .header("Authorization", authToken)
                .put(Entity.entity(image, MediaType.APPLICATION_JSON));

        Assertions.assertEquals(200, response.getStatus());
    }

    @Test
    public void deleteArchivedImageTest() {
        Response response = target("/images/archive/birds/delete/1")
                .request()
                .header("Authorization", authToken)
                .delete();
        Assertions.assertEquals(200, response.getStatus());

    }

    @Test
    public void filterAllBirdImagesTest() {
        Response response = target("/images/archive/filter/allspecies/images")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());

    }




}
