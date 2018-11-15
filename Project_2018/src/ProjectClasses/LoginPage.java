package ProjectClasses;

import Database.DB_Properties;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class LoginPage {

    static Stage mainStage;
    static String userData;

    @FXML
    private TextField username;
    @FXML
    private TextField password;
    @FXML
    private Label error_label;


    protected void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../FXML/loginPageFXML.fxml"));
        Scene scene = new Scene(root, 600, 300);
        mainStage = stage;
        mainStage.setScene(scene);
        mainStage.show();


    }

    public void signInButtonAction() throws Exception {

        DB_Properties properties = new DB_Properties();
        Class.forName(properties.getDriver());
        Connection connection = DriverManager.getConnection(properties.getURL(), properties.getUsername(), properties.getPassword());
        Statement command = connection.createStatement();
        ResultSet resultSet = command.executeQuery("select count(username) as \"num\" from users where username = '" + username.getText() + "'");
        resultSet.next();

        if(resultSet.getString("num").equals("1")){
            resultSet = command.executeQuery("select password from users where username = '" + username.getText() + "'");
            resultSet.next();
            if(resultSet.getString("password").equals(password.getText()))
            {
                userData = username.getText();
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../FXML/myPageFXML.fxml"));
                Parent root = fxmlLoader.load();
                root.getStylesheets().add(getClass().getResource("../CSS/myPageFXML.css").toString());
                Scene scene = new Scene(root, 1000, 650);
                mainStage.setX(40);
                mainStage.setY(20);
                mainStage.setScene(scene);
                mainStage.sizeToScene();
            }
            else {
                error_label.setText("Wrong Password");
                error_label.setTextFill(Color.web("#c32544"));
            }
        }else {
            error_label.setText("Username not found");
            error_label.setTextFill(Color.web("#c32544"));
        }
    }

    public void signUpButtonAction() throws Exception{

        if (!checkRightInputOfSignUp()) return;

        DB_Properties properties = new DB_Properties();
        Class.forName(properties.getDriver());
        Connection connection = DriverManager.getConnection(properties.getURL(), properties.getUsername(), properties.getPassword());
        Statement command = connection.createStatement();
        command.executeQuery("INSERT INTO USERS VALUES ('" + username.getText().trim() + "', '" + password.getText().trim() + "')");
        command.executeQuery("commit");
        error_label.setText("User SAVED (You can SignIn)");
        error_label.setTextFill(Color.web("#18e0c4"));
    }

    public boolean checkRightInputOfSignUp() throws Exception {
        if (username.getText().equals("") ||  password.getText().equals("")){
            error_label.setText("Please enter characters");
            return false;
        }

        DB_Properties properties = new DB_Properties();
        Class.forName(properties.getDriver());
        Connection connection = DriverManager.getConnection(properties.getURL(), properties.getUsername(), properties.getPassword());
        Statement command = connection.createStatement();
        ResultSet resultSet = command.executeQuery("select count(username) as \"num\" from users where username = '" + username.getText() + "'");
        resultSet.next();

        if (!resultSet.getString("num").equals("0")) {
            error_label.setText("Please try different USERNAME");
            return false;
        }

        return true;
    }
}
