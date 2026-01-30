package jamjar.ui;

import jamjar.services.UserService;
import jamjar.ui.JamJarHomeUI;

import javax.swing.*;
import java.awt.*;

public class LoginUI {

    private UserService userService;

    public LoginUI(UserService userService) {
        this.userService = userService;

        JFrame frame = new JFrame("JamJar - Login");
        frame.setSize(350, 220);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 2));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Register");

        frame.add(new JLabel("Username:"));
        frame.add(usernameField);
        frame.add(new JLabel("Password:"));
        frame.add(passwordField);
        frame.add(loginBtn);
        frame.add(registerBtn);

        loginBtn.addActionListener(e -> {
            boolean success = userService.login(
                    usernameField.getText(),
                    new String(passwordField.getPassword())
            );

            if (success) {
                JOptionPane.showMessageDialog(frame, "Login successful!");
                frame.dispose();
                new JamJarHomeUI(usernameField.getText(), userService);
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid login.");
            }
        });

        registerBtn.addActionListener(e ->
                new RegisterUI(userService)
        );

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
