package net.loveyu.remotecontrol;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.annotation.*;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TabHost;
import android.widget.TextView;

/**
 * �ļ�����ҳ��
 * 
 * @author loveyu
 */
public class ActivityDownload extends Activity {
	/**
	 * �����е������б�
	 */
	private static List<HashMap<String, String>> downloading = new ArrayList<HashMap<String, String>>();

	/**
	 * �����ص������б�
	 */
	private static List<HashMap<String, String>> downloaded = new ArrayList<HashMap<String, String>>();

	/**
	 * ��Ϣ�������
	 */
	private Handler handler;

	/**
	 * ����ģʽ����ʵ��
	 */
	private static ActivityDownload instance = null;

	/**
	 * ��������Ϣ������
	 */
	private static final int UpdateDownloading = 1;

	/**
	 * ��������Ϣ���
	 */
	private static final int UpdateDownloaded = 2;

	/**
	 * �ж�ҳ���Ƿ��ں�̨����
	 */
	private static boolean isPause = false;

	/**
	 * ���һ���ļ��������б�����ļ������򸲸�
	 * 
	 * @param file
	 *            �������ļ�·��
	 * @param size
	 *            �ļ���С
	 */
	public static void AddDownloading(String file, long size) {
		synchronized (downloading) {
			for (int i = 0; i < downloading.size(); i++) {
				if (downloading.get(i).get("Name").equals(file)) {
					downloading.remove(i);
				}
			}
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("BaseName", RcFile.BaseName(file));
			map.put("Name", file);
			map.put("Size", RcFile.ShowSize(size, 2));
			map.put("Percentage", "0");
			map.put("IsError", "false");
			downloading.add(map);
		}
	}

	/**
	 * ��ҳ����ͣʱ����һ�����
	 */
	@Override
	protected void onPause() {
		super.onPause();
		isPause = true;
	}

	/**
	 * ��ҳ��ָ�ʱ���ñ��
	 */
	@Override
	protected void onResume() {
		super.onResume();
		isPause = false;
	}

	/**
	 * �������ؽ�����
	 * 
	 * @param file
	 *            �������ļ�·��
	 * @param p
	 *            1000�ֵĽ��ȱ�
	 * @param size
	 *            �ļ���С�����ౣ������
	 */
	public static void UpdateDownloading(String file, int p, long size) {
		synchronized (downloading) {
			for (int i = 0; i < downloading.size(); i++) {
				if (downloading.get(i).get("Name").equals(file)) {
					HashMap<String, String> map = downloading.get(i);
					map.put("Percentage", p + "");
					downloading.set(i, map);
				}
			}
		}
		if (instance != null && isPause == false) {
			Message msg = instance.handler.obtainMessage(UpdateDownloading);
			instance.handler.sendMessage(msg);
		}
	}

	/**
	 * ���������б�Ϊ���ش���
	 * 
	 * @param file
	 *            �����ļ��ķ�����·��
	 */
	public static void UpdateDownloadingIsError(String file) {
		synchronized (downloading) {
			for (int i = 0; i < downloading.size(); i++) {
				if (downloading.get(i).get("Name").equals(file)) {
					HashMap<String, String> map = downloading.get(i);
					map.put("IsError", "true");
					downloading.set(i, map);
				}
			}
		}
		if (instance != null && isPause == false) {
			Message msg = instance.handler.obtainMessage(UpdateDownloading);
			instance.handler.sendMessage(msg);
		}
	}

	/**
	 * ��������������б����Ƴ�
	 * 
	 * @param file
	 *            �������ļ�·��
	 */
	public static void RemoveDownloading(String file) {
		synchronized (downloading) {
			for (int i = 0; i < downloading.size(); i++) {
				if (downloading.get(i).get("Name").equals(file)) {
					downloading.remove(i);
					break;
				}
			}
		}
		if (instance != null && isPause == false) {
			Message msg = instance.handler.obtainMessage(UpdateDownloading);
			instance.handler.sendMessage(msg);
		}
	}

	/**
	 * ���ļ���ӵ��������б���
	 * 
	 * @param file
	 *            �������ļ�·��
	 * @param size
	 *            �ļ���С
	 */
	public static void AddDownloaded(String file, long size) {
		synchronized (downloaded) {
			for (int i = 0; i < downloaded.size(); i++) {
				if (downloaded.get(i).get("Name").equals(file)) {
					downloaded.remove(i);
					break;
				}
			}
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("BaseName", RcFile.BaseName(file));
			map.put("Name", file);
			map.put("Size", RcFile.ShowSize(size, 2));
			downloaded.add(map);
		}
		if (instance != null && isPause == false) {
			Message msg = instance.handler.obtainMessage(UpdateDownloaded);
			instance.handler.sendMessage(msg);
		}
	}

