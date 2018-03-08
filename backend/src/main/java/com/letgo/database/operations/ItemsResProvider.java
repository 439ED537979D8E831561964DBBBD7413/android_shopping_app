package com.letgo.database.operations;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.sql.Blob;

import com.letgo.objects.Item;
import com.letgo.objects.User;
import com.letgo.utils.Constants;

public class ItemsResProvider {

	private static final String UPDATE_ITEM = "UPDATE " +
			Constants.ITEMS +
			" SET "+ Constants.NAME + " =?, " +
			Constants.DESCRIPTION + " =?, " +
			Constants.CATEGORY + " =?, " +
			Constants.PRICE + " =?, " +
			Constants.IMAGE + " =?, " +
			Constants.USERNAME + " =?, " +
			Constants.SOLD + " =?" +
			" WHERE " + Constants.ID + "=?;";

	private static final String GET_ITEMS_BY_USERNAME = "SELECT * FROM  " + Constants.FAVOURITE_ITEM + " WHERE " + Constants.USERNAME + "=?;";

	private static final String GET_ITEM_BY_ID = "SELECT * FROM  " + Constants.ITEMS + " WHERE " + Constants.ID + "=?;";

	private static final String GET_IMAGE =  "SELECT " + Constants.IMAGE + " FROM " + Constants.ITEMS + " WHERE " + Constants.ID + " =?;";

	private static final String INSERT_ITEM = "INSERT INTO " + Constants.ITEMS + " (" +
			Constants.ID + ", " +
			Constants.NAME + ", " +
			Constants.DESCRIPTION + ", " +
			Constants.CATEGORY + ", " +
			Constants.PRICE + ", " +
			Constants.IMAGE + ", " +
			Constants.USERNAME + ", " +
			Constants.SOLD +
			")" +
			"VALUES (?, ?, ?, ?, ?, ?, ?, ?);";

	private static final String DELETE_ITEM_BY_ID = "DELETE FROM " + Constants.ITEMS + " WHERE " + Constants.ID + " =?;";

	private static final String DELETE_ALL_FOR_USER = "DELETE FROM " + Constants.ITEMS + " WHERE " + Constants.USERNAME + " =?;";

	private static final String GET_ALL = "SELECT * FROM " + Constants.ITEMS + " WHERE " + Constants.SOLD + " = 0;"; // get items

	private static final String REMOVE_FAVE_ITEM = "DELETE FROM " + Constants.FAVOURITE_ITEM + " WHERE " +
				Constants.ID + " =? AND " + Constants.USERNAME + " =?;";

	private static final String INSERT_FAVE_ITEM = "INSERT INTO " + Constants.FAVOURITE_ITEM + " (" +
			Constants.ID + ", " +
			Constants.USERNAME +
			")" +
			"VALUES (?, ?);";
	private static final java.lang.String GET_ALL_SOLD  = "SELECT * FROM " + Constants.ITEMS + " WHERE " + Constants.USERNAME + " =?  AND " + Constants.SOLD + " = 1;";

	public List<Item> getAllItems(Connection conn)
			throws SQLException {
		return prepItems(conn);
	}

	public List<Item> getItemForUser(String username, Connection conn)
			throws SQLException {
		return getFaveItems(username, conn);
	}

