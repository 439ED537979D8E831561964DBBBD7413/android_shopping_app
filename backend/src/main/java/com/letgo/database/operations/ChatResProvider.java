package com.letgo.database.operations;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.letgo.objects.Chat;
import com.letgo.objects.Message;
import com.letgo.objects.User;
import com.letgo.utils.Constants;

public class ChatResProvider {

	private static final String INSERT_CHAT = "INSERT INTO " + Constants.CHATS + "(" +
			Constants.SENDER + ", " +
			Constants.RECEIVER +
			") VALUES (?,?);";

	private static final String GET_MESSAGES = "SELECT * FROM " + Constants.MESSAGES + " WHERE "+ Constants.SENDER +" =? AND " + Constants.RECEIVER + " =? "+
			"UNION " +
			"SELECT * FROM " + Constants.MESSAGES + " WHERE " + Constants.RECEIVER +" =? AND " + Constants.SENDER + " =? ORDER BY "+ Constants.DATE+" ASC;";

	private static final String GET_CONVERSATIONS = "SELECT * FROM " + Constants.CHATS + " WHERE "+ Constants.SENDER +" =? " +
			"UNION " +
			"SELECT * FROM " + Constants.CHATS + " WHERE " + Constants.RECEIVER + " =?;";

	private static final String INSERT_MESSAGE = "INSERT INTO " + Constants.MESSAGES + "(" +
			Constants.SENDER + ", " +
			Constants.RECEIVER + ", " +
			Constants.DESCRIPTION + ", " +
			Constants.DATE +
			") VALUES (?,?,?,?);";

	private static final String GET_CHAT = "SELECT * FROM " + Constants.CHATS + " WHERE "+ Constants.SENDER +" =? AND " + Constants.RECEIVER + " =? "+
			"UNION " +
			"SELECT * FROM " + Constants.CHATS + " WHERE " + Constants.RECEIVER +" =? AND " + Constants.SENDER + " =?;";

    private static final String GET_UNSYNCED_CONVOS = "SELECT * FROM " + Constants.MESSAGES + " WHERE synced = 0 AND "+ Constants.SENDER +" =? AND " + Constants.RECEIVER + " =? ORDER BY "+ Constants.DATE + " ASC;";

    private static final String SYNC_MSGS = "UPDATE " + Constants.MESSAGES + " SET synced = 1  WHERE synced = 0 AND "+ Constants.SENDER +" =? AND " + Constants.RECEIVER + " =?;";

	public List<Chat> getAllChats(String username, Connection conn) throws SQLException {
		return preChats(GET_CONVERSATIONS, username, conn);

	}

    public List<Chat> getUnSynced(String id, Connection conn) throws SQLException {
        return preChats(GET_UNSYNCED_CONVOS, id, conn);
    }

