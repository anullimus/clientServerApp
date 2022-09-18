package serverLogic.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;

public interface Table<T> {
    void init() throws SQLException;

    TreeSet<T> getCollection() throws SQLException;

    T mapRowToObject(ResultSet resultSet) throws SQLException;

    int add(T element) throws SQLException;
}
