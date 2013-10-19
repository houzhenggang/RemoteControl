package net.loveyu.remotecontrol;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

/**
 * �������ҳ
 * 
 * @author loveyu
 * 
 */
public class ActivityTask extends Activity {
	/**
	 * �����б��Ž���
	 */
	private static TaskAdapter adapter;

	/**
	 * �����б�
	 */
	private static List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	/**
	 * ��ʱ�洢�������б�
	 */
	private static ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

	/**
	 * ��Ϣ����ʵ��
	 */
	private Handler handler;

	/**
	 * ��������
	 */
	private static ActivityTask instance = null;

	/**
	 * ��Ϣֵ
	 */
	private static final int Update = 1;

	/**
	 * �Ƿ��̨����
	 */
	private static boolean isPause = false;

	/**
	 * ���ز����ļ�����ʼ������Ϣ
	 */
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_task);
		adapter = new TaskAdapter();
		((ListView) findViewById(R.id.task_listView)).setAdapter(adapter);
		((Button) findViewById(R.id.task_refresh)).setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				list.clear();
				RcNet.Get().Send(RcSendMsg.createTask("get"));
			}
		});
		instance = this;
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				switch (msg.what) {
				case Update:
					adapter.notifyDataSetChanged();
					break;
				}
			}
		};
		if (list.isEmpty()) {
			RcNet.Get().Send(RcSendMsg.createTask("get"));
		}
	}

	/**
	 * ���߳��и�����Ϣ
	 * 
	 * @param ls
	 *            ��ȡ���������б�
	 * @param i
	 *            �����б�ĵݼ�id,������Ϊ1ʱ��ʾ����
	 */
	public static void UpdateList(List<HashMap<String, String>> ls, int i) {
		synchronized (data) {
			data.addAll(ls);
			if (i == 1 && instance != null && isPause == false) {
				ls.clear();
				Collections.sort(data, new Comparator<HashMap<String, String>>() {
					public int compare(HashMap<String, String> lhs, HashMap<String, String> rhs) {
						return lhs.get("name").toLowerCase().compareTo(rhs.get("name").toLowerCase());
					};
				});
				list.addAll(data);
				data.clear();
				Message msg = new Message();
				msg.what = Update;
				instance.handler.sendMessage(msg);
			}
		}
	}

	/**
	 * ��̨�����¼�
	 */
	@Override
	protected void onPause() {
		super.onPause();
		isPause = true;
	}

	/**
	 * �ָ��¼�
	 */
	@Override
	protected void onResume() {
		super.onResume();
		isPause = false;
	}

	/**
	 * �����б��Ž���
	 * 
	 * @author loveyu
	 * 
	 */
	class TaskAdapter extends BaseAdapter {
		/**
		 * ��ȡ�����б�����
		 */
		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		/**
		 * ��ȡĳһ�������ͼ
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HashMap<String, String> map = list.get(position);
			View view = View.inflate(ActivityTask.this, R.layout.activity_task_item, null);
			((TextView) view.findViewById(R.id.task_name)).setText(map.get("name"));
			((TextView) view.findViewById(R.id.task_id)).setText(map.get("id"));
			((TextView) view.findViewById(R.id.task_time)).setText(map.get("time"));
			((TextView) view.findViewById(R.id.task_memory)).setText(RcFile.ShowSize(
					Double.parseDouble(map.get("memory")), 2));
			((TextView) view.findViewById(R.id.task_path)).setText(map.get("path"));
			view.setOnLongClickListener(new TaskLongListener(position));
			return view;
		}

		/**
		 * ��ʱ�䰴ס�����¼�������
		 * 
		 * @author loveyu
		 * 
		 */
		class TaskLongListener implements View.OnLongClickListener {
			/**
			 * ��ǰ�����������б��е����
			 */
			int position;

			/**
			 * ������ �����������
			 * 
			 * @param position
			 *            �������б������
			 */
			public TaskLongListener(int position) {
				this.position = position;
			}

			/**
			 * ��ʱ�䰴ס�ļ����¼�
			 */
			@Override
			public boolean onLongClick(View v) {
				HashMap<String, String> map = list.get(position);
				View view = View.inflate(ActivityTask.this, R.layout.activity_task_item, null);
				((TextView) view.findViewById(R.id.task_name)).setText(map.get("name"));
				((TextView) view.findViewById(R.id.task_id)).setText(map.get("id"));
				((TextView) view.findViewById(R.id.task_time)).setText(map.get("time"));
				((TextView) view.findViewById(R.id.task_memory)).setText(RcFile.ShowSize(
						Double.parseDouble(map.get("memory")), 2));
				((TextView) view.findViewById(R.id.task_path)).setText(map.get("path"));
				new AlertDialog.Builder(ActivityTask.this).setTitle(getResources().getString(R.string.input_kill_task))
						.setView(view)
						.setPositiveButton(getResources().getString(R.string.ok), new MyDialogListener(map.get("id")))
						.setNegativeButton(getResources().getString(R.string.cancel), null).show();
				return false;
			}

			/**
			 * �����Ի���ļ����¼�
			 * 
			 * @author loveyu
			 * 
			 */
			class MyDialogListener implements OnClickListener {
				/**
				 * ��������ϵͳ�ж�Ӧ��PID
				 */
				int pid;

				/**
				 * ��������������PID
				 * 
				 * @param pid
				 */
				public MyDialogListener(String pid) {
					this.pid = Integer.parseInt(pid);
				}

				/**
				 * ����kill��Ϣ��������
				 */
				@Override
				public void onClick(DialogInterface dialog, int which) {
					RcNet.Get().Send(RcSendMsg.createTask("kill\n" + pid));
				}
			}
		}
	}
}
