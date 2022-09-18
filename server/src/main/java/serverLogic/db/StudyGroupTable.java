package serverLogic.db;

import data.initial.*;

import java.sql.*;
import java.util.TreeSet;

public class StudyGroupTable implements Table<StudyGroup> {
    private static final int PARAMETER_OFFSET_NAME = 1;
    private static final int PARAMETER_OFFSET_COORDINATES_X = 2;
    private static final int PARAMETER_OFFSET_COORDINATES_Y = 3;
    private static final int PARAMETER_OFFSET_TIMESTAMP = 4;
    private static final int PARAMETER_OFFSET_STUDENTS_COUNT = 5;
    private static final int PARAMETER_OFFSET_FORM_OF_EDUCATION = 6;
    private static final int PARAMETER_OFFSET_SEMESTER_ENUM = 7;
    private static final int PARAMETER_OFFSET_ADMIN_NAME = 8;
    private static final int PARAMETER_OFFSET_ADMIN_HEIGHT = 9;
    private static final int PARAMETER_OFFSET_ADMIN_NATIONALITY = 10;
    private static final int PARAMETER_OFFSET_ADMIN_LOCATION_X = 11;
    private static final int PARAMETER_OFFSET_ADMIN_LOCATION_Y = 12;
    private static final int PARAMETER_OFFSET_ADMIN_LOCATION_NAME = 13;
    private static final int PARAMETER_OFFSET_AUTHOR = 14;
    private final Connection connection;


    public StudyGroupTable(Connection connection) {
        this.connection = connection;
    }


