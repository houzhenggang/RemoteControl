package net.loveyu.remotecontrol;

import java.io.IOException;

import android.content.Context;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

/**
 * ��¼��ʾ��������
 * 
 * @author loveyu
 * 
 */
public class RcDebug {
	public static void v(String type, String msg) {
		Log.v(type, msg);
	}

	public static void d(String type, String msg) {
		Log.d(type, msg);
	}

	public static void e(String type, String msg) {
		Log.e(type, msg);
	}

	public static void w(String type, String msg) {
		Log.w(type, msg);
	}

	/**
	 * ���������������ʾ
	 * 
	 * @param context
	 *            ������
	 * @param str
	 *            ��ʾ�ַ���
	 */
	public static void N(Context context, String str) {
		v("Notice", str);
		Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
	}

	/**
	 * ��¼��Ϣ���ļ�������
	 * 
	 * @param str
	 *            ����
	 */
	public static void Log(String str) {
		String path = RcConfig.GetLogPath();
		if (path.length() == 0)
			return;
		try {
			String time = LogTime();
			String[] logName = time.split(" ");
			if (logName.length > 0)
				FileAction.addFile(path + logName[0] + ".log", time + ":\r\n" + str + "\r\n");
		} catch (IOException e) {
		}
	}

	/**
	 * ��ʾһ���߳��е���ʾ
	 * 
	 * @param str
	 *            ��ʾ����
	 */
	public static void tN(String str) {
		if (ActivityMain.instance != null) {
			Message msg = new Message();
			msg.what = ActivityMain.MsgNotice;
			msg.obj = str;
			ActivityMain.instance.messageHandler.sendMessage(msg);
		}
	}

	/**
	 * ��ȡһ����¼ʱ��
	 * 
	 * @return ��ʽ�����ʱ��
	 */
	private static String LogTime() {
		Time time = new Time();
		time.setToNow();
		return time.format("%Y-%m-%d %H:%M:%S");
	}

}
