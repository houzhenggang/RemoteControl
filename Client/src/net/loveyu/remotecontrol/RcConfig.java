package net.loveyu.remotecontrol;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.HashMap;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

/**
 * ������Ϣ��
 * 
 * @author loveyu
 * 
 */
public class RcConfig {
	/**
	 * Ӧ�ó����ļ�·��
	 */
	private static String FilePath;
	/**
	 * ��ʱ�ļ�·��
	 */
	private static String CachePath;
	/**
	 * ����IP��ַ
	 */
	private static String LocalIpAddress;
	/**
	 * Զ�̵�¼��Ϣ
	 */
	private static String ServerName = "", FileServerName = "", User = "", Password = "";
	/**
	 * Զ�̶˿���Ϣ
	 */
	private static int ServerPort = 0, FileServerPort = 0;
	/**
	 * ��¼������Ϣ
	 */
	private static boolean SaveLoginInfo = true, ForceLogin = false, ConfigStatus = false;
	/**
	 * SD��·����Ϣ
	 */
	private static String Sdcard = "", SdcardDownload = "", SdcardLog = "";

	/**
	 * ��������״̬��Ϣ
	 * 
	 * @return ״̬
	 */
	public static boolean Status() {
		return ConfigStatus;
	}

	/**
	 * ���������ļ�
	 * 
	 * @param context
	 *            Context����
	 */
	public static void Load(Context context) {
		FilePath = context.getFilesDir().getPath();
		CachePath = context.getCacheDir().getPath();
		LocalIpAddress = getLocalIpAddress();
		LoadSeverConfig();
		LoadLoginConfig();
		LoadSdcardPath(context);
		if (FileServerPort > 0 && ServerPort > 0 && "".equals(ServerName) == false
				&& "".equals(FileServerName) == false) {
			ConfigStatus = true;
		} else {
			ConfigStatus = false;
		}
	}

	/**
	 * ����Sd��·����Ϣ
	 * 
	 * @param context
	 *            Context����
	 */
	private static void LoadSdcardPath(Context context) {
		if (sdcardStatus()) {
			File sdCardDir = Environment.getExternalStorageDirectory();
			Sdcard = sdCardDir.toString() + "/" + context.getResources().getString(R.string.sdcard_path) + "/";
			SdcardDownload = Sdcard + context.getResources().getString(R.string.sdcard_path_download) + "/";
			SdcardLog = Sdcard + context.getResources().getString(R.string.sdcard_path_log) + "/";
			File sdf = new File(Sdcard);
			if (sdf.exists()) {
				if (sdf.isFile()) {
					Sdcard = SdcardDownload = SdcardLog = "";
					return;
				}
			} else {
				if (sdf.mkdir() == false) {
					Sdcard = SdcardDownload = SdcardLog = "";
					return;
				}
			}
			sdf = new File(SdcardDownload);
			if (sdf.exists()) {
				if (sdf.isFile()) {
					SdcardDownload = "";
				}
			} else {
				if (sdf.mkdir() == false) {
					SdcardDownload = "";
				}
			}
			sdf = new File(SdcardLog);
			if (sdf.exists()) {
				if (sdf.isFile()) {
					SdcardLog = "";
				}
			} else {
				if (sdf.mkdir() == false) {
					SdcardLog = "";
				}
			}

		}
	}

	/**
	 * �Ƿ��ж�ȡ�ڴ濨��Ȩ��
	 * 
	 * @return �����ڴ濨��ȡȨ��
	 */
	public static boolean sdcardStatus() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	/**
	 * ��ȡSD��Ŀ¼
	 * 
	 * @return SD��Ŀ¼
	 */
	public static String GetSdcardPath() {
		return Sdcard;
	}

	/**
	 * ��ȡ����·��
	 * 
	 * @return ����·��
	 */
	public static String GetDownloadPath() {
		return SdcardDownload;
	}

	/**
	 * ��ȡ��־�ļ���
	 * 
	 * @return ��־�ļ���
	 */
	public static String GetLogPath() {
		return SdcardLog;
	}

	/**
	 * ���ط���������
	 */
	public static void LoadSeverConfig() {
		HashMap<String, String> map = ReadConfig(GetSeverConfigPath());
		if (map.containsKey("ServerName"))
			ServerName = map.get("ServerName");
		if (map.containsKey("ServerPort")) {
			ServerPort = Integer.parseInt(map.get("ServerPort"));
			if (ServerPort < 0)
				ServerPort = 0;
		}
		if (map.containsKey("FileServerName"))
			FileServerName = map.get("FileServerName");
		if (map.containsKey("FileServerPort")) {
			FileServerPort = Integer.parseInt(map.get("FileServerPort"));
			if (FileServerPort < 0)
				FileServerPort = 0;
		}
	}