    @Override
    public void init() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("CREATE TABLE IF NOT EXISTS study_groups ("
                    + "id serial PRIMARY KEY,"
                    + "name varchar(100) NOT NULL,"
                    + "coordinates_x bigint NOT NULL,"
                    + "coordinates_y double precision NOT NULL,"
                    + "creation_date TIMESTAMP NOT NULL,"
                    + "students_count integer,"
                    + "form_of_education varchar(100) NOT NULL,"
                    + "semester varchar(100),"
                    + "admin_name varchar(100) NOT NULL,"
                    + "admin_height integer NOT NULL,"
                    + "admin_nationality varchar(100),"
                    + "admin_location_x real NOT NULL,"
                    + "admin_location_y bigint NOT NULL,"
                    + "admin_location_name varchar(100),"
                    + "author_username varchar (100) NOT NULL)");
        }
    }

    // рубрика "в котлине было бы в 100 раз короче"
    @Override
    public StudyGroup mapRowToObject(ResultSet resultSet) throws SQLException {
        final StudyGroup studyGroup = new StudyGroup(
                resultSet.getString("name"),
                new Coordinates(resultSet.getLong("coordinates_x"), resultSet.getDouble("coordinates_y")),
                resultSet.getInt("students_count"),
                FormOfEducation.valueOf(resultSet.getString("form_of_education")),
                resultSet.getString("semester") != null ? Semester.valueOf(resultSet.getString("semester")) : null,
                new Person(resultSet.getString("admin_name"),
                        resultSet.getInt("admin_height"),
                        resultSet.getString("admin_nationality") != null ? Country.valueOf(resultSet.getString("admin_nationality")) : null,
                        new Location(resultSet.getFloat("admin_location_x"),
                                resultSet.getLong("admin_location_y"),
                                resultSet.getString("admin_location_name") != null ? resultSet.getString("admin_location_name") : null)),
                resultSet.getTimestamp("creation_date").toLocalDateTime().toLocalDate(),
                resultSet.getString("author_username"));

        studyGroup.setId(resultSet.getInt("id"));

        return studyGroup;
    }

    @Override
    public int add(StudyGroup element) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO study_groups VALUES (default, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id")) {
            makePreparedStatementOfStudyGroup(preparedStatement, element);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                resultSet.next();
                return resultSet.getInt("id");
            }
        }
    }

    private void makePreparedStatementOfStudyGroup(PreparedStatement preparedStatement, StudyGroup studyGroup) throws SQLException {
        preparedStatement.setString(PARAMETER_OFFSET_NAME, studyGroup.getName());
        preparedStatement.setLong(PARAMETER_OFFSET_COORDINATES_X, studyGroup.getCoordinates().getX());
        preparedStatement.setDouble(PARAMETER_OFFSET_COORDINATES_Y, studyGroup.getCoordinates().getY());
        preparedStatement.setTimestamp(PARAMETER_OFFSET_TIMESTAMP, Timestamp.valueOf(studyGroup.getCreationDate().atStartOfDay()));
        if (studyGroup.getStudentsCount() != null) {
            preparedStatement.setInt(PARAMETER_OFFSET_STUDENTS_COUNT, studyGroup.getStudentsCount());
        } else {
            preparedStatement.setNull(PARAMETER_OFFSET_STUDENTS_COUNT, Types.INTEGER);
        }
        preparedStatement.setString(PARAMETER_OFFSET_FORM_OF_EDUCATION, studyGroup.getFormOfEducation().toString());
        if (studyGroup.getSemesterEnum() != null) {
            preparedStatement.setString(PARAMETER_OFFSET_SEMESTER_ENUM, studyGroup.getSemesterEnum().toString());
        } else {
            preparedStatement.setNull(PARAMETER_OFFSET_SEMESTER_ENUM, Types.VARCHAR);
        }
        preparedStatement.setString(PARAMETER_OFFSET_ADMIN_NAME, studyGroup.getGroupAdmin().getName());
        preparedStatement.setInt(PARAMETER_OFFSET_ADMIN_HEIGHT, studyGroup.getGroupAdmin().getHeight());
        if (studyGroup.getGroupAdmin().getNationality() != null) {
            preparedStatement.setString(PARAMETER_OFFSET_ADMIN_NATIONALITY, studyGroup.getGroupAdmin().getNationality().toString());
        } else {
            preparedStatement.setNull(PARAMETER_OFFSET_ADMIN_NATIONALITY, Types.VARCHAR);
        }
        preparedStatement.setFloat(PARAMETER_OFFSET_ADMIN_LOCATION_X, studyGroup.getGroupAdmin().getLocation().getX());
        preparedStatement.setLong(PARAMETER_OFFSET_ADMIN_LOCATION_Y, studyGroup.getGroupAdmin().getLocation().getY());
        if (studyGroup.getGroupAdmin().getLocation().getName() != null) {
            preparedStatement.setString(PARAMETER_OFFSET_ADMIN_LOCATION_NAME, studyGroup.getGroupAdmin().getLocation().getName());
        } else {
            preparedStatement.setNull(PARAMETER_OFFSET_ADMIN_LOCATION_NAME, Types.VARCHAR);
        }
        preparedStatement.setString(PARAMETER_OFFSET_AUTHOR, studyGroup.getAuthorName());
    }

    @Override
    public TreeSet<StudyGroup> getCollection() throws SQLException {
        final TreeSet<StudyGroup> newCollection = new TreeSet<>();

        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM study_groups")) {
            while (resultSet.next()) {
                StudyGroup studyGroup = mapRowToObject(resultSet);
                newCollection.add(studyGroup);
            }
        }
        return newCollection;
    }

    public void clearOwnedData(String username) throws SQLException {
        String query = "DELETE FROM study_groups WHERE author_username=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.execute();
        }

    }

    public void removeById(int id) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.execute("DELETE FROM study_groups WHERE id=" + id);
        }
    }

    public void updateById(int id, StudyGroup studyGroup) throws SQLException {
        String query = "UPDATE study_groups SET "
                + "name=?"
                + ",coordinates_x=?"
                + ",coordinates_y=?"
                + ",creation_date=?"
                + ",students_count=?"
                + ",form_of_education=?"
                + ",semester=?"
                + ",admin_name=?"
                + ",admin_height=?"
                + ",admin_nationality=?"
                + ",admin_location_x=?"
                + ",admin_location_y=?"
                + ",admin_location_name=?"
                + ",author_username=? "
                + "WHERE id =" + id;

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            makePreparedStatementOfStudyGroup(preparedStatement, studyGroup);
            preparedStatement.execute();
        }
    }
}
