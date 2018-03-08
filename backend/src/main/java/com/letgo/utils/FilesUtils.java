package com.letgo.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FilesUtils {

	public static String appDirName = "";

	public static boolean writeLocalCopy(String fileName, byte[] bytes, boolean append) {
		boolean res = false;
		FileOutputStream output = null;
		try {

			File root = new File(appDirName);
			if(!root.exists()){
				root.mkdirs();
			}
			
			File file = new File(root,fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			output = new FileOutputStream(file, append);
			output.write(bytes);
			res = true;
			System.out.println("file created ==>" + file.getAbsolutePath());
		} catch (Throwable e) {
			e.printStackTrace();
			res = false;
		} finally {
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return res;

	}



	public static byte[] getLocalCopy(File file) {

		if (!file.exists()) {
			return new byte[0];
		}

		ByteArrayOutputStream stream = new ByteArrayOutputStream(4096 * 2);

		InputStream in = null;
		try {
			in = new FileInputStream(file);
			byte[] buffer = new byte[4096 * 2];
			int n = -1;
			while ((n = in.read(buffer)) != -1) {
				if (n > 0) {
					stream.write(buffer, 0, n);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return stream.toByteArray();
	}

}
