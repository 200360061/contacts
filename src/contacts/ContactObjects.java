package contacts;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Month;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ContactObjects {

    ObservableList<Contacts> ContactsList = FXCollections.observableArrayList();
    // Static contact objects 
    static Contacts contact1 = new Contacts("Gagan", "Deep", "Canada", "456-222-1234", "I myself", LocalDate.of(1999, Month.MARCH, 1));
    static Contacts contact2 = new Contacts("Sukhdeep", "Kaur", "Toronto", "555-222-4567", "Friend of mine", LocalDate.of(1998, Month.JUNE, 10));
    static Contacts contact3 = new Contacts("Mehar", "Preet", "Ontario", "377-132-1564", "Best friend", LocalDate.of(2000, Month.SEPTEMBER, 12));

    // to return List of contacts
    public ObservableList<Contacts> initialise() throws SQLException {
        contact1.setContactId(101);
        contact2.setContactId(102);
        contact3.setContactId(103);
        ContactsList.add(contact1);
        ContactsList.add(contact2);
        ContactsList.add(contact3);
        loadFromDatabase();
        return ContactsList;
    }

    // Load all contacts from database
    public void loadFromDatabase() throws SQLException {
        Connection conn = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/GaganDeep", "root", "123");
            statement = conn.createStatement();

            resultSet = statement.executeQuery("SELECT * FROM Contacts");

            while (resultSet.next()) {
                Contacts newContact = new Contacts(resultSet.getString("firstName"), resultSet.getString("lastName"), resultSet.getString("address"), resultSet.getString("phoneNumber"), resultSet.getString("notes"), resultSet.getDate("dateOfBirth").toLocalDate());
                newContact.setContactId(resultSet.getInt("contactId"));
                ContactsList.add(newContact);
            }
        } catch (SQLException e) {
            System.err.println(e);
        } finally {
            if (conn != null) {
                conn.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }

    }
}
