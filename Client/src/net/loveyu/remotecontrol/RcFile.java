package net.loveyu.remotecontrol;

import android.annotation.SuppressLint;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * �ļ�������
 * 
 * @author loveyu
 * 
 */
public class RcFile {
	/**
	 * ��ȡ�ļ��Ļ����ļ���
	 * 
	 * @param path
	 *            �ļ�·��
	 * @return �ļ���
	 */
	public static String BaseName(String path) {
		return path.replaceAll("^.+[\\\\\\/]", "").trim();
	}

	/**
	 * ��ȡ�ļ�������Ŀ¼
	 * 
	 * @param path
	 *            �ļ�·��
	 * @return Ŀ¼
	 */
	public static String ParentName(String path) {
		path = path.trim();
		int i1 = path.lastIndexOf('\\');
		int i2 = path.lastIndexOf('/');
		if (i1 < i2) {
			i1 = i2;
		}
		if (i1 == -1)
			return path;
		if (i1 == path.length() - 1) {
			return ParentName(path.substring(0, i1));
		}
		return path.substring(0, i1).trim();
	}

	/**
	 * �Ƚ�·���Ƿ����
	 * 
	 * @param a
	 *            ·��A
	 * @param b
	 *            ·��B
	 * @return �Ƿ����
	 */
	public static boolean CompPath(String a, String b) {
		if (a == null || b == null)
			return false;
		a += "\\";
		b += "\\";
		a = a.replaceAll("[\\\\/]+", "\\\\");
		b = b.replaceAll("[\\\\/]+", "\\\\");
		return a.equals(b);
	}

	/**
	 * ����һ�����ӻ����ļ���С
	 * 
	 * @param size
	 *            �ļ���longֵ
	 * @param save
	 *            Ҫ������С��λ
	 * @return ����λ�Ĵ�С�ַ����ַ���
	 */
	public static String ShowSize(double size, int save) {
		String[] type = { "B", "KB", "MB", "GB", "TB", "PB", "EB" };
		for (int i = 0; i < type.length - 1; i++) {
			if (size > 1024) {
				size /= 1024;
			} else {
				return String.format("%." + save + "f" + type[i], size);
			}
		}
		if (type.length == 0)
			return String.format("%." + save + "f", size);
		if (type.length == 1)
			return String.format("%." + save + "f" + type[0], size);
		return String.format("%." + save + "f" + type[type.length - 1], size);
	}

	/**
	 * ��ʼ�����ļ�
	 * 
	 * @param path
	 *            Զ���ļ�·��
	 * @param size
	 *            �ļ���С
	 * @param fid
	 *            �ļ���ΨһID
	 */
	public static void Download(String path, String size, String fid) {
		RcDebug.Log("File download:" + path + ",size:" + size + ",fid:" + fid);
		String dp = RcConfig.GetDownloadPath();
		if ("".equals(dp)) {
			RcDebug.tN("Download dir is not found!");
			return;
		}
		String baseName = BaseName(path);
		dp += baseName;
		long m_sze = 0;
		try {
			m_sze = Long.parseLong(size);
		} catch (Exception ex) {
			RcDebug.tN("File size error!");
			return;
		}
		if ("".equals(dp)) {
			RcDebug.tN("Can't mount sdcard!");
			return;
		}
		if (new File(dp).exists()) {
			RcDebug.tN("Download File exists!");
			return;
		}
		try {
			Socket s = new Socket(RcConfig.GetFileServerName(), RcConfig.GetFileSevrePort());
			PrintWriter output = new PrintWriter(s.getOutputStream(), false);
			output.print(RcLogin.Get().sid + "\n" + fid + "\n");
			output.flush();
			BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));

			String message = input.readLine();
			if (message == null || "".equals(message)) {
				RcDebug.tN("File get error!");
				s.close();
				return;
			}
			long tcp_size = 0;
			try {
				tcp_size = Long.parseLong(message);
				if (tcp_size != m_sze) {
					RcDebug.tN("File size can not be verified!");
					return;
				}
				output.print("send\nsend\n");
				output.flush();
			} catch (Exception ex) {
				RcDebug.tN("File get size error!");
				return;
			}
			long read = 0;
			int percentage = 0, tmp_f;
			InputStream is = s.getInputStream();
			byte[] bytes = new byte[1024];
			int rl = -1;
			ActivityDownload.AddDownloading(path, tcp_size);
			FileOutputStream fs = new FileOutputStream(dp);
			while ((rl = is.read(bytes, 0, 1024)) != -1) {
				fs.write(bytes, 0, rl);
				read += rl;
				if (tcp_size != 0) {
					tmp_f = (int) (read * 1000 / tcp_size);
					if (tmp_f > percentage + 5) {
						percentage = tmp_f;
						ActivityDownload.UpdateDownloading(path, percentage, tcp_size);
					}
				}
				if (ActivityMain.runing == false) {
					break;
				}
			}
			s.close();
			fs.close();
			if (read != tcp_size) {
				RcDebug.tN("Download Error:" + baseName + "\nRead:" + read + "\nSize:" + tcp_size);
				ActivityDownload.UpdateDownloadingIsError(path);
				new File(dp).delete();
				return;
			}
			ActivityDownload.AddDownloaded(path, tcp_size);
			ActivityDownload.RemoveDownloading(path);
			RcDebug.tN(BaseName(path) + "\nDownload Finish.");
			RcDebug.Log("File download finish:" + fid);
		} catch (UnknownHostException e) {
			RcDebug.tN("Download Ex: Unknown Host");
		} catch (Exception e) {
			RcDebug.tN("Download Ex: " + e.getMessage());
		}
	}

	/**
	 * �����ļ��б����б�
	 * 
	 * @param info
	 *            ԭʼ�ı�Ŀ¼��Ϣ
	 * @return ��ϸ���б�
	 */
	@SuppressLint("SimpleDateFormat")
	public static List<HashMap<String, String>> ParseDirInfo(String info) {
		List<HashMap<String, String>> rt = new ArrayList<HashMap<String, String>>();
		String[] list = info.split("\\n");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (String str : list) {
			String[] item = str.split("\\t");
			if (item.length < 3)
				continue;
			HashMap<String, String> map = new HashMap<String, String>();
			if (item.length > 3) {
				map.put("Size", item[3]);
			}
			map.put("Type", item[0]);
			map.put("Name", item[1]);
			map.put("Date", item[2]);
			map.put("ShowDate", sdf.format(Long.parseLong(item[2]) * 1000));
			map.put("BaseName", BaseName(item[1]));
			rt.add(map);
		}
		return rt;
	}

}
