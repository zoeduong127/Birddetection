package unitTests;

import dao.AccountDao;
import dao.BirdImageDao;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.Response;
import model.*;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.*;
import resources.AccountResource;
import resources.ArchivedImageResource;
import resources.BirdImageResource;
import resources.ImageResource;

import java.sql.SQLException;
import java.util.ArrayList;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class BirdImageResourceTest extends JerseyTest {
    private final String USERNAME = "testaccount";
    private final String EMAIL = "testaccount@gmail.com";
    private final String PASSWORD = "testaccountpassword";
    private static String authToken;

    @Override
    protected Application configure() {
        ResourceConfig config = new ResourceConfig();
        config.register(BirdImageResource.class);
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
    public void testGetRecentBirdImages() {
        Response response = target("/images/main/filter/recent")
                .queryParam("limit", 2)
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertNotEquals(null, response.getEntity());
    }

    @Test
    public void testfilterBirdImagesBySpecies() throws SQLException {
        ArrayList<String> species = (ArrayList<String>) BirdImageDao.instance.getAllUniqueSpeciesNames();
        assert species != null;

        for (String s : species) {
            Response response = target("/images/main/filter/species")
                    .queryParam("species", s)
                    .request()
                    .header("Authorization", authToken)
                    .get();

            Assertions.assertEquals(200, response.getStatus());
            Assertions.assertNotEquals(null, response.getEntity());
            Assertions.assertFalse(response.readEntity(ImageCollection.class).getVisits().isEmpty());
        }
    }

    @Test
    public void testGetBirdImageById() throws SQLException {
        ImageCollection imageCollection = BirdImageDao.instance.getRecentBirdImages(3);

        for (Visit visit : imageCollection.getVisits()) {
            for (BirdImage b : visit.getImages()) {
                Response response = target("/images/main/birds/" + b.getImageId())
                        .request()
                        .header("Authorization", authToken)
                        .get();
                Assertions.assertEquals(200, response.getStatus());
                Assertions.assertNotEquals(null, response.getEntity());
                Assertions.assertFalse(response.readEntity(Visit.class).getImages().isEmpty());
            }
        }
    }

    @Test
    public void testFilterByStartAndEndDate() throws SQLException {
        Response response = target("/images/main/filter/date")
                .queryParam("startDate","2022-03-03")
                .queryParam("endDate", "2024-03-03")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertNotEquals(null, response.getEntity());
        Assertions.assertFalse(response.readEntity(ImageCollection.class).getVisits().isEmpty());

        response = target("/images/main/filter/date")
                .queryParam("startDate","2024-03-03")
                .queryParam("endDate", "2022-03-03")
                .request()
                .header("Authorization", authToken)
                .get();
        Assertions.assertEquals(200, response.getStatus());
        Assertions.assertNotEquals(null, response.getEntity());
        Assertions.assertTrue(response.readEntity(ImageCollection.class).getVisits().isEmpty());
    }
}
