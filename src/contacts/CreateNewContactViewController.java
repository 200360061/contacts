/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package contacts;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import jdk.nashorn.internal.parser.DateParser;

/**
 * FXML Controller class
 *
 * @author Mehar Preet
 */
public class CreateNewContactViewController implements Initializable {

    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField address;
    @FXML
    private TextField phoneNumber;
    @FXML
    private TextArea notes;
    @FXML
    private DatePicker dateOfBirth;
    @FXML
    private Label header;
    @FXML
    private Label errorMessage;
    @FXML
    private ImageView imageView;

    private boolean otherImageSelected;

    private File imageFile;
    private Contacts contact;

    /**
     * Initialize controller class
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dateOfBirth.setValue(LocalDate.now().minusYears(20));
        otherImageSelected = false;
        try {
            imageFile = new File("./src/images/defaultContact.png");
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image image = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(image);

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }

    }

    /**
     * This method will open dialouge box to choose image for contact
     */
    public void chooseImageButtonAction(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        FileChooser fc = new FileChooser();
        fc.setTitle("Select Image");

        FileChooser.ExtensionFilter jpg = new FileChooser.ExtensionFilter("Image File (*.jpg)", "*.jpg");
        FileChooser.ExtensionFilter png = new FileChooser.ExtensionFilter("Image File (*.png)", "*.png");
        fc.getExtensionFilters().addAll(jpg, png);

        String userDirectoryString = System.getProperty("user.home") + "\\Pictures";
        File userDirectory = new File(userDirectoryString);

        if (!userDirectory.canRead()) {
            userDirectory = new File(System.getProperty("user.home"));
        }

        fc.setInitialDirectory(userDirectory);

        File temp = fc.showOpenDialog(stage);

        if (temp != null) {
            imageFile = temp;

            if (imageFile.isFile()) {
                try {
                    BufferedImage bufferedImage = ImageIO.read(imageFile);
                    Image img = SwingFXUtils.toFXImage(bufferedImage, null);
                    imageView.setImage(img);
                    otherImageSelected = true;
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }

    /**
     * This method will return scene to All contacts tableView
     */
    public void cancelButtonAction(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("AllContactTableView.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("All Contacts");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method Save contact or update contact in database or static object
     * of contact
     */
    public void saveButtonAction(ActionEvent event) throws IOException, SQLException {
        try {
            if (contact != null) {
                contact.setContactId(contact.getContactId());
                contact.setFirstName(firstName.getText());
                contact.setLastName(lastName.getText());
                contact.setPhoneNumber(phoneNumber.getText());
                contact.setAddress(address.getText());
                contact.setDateOfBirth(dateOfBirth.getValue());
                contact.setNotes(notes.getText());
                if (otherImageSelected) {
                    contact.setImageFile(imageFile);
                }
                contact.copyImageFile();
                contact.updateDatabase();
            } else {
                if (otherImageSelected) {
                    contact = new Contacts(firstName.getText(), lastName.getText(), address.getText(), phoneNumber.getText(), notes.getText(), dateOfBirth.getValue(), imageFile);
                } else {
                    contact = new Contacts(firstName.getText(), lastName.getText(), address.getText(), phoneNumber.getText(), notes.getText(), dateOfBirth.getValue());
                }
                contact.saveToDatabase();
            }
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("AllContactTableView.fxml"));
            Parent parent = loader.load();
            Scene scene = new Scene(parent);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("All Contacts");
            stage.setScene(scene);
            stage.show();
        } catch (Exception ex) {
            errorMessage.setText(ex.getMessage());
        }

    }

    /**
     *  This method will set selected contact details in respected fields for edit
     */
    public void setData(Contacts contact) throws IOException {
        this.contact = contact;
        firstName.setText(contact.getFirstName());
        lastName.setText(contact.getLastName());
        address.setText(contact.getAddress());
        phoneNumber.setText(contact.getPhoneNumber());
        notes.setText(contact.getNotes());
        dateOfBirth.setValue(contact.getDateOfBirth());
        header.setText("Edit Contact");
        try {
            String imgLocation = ".\\src\\images\\" + contact.getImageFile().getName();
            imageFile = new File(imgLocation);
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            Image img = SwingFXUtils.toFXImage(bufferedImage, null);
            imageView.setImage(img);
        } catch (Exception ex) {
            errorMessage.setText(ex.toString());
        }
    }

}
