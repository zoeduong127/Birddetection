package dao;

import com.zaxxer.hikari.HikariDataSource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import model.BirdImage;
import model.ImageCollection;
import model.Visit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public enum ArchivedImageDao {
    instance;
    private HikariDataSource dataSource;

    ArchivedImageDao() {
        dataSource = CPDataSource.createNewDataSource();
    }

    public boolean addBirdImageToArchive(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO bird_image (visit_id, date,  image) " +
                        "SELECT visit_id, date, image FROM  archived_image " +
                        "WHERE image_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean addNewArchivedImage(BirdImage image) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO archived_image (visit_id, date, image) VALUES (?,?,?);";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, image.getImageId());
            ps.setDate(2, image.getDate());
            ps.setString(3, image.getImage_path());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public ImageCollection getAllBirdImages() throws SQLException {
        Connection connection = dataSource.getConnection();

        ImageCollection imageCollection = new ImageCollection();
        List<Visit> visits = new ArrayList<>();

        String query = "SELECT v.*, ai.* FROM visit v " +
                "INNER JOIN archived_image ai ON v.visit_id = ai.visit_id ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int visitId = rs.getInt(1);
                // Retrieve other Visit fields

                BirdImage image = new BirdImage(rs.getInt(7), visitId, rs.getDate(9), rs.getString(10));

                // Check if the visit is already in the visits list
                boolean visitExists = false;
                for (Visit existingVisit : visits) {
                    if (existingVisit.getVisitId() == visitId) {
                        visitExists = true;
                        existingVisit.addImage(image);
                        break;
                    }
                }

                if (!visitExists) {
                    Visit visit = new Visit(visitId, rs.getString(2), rs.getDate(3),
                            rs.getDate(4), rs.getInt(5), rs.getInt(6), new ArrayList<>());
                    visit.addImage(image);
                    visits.add(visit);
                }
            }

            imageCollection.setVisits(visits);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Close connection.
        connection.close();
        return imageCollection;
    }
    public boolean deleteArchivedImageById(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "DELETE FROM archived_image WHERE image_id = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1,id);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean addVisit(Visit visit) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO visit (species, arrival, departure, visit_len, accuracy) VALUES (?,?,?,?,?);";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setString(1, visit.getSpecies());
            ps.setDate(2, visit.getArrival());
            ps.setDate(3, visit.getDeparture());
            ps.setInt(4, visit.getVisitLen());
            ps.setDouble(5, visit.getAccuracy());

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                return false;
            }
            sql = "SELECT LAST_INSERT_ID()";

            ps = connection.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) {
                return false;
            }
            int visit_id = rs.getInt(1);

            sql = "INSERT INTO archived_image (visit_id, date, image) VALUES (?,?,?)";
            for (BirdImage image : visit.getImages()) {
                ps = connection.prepareStatement(sql);
                ps.setInt(1, visit_id);
                ps.setDate(2, image.getDate());
                ps.setString(3, image.getImage_path());

                rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {
                    System.out.println("Something went wrong while adding an archived image in addVisit().");
                }
            }
            return true;
        }
    }
}
