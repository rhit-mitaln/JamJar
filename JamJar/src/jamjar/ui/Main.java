package jamjar.ui;



import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

import jamjar.services.DatabaseConnectionService;
import jamjar.services.UserService;

public class Main {

    private static DatabaseConnectionService dbService;
    private static UserService userService;

    public static void main(String[] args) {

        Properties props = loadProperties();

        String serverUsername = props.getProperty("serverUsername");
        String serverPassword = props.getProperty("serverPassword");
        String serverName     = props.getProperty("serverName");
        String databaseName   = props.getProperty("databaseName");

        dbService = new DatabaseConnectionService(serverName, databaseName);

        if (!dbService.connect(serverUsername, serverPassword)) {
            JOptionPane.showMessageDialog(null,
                    "Could not connect to JamJar database.");
            System.exit(1);
        }

        userService = new UserService(dbService);
        new LoginUI(userService);
    }

    private static Properties loadProperties() {
        Properties props = new Properties();
        String binDir = System.getProperty("user.dir") + "/bin/";

        try (FileInputStream fis =
                     new FileInputStream(binDir + "jamjar.properties")) {
            props.load(fis);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "jamjar.properties could not be loaded.");
            System.exit(1);
        }
        return props;
    }
}