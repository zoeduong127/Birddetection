package dao;

import com.zaxxer.hikari.HikariDataSource;
import model.BirdImage;
import model.ImageCollection;
import model.Visit;

import java.sql.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public enum BirdImageDao {
    instance;
    private HikariDataSource dataSource;

    BirdImageDao() {
        dataSource = CPDataSource.createNewDataSource();
    }

    public Visit getVisitImagesById(int id) throws SQLException {
        Connection connection = dataSource.getConnection();

        String sql = "SELECT * FROM visit WHERE visit_id = ?";
        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();
        Visit visitResult = new Visit();
        rs.close();
        if (rs.next()) {
            visitResult.setVisitId(rs.getInt(1));
            visitResult.setSpecies(rs.getString(2));
            visitResult.setArrival(rs.getDate(3));
            visitResult.setDeparture(rs.getDate(4));
            visitResult.setVisitLen((rs.getInt(5)));
            visitResult.setAccuracy(rs.getDouble(6));
            visitResult.setImages(new ArrayList<>());
        } else {
            return null;
        }

        sql = "SELECT * FROM bird_image WHERE visit_id = ?;";
        ps = connection.prepareStatement(sql);
        ps.setInt(1, id);

        rs = ps.executeQuery();


        while (rs.next()) {
            visitResult.addImage(new BirdImage(rs.getInt(1), rs.getInt(2),
                    rs.getDate(3), rs.getString(4)));
        }
        // Close connection.
        connection.close();
        return visitResult;
    }

    public Visit getBirdImageById(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT vi.*\n" +
                    "FROM bird_image bi\n" +
                    "JOIN visit vi ON bi.visit_id = vi.visit_id\n" +
                    "WHERE bi.image_id = ?;";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            ResultSet rs = ps.executeQuery();
            Visit visitResult = new Visit();

            if (rs.next()) {
                visitResult.setVisitId(rs.getInt(1));
                visitResult.setSpecies(rs.getString(2));
                visitResult.setArrival(rs.getDate(3));
                visitResult.setDeparture(rs.getDate(4));
                visitResult.setVisitLen((rs.getInt(5)));
                visitResult.setAccuracy(rs.getDouble(6));
                visitResult.setImages(new ArrayList<>());
            } else {
                return null;
            }

            sql = "SELECT * FROM bird_image WHERE image_id = ?;";
            ps = connection.prepareStatement(sql);
            ps.setInt(1, id);

            rs = ps.executeQuery();


            while (rs.next()) {
                visitResult.addImage(new BirdImage(rs.getInt(1), rs.getInt(2),
                        rs.getDate(3), rs.getString(4)));
            }
            return visitResult;
        }
    }

    public ImageCollection getBirdImagesBySpecies(String species) throws SQLException {
        Connection connection = dataSource.getConnection();

        ImageCollection imageCollection = new ImageCollection();
        List<Visit> visits = new ArrayList<>();

        String query = "SELECT v.*, bi.* FROM visit v " +
                "INNER JOIN bird_image bi ON v.visit_id = bi.visit_id " +
                "WHERE v.species = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, species);
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

    public ImageCollection getAllBirdImages() throws SQLException {
        Connection connection = dataSource.getConnection();

        ImageCollection imageCollection = new ImageCollection();
        List<Visit> visits = new ArrayList<>();

        String query = "SELECT v.*, bi.* FROM visit v " +
                "INNER JOIN bird_image bi ON v.visit_id = bi.visit_id ";

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

    public ImageCollection getRecentBirdImages(int limit) throws SQLException {
        Connection connection = dataSource.getConnection();

        ImageCollection imageCollection = new ImageCollection();
        List<Visit> visits = new ArrayList<>();

        String query = "SELECT v.*, bi.*\n" +
                "FROM visit v\n" +
                "INNER JOIN bird_image bi ON bi.visit_id = v.visit_id\n" +
                "ORDER BY bi.date DESC\n" +
                "LIMIT ?;";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, limit);
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

    public List<String> getAllUniqueSpeciesNames() throws SQLException {
        Connection connection = dataSource.getConnection();
        String sql = "SELECT DISTINCT species FROM visit;";
        PreparedStatement ps = connection.prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        ArrayList<String> species = new ArrayList<>();

        while (rs.next()) {
            species.add(rs.getString(1));
        }

        // Close connection.
        connection.close();
        return species;
    }

    public ImageCollection getBirdsByStartEndDate(Date startDate, Date endDate) throws SQLException {
        Connection connection = dataSource.getConnection();

        ImageCollection imageCollection = new ImageCollection();
        List<Visit> visits = new ArrayList<>();

        String query = "SELECT v.*, bi.* FROM visit v " +
                "INNER JOIN bird_image bi ON v.visit_id = bi.visit_id " +
                "WHERE v.arrival >= ? AND v.arrival <= ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setDate(1, startDate);
            preparedStatement.setDate(2, endDate);
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

    public boolean deleteBirdImageById(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "DELETE FROM bird_image WHERE image_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }

    }

    public boolean addBirdImage(BirdImage image) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO bird_image (visit_id, date, image)" +
                    "VALUES (?, ?, ?);";
            PreparedStatement ps = connection.prepareStatement(sql);

            ps.setInt(1, image.getVisitId());
            ps.setDate(2, image.getDate());
            ps.setString(3, image.getImage_path());

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public boolean sendToArchive(int id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "INSERT INTO archived_image (visit_id, date,  image) " +
                    "SELECT visit_id, date, image FROM  bird_image " +
                    "WHERE image_id = ?";
            PreparedStatement ps = connection.prepareStatement(sql);


            ps.setInt(1, id);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }

    public double getConfidence() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String sql = "SELECT AVG(v.accuracy) AS avg_accuracy " +
                    "FROM visit v";


            PreparedStatement ps = connection.prepareStatement(sql);
            ResultSet result = ps.executeQuery();

            if (result.next()) {
                double averageAccuracy = result.getDouble("avg_accuracy");

                // Format the average accuracy to have 2 decimal points
                DecimalFormat df = new DecimalFormat("#.##");
                String formattedAccuracy = df.format(averageAccuracy);

                // Parse the formatted string back to a double
                return Double.parseDouble(formattedAccuracy);
            } else {
                return -1;
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

            sql = "INSERT INTO bird_image (visit_id, date, image) VALUES (?,?,?)";
            for (BirdImage image : visit.getImages()) {
                ps = connection.prepareStatement(sql);
                ps.setInt(1, visit_id);
                ps.setDate(2, image.getDate());
                ps.setString(3, image.getImage_path());

                rowsAffected = ps.executeUpdate();

                if (rowsAffected == 0) {
                    System.out.println("Something went wrong while adding a bird image in addVisit().");
                }
            }
            return true;
        }
    }

    /*public Object getAllVisitsInAMonth() throws SQLException {
        try {
            try (Connection connection = dataSource.getConnection()) {
                String sql =    "SELECT EXTRACT(MONTH FROM entry_date) AS month, " +
                                "EXTRACT(YEAR FROM entry_date) AS year, " +
                                "SUM(1) AS entry_count " +
                                "FROM entries " +
                                "GROUP BY EXTRACT(MONTH FROM entry_date), EXTRACT(YEAR FROM entry_date)" +
                                "ORDER BY year, month";

                PreparedStatement ps = connection.prepareStatement(sql);

        }
    }*/
}