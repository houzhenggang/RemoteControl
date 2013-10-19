package net.loveyu.remotecontrol;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * ��¼ҳ��
 * 
 * @author loveyu
 * 
 */
public class ActivityLogin extends Activity {
	/**
	 * �Ƿ񱣴��û���Ϣ
	 */
	private boolean SaveInfo;

	/**
	 * �Ƿ�ǿ�Ƶ�½���ѷ�ֹ�û��������ط��ѵ�½�����
	 */
	private boolean ForceLogin;

	/**
	 * �û���������
	 */
	private String User, Passwrod;

	/**
	 * ���߳���Ϣ����
	 */
	public Handler messageHandler;

	/**
	 * ��Ϣ
	 */
	public static final int MsgNotice = 1;

	/**
	 * ��д�¼�������xml���֣�ͬʱʵ������Ϣ�¼�
	 */
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		if (!RcConfig.Status()) {
			OpenSetting();
		}
		LoadConfig();
		messageHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case MsgNotice:
					RcDebug.N(getApplicationContext(), (String) msg.obj);
					break;
				}
			}
		};
	}

	/**
	 * ���ز˵�
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
		return true;
	}

	/**
	 * �˵��¼�
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_settings_login:
			OpenSetting();
			break;
		case R.id.menu_help:
			Intent help = new Intent(this, ActivityHelp.class);
			startActivity(help);
			break;
		}
		return true;
	}

	/**
	 * �������ؼ�������ʱ��������
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			try {
				RcNet.Get().Stop();
			} catch (Exception ex) {

			}
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * �����ý���
	 */
	private void OpenSetting() {
		Intent intent = new Intent(this, ActivitySetting.class);
		startActivity(intent);
	}

	/**
	 * ��ʼ��½����
	 * 
	 * @param v ��¼�¼����ݵĲ���
	 */
	public void Login(View v) {
		SaveInfo = ((CheckBox) findViewById(R.id.checkBox_SaveInfo)).isChecked();
		ForceLogin = ((CheckBox) findViewById(R.id.checkBox_ForcedLogin)).isChecked();
		User = ((EditText) findViewById(R.id.editText_UserName)).getText().toString();
		Passwrod = ((EditText) findViewById(R.id.editText_Password)).getText().toString();
		if ("".equals(User) || "".equals(Passwrod)) {
			Notice(getResources().getString(R.string.user_and_password_can_not_be_empty));
			return;
		}

		RcConfig.SetLoginConfig(User, Passwrod, SaveInfo, ForceLogin);
		RcConfig.WriteLoginConfig();
		((Button) findViewById(R.id.button_Login)).setText(getResources().getString(R.string.logining));
		new Thread(new Runnable() {

			@Override
			public void run() {
				TestLogin();
			}
		}).start();
	}

	/**
	 * ���ص�½������Ϣ
	 */
	private void LoadConfig() {
		SaveInfo = RcConfig.GetSaveLoginInfo();
		((CheckBox) findViewById(R.id.checkBox_SaveInfo)).setChecked(SaveInfo);
		if (SaveInfo) {
			ForceLogin = RcConfig.GetForceLogin();
			User = RcConfig.GetUser();
			Passwrod = RcConfig.GetPassword();
			((CheckBox) findViewById(R.id.checkBox_ForcedLogin)).setChecked(ForceLogin);
			((EditText) findViewById(R.id.editText_UserName)).setText(User);
			((EditText) findViewById(R.id.editText_Password)).setText(Passwrod);
		}
	}

	/**
	 * �����Ƿ��ܹ���½
	 */
	private void TestLogin() {
		if (RcConfig.NetConnected(getApplicationContext()) == true) {
			try {
				if (RcNet.Get().Runing == false)
					RcNet.Get().Start();
				RcLogin.Get().Clear();
				RcNet.Get().Send(RcSendMsg.createLogin());
				new Thread(new Runnable() {
					// waiting network return data

					@Override
					public void run() {
						RcDebug.v("debug", "Before run status:" + RcLogin.Get().status);
						int i = 0;
						while (RcLogin.Get().status == 0) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								RcDebug.v("debug", "Login Thread ex:" + e.getMessage());
							}
							if (RcQueue.Get().Count() == 0 || ++i > 50)
								break;
						}
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
						int status = RcLogin.Get().status;
						RcDebug.v("debug", "status:" + status);
						ActivityLogin.this.setLoginButtonValue();
						if (status != 200) {
							RcNet.Get().Stop();
						}
						if (status == 0) {
							// timeout
							Notice(getResources().getString(R.string.login_timeout));
						} else if (status == 200) {
							// success
							GoMainPage();
							return;
						} else if (status == 201) {
							// must send logout
							Notice("must send logout and try agin");
						} else {
							Notice("Login Error: " + status + " " + RcLogin.Get().info);
						}

					}
				}).start();
			} catch (Exception e) {
				Notice(getResources().getString(R.string.net_server_start_exception));
				RcDebug.v("debug", "Net Start Exception:" + e.getClass().getName());
			}
		} else {
			Notice(getResources().getString(R.string.network_disconnect));
		}

	}

	/**
	 * ���õ�¼Button��ֵ
	 */
	private void setLoginButtonValue() {
		findViewById(R.id.button_Login).post(new Runnable() {

			@Override
			public void run() {
				((Button) findViewById(R.id.button_Login)).setText(getResources().getString(R.string.login));
			}
		});
	}

	/**
	 * ��ʾһ����Ϣ
	 * 
	 * @param notice
	 *            ��Ϣ����
	 */
	private void Notice(String notice) {
		Message msg = new Message();
		msg.what = ActivityMain.MsgNotice;
		msg.obj = notice;
		messageHandler.sendMessage(msg);

	}

	/**
	 * ��ת����ҳ��
	 */
	private void GoMainPage() {
		Intent intent = new Intent(ActivityLogin.this, ActivityMain.class);
		ActivityLogin.this.startActivity(intent);
		ActivityLogin.this.finish();
	}
}