	/**
	 * ���ص�½����
	 */
	public static void LoadLoginConfig() {
		HashMap<String, String> map = ReadConfig(GetLoginConfigPath());
		if (map.containsKey("User")) {
			User = map.get("User");
		}
		if (map.containsKey("Password")) {
			Password = map.get("Password");
		}
		if (map.containsKey("SaveInfo")) {
			SaveLoginInfo = Boolean.parseBoolean(map.get("SaveInfo"));
		}
		if (map.containsKey("ForceLogin")) {
			ForceLogin = Boolean.parseBoolean(map.get("ForceLogin"));
		}
	}

	/**
	 * д���½����
	 */
	public static void WriteLoginConfig() {
		try {
			if (SaveLoginInfo) {
				FileAction.writeFile(RcConfig.GetLoginConfigPath(), "User\t" + User + "\nPassword\t" + Password
						+ "\nSaveInfo\t" + SaveLoginInfo + "\nForceLogin\t" + ForceLogin);
			} else {
				FileAction.writeFile(RcConfig.GetLoginConfigPath(), "SaveInfo\t" + SaveLoginInfo);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * ���õ�¼��Ϣ
	 * 
	 * @param user
	 *            �û���
	 * @param password
	 *            ����
	 * @param save
	 *            �Ƿ񱣴���Ϣ
	 * @param force
	 *            �Ƿ�ǿ�Ƶ�½
	 */
	public static void SetLoginConfig(String user, String password, boolean save, boolean force) {
		User = user;
		Password = password;
		SaveLoginInfo = save;
		ForceLogin = force;
	}

	/**
	 * ��ȡ����IP��ַ
	 * 
	 * @return ʧ�ܷ���null
	 */
	private static String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			RcDebug.e("debug", ex.toString());
		}
		return null;
	}

	/**
	 * ��ȡ�����ļ�Ϊһ��hash��
	 * 
	 * @param path
	 *            �����ļ�·��
	 * @return hasl��
	 */
	public static HashMap<String, String> ReadConfig(String path) {
		HashMap<String, String> map = new HashMap<String, String>();

		String c;
		try {
			c = FileAction.readFile(path);
		} catch (IOException e) {
			e.printStackTrace();
			return map;
		}
		for (String str : c.split("\n")) {
			String[] in = str.split("\t");
			if (in.length != 2)
				continue;
			in[0] = in[0].trim();
			in[1] = in[1].trim();
			if (in[0].length() > 0 && in[1].length() > 0)
				map.put(in[0], in[1]);
		}
		return map;
	}

	/**
	 * ��������Ƿ�������
	 * 
	 * @param context
	 *            Context����
	 * @return ��������״̬
	 */
	public static boolean NetConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm != null) {
			NetworkInfo[] info = cm.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;

	}

	/**
	 * ��ȡIP��ַ
	 * 
	 * @return IP��ַ
	 */
	public static String GetIpAddress() {
		return LocalIpAddress;
	}

	/**
	 * ��ȡ�����������ļ�·��
	 * 
	 * @return �ļ�·��
	 */
	public static String GetSeverConfigPath() {
		return FilePath + "/server.conf";
	}

	/**
	 * ��ȡ��¼��Ϣ�����ļ�·��
	 * 
	 * @return �ļ�·��
	 */
	public static String GetLoginConfigPath() {
		return CachePath + "/login.conf";
	}

	/**
	 * ��ȡ����������
	 * 
	 * @return IP������
	 */
	public static String GetServerName() {
		return ServerName;
	}

	/**
	 * ��ȡ�ļ����������·��
	 * 
	 * @return IP������
	 */
	public static String GetFileServerName() {
		return FileServerName;
	}

	/**
	 * ��ȡ�������˿�
	 * 
	 * @return �˿ں�,Ĭ��2001
	 */
	public static int GetSevrePort() {
		return ServerPort;
	}

	/**
	 * ��ȡ�ļ�����˿�
	 * 
	 * @return �˿ںţ�Ĭ��2002
	 */
	public static int GetFileSevrePort() {
		return FileServerPort;
	}

	/**
	 * ��ȡ�Ƿ񱣴��½����
	 * 
	 * @return ������Ϣ��״̬
	 */
	public static boolean GetSaveLoginInfo() {
		return SaveLoginInfo;
	}

	/**
	 * ��ȡ�Ƿ�ǿ�Ƶ�½
	 * 
	 * @return ǿ�Ƶ�½��״̬
	 */
	public static boolean GetForceLogin() {
		return ForceLogin;
	}

	/**
	 * ��ȡ�û���
	 * 
	 * @return ��½�û���
	 */
	public static String GetUser() {
		return User;
	}

	/**
	 * ��ȡ��¼������
	 * 
	 * @return ��¼����
	 */
	public static String GetPassword() {
		return Password;
	}

}