	public List<Item> getSoldItemForUser(String username, Connection conn) throws SQLException {
		List<Item> results = new ArrayList<>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		String uanme = null;
		byte[] image = null;

		try {

			ps = conn.prepareStatement(GET_ALL_SOLD);
			ps.setString(1, username);
			rs = ps.executeQuery();

			while (rs.next()) {

				String id = rs.getString(Constants.ID);
				String name = rs.getString(Constants.NAME);
				String description = rs.getString(Constants.DESCRIPTION);
				long price = rs.getLong(Constants.PRICE);
				String category = rs.getString(Constants.CATEGORY);
				boolean sold = rs.getBoolean(Constants.SOLD);



				Blob imageBlob = rs.getBlob(Constants.IMAGE);
				image = null;

				if (imageBlob != null) {
					image = imageBlob.getBytes(1, (int) imageBlob.length());
				}

				Item item = new Item(id, name, category, price, description, image, username, sold);

				results.add(item);

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

	private List<Item> prepItems(Connection conn) throws SQLException{
		List<Item> results = new ArrayList<>();
		ResultSet rs = null;
		PreparedStatement ps = null;
		String uanme = null;
		byte[] image = null;

		try {

			ps = conn.prepareStatement(GET_ALL);

			rs = ps.executeQuery();

			while (rs.next()) {

				String id = rs.getString(Constants.ID);
				String name = rs.getString(Constants.NAME);
				String description = rs.getString(Constants.DESCRIPTION);
				long price = rs.getLong(Constants.PRICE);
				String category = rs.getString(Constants.CATEGORY);
				boolean sold = rs.getBoolean(Constants.SOLD);

				uanme = rs.getString(Constants.USERNAME);

				Blob imageBlob = rs.getBlob(Constants.IMAGE);
				image = null;

				if (imageBlob != null) {
					image = imageBlob.getBytes(1, (int) imageBlob.length());
				}

				Item item = new Item(id, name, category, price, description, image, uanme, sold);

				results.add(item);

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

	private List<Item> getFaveItems(String username, Connection conn) throws SQLException{
		List<Item> results = new ArrayList<>();
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement ps = null;
		PreparedStatement stt = null;

		String uanme = null;
		byte[] image = null;

		try {

			ps = conn.prepareStatement(GET_ITEMS_BY_USERNAME);
			ps.setString(1, username);

			rs = ps.executeQuery();

			while (rs.next()) {

				String id = rs.getString(Constants.ID);
				stt = conn.prepareStatement(GET_ITEM_BY_ID);
				rs1 = stt.executeQuery();

				if(rs1.next()){
					String name = rs1.getString(Constants.NAME);
					String description = rs1.getString(Constants.DESCRIPTION);
					long price = rs1.getLong(Constants.PRICE);
					String category = rs1.getString(Constants.CATEGORY);
					boolean sold = rs1.getBoolean(Constants.SOLD);

					Blob imageBlob = rs.getBlob(Constants.IMAGE);
					image = null;

					if (imageBlob != null) {
						image = imageBlob.getBytes(1, (int) imageBlob.length());
					}

					Item item = new Item(id, name, category, price, description, image, username, sold);

					results.add(item);
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

	public boolean removeFaveItem(String id, String username, Connection conn){
		boolean result = false;

		PreparedStatement ps = null;

		try {

			// its execute insert
			ps = (PreparedStatement) conn.prepareStatement(REMOVE_FAVE_ITEM);

			ps.setString(1, id);
			ps.setString(2, username);


			ps.execute();

			result = true;

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

	public boolean insertFaveItem(String id, String username, Connection conn){
		boolean result = false;

		PreparedStatement ps = null;

		try {

			// its execute insert
			ps = (PreparedStatement) conn.prepareStatement(INSERT_FAVE_ITEM);
			ps.setString(1, id);
			ps.setString(2, username);

			ps.execute();

			result = true;

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

	public boolean insertItem(Item obj, Connection conn) {

		boolean result = false;
		ResultSet rs = null;
		ResultSet rs1 = null;
		PreparedStatement ps = null;
		PreparedStatement stt = null;

		try {

			String id = obj.getId();
			String name = obj.getName();
			String description = obj.getDesc();
			long price = obj.getPrice();
			String category = obj.getCategory();
			String username = obj.getUser();
			boolean sold = obj.isSold();

			byte[] imageBytes = obj.getImage();

			if (imageBytes == null) {
				imageBytes = getImage(id, conn);
			}

			stt = (PreparedStatement) conn.prepareStatement(GET_ITEM_BY_ID);
			stt.setString(1, id);

			if (stt.execute()) {
				rs1 = stt.getResultSet();
				if (rs1.next()) {
					// its execute update
					ps = (PreparedStatement) conn.prepareStatement(UPDATE_ITEM);

					ps.setString(1, name);
					ps.setString(2, description);
					ps.setString(3, category);
					ps.setLong(4, price);
					ps.setString(6, username);
					ps.setBoolean(7, sold);

					if (imageBytes != null) {
						InputStream is = new ByteArrayInputStream(imageBytes);
						ps.setBlob(5, is);

					} else {

						ps.setNull(5, Types.BLOB);
					}
					// where
					ps.setString(8, id);
					ps.execute();

					result = true;

				} else {

					// its execute insert
					ps = (PreparedStatement) conn.prepareStatement(INSERT_ITEM);

					ps.setString(1, id);
					ps.setString(2, name);
					ps.setString(3, description);
					ps.setString(4, category);
					ps.setLong(5, price);
					ps.setString(7, username);
					ps.setBoolean(8, sold);

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

	public boolean deleteAllItemsByFolderId(String username, Connection conn)
			throws SQLException {

		boolean result = false;

		PreparedStatement ps = null;

		try {

			ps = (PreparedStatement) conn.prepareStatement(DELETE_ALL_FOR_USER);

			ps.setString(1, username);

			ps.execute();

			result = true;

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

	public boolean deleteItem(Item obj, Connection conn) throws SQLException {

		boolean result = false;
		PreparedStatement ps = null;

		try {

			if (obj != null) {

				ps = (PreparedStatement) conn.prepareStatement(DELETE_ITEM_BY_ID);

				String id = obj.getId();

				ps.setString(1, id);

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

}
