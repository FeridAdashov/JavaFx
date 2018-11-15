package ProjectClasses;

import Database.DB_Properties;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyPage implements Initializable {


    @FXML
    ImageView imageView;
    @FXML
    ListView<String> listViewProfiles;
    @FXML
    TextField profileNameTextField;
    @FXML
    Button add, delete;

    private ArrayList<Image> imageList;
    private int indexOfImage;
    private ArrayList<Integer> idOfImages;
    private String activeUserPage = LoginPage.userData;

    public void imagesLoad() {

        indexOfImage = -1;

        imageList = new ArrayList<>();
        idOfImages = new ArrayList<>();

        try {
            DB_Properties properties = new DB_Properties();
            Class.forName(properties.getDriver());
            Connection connection = DriverManager.getConnection(properties.getURL(), properties.getUsername(), properties.getPassword());
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM PHOTOS WHERE USERNAME = '" + activeUserPage + "' ORDER BY ID DESC");
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                InputStream is = rs.getBinaryStream("photo");
                Image image = new Image(is);
                imageList.add(image);
            }

            ps = connection.prepareStatement("SELECT id FROM PHOTOS WHERE USERNAME = '" + activeUserPage + "' ORDER BY ID DESC ");
            rs = ps.executeQuery();

            while (rs.next()) {
                idOfImages.add(rs.getInt("id"));
            }

            if (idOfImages.isEmpty()){
                idOfImages.add(0);
                imageView.setImage(null);
            }


        } catch (Exception e) {

        }

        if (imageList.size() > 0)
            imageView.setImage(imageList.get(++indexOfImage));
    }


    public void nextImage() {
        if (indexOfImage < imageList.size() - 1)
            imageView.setImage(imageList.get(++indexOfImage));
    }

    public void previousImage() {
        if (indexOfImage > 0)
            imageView.setImage(imageList.get(--indexOfImage));
    }


    public void addImage() throws Exception {

        FileChooser fileChooser = new FileChooser();
        File selectedFile = fileChooser.showOpenDialog(null);


        if (ImageIO.read(selectedFile) != null) {
            BufferedImage bf;


            ImageView imgView = new ImageView();
            bf = ImageIO.read(selectedFile);
            Image image = SwingFXUtils.toFXImage(bf, null);
            imgView.setImage(image);
            FileInputStream fin = new FileInputStream(selectedFile);
            int len = (int) selectedFile.length();


            DB_Properties properties = new DB_Properties();
            Class.forName(properties.getDriver());
            Connection connection = DriverManager.getConnection(properties.getURL(), properties.getUsername(), properties.getPassword());
            PreparedStatement ps = connection.prepareStatement("INSERT INTO photos(id, username, photo)VALUES(?, ?, ?)");
            ps.setBinaryStream(3, fin, len);
            ps.setInt(1, Collections.max(idOfImages) + 1);
            ps.setString(2, LoginPage.userData);
            ps.executeQuery();

            imagesLoad();
        }
    }

    public void deleteImage() throws Exception {

        if (!idOfImages.isEmpty() && indexOfImage > -1) {
            DB_Properties properties = new DB_Properties();
            Class.forName(properties.getDriver());
            Connection connection = DriverManager.getConnection(properties.getURL(), properties.getUsername(), properties.getPassword());
            PreparedStatement ps = connection.prepareStatement("DELETE FROM photos WHERE id = " + idOfImages.get(indexOfImage) + " and username = '" + LoginPage.userData + "'");
            ps.executeQuery();
        }
        imagesLoad();
    }

    public void exit()throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../FXML/loginPageFXML.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root, 600, 300);
        LoginPage.mainStage.setX(250);
        LoginPage.mainStage.setY(200);
        LoginPage.mainStage.setScene(scene);
        LoginPage.mainStage.sizeToScene();
    }

    public void goToProfile() {

        if(listViewProfiles.getItems().contains(profileNameTextField.getText())) {
            add.setVisible(false);
            delete.setVisible(false);
            if(!activeUserPage.equals(LoginPage.userData))
                listViewProfiles.getItems().add(activeUserPage);
            activeUserPage = profileNameTextField.getText();
            listViewProfiles.getItems().remove(activeUserPage);
            imagesLoad();
        }
    }

    public void home(){

        if(!activeUserPage.equals(LoginPage.userData)) {
            listViewProfiles.getItems().add(activeUserPage);
            activeUserPage = LoginPage.userData;
            add.setVisible(true);
            delete.setVisible(true);
            imagesLoad();
        }
    }

    public void profilesLoad(){

        try {
            DB_Properties properties = new DB_Properties();
            Class.forName(properties.getDriver());
            Connection connection = DriverManager.getConnection(properties.getURL(), properties.getUsername(), properties.getPassword());
            PreparedStatement ps = connection.prepareStatement("SELECT USERNAME FROM  USERS WHERE USERNAME != '" + activeUserPage + "'");
            ResultSet rs = ps.executeQuery();

            ObservableList<String> items = FXCollections.observableArrayList ();
            while (rs.next())
            {
                items.add(rs.getString("username"));
            }
            listViewProfiles.setItems(items);
            listViewProfiles.setOnMousePressed(e->{
                    profileNameTextField.setText(listViewProfiles.getSelectionModel().getSelectedItem());
            });
        }catch (Exception e)
        {

        }
    }

    public void deleteProfile()throws Exception
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Removing Profile");
        alert.setHeaderText("You are Removing your Profile (" + LoginPage.userData + ")");
        alert.setContentText("Are you ok with this?");
        Optional<ButtonType> result = alert.showAndWait();
        if(result.get() == ButtonType.OK)
        {
            DB_Properties properties = new DB_Properties();
            Class.forName(properties.getDriver());
            Connection connection = DriverManager.getConnection(properties.getURL(), properties.getUsername(), properties.getPassword());
            Statement command = connection.createStatement();
            command.executeQuery("DELETE USERS WHERE USERNAME = '" + LoginPage.userData + "'");
            command.executeQuery("DELETE PHOTOS WHERE USERNAME = '" + LoginPage.userData + "'");
            exit();
        }
    }

    public void about()
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("/*** FARID ADASHOV ***/\nLearning JavaFX, 11.2018");
        alert.showAndWait();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imagesLoad();
        profilesLoad();
    }
}
