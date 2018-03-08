package com.letgo.servlets;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.letgo.database.operations.ChatResProvider;
import com.letgo.database.operations.ConnPool;
import com.letgo.database.operations.ItemsResProvider;
import com.letgo.database.operations.UserResProvider;
import com.letgo.objects.Chat;
import com.letgo.objects.Item;
import com.letgo.objects.Message;
import com.letgo.objects.User;
import com.letgo.utils.Constants;
import com.letgo.utils.FilesUtils;

/**
 * Servlet implementation class ProjectResourceServlet
 */
public class ProjectResourceServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// ========
	private static final int GET_ALL_ITEMS_JSON_REQ = 0;
	private static final int GET_USER_JSON_REQ = 1;
	private static final int GET_MESSAGES_OF_CHAT_JSON_REQ = 2;
	private static final int INSERT_MESSAGE_REQ = 6;
	private static final int DELETE_ITEM_REQ = 7;
	private static final int GET_ITEM_IMAGE_REQ = 10;
	private static final int GET_USER_IMAGE_REQ = 11;
	private static final int INSERT_ITEM_FAVE_REQ = 13;
	private static final int GET_FAVE_ITEM_USER_REQ = 14;
	private static final int REMOVE_FAVE_ITEM = 15;
	private static final int GET_SOLD_ITEM_USER_REQ = 16;
	private static final int UNSYNCED_MESSAGES = 17;

	private static final String RESOURCE_FAIL_TAG = "{\"result_code\":0}";
	private static final String RESOURCE_SUCCESS_TAG = "{\"result_code\":1}";

	private static final String REQ = "req";

	public static final int DB_RETRY_TIMES = 5;


	public void init(ServletConfig config) throws ServletException {

		super.init();

		String tmp = config.getServletContext().getInitParameter("localAppDir");
		if (tmp != null) {
			FilesUtils.appDirName = config.getServletContext().getRealPath(tmp);
			System.out.println(FilesUtils.appDirName);

		}

	}

	protected void service(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		req.setCharacterEncoding("UTF-8");
		resp.setCharacterEncoding("UTF-8");

		String respPage = null;
		String userReq = req.getParameter(REQ);
		Connection conn = null;
		int retry = DB_RETRY_TIMES;

		if (userReq != null) {

			int reqNo = Integer.valueOf(userReq);
			System.out.println("ProjectResourceServlet:: req code ==>" + reqNo);

			while (retry > 0) {
				try {
					switch (reqNo) {

					case GET_ALL_ITEMS_JSON_REQ: {

						conn = ConnPool.getInstance().getConnection();
						respPage = RESOURCE_FAIL_TAG;

						ItemsResProvider itemsResProvider = new ItemsResProvider();
						List<Item> itemsList = itemsResProvider
								.getAllItems(conn);
						String resultJson = Item.toJson(itemsList);
						resp.addHeader("Content-Type",
								"application/json; charset=UTF-8");
						if (resultJson != null && !resultJson.isEmpty()) {
							respPage = resultJson;

						} else {
							resp.sendError(404);
						}

						PrintWriter pw = resp.getWriter();
						pw.write(respPage);
						retry = 0;
						break;
					}

					case GET_USER_JSON_REQ: {
						String id = req.getParameter(Constants.USERNAME);

						respPage = RESOURCE_FAIL_TAG;
						conn = ConnPool.getInstance().getConnection();
						resp.addHeader("Content-Type",
								"application/json; charset=UTF-8");

						UserResProvider userResProvider = new UserResProvider();
						User user = userResProvider.getUser(id, conn);

						if(user != null) {
							String resultJson = user.toJson();
							if (resultJson != null && !resultJson.isEmpty()) {
								respPage = resultJson;
							} else {
								resp.sendError(404);
							}
						}

						PrintWriter pw = resp.getWriter();
						pw.write(respPage);
						retry = 0;
						break;
					}

					case DELETE_ITEM_REQ: {
						String id = req.getParameter(Constants.ID);
						respPage = RESOURCE_FAIL_TAG;
						resp.addHeader("Content-Type",
								"application/json; charset=UTF-8");
						conn = ConnPool.getInstance().getConnection();

						ItemsResProvider itemsResProvider = new ItemsResProvider();
						Item item = new Item(id);
						if (itemsResProvider.deleteItem(item, conn)) {
							respPage = RESOURCE_SUCCESS_TAG;
						}
						PrintWriter pw = resp.getWriter();
						pw.write(respPage);

						retry = 0;
						break;
					}

					case INSERT_MESSAGE_REQ: {

						String sender = req.getParameter(Constants.USERNAME);
						String receiver = req.getParameter(Constants.SECONDUSERNAME);
						String msg = req.getParameter(Constants.DESCRIPTION);

						respPage = RESOURCE_FAIL_TAG;

						conn = ConnPool.getInstance().getConnection();
						UserResProvider userResProvider = new UserResProvider();
						User sUser = userResProvider.getUser(sender, conn),
								rUser = userResProvider.getUser(receiver, conn);

						Chat chat = new Chat(sUser, rUser);

						Message message = new Message(sUser, msg);
						chat.addMessege(message);

						ChatResProvider chatResProvider = new ChatResProvider();
						if (chatResProvider.insertMessage(chat, conn)) {
							respPage = RESOURCE_SUCCESS_TAG;
						}

						PrintWriter pw = resp.getWriter();
						pw.write(respPage);

						retry = 0;
						break;
					}

					case GET_ITEM_IMAGE_REQ: {
						String id = req.getParameter(Constants.ID);
						respPage = RESOURCE_FAIL_TAG;

						conn = ConnPool.getInstance().getConnection();
						ItemsResProvider itemsResProvider = new ItemsResProvider();

						byte[] imgBlob = itemsResProvider.getImage(id, conn);

						if (imgBlob != null && imgBlob.length > 0) {
							ServletOutputStream os = resp.getOutputStream();
							os.write(imgBlob);
						} else {
							resp.sendError(404);
						}

						retry = 0;
						break;
					}

					case GET_USER_IMAGE_REQ: {
						String id = req.getParameter(Constants.USERNAME);
						respPage = RESOURCE_FAIL_TAG;

						conn = ConnPool.getInstance().getConnection();
						UserResProvider userResProvider = new UserResProvider();

						byte[] imgBlob = userResProvider.getImage(id, conn);

						if (imgBlob != null && imgBlob.length > 0) {
							ServletOutputStream os = resp.getOutputStream();
							os.write(imgBlob);
						} else {
							resp.sendError(404);
						}

						retry = 0;
						break;
					}
					case GET_MESSAGES_OF_CHAT_JSON_REQ: {
						String id = req.getParameter(Constants.USERNAME);
						conn = ConnPool.getInstance().getConnection();
						respPage = RESOURCE_FAIL_TAG;

						ChatResProvider chatResProvider = new ChatResProvider();
						List<Chat> itemsList = chatResProvider
								.getAllChats(id, conn);
						String resultJson = Chat.toJson(itemsList);
						resp.addHeader("Content-Type",
								"application/json; charset=UTF-8");
						if (resultJson != null && !resultJson.isEmpty()) {
							respPage = resultJson;

						} else {
							resp.sendError(404);
						}

						PrintWriter pw = resp.getWriter();
						pw.write(respPage);
						retry = 0;
						break;
					}
					case INSERT_ITEM_FAVE_REQ:{
						String id = req.getParameter(Constants.ID);
						String username = req.getParameter(Constants.USERNAME);

						respPage = RESOURCE_FAIL_TAG;
						resp.addHeader("Content-Type",
								"application/json; charset=UTF-8");
						conn = ConnPool.getInstance().getConnection();
						ItemsResProvider itemsResProvider = new ItemsResProvider();

						if (itemsResProvider.insertFaveItem(id, username, conn)) {
							respPage = RESOURCE_SUCCESS_TAG;
						}
						PrintWriter pw = resp.getWriter();
						pw.write(respPage);

						retry = 0;
						break;
					}
					case REMOVE_FAVE_ITEM: {
						String id = req.getParameter(Constants.ID);
						String username = req.getParameter(Constants.USERNAME);

						respPage = RESOURCE_FAIL_TAG;
						resp.addHeader("Content-Type",
								"application/json; charset=UTF-8");
						conn = ConnPool.getInstance().getConnection();
						ItemsResProvider itemsResProvider = new ItemsResProvider();

						if (itemsResProvider.removeFaveItem(id, username, conn)) {
							respPage = RESOURCE_SUCCESS_TAG;
						}
						PrintWriter pw = resp.getWriter();
						pw.write(respPage);

						retry = 0;
						break;
					}
					case GET_FAVE_ITEM_USER_REQ: {

						conn = ConnPool.getInstance().getConnection();
						respPage = RESOURCE_FAIL_TAG;

						String username = req.getParameter(Constants.USERNAME);

						ItemsResProvider itemsResProvider = new ItemsResProvider();
						List<Item> itemsList = itemsResProvider
								.getItemForUser(username, conn);
						String resultJson = Item.toJson(itemsList);
						resp.addHeader("Content-Type",
								"application/json; charset=UTF-8");
						if (resultJson != null && !resultJson.isEmpty()) {
							respPage = resultJson;

						} else {
							resp.sendError(404);
						}

						PrintWriter pw = resp.getWriter();
						pw.write(respPage);
						retry = 0;
						break;
					}
					case GET_SOLD_ITEM_USER_REQ: {

						conn = ConnPool.getInstance().getConnection();
						respPage = RESOURCE_FAIL_TAG;

						String username = req.getParameter(Constants.USERNAME);

						ItemsResProvider itemsResProvider = new ItemsResProvider();
						List<Item> itemsList = itemsResProvider
								.getSoldItemForUser(username, conn);
						String resultJson = Item.toJson(itemsList);
						resp.addHeader("Content-Type",
								"application/json; charset=UTF-8");
						if (resultJson != null && !resultJson.isEmpty()) {
							respPage = resultJson;

						} else {
							resp.sendError(404);
						}

						PrintWriter pw = resp.getWriter();
						pw.write(respPage);
						retry = 0;
						break;
					}
					case UNSYNCED_MESSAGES: {
						String id = req.getParameter(Constants.USERNAME);
						conn = ConnPool.getInstance().getConnection();
						respPage = RESOURCE_FAIL_TAG;
						ChatResProvider chatResProvider = new ChatResProvider();
						List<Chat> itemsList = chatResProvider
								.getUnSynced(id, conn);
						String resultJson = Chat.toJson(itemsList);
						resp.addHeader("Content-Type",
								"application/json; charset=UTF-8");
						if (resultJson != null && !resultJson.isEmpty()) {
							respPage = resultJson;

						} else {
							resp.sendError(404);
						}

						PrintWriter pw = resp.getWriter();
						pw.write(respPage);
						retry = 0;
						break;
					}
					// == end items apis

					default:
						retry = 0;
					}

				} catch (SQLException e) {
					e.printStackTrace();
					retry--;
				} catch (Throwable t) {
					t.printStackTrace();
					retry = 0;
				} finally {
					if (conn != null) {
						ConnPool.getInstance().returnConnection(conn);
					}
				}
			}
		}
	}

}
