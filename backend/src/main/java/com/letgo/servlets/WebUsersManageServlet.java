package com.letgo.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.letgo.database.operations.ConnPool;
import com.letgo.database.operations.UserResProvider;
import com.letgo.objects.User;
import com.letgo.utils.Constants;
import com.letgo.utils.FilesUtils;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

/**
 * Servlet implementation class WebUsersManageServlet
 */


public class WebUsersManageServlet extends HttpServlet {


    private static final long serialVersionUID = 1L;

    public static final int DB_RETRY_TIMES=5;

    private static final String RESOURCE_FAIL_TAG = "{\"result_code\":0}";
    private static final String RESOURCE_SUCCESS_TAG = "{\"result_code\":1}";

    private static final String IS_DELETE = "delete";


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

        // Commons file upload classes are specifically instantiated
        DiskFileItemFactory factory = new DiskFileItemFactory();

        factory.setSizeThreshold(500000);

        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(500000);
        ServletOutputStream out = null;

        int retry = DB_RETRY_TIMES;

        PrintWriter pw = resp.getWriter();
        Connection conn = null;


        System.out.println("======= User Servlet =======");
        String username = null;
        String fname = null;
        String lname = null;
        String email = null;
        String password = null;
        String isDeleteParam = null;
        byte[] image = null;
        boolean isDelete = false;

        String respPage = RESOURCE_FAIL_TAG;
        try {

            List<FileItem> items = upload.parseRequest(req);
            Iterator<FileItem> iter = items.iterator();

            while (iter.hasNext()) {
                // Get the current item in the iteration
                FileItem item = (FileItem) iter.next();

                String fieldname = item.getFieldName();
                String fieldvalue = item.getString();

                System.out.println(fieldname + "=" + fieldvalue);

                if (fieldname.equals(Constants.USERNAME)) {
                    username = fieldvalue;
                } else if (fieldname.equals(Constants.FNAME)) {
                    fname = fieldvalue;
                } else if (fieldname.equals(Constants.LNAME)) {
                    lname  = fieldvalue;
                } else if (fieldname.equals(Constants.EMAIL)) {
                    email = fieldvalue;
                } else if(fieldname.equals(Constants.PASSWORD)) {
                    password = fieldvalue;
                }else if(fieldname.equals(IS_DELETE)){

                    isDeleteParam = req.getParameter(IS_DELETE);
                    isDelete = Boolean.parseBoolean(isDeleteParam);

                }else{
                    image = item.get();

                }
            }

            while (retry > 0) {

                try {
                    conn = ConnPool.getInstance().getConnection();

                    UserResProvider userResProvider = new UserResProvider();
                    User user = new User(username, fname, lname, email, password, image);

                    if(isDelete){

                        if(userResProvider.deleteUser(user, conn)){
                            respPage = RESOURCE_SUCCESS_TAG;
                        }

                    }
                    else{
                        if(userResProvider.insertUser(user, conn)){
                            respPage = RESOURCE_SUCCESS_TAG;
                        }

                    }

                    retry = 0;

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

            pw.write(respPage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
