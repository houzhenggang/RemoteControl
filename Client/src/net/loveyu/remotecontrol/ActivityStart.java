package net.loveyu.remotecontrol;

import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.content.Intent;
import android.view.Window;

/**
 * ����ҳ
 * 
 * @author loveyu
 * 
 */
public class ActivityStart extends Activity {

	/**
	 * �ӳ������ĺ�����
	 */
	private static final long SPLASH_DELAY_MILLIS = 2000;

	/**
	 * ���ز����ļ�
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_start);
		RcConfig.Load(this);
		if (ActivityMain.runing == true) {
			goMain();
			return;
		}
		new Handler().postDelayed(new Runnable() {
			public void run() {
				goLogin();
			}
		}, SPLASH_DELAY_MILLIS);
	}

	/**
	 * ��ת����¼����
	 */
	private void goLogin() {
		Intent intent = new Intent(ActivityStart.this, ActivityLogin.class);
		ActivityStart.this.startActivity(intent);
		ActivityStart.this.finish();
	}

	/**
	 * ��ת��������
	 */
	private void goMain() {
		Intent intent = new Intent(ActivityStart.this, ActivityMain.class);
		ActivityStart.this.startActivity(intent);
		ActivityStart.this.finish();
	}

}
