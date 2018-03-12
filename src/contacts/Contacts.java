package contacts;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

public class Contacts {

    private String firstName, lastName, address, phoneNumber, notes;
    private int contactId;
    private LocalDate dateOfBirth;
    private File imageFile;
    Date dob;

    public Contacts(String firstName, String lastName, String address, String phoneNumber, String notes, LocalDate dateOfBirth) {
        setFirstName(firstName);
        setLastName(lastName);
        setAddress(address);
        setPhoneNumber(phoneNumber);
        setNotes(notes);
        setDateOfBirth(dateOfBirth);
        setImageFile(new File("./src/images/defaultContact.png"));
        dob = Date.valueOf(dateOfBirth);

    }

    public Contacts(String firstName, String lastName, String address, String phoneNumber, String notes, LocalDate dateOfBirth, File imageFile) throws IOException {
        this(firstName, lastName, address, phoneNumber, notes, dateOfBirth);
        setImageFile(imageFile);
        copyImageFile();
        dob = Date.valueOf(dateOfBirth);
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        if (!firstName.isEmpty()) {
            this.firstName = firstName;
        } else {
            throw new IllegalArgumentException("Please Fill first name field");
        }
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        if (!lastName.isEmpty()) {
            this.lastName = lastName;
        } else {
            throw new IllegalArgumentException("Please Fill last name field");
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        if (!address.isEmpty()) {
            this.address = address;
        } else {
            throw new IllegalArgumentException("Please Fill address field");
        }
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber.matches("[2-9]\\d{2}[-.]?\\d{3}[-.]\\d{4}")) {
            this.phoneNumber = phoneNumber;
        } else {
            throw new IllegalArgumentException("Phone Number must be of NXX-XXX-XXXX Pattern");
        }
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public int getContactId() {
        return contactId;
    }

    public void setContactId(int contactId) {
        this.contactId = contactId;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        if (dateOfBirth.isBefore(LocalDate.now())) {
            this.dateOfBirth = dateOfBirth;
        } else {
            throw new IllegalArgumentException("Date Of Birth is Invalid");
        }
    }

    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public void copyImageFile() throws IOException {
        Path path = imageFile.toPath();

        String uniqueFileName = getUniqueFileName(imageFile.getName());

        Path target = Paths.get("./src/images/" + uniqueFileName);

        Files.copy(path, target, StandardCopyOption.REPLACE_EXISTING);

        imageFile = new File(target.toString());
    }

    private String getUniqueFileName(String FileName) {
        String Name;

        SecureRandom sr = new SecureRandom();

        do {
            Name = "";

            for (int count = 1; count <= 32; count++) {
                int Char;

                do {
                    Char = sr.nextInt(123);
                } while (!isValidCharacter(Char));

                Name = String.format("%s%c", Name, Char);
            }
            Name += FileName;

        } while (!uniqueFileName(Name));

        return Name;
    }

    public boolean uniqueFileName(String fileName) {
        File directory = new File("./src/images/");

        File[] contents = directory.listFiles();

        for (File file : contents) {
            if (file.getName().equals(fileName)) {
                return false;
            }
        }
        return true;
    }

    public boolean isValidCharacter(int i) {

        if (i >= 48 && i <= 57) {
            return true;
        }

        if (i >= 65 && i <= 90) {
            return true;
        }

        if (i >= 97 && i <= 122) {
            return true;
        }

        return false;
    }

    public void saveToDatabase() throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/GaganDeep", "root", "123");

            String sql = "Insert into contacts(firstName,lastName,address,notes,phoneNumber,dateofbirth,image) Values(?,?,?,?,?,?,?)";

            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, notes);
            preparedStatement.setString(5, phoneNumber);
            preparedStatement.setDate(6, dob);
            preparedStatement.setString(7, imageFile.getName());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (conn != null) {
                conn.close();
            }
        }
    }

    public void updateDatabase() throws SQLException {
        Connection conn = null;
        PreparedStatement preparedStatement = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/GaganDeep", "root", "123");

            String sql = "Update contacts Set firstName = ? , lastName = ?, address = ?, notes= ?, phoneNumber= ?, dateOfBirth= ?, image = ?  Where contactId = " + contactId;
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, firstName);
            preparedStatement.setString(2, lastName);
            preparedStatement.setString(3, address);
            preparedStatement.setString(4, notes);
            preparedStatement.setString(5, phoneNumber);
            preparedStatement.setDate(6, dob);
            preparedStatement.setString(7, imageFile.getName());
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }

            if (conn != null) {
                conn.close();
            }
        }
    }

}
