package contacts;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class AllContactTableViewController implements Initializable {

    @FXML
    private TableView contactTableView;
    @FXML
    private TableColumn<Contacts, Integer> contactID;
    @FXML
    private TableColumn<Contacts, String> firstName;
    @FXML
    private TableColumn<Contacts, String> lastName;
    @FXML
    private TableColumn<Contacts, String> address;
    @FXML
    private TableColumn<Contacts, String> phoneNumber;
    @FXML
    private TableColumn<Contacts, String> Notes;
    @FXML
    private TableColumn<Contacts, LocalDate> dateOfBirth;
    @FXML 
    private TextField searchBar;
    
    /**
     * Initializes the controller class. and populates coloumn
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        contactID.setCellValueFactory(new PropertyValueFactory<Contacts, Integer>("contactId"));
        firstName.setCellValueFactory(new PropertyValueFactory<Contacts, String>("firstName"));
        lastName.setCellValueFactory(new PropertyValueFactory<Contacts, String>("lastName"));
        address.setCellValueFactory(new PropertyValueFactory<Contacts, String>("address"));
        phoneNumber.setCellValueFactory(new PropertyValueFactory<Contacts, String>("phoneNumber"));
        Notes.setCellValueFactory(new PropertyValueFactory<Contacts, String>("notes"));
        dateOfBirth.setCellValueFactory(new PropertyValueFactory<Contacts, LocalDate>("dateOfBirth"));
        /**
         *  Class having methods to create Observable list for Table View
         */
        ContactObjects co=new ContactObjects();
        try {
            contactTableView.getItems().addAll(co.initialise());
        } catch (SQLException ex) {
            System.err.println(ex.toString());
        }
    }
    
    /**
     *  This method will change scene to Create New Contact
     */
    public void createButtonAction(ActionEvent event) throws IOException {

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("CreateNewContactView.fxml"));
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Create New Contact");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     *  This method will change scene to Edit Contact and call setData Method in Create New Contact Controller class
     */
    public void editButtonAction(ActionEvent event) throws IOException {
        Contacts contact = (Contacts) this.contactTableView.getSelectionModel().getSelectedItem();
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("CreateNewContactView.fxml"));
        Parent parent = loader.load();

        Scene scene = new Scene(parent);
        CreateNewContactViewController controller = loader.getController();
        controller.setData(contact);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Edit Contact");
        stage.setScene(scene);
        stage.show();
    }
    
    /**
     *  This method will filter contacts that will have search text
     */
    public void searchButtonAction(ActionEvent event) throws SQLException
    {
        String text=searchBar.getText();
        ContactObjects co=new ContactObjects();
        // To get all contacts
        ObservableList<Contacts> list=co.initialise();
        // New Observable List to create filtered list
        ObservableList<Contacts> filtered=FXCollections.observableArrayList();
        for(Contacts obj:list)
        {
            if(obj.getFirstName().toLowerCase().contains(text.toLowerCase())||obj.getLastName().toLowerCase().contains(text.toLowerCase()))
            {
                filtered.add(obj);
            }
        }
        // clear current table view
        for(int i=0;i<contactTableView.getItems().size();i++)
        {
            contactTableView.getItems().clear();
        }
        contactTableView.getItems().addAll(filtered);
    }
        
}
