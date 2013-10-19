package net.loveyu.remotecontrol;

import com.viewpagerindicator.TabPageIndicator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;

/**
 * Ӧ�ò�����ҳ��
 * 
 * @author loveyu
 * 
 */
public class ActivityMain extends FragmentActivity {
	/**
	 * ָʾ�Ľ����Ƿ񼤻�
	 */
	public static boolean runing = false;

	/**
	 * ָʾ�ý����Ƿ��ں�̨����
	 */
	public static boolean isActive = true;

	/**
	 * �û�����ҳ����ʾ������
	 */
	private String[] CONTENT;

	/**
	 * �������ĵ���ģʽ
	 */
	public static ActivityMain instance = null;

	/**
	 * ��Ϣ�������
	 */
	public Handler messageHandler;

	/**
	 * ���������Ϣ����
	 */
	public static final int MsgNotice = 1;

	/**
	 * �Է����¼��ļ�����ֱ����ת����̨����
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			moveTaskToBack(false);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * ��ʼ��ҳ�沢����Pager,��������Ϣ����
	 */
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		FragmentPagerAdapter adapter = new RemotePagerAdapter(getSupportFragmentManager());

		ViewPager pager = (ViewPager) findViewById(R.id.main_pager);
		pager.setAdapter(adapter);

		TabPageIndicator indicator = (TabPageIndicator) findViewById(R.id.main_indicator);
		indicator.setViewPager(pager);
		runing = true;
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
	 * �˵�ѡ��
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try {
			switch (item.getItemId()) {
			case R.id.menu_download:
				Intent download_intent = new Intent(this, ActivityDownload.class);
				startActivity(download_intent);
				break;
			case R.id.menu_file_manager:
				Intent file_manager_intent = new Intent(this, ActivityFile.class);
				startActivity(file_manager_intent);
				break;
			case R.id.menu_settings:
				Intent intent = new Intent(this, ActivitySetting.class);
				startActivity(intent);
				break;
			case R.id.menu_task_manager:
				Intent task_manager = new Intent(this, ActivityTask.class);
				startActivity(task_manager);
				break;
			case R.id.menu_help:
				Intent help = new Intent(this, ActivityHelp.class);
				startActivity(help);
				break;
			case R.id.menu_terminal:
				Intent terminal = new Intent(this, ActivityTerminal.class);
				startActivity(terminal);
				break;
			case R.id.menu_logout:
				RcNet.Get().Send(RcSendMsg.createLogout());
				Intent loginInit = new Intent(this, ActivityLogin.class);
				startActivity(loginInit);
				finish();
				RcNet.Get().Stop();
				runing = false;
				break;
			case R.id.menu_exit:
				RcNet.Get().Send(RcSendMsg.createLogout());
				RcNet.Get().Stop();
				finish();
				runing = false;
				break;
			default:
				break;
			}
		} catch (Exception ex) {

		}
		return true;
	}

	/**
	 * �����˵�
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * ����ҳ���Ž���
	 * 
	 * @author loveyu
	 * 
	 */
	class RemotePagerAdapter extends FragmentPagerAdapter {
		// { "ִ������", "����", "��Ϣ", "��ͼ", "������Ϣ", "���;���", "������Ϣ" };
		/**
		 * �Ž��๹�췽�棬��ʼ������ҳ����⣬����������
		 * 
		 * @param fm
		 *            �������ʵ��
		 */
		public RemotePagerAdapter(FragmentManager fm) {
			super(fm);
			CONTENT = new String[] { getResources().getString(R.string.run_command),
					getResources().getString(R.string.function), getResources().getString(R.string.message),
					getResources().getString(R.string.screen_shots), getResources().getString(R.string.send_message),
					getResources().getString(R.string.send_warning), getResources().getString(R.string.error_message) };
		}

		/**
		 * ��ȡ��Ӧ��Ԫ�ص�ҳ��
		 */
		@Override
		public Fragment getItem(int position) {
			String str = CONTENT[position % CONTENT.length];
			switch (position % CONTENT.length) {
			case 0:
				return PagerCommand.newInstance(str);
			case 1:
				return PagerFunction.newInstance(str);
			case 2:
				return PagerMessage.newInstance(str);
			case 3:
				return PagerScreenShots.newInstance(str);
			case 4:
				return PagerSendNotice.newInstance(str);
			case 5:
				return PagerSendWarning.newInstance(str);
			case 6:
				return PagerErrorMessage.newInstance(str);
			}
			return PagerEmpty.newInstance(str);
		}

		/**
		 * ��ȡҳ��ı�����Ϣ
		 */
		@Override
		public CharSequence getPageTitle(int position) {
			return CONTENT[position % CONTENT.length];
		}

		/**
		 * ��ȡҳ�������
		 */
		@Override
		public int getCount() {
			return CONTENT.length;
		}

	}

}