    public boolean insertMessage(Chat obj, Connection conn) {

        boolean result = false;
        ResultSet rs = null;
        PreparedStatement ps2 = null;
        PreparedStatement ps = null;
        PreparedStatement stt = null;

        try {

            User sender = obj.getSender();
            User guest = obj.getGuest();
            String text = obj.getLastMessage().getMessage();
            Calendar calendar = Calendar.getInstance();

            ps2 = (PreparedStatement) conn.prepareStatement(GET_CHAT);
            ps2.setString(1, sender.getUsername());
            ps2.setString(2, guest.getUsername());
            ps2.setString(3, guest.getUsername());
            ps2.setString(4, sender.getUsername());

            if (ps2.execute()) {
                rs = ps2.getResultSet();
                if (!rs.next()) {

                    ps = (PreparedStatement) conn.prepareStatement(INSERT_CHAT);
                    ps.setString(1, sender.getUsername());
                    ps.setString(2, guest.getUsername());

                    ps.execute();

                    stt = (PreparedStatement) conn.prepareStatement(INSERT_MESSAGE);
                    stt.setString(1, sender.getUsername());
                    stt.setString(2, guest.getUsername());
                    stt.setString(3, text);
                    stt.setLong(4, calendar.getTime().getTime());

                    stt.execute();
                    result = true;
                }else{
                    // execute insert
                    ps = (PreparedStatement) conn.prepareStatement(INSERT_MESSAGE);
                    ps.setString(1, sender.getUsername());
                    ps.setString(2, guest.getUsername());
                    ps.setString(3, text);
                    ps.setLong(4, calendar.getTime().getTime());

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

            if (stt != null) {
                try {
                    stt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }

            if(ps2 != null) {
                try {
                    ps2.close();
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

    private List<Chat> preChats(String query, String username, Connection conn) throws SQLException {
        List<Chat> results = new ArrayList<>();
        List<Chat> tmp = new ArrayList<>();
        Chat chat = null;
        ResultSet rs = null;
        User sender, guest;
        PreparedStatement ps2, ps = null;
        UserResProvider userResProvider;

        try {
            ps = conn.prepareStatement(GET_CONVERSATIONS);
            ps.setString(1, username);
            ps.setString(2, username);

            rs = ps.executeQuery();

            userResProvider = new UserResProvider();

            while (rs.next()) {
                chat = new Chat();

                if (username.equals(rs.getNString(Constants.SENDER))) {
                    sender = userResProvider.getUser(username, conn);
                    guest = userResProvider.getUser(rs.getNString(Constants.RECEIVER), conn);

                } else {
                    sender = userResProvider.getUser(rs.getNString(Constants.SENDER), conn);
                    guest = userResProvider.getUser(username, conn);

                }

                chat.setSender(sender);
                chat.setGuest(guest);
                tmp.add(chat);
            }
            if(query.equals(GET_UNSYNCED_CONVOS)) {
                for (Chat c : tmp) {
                    getChatMsgsUnsynced(c, username, conn);

                }
            }else {
                for (Chat c : tmp){
                    getChatMsgs(c, conn);
                }
            }
            for( Chat u : tmp){
                if(u != null){
                    if(!u.getMessages().isEmpty())
                        results.add(u);
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
        return results;
    }

	private void getChatMsgs(Chat c, Connection conn) throws SQLException {

		PreparedStatement ps2 =  null, ps = null;
		ResultSet rs = null;

		try {

			ps = conn.prepareStatement(GET_MESSAGES);
			ps.setString(1, c.getSender().getUsername());
			ps.setString(2, c.getGuest().getUsername());
			ps.setString(3, c.getGuest().getUsername());
			ps.setString(4, c.getSender().getUsername());

			rs = ps.executeQuery();
			String text, sender, rec;
			long date;
			Message msg;

			while (rs.next()) {

				sender = rs.getNString(Constants.SENDER);
				rec = rs.getNString(Constants.RECEIVER);

				text = rs.getNString(Constants.DESCRIPTION);
				date = rs.getLong(Constants.DATE);
				if(rec.equals(c.getGuest().getUsername()))
					msg = new Message(c.getSender(), text);
				else
					msg = new Message(c.getGuest(), text);
				msg.setDate(date);

				c.addMessege(msg);
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
	}

    private void getChatMsgsUnsynced(Chat c, String username, Connection conn) throws SQLException {

        PreparedStatement ps = null, ps2 = null;
        ResultSet rs = null;

        try {
            String chatSender = c.getSender().getUsername();
            String chatGuset = c.getGuest().getUsername();

            ps = conn.prepareStatement(GET_UNSYNCED_CONVOS);

            if(username.equals(chatSender))
                ps.setString(1, chatGuset);
            else
                ps.setString(1, chatSender);

            ps.setString(2, username);

            rs = ps.executeQuery();
            String text;
            long date;
            Message msg;

            while (rs.next()) {

                text = rs.getNString(Constants.DESCRIPTION);
                date = rs.getLong(Constants.DATE);

                if(username.equals(chatGuset))
                    msg = new Message(c.getSender(), text);
                else
                    msg = new Message(c.getGuest(), text);

                msg.setDate(date);

                c.addMessege(msg);
            }

            ps2 = conn.prepareStatement(SYNC_MSGS);

            if(username.equals(chatSender))
                ps2.setString(1, chatGuset);
            else
                ps2.setString(1, chatSender);

            ps2.setString(2, username);

            ps2.execute();
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
            if (ps2 != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
