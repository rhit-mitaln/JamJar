package jamjar.ui;

import jamjar.models.ListenerStats;
import jamjar.services.UserService;

import javax.swing.*;
import java.awt.*;

public class ListenerStatsUI extends JFrame {

    public ListenerStatsUI(String username, UserService userService) {
        super("My Listening Stats");

        setSize(400, 250);
        setLayout(new GridLayout(4, 2, 10, 10));
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        ListenerStats stats = userService.getListenerStats(username);

        String song = (stats == null || stats.favoriteSong == null)
                ? "Not set"
                : stats.favoriteSong;

        String artist = (stats == null || stats.favoriteArtist == null)
                ? "Not set"
                : stats.favoriteArtist;

        String genre = (stats == null || stats.favoriteGenre == null)
                ? "Not set"
                : stats.favoriteGenre;

        add(new JLabel("Favorite Song:"));
        add(new JLabel(song));

        add(new JLabel("Favorite Artist:"));
        add(new JLabel(artist));

        add(new JLabel("Favorite Genre:"));
        add(new JLabel(genre));

        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> dispose());

        add(new JLabel());
        add(closeBtn);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
