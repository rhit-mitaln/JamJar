package jamjar.services;


import java.sql.*;
import java.time.LocalDate;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.UUID;

import jamjar.models.ListenerStats;

public class UserService {

    private DatabaseConnectionService db;

    public UserService(DatabaseConnectionService db) {
        this.db = db;
    }

    // Login
    public boolean login(String username, String password) {

        String sql = """
            SELECT [Password], PasswordSalt, PasswordHash
            FROM [User Account]
            WHERE Username = ?
        """;

        try (PreparedStatement ps =
                 db.getConnection().prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return false;

            String storedPassword = rs.getString("Password");
            String salt = rs.getString("PasswordSalt");
            String hash = rs.getString("PasswordHash");

            // Try hashed login first
            if (salt != null && hash != null) {
                String enteredHash = hashPassword(password, salt);
                if (enteredHash.equals(hash)) return true;
            }

            // Fallback to plaintext
            return password.equals(storedPassword);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // registering
    public boolean register(String username, String fname, String lname,
            String password, String country, String city) {

	String sql = "{ call dbo.AddUser(?, ?, ?, ?, ?, ?, ?, ?) }";
	
	try (CallableStatement cs =
	 db.getConnection().prepareCall(sql)) {
	
	String salt = UUID.randomUUID().toString();
	String hash = hashPassword(password, salt);
	
	cs.setString(1, username);
	cs.setString(2, fname);
	cs.setString(3, lname);
	cs.setString(4, password); // REQUIRED by table
	cs.setString(5, country);
	cs.setString(6, city);
	cs.setString(7, salt);
	cs.setString(8, hash);
	
	cs.execute();
	return true;
	
	} catch (SQLException e) {
	// SQL THROW from sproc
	System.out.println("REGISTER FAILED:");
	System.out.println(e.getMessage());
	return false;
	} catch (Exception e) {
	e.printStackTrace();
	return false;
	}
	}

    // Password Hashing (Work on this, as there are a few issues
    private String hashPassword(String password, String salt) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(salt.getBytes());
        byte[] hashed = md.digest(password.getBytes());
        return Base64.getEncoder().encodeToString(hashed);
    }
    
    // for the update user
    public boolean updateUser(String username,
            String fname,
            String lname,
            String country,
            String city) {

	String sql = "{ call dbo.UpdateUser(?, ?, ?, ?, ?) }";
	
	try (CallableStatement cs =
	db.getConnection().prepareCall(sql)) {
	
	cs.setString(1, username);
	cs.setString(2, fname);
	cs.setString(3, lname);
	cs.setString(4, country);
	cs.setString(5, city);
	
	cs.execute();
	return true;
	
	} catch (SQLException e) {
	System.out.println("UPDATE USER FAILED:");
	System.out.println(e.getMessage());
	return false;
	}
    }
    
    // checks if the user is an artist
    public boolean isArtist(String username) {
        String sql = "SELECT 1 FROM dbo.Artist WHERE Username = ?";

        try (PreparedStatement ps =
                 db.getConnection().prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // checks if the user is a listener
    public boolean isListener(String username) {
        String sql = "SELECT 1 FROM dbo.Listener WHERE Username = ?";

        try (PreparedStatement ps =
                 db.getConnection().prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            return rs.next();

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // adds an artist
    public boolean addArtist(String username) {
        String sql = """
            INSERT INTO dbo.Artist (Username, TopSongID, TopLocation)
            VALUES (?, NULL, NULL)
        """;

        try (PreparedStatement ps =
                 db.getConnection().prepareStatement(sql)) {

            ps.setString(1, username);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // adds an listener
    public boolean addListener(String username) {
        String sql = """
            INSERT INTO dbo.Listener
            (Username, DOB, Age, FavoriteSongID, FavoriteArtistUser, FavoriteGenreID)
            VALUES (?, NULL, NULL, NULL, NULL, NULL)
        """;

        try (PreparedStatement ps =
                 db.getConnection().prepareStatement(sql)) {

            ps.setString(1, username);
            ps.executeUpdate();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    // logic to getting user information
    public ListenerStats getListenerStats(String username) {

        ensureListenerExists(username);

        String sql = """
            SELECT
                s.Name AS FavoriteSong,
                ua.FName + ' ' + ua.LName AS FavoriteArtist,
                g.genreName AS FavoriteGenre
            FROM dbo.Listener l
            LEFT JOIN dbo.Song s
                ON l.FavoriteSongID = s.sID
            LEFT JOIN dbo.Artist a
                ON l.FavoriteArtistUser = a.Username
            LEFT JOIN dbo.[User Account] ua
                ON a.Username = ua.Username
            LEFT JOIN dbo.Genre g
                ON l.FavoriteGenreID = g.genre_id
            WHERE l.Username = ?
        """;

        try (PreparedStatement ps =
                 db.getConnection().prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) return null;

            ListenerStats stats = new ListenerStats();
            stats.favoriteSong = rs.getString("FavoriteSong");
            stats.favoriteArtist = rs.getString("FavoriteArtist");
            stats.favoriteGenre = rs.getString("FavoriteGenre");

            return stats;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // since artist can also be listeners
    public void ensureListenerExists(String username) {

        String checkSql = "SELECT 1 FROM dbo.Listener WHERE Username = ?";
        String insertSql = """
            INSERT INTO dbo.Listener
            (Username, DOB, Age, FavoriteSongID, FavoriteArtistUser, FavoriteGenreID)
            VALUES (?, NULL, NULL, NULL, NULL, NULL)
        """;

        try (
            PreparedStatement checkPs =
                db.getConnection().prepareStatement(checkSql)
        ) {
            checkPs.setString(1, username);
            ResultSet rs = checkPs.executeQuery();

            if (rs.next()) {
                // Listener already exists
                return;
            }

            try (PreparedStatement insertPs =
                         db.getConnection().prepareStatement(insertSql)) {

                insertPs.setString(1, username);
                insertPs.executeUpdate();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // use this to add DOB to listeners.
    public boolean addListenerWithDOB(String username, LocalDate dob) {

        String sql = "{ call dbo.AddListener(?, ?, NULL, NULL, NULL) }";

        try (CallableStatement cs =
                 db.getConnection().prepareCall(sql)) {

            cs.setString(1, username);
            cs.setDate(2, java.sql.Date.valueOf(dob));
            cs.execute();
            return true;

        } catch (SQLException e) {
            System.out.println("ADD LISTENER FAILED:");
            System.out.println(e.getMessage());
            return false;
        }
    }
}
        
        

