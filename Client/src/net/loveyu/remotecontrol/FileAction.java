package net.loveyu.remotecontrol;

import java.io.*;
import org.apache.http.util.EncodingUtils;
import android.content.Intent;
import android.net.Uri;

/**
 * �ļ�������
 * 
 * @author loveyu
 * 
 */
public class FileAction {

	/**
	 * ��ͬ�ļ���׺��Mime����
	 */
	private final static String[][] MIME_MapTable = {
			// {��׺���� MIME����}
			{ ".3gp", "video/3gpp" }, { ".apk", "application/vnd.android.package-archive" },
			{ ".asf", "video/x-ms-asf" }, { ".avi", "video/x-msvideo" }, { ".bin", "application/octet-stream" },
			{ ".bmp", "image/bmp" }, { ".c", "text/plain" }, { ".class", "application/octet-stream" },
			{ ".conf", "text/plain" }, { ".cpp", "text/plain" }, { ".doc", "application/msword" },
			{ ".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document" },
			{ ".xls", "application/vnd.ms-excel" },
			{ ".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" },
			{ ".exe", "application/octet-stream" }, { ".gif", "image/gif" }, { ".gtar", "application/x-gtar" },
			{ ".gz", "application/x-gzip" }, { ".h", "text/plain" }, { ".htm", "text/html" }, { ".html", "text/html" },
			{ ".jar", "application/java-archive" }, { ".java", "text/plain" }, { ".jpeg", "image/jpeg" },
			{ ".jpg", "image/jpeg" }, { ".js", "application/x-javascript" }, { ".log", "text/plain" },
			{ ".m3u", "audio/x-mpegurl" }, { ".m4a", "audio/mp4a-latm" }, { ".m4b", "audio/mp4a-latm" },
			{ ".m4p", "audio/mp4a-latm" }, { ".m4u", "video/vnd.mpegurl" }, { ".m4v", "video/x-m4v" },
			{ ".mov", "video/quicktime" }, { ".mp2", "audio/x-mpeg" }, { ".mp3", "audio/x-mpeg" },
			{ ".mp4", "video/mp4" }, { ".mpc", "application/vnd.mpohun.certificate" }, { ".mpe", "video/mpeg" },
			{ ".mpeg", "video/mpeg" }, { ".mpg", "video/mpeg" }, { ".mpg4", "video/mp4" }, { ".mpga", "audio/mpeg" },
			{ ".msg", "application/vnd.ms-outlook" }, { ".ogg", "audio/ogg" }, { ".pdf", "application/pdf" },
			{ ".png", "image/png" }, { ".pps", "application/vnd.ms-powerpoint" },
			{ ".ppt", "application/vnd.ms-powerpoint" },
			{ ".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation" },
			{ ".prop", "text/plain" }, { ".rc", "text/plain" }, { ".rmvb", "audio/x-pn-realaudio" },
			{ ".rtf", "application/rtf" }, { ".sh", "text/plain" }, { ".tar", "application/x-tar" },
			{ ".tgz", "application/x-compressed" }, { ".txt", "text/plain" }, { ".wav", "audio/x-wav" },
			{ ".wma", "audio/x-ms-wma" }, { ".wmv", "audio/x-ms-wmv" }, { ".wps", "application/vnd.ms-works" },
			{ ".xml", "text/plain" }, { ".z", "application/x-compress" }, { ".zip", "application/x-zip-compressed" },
			{ "", "*/*" } };

	/**
	 * ���ַ���д�뵽�ļ�
	 * 
	 * @param fileName
	 *            �ļ�·��
	 * @param writestr
	 *            ��д����ַ���
	 * @throws IOException
	 *             IO��д�쳣
	 */
	public static void writeFile(String fileName, String writestr) throws IOException {
		FileOutputStream fout = new FileOutputStream(fileName);
		byte[] bytes = writestr.getBytes();
		fout.write(bytes);
		fout.close();
	}

	/**
	 * ��һ���ļ�����׷������
	 * 
	 * @param fileName
	 *            �ļ�·��
	 * @param content
	 *            ׷�ӵ�����
	 * @throws IOException
	 *             IO�쳣��Ϣ
	 */
	public static void addFile(String fileName, String content) throws IOException {
		FileOutputStream fout = new FileOutputStream(fileName, true);
		byte[] bytes = content.getBytes();
		fout.write(bytes);
		fout.close();
	}

	/**
	 * ���ı��ļ�����UTF-8�����ȡ�ļ�������
	 * 
	 * @param fileName
	 *            �ļ�·��
	 * @return �ļ�����
	 * @throws IOException
	 *             ��ȡ�����е��쳣
	 */
	public static String readFile(String fileName) throws IOException {
		String res = "";
		FileInputStream fin = new FileInputStream(fileName);
		int length = fin.available();
		byte[] buffer = new byte[length];
		fin.read(buffer);
		res = EncodingUtils.getString(buffer, "UTF-8");
		fin.close();
		return res;
	}

	/**
	 * ɾ��ϵͳ��ĳһ�ļ�
	 * 
	 * @param fileName
	 *            �ļ�·��
	 * @return ɾ����״̬
	 */
	public static boolean deleteFile(String fileName) {
		return new File(fileName).delete();
	}

	/**
	 * ����һ������ϵͳ���ļ���Intent
	 * 
	 * @param file
	 *            �ļ���
	 * @return ��ֱ��������ҳ
	 */
	public static Intent openFile(File file) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		String type = getMIMEType(file);
		intent.setDataAndType(Uri.fromFile(file), type);
		return intent;
	}

	/**
	 * ��ȡ�ļ���Mime����
	 * 
	 * @param file
	 *            �ļ�����
	 * @return mime����
	 */
	private static String getMIMEType(File file) {
		String type = "*/*";
		String fName = file.getName();
		int dotIndex = fName.lastIndexOf(".");
		if (dotIndex < 0) {
			return type;
		}
		String end = fName.substring(dotIndex, fName.length()).toLowerCase();
		if (end == "")
			return type;
		for (int i = 0; i < MIME_MapTable.length; i++) {
			if (end.equals(MIME_MapTable[i][0]))
				type = MIME_MapTable[i][1];
		}
		return type;
	}

}