	/**
	 * �����б��Ž���
	 */
	private DownloadingAdapter diAdapter = new DownloadingAdapter();

	/**
	 * �������б��Ž���
	 */
	private DownloadedAdapter deAdapter = new DownloadedAdapter();

	/**
	 * �������ؽ��漰��Ϣ�¼����б��Ž���
	 */
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_download);
		((ListView) findViewById(R.id.downloading)).setAdapter(diAdapter);
		((ListView) findViewById(R.id.downloaded)).setAdapter(deAdapter);
		instance = this;
		TabHost mTabHost = (TabHost) findViewById(R.id.download_tabhost);
		mTabHost.setup();
		mTabHost.addTab(mTabHost.newTabSpec("downloading").setIndicator(getResources().getString(R.string.downloading))
				.setContent(R.id.downloading));
		mTabHost.addTab(mTabHost.newTabSpec("downloaded").setIndicator(getResources().getString(R.string.downloaded))
				.setContent(R.id.downloaded));
		mTabHost.setCurrentTab(0);

		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case UpdateDownloading:
					diAdapter.notifyDataSetChanged();
					break;
				case UpdateDownloaded:
					deAdapter.notifyDataSetChanged();
					break;
				}
			}
		};
	}

	/**
	 * �������ļ��б��Ž���
	 * 
	 * @author loveyu
	 * 
	 */
	class DownloadingAdapter extends BaseAdapter {
		/**
		 * �����������б������
		 */
		@Override
		public int getCount() {
			return downloading.size();
		}

		/**
		 * ��ȡ�������б��е�ĳһ����Ŀ�����ؿ�ֵ
		 */
		@Override
		public Object getItem(int position) {
			return null;
		}

		/**
		 * ��ȡ�����б��ĳһ������
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(ActivityDownload.this, R.layout.activity_downloading_list_item, null);
			HashMap<String, String> map = downloading.get(position);
			((TextView) view.findViewById(R.id.name)).setText(map.get("BaseName"));
			if (Boolean.parseBoolean(map.get("IsError"))) {
				((TextView) view.findViewById(R.id.name)).setTextColor(Color.RED);
			}
			((TextView) view.findViewById(R.id.size)).setText(map.get("Size"));
			((ProgressBar) view.findViewById(R.id.progressBar)).setMax(1000);
			int p = 0;
			try {
				p = Integer.parseInt(map.get("Percentage"));
			} catch (Exception ex) {
			}
			((ProgressBar) view.findViewById(R.id.progressBar)).setProgress(p);
			((TextView) view.findViewById(R.id.percentage)).setText(String.format("%.1f%%", (float) p / 10));
			return view;
		}

		/**
		 * ��ȡĳһ�б��ID
		 */
		@Override
		public long getItemId(int position) {
			return 0;
		}
	}

	/**
	 * �������ļ��Ž���
	 * 
	 * @author loveyu
	 * 
	 */
	class DownloadedAdapter extends BaseAdapter {
		/**
		 * ���������ص��ļ�����
		 */
		@Override
		public int getCount() {
			return downloaded.size();
		}

		/**
		 * ���������صĶ�Ӧ��Ŀ
		 */
		@Override
		public Object getItem(int position) {
			return null;
		}

		/**
		 * ���������صĶ�Ӧview
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = View.inflate(ActivityDownload.this, R.layout.activity_downloaded_list_item, null);
			HashMap<String, String> map = downloaded.get(position);
			((TextView) view.findViewById(R.id.BaseName)).setText(map.get("BaseName"));
			((TextView) view.findViewById(R.id.SeverPath)).setText(RcFile.ParentName(map.get("Name")));
			((TextView) view.findViewById(R.id.size)).setText(map.get("Size"));
			((Button) view.findViewById(R.id.open_file)).setOnClickListener(new OpenFileListener(map.get("Name")));
			return view;
		}

		/**
		 * ���ض�ӦԪ��ID
		 */
		@Override
		public long getItemId(int position) {
			return 0;
		}

		/**
		 * ��ϵͳ���ļ�������
		 * 
		 * @author loveyu
		 * 
		 */
		class OpenFileListener implements Button.OnClickListener {
			String path;

			/**
			 * ����Ҫ�򿪵��ļ�·��
			 * 
			 * @param path
			 *            ��Ӧ���ļ�·��
			 */
			public OpenFileListener(String path) {
				this.path = RcConfig.GetDownloadPath() + RcFile.BaseName(path);
			}

			/**
			 * �ļ��򿪼����¼�
			 */
			@Override
			public void onClick(View v) {
				startActivity(FileAction.openFile(new File(path)));
			}
		}
	}
}
