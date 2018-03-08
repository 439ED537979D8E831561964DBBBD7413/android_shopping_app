package com.letgo.database.operations;

import com.letgo.objects.User;
import com.letgo.utils.Constants;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.sql.Blob;

public class UserResProvider {

    private static final String GET_USER_FOR_ITEM = "SELECT * FROM " + Constants.USERS + " WHERE " + Constants.USERNAME + " =?;";
    private static final String GET_USER = "SELECT * FROM " + Constants.USERS + " WHERE " + Constants.USERNAME + " =? AND " +
                                Constants.PASSWORD + " =?;";
    private static final String GET_IMAGE =  "SELECT " + Constants.IMAGE + " FROM " + Constants.USERS + " WHERE " + Constants.USERNAME + " =?;";
    private static final String INSERT_USER =  "INSERT INTO " + Constants.USERS + " (" +
            Constants.USERNAME + ", " +
            Constants.FNAME + ", " +
            Constants.LNAME + ", " +
            Constants.EMAIL +", " +
            Constants.PASSWORD +", " +
            Constants.IMAGE +") " +
            "VALUES (?, ?, ?, ?, ?, ?);";
    private static final String UPDATE_USER = "UPDATE "+ Constants.USERS + " SET " +
            Constants.FNAME + " =?, " +
            Constants.LNAME + " =?, " +
            Constants.EMAIL +" =?, "  +
            Constants.PASSWORD +" =?, " +
            Constants.IMAGE +" =?"
            + " WHERE " + Constants.USERNAME + " =?;";
    private static final String DELETE_USER = "DELETE FROM " + Constants.USERS + " WHERE " + Constants.USERNAME + " =?;";


    public boolean insertUser(User user, Connection conn){
        boolean result = false;
        ResultSet rs = null;
        ResultSet rs1 = null;
        PreparedStatement ps = null;
        PreparedStatement stt = null;

        try {

            String username = user.getUsername();
            String fname = user.getFirstName();
            String lname = user.getLastName();
            String email = user.getEmail();
            String password = user.getPassword();

            byte[] imageBytes = user.getImage();

            if (imageBytes == null) {  // if an image wasn't provided
                imageBytes = getImage(username, conn);
            }

            stt = (PreparedStatement) conn.prepareStatement(GET_USER_FOR_ITEM);
            stt.setString(1, username);

            if (stt.execute()) {
                rs1 = stt.getResultSet();
                if (rs1.next()) {
                    //  execute update
                    ps = (PreparedStatement) conn.prepareStatement(UPDATE_USER);

                    ps.setString(1, fname);
                    ps.setString(2, lname);
                    ps.setString(3, email);
                    ps.setString(4, password);

                    if (imageBytes != null) {
                        InputStream is = new ByteArrayInputStream(imageBytes);
                        ps.setBlob(5, is);

                    } else {
                        ps.setNull(5, Types.BLOB);
                    }
                    // where
                    ps.setString(6, username);

                    ps.execute();
                    result = true;

                } else {
                    // execute insert
                    ps = (PreparedStatement) conn.prepareStatement(INSERT_USER);
                    ps.setString(1, username);
                    ps.setString(2, fname);
                    ps.setString(3, lname);
                    ps.setString(4, email);
                    ps.setString(5, password);

                    if (imageBytes != null) {
                        InputStream is = new ByteArrayInputStream(imageBytes);
                        ps.setBlob(6, is);
                    } else {
                        ps.setNull(6, Types.BLOB);
                    }
                    ps.execute();
                    result = true;
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            if (rs1 != null) {
                try {
                    rs1.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (stt != null) {
                try {
                    stt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public byte[] getImage(String itemId, Connection conn)
            throws SQLException {

        byte[] result = null;

        ResultSet rs = null;
        PreparedStatement ps = null;
        try {

            ps = conn.prepareStatement(GET_IMAGE);
            ps.setString(1, itemId);
            rs = ps.executeQuery();

            while (rs.next()) {

                Blob imageBlob = rs.getBlob(1);
                if (imageBlob != null) {
                    result = imageBlob.getBytes(1, (int) imageBlob.length());
                }
            }
        } catch (SQLException e) {
            throw e;

        } catch (Throwable e) {
            e.printStackTrace();

        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return result;
    }

    public boolean deleteUser(User user, Connection conn) throws SQLException{
        boolean result = false;
        PreparedStatement ps = null;

        try {
            if (user != null) {

                ps = (PreparedStatement) conn.prepareStatement(DELETE_USER);
                String username = user.getUsername();

                ps.setString(1, username);
                ps.execute();

                result = true;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {

                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    public User getUser(String username, String password, Connection conn) throws SQLException{
        return prepUser(GET_USER, username, password, conn);
    }

    public User getUser(String username, Connection conn) throws SQLException{
        return prepUser(GET_USER_FOR_ITEM, username, null, conn);

    }

    private User prepUser(String sql, String username, String password, Connection conn) throws SQLException{
        ResultSet rs = null;
        User user = null;
        PreparedStatement ps = null;
        try {

            ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            if (password != null)
                ps.setString(2, password);

            rs = ps.executeQuery();

            while (rs.next()) {

                String fname = rs.getString(Constants.FNAME);
                String lname = rs.getString(Constants.LNAME);
                String email = rs.getString(Constants.EMAIL);


                Blob imageBlob = rs.getBlob(Constants.IMAGE);
                byte[] image = null;
                if (imageBlob != null) {
                    image = imageBlob.getBytes(1, (int) imageBlob.length());
                }
                user = new User(username, fname, lname, email, image);
                return user;
            }

        } catch (SQLException e) {
            throw e;

        } catch (Throwable e) {
            e.printStackTrace();

        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return user;
    }

}
