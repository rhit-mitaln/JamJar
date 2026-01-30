package jamjar.ui;

import jamjar.services.UserService;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeParseException;

public class RegisterUI extends JFrame {

    private final UserService userService;

    public RegisterUI(UserService userService) {
        super("JamJar - Register");
        this.userService = userService;

        setSize(450, 450);
        setLayout(new GridLayout(11, 2, 5, 5));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // ===== Base fields =====
        JTextField usernameField = new JTextField();
        JTextField fnameField = new JTextField();
        JTextField lnameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField countryField = new JTextField();
        JTextField cityField = new JTextField();

        // ===== Listener-only fields =====
        JTextField dobField = new JTextField("YYYY-MM-DD");
        JTextField ageField = new JTextField();

        dobField.setEnabled(false);
        ageField.setEnabled(false);

        // ===== Role selection =====
        JRadioButton listenerBtn = new JRadioButton("Listener", true);
        JRadioButton artistBtn = new JRadioButton("Artist");

        ButtonGroup roleGroup = new ButtonGroup();
        roleGroup.add(listenerBtn);
        roleGroup.add(artistBtn);

        JPanel rolePanel = new JPanel();
        rolePanel.add(listenerBtn);
        rolePanel.add(artistBtn);

        // Enable/disable listener fields
        listenerBtn.addActionListener(e -> {
            dobField.setEnabled(true);
            ageField.setEnabled(true);
        });

        artistBtn.addActionListener(e -> {
            dobField.setEnabled(false);
            ageField.setEnabled(false);
        });

        // ===== Buttons =====
        JButton createBtn = new JButton("Create Account");
        JButton cancelBtn = new JButton("Cancel");

        // ===== Layout =====
        add(new JLabel("Username:")); add(usernameField);
        add(new JLabel("First Name:")); add(fnameField);
        add(new JLabel("Last Name:")); add(lnameField);
        add(new JLabel("Password:")); add(passwordField);
        add(new JLabel("Country:")); add(countryField);
        add(new JLabel("City:")); add(cityField);
        add(new JLabel("Account Type:")); add(rolePanel);
        add(new JLabel("Date of Birth:")); add(dobField);
        add(new JLabel("Age:")); add(ageField);
        add(createBtn); add(cancelBtn);

        // ===== Create account logic =====
        createBtn.addActionListener(e -> {

            boolean registered = userService.register(
                    usernameField.getText(),
                    fnameField.getText(),
                    lnameField.getText(),
                    new String(passwordField.getPassword()),
                    countryField.getText(),
                    cityField.getText()
            );

            if (!registered) {
                JOptionPane.showMessageDialog(this,
                        "Registration failed.");
                return;
            }

            // ===== Listener logic =====
            if (listenerBtn.isSelected()) {
                try {
                    LocalDate dob = LocalDate.parse(dobField.getText());
                    int ageInput = Integer.parseInt(ageField.getText());

                    int calculatedAge =
                            Period.between(dob, LocalDate.now()).getYears();

                    if (Math.abs(calculatedAge - ageInput) > 1) {
                        JOptionPane.showMessageDialog(this,
                                "Age does not match DOB.");
                        return;
                    }

                    boolean added = userService.addListenerWithDOB(
                            usernameField.getText(), dob);

                    if (!added) {
                        JOptionPane.showMessageDialog(this,
                                "Listener creation failed.");
                        return;
                    }

                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(this,
                            "DOB must be in YYYY-MM-DD format.");
                    return;
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                            "Age must be a number.");
                    return;
                }
            } else {
                // Artist
                userService.addArtist(usernameField.getText());
            }

            JOptionPane.showMessageDialog(this,
                    "Account created successfully!");
            dispose();
        });

        cancelBtn.addActionListener(e -> dispose());

        setLocationRelativeTo(null);
        setVisible(true);
    }
}