package jamjar.ui;

import javax.swing.*;
import java.awt.*;

import jamjar.services.UserService;

public class JamJarHomeUI extends JFrame {

    public JamJarHomeUI(String username, UserService userService) {
        super("JamJar - Home");

        setSize(500, 350);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        boolean isArtist = userService.isArtist(username);

        // ===== Top welcome =====
        JLabel welcomeLabel = new JLabel(
                "Welcome to JamJar, " + username + "!",
                SwingConstants.CENTER
        );
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(welcomeLabel, BorderLayout.NORTH);

        // ===== Center buttons =====
        JPanel centerPanel = new JPanel(new GridLayout(0, 1, 10, 10));

        JButton updateProfileBtn = new JButton("Update Profile");

        centerPanel.add(updateProfileBtn);
        
        JButton viewStatsBtn = new JButton("View My Stats");
        centerPanel.add(viewStatsBtn);

        viewStatsBtn.addActionListener(e ->
                new ListenerStatsUI(username, userService)
        );
  

        if (isArtist) {
            JButton manageSongsBtn = new JButton("Manage My Songs");
            JButton artistAnalyticsBtn = new JButton("View My Analytics");

            centerPanel.add(manageSongsBtn);
            centerPanel.add(artistAnalyticsBtn);

            manageSongsBtn.addActionListener(e ->
                    JOptionPane.showMessageDialog(this, "Artist feature coming soon!")
            );

            artistAnalyticsBtn.addActionListener(e ->
                    JOptionPane.showMessageDialog(this, "Artist analytics coming soon!")
            );
         }
        
        
        
        JButton logoutBtn = new JButton("Logout");
        centerPanel.add(logoutBtn);

        add(centerPanel, BorderLayout.CENTER);

        // ===== Button actions =====

        updateProfileBtn.addActionListener(e ->
                new UpdateUserUI(username, userService)
        );

        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginUI(userService);
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
