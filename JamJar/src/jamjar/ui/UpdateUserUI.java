package jamjar.ui;

import jamjar.services.UserService;

import javax.swing.*;
import java.awt.*;

public class UpdateUserUI extends JFrame {

    public UpdateUserUI(String username, UserService userService) {
        super("Update Profile");

        setSize(400, 300);
        setLayout(new GridLayout(6, 2, 5, 5));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JTextField fnameField = new JTextField();
        JTextField lnameField = new JTextField();
        JTextField countryField = new JTextField();
        JTextField cityField = new JTextField();

        JButton updateBtn = new JButton("Update");
        JButton cancelBtn = new JButton("Cancel");

        add(new JLabel("First Name:"));
        add(fnameField);
        add(new JLabel("Last Name:"));
        add(lnameField);
        add(new JLabel("Country:"));
        add(countryField);
        add(new JLabel("City:"));
        add(cityField);
        add(updateBtn);
        add(cancelBtn);

        updateBtn.addActionListener(e -> {
            boolean success = userService.updateUser(
                    username,
                    fnameField.getText(),
                    lnameField.getText(),
                    countryField.getText(),
                    cityField.getText()
            );

            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Profile updated successfully.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Update failed. Check console for details.");
            }
        });

        cancelBtn.addActionListener(e -> dispose());

        setLocationRelativeTo(null);
        setVisible(true);
    }
}