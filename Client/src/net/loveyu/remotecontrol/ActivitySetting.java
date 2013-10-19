package net.loveyu.remotecontrol;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.app.Activity;

import java.net.InetAddress;

/**
 * ����ҳ��
 * 
 * @author loveyu
 * 
 */
public class ActivitySetting extends Activity {
	/**
	 * ������IP������
	 */
	String ServerName = "", FileServerName = "";
	/**
	 * �������˿�
	 */
	int ServerPort = 0, FileServerPort = 0;

	/**
	 * ���Ե�״̬
	 */
	boolean TestStatus;

	/**
	 * ���ؼ��ز���
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		load();
	}

	/**
	 * �����¼�
	 * 
	 * @param v
	 *            �¼�����
	 */
	public void save(View v) {
		if (read() == false) {
			return;
		}
		if (write() == false) {
			Notice(getResources().getString(R.string.config_save_error));
			return;
		}
		Notice(getResources().getString(R.string.config_saved));
		RcConfig.Load(this);
	}

	/**
	 * �����¼�
	 * 
	 * @param v
	 *            �¼�����
	 */
	public void test(View v) {
		if (RcConfig.NetConnected(getApplicationContext()) == false) {
			Notice(getResources().getString(R.string.network_disconnect));
			return;
		}
		if (read() == false)
			return;
		if (ServerName.length() > 2) {
			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					TestStatus = false;
					try {
						InetAddress ipsn = InetAddress.getByName(ServerName);
						RcDebug.v("debug", ServerName + ":" + ipsn.getHostAddress());
						TestStatus = true;
					} catch (Exception e) {
						ErrorNotice(getResources().getString(R.string.server_name_error));
						return;
					}
				}
			});
			thread.start();
			try {
				thread.join();
				if (TestStatus == false)
					return;
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		} else {
			ErrorNotice(getResources().getString(R.string.server_name_error));
			return;
		}
		if (FileServerName.length() > 2) {
			Thread thread2 = new Thread(new Runnable() {

				/**
				 * �����߳��в�������
				 */
				@Override
				public void run() {
					TestStatus = false;
					try {
						InetAddress ipsn = InetAddress.getByName(FileServerName);
						RcDebug.v("debug", FileServerName + ":" + ipsn.getHostAddress());
						TestStatus = true;
					} catch (Exception e) {
						ErrorNotice(getResources().getString(R.string.file_server_name_error));
						return;
					}
				}
			});
			thread2.start();
			try {
				thread2.join();
				if (TestStatus == false)
					return;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			ErrorNotice(getResources().getString(R.string.file_server_name_error));
			return;
		}
		if (ServerPort < 1) {
			Notice(getResources().getString(R.string.server_port_error));
			return;
		}
		if (FileServerPort < 1) {
			Notice(getResources().getString(R.string.file_server_port_error));
			return;
		}
		if (TestStatus)
			Notice(getResources().getString(R.string.test_success));
	}

	/**
	 * ���������ʾ
	 * 
	 * @param str
	 *            ��ʾ����
	 */
	private void ErrorNotice(String str) {
		Notice(str);
	}

	/**
	 * ��ȡ�����ļ�
	 * 
	 * @return ���ض�ȡ��״̬
	 */
	private boolean read() {
		try {
			ServerName = ((EditText) findViewById(R.id.editText_ServerName)).getText().toString();
			String tmp = ((EditText) findViewById(R.id.editText_ServerPort)).getText().toString();
			if (tmp.equals("") == false && Integer.parseInt(tmp) > 0)
				ServerPort = Integer.parseInt(tmp);
			else
				ServerPort = 0;
			FileServerName = ((EditText) findViewById(R.id.EditText_FileServerName)).getText().toString();
			tmp = ((EditText) findViewById(R.id.EditText_FileServerPort)).getText().toString();
			if (tmp.equals("") == false && Integer.parseInt(tmp) > 0)
				FileServerPort = Integer.parseInt(tmp);
			else
				FileServerPort = 0;

			return true;
		} catch (Exception ex) {
			Notice(getResources().getString(R.string.config_read_error) + ":" + ex.getMessage());
		}
		return false;
	}

	/**
	 * �������ļ�д�뵽ϵͳ
	 * 
	 * @return ����״̬��ʾ�Ƿ�д��ɹ�
	 */
	private boolean write() {
		try {
			FileAction.writeFile(RcConfig.GetSeverConfigPath(), "ServerName\t" + ServerName + "\nServerPort\t"
					+ ServerPort + "\nFileServerName\t" + FileServerName + "\nFileServerPort\t" + FileServerPort);
			return true;
		} catch (Exception e) {
			RcDebug.v("debug", e.getMessage());
			return false;
		}
	}

	/**
	 * ���������ļ�
	 */
	private void load() {
		ServerName = RcConfig.GetServerName();
		ServerPort = RcConfig.GetSevrePort();
		FileServerName = RcConfig.GetFileServerName();
		FileServerPort = RcConfig.GetFileSevrePort();

		((EditText) findViewById(R.id.editText_ServerName)).setText(ServerName);
		if (ServerPort > 0)
			((EditText) findViewById(R.id.editText_ServerPort)).setText("" + ServerPort);
		else
			((EditText) findViewById(R.id.editText_ServerPort)).setText("");
		((EditText) findViewById(R.id.EditText_FileServerName)).setText(FileServerName);
		if (FileServerPort > 0)
			((EditText) findViewById(R.id.EditText_FileServerPort)).setText("" + FileServerPort);
		else
			((EditText) findViewById(R.id.EditText_FileServerPort)).setText("");
	}

	/**
	 * ��ʾһ����ʾ��Ϣ
	 * 
	 * @param notice
	 *            ��ʾ����
	 */
	private void Notice(String notice) {
		Toast.makeText(this, notice, Toast.LENGTH_SHORT).show();
	}
}
