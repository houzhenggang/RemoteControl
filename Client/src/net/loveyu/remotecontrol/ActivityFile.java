package net.loveyu.remotecontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Զ���ļ�����ҳ��
 * 
 * @author loveyu
 * 
 */
public class ActivityFile extends Activity {
	/**
	 * �ļ��б��ջ���û�����ʱֱ�ӵ��ö�ջ������ʾ
	 */
	private static Stack<List<HashMap<String, String>>> listStack = new Stack<List<HashMap<String, String>>>();

	/**
	 * �ļ�Ŀ¼����ջ����Ӧ�ļ��б��ջ
	 */
	private static Stack<String> dirStack = new Stack<String>();

	/**
	 * ��ǰҳ���ļ��б�ÿ����ʾʱ�����ݴ�����б�
	 */
	private static List<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

	/**
	 * �ļ��б�ѡ�п��״̬��Ϣ
	 */
	private static List<Boolean> checkBoxList = new ArrayList<Boolean>();

	/**
	 * ��ǰĿ¼���ƣ���Ӧlist����
	 */
	private static String NowDir = "";

	/**
	 * ������ľ�̬����
	 */
	private static ActivityFile instance;

	/**
	 * ҳ���Ƿ��̨���еı��
	 */
	private static boolean isPause = false;

	/**
	 * ���߳���Ϣ�������
	 */
	private Handler messageHandler;

	/**
	 * ��Ϣ����ֵ
	 */
	private static final int DateUpdata = 1;

	/**
	 * ��������жϵ�ǰ�б������Ƿ�����ջ
	 */
	private static boolean addStack = true;

	/**
	 * �û������ļ��������ֵ
	 */
	private EditText alertDialogEditText;

	/**
	 * ���������ļ���������ʾ��Ϣ
	 */
	private TextView alertDialogTextView;

	/**
	 * ����ҳ�沢���ø���������
	 */
	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		instance = this;
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_file);
		((ListView) findViewById(R.id.file_manager_listView)).setAdapter(new FileAdapter());
		((Button) findViewById(R.id.last_dir)).setOnClickListener(new Button.OnClickListener() {
			/**
			 * �����ϼ�Ŀ¼������
			 */
			@Override
			public void onClick(View v) {
				String parentPath = RcFile.ParentName(NowDir);
				if (RcFile.CompPath(parentPath, NowDir))
					parentPath = "ROOT";
				if ("root".equals(NowDir.toLowerCase())) {
					RcDebug.N(ActivityFile.this, "Is Root Path.");
				} else {
					if (dirStack.isEmpty() == false && RcFile.CompPath(parentPath, dirStack.peek())) {
						list = listStack.pop();
						NowDir = dirStack.pop();
						UpdateData();
					} else {
						addStack = false;
						RcNet.Get().Send(RcSendMsg.createFile("get\n" + parentPath));
					}
				}
			}
		});
		((Button) findViewById(R.id.refresh)).setOnClickListener(new Button.OnClickListener() {
			/**
			 * ����ˢ�°�ť������
			 */
			@Override
			public void onClick(View v) {
				if ("".equals(NowDir)) {
					RcNet.Get().Send(RcSendMsg.createFile("get\nROOT"));
				} else {
					RcNet.Get().Send(RcSendMsg.createFile("get\n" + NowDir));
				}
				UpdateData();
			}
		});
		((Button) findViewById(R.id.select_all)).setOnClickListener(new Button.OnClickListener() {
			/**
			 * ����ȫѡ��ť������
			 */
			@Override
			public void onClick(View v) {

				if (checkBoxList.contains(false) == false) {
					for (int i = 0; i < checkBoxList.size(); i++) {
						checkBoxList.set(i, false);
					}
				} else {
					for (int i = 0; i < checkBoxList.size(); i++) {
						checkBoxList.set(i, true);
					}
				}
				((FileAdapter) ((ListView) findViewById(R.id.file_manager_listView)).getAdapter())
						.notifyDataSetChanged();
			}
		});
		((Button) findViewById(R.id.download)).setOnClickListener(new Button.OnClickListener() {
			/**
			 * �������ذ�ť������
			 */
			@Override
			public void onClick(View v) {
				if ("ROOT".equals(NowDir.toUpperCase())) {
					RcDebug.N(ActivityFile.this, "Is Root Path, can't do it.");
				} else {
					int flag = 0;
					for (int i = 0; i < checkBoxList.size(); i++) {
						if (checkBoxList.get(i) == false)
							continue;
						HashMap<String, String> map = list.get(i);
						if ("file".equals(map.get("Type").toLowerCase())) {
							RcNet.Get().Send(RcSendMsg.createFile("Get\n" + map.get("Name")));
							++flag;
						}
					}
					if (flag == 0) {
						RcDebug.N(ActivityFile.this, "No Selected File");
					} else {
						RcDebug.N(ActivityFile.this, "Add " + flag + " file to download list.");
					}
				}
			}
		});
		((Button) findViewById(R.id.rename)).setOnClickListener(new Button.OnClickListener() {
			/**
			 * ������������ť������
			 */
			@Override
			public void onClick(View v) {
				if ("ROOT".equals(NowDir.toUpperCase())) {
					RcDebug.N(ActivityFile.this, "Is Root Path, can't do it.");
				} else {
					boolean flag = true;
					for (int i = 0; i < checkBoxList.size(); i++) {
						if (checkBoxList.get(i) == false)
							continue;
						HashMap<String, String> map = list.get(i);
						alertDialogEditText = new EditText(ActivityFile.this);
						alertDialogEditText.setText(map.get("Name"));
						alertDialogEditText.setTag(map.get("Name"));
						new AlertDialog.Builder(ActivityFile.this)
								.setTitle(getResources().getString(R.string.input_rename)).setView(alertDialogEditText)
								.setPositiveButton(getResources().getString(R.string.ok), new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										RcNet.Get().Send(
												RcSendMsg.createFile("RENAME\n" + (String) alertDialogEditText.getTag()
														+ "\n" + alertDialogEditText.getText().toString()));
									}
								}).setNegativeButton(getResources().getString(R.string.cancel), null).show();
						flag = false;
						break;
					}
					if (flag) {
						RcDebug.N(ActivityFile.this, "No Selected File");
					}
				}
			}
		});
		((Button) findViewById(R.id.copy)).setOnClickListener(new Button.OnClickListener() {
			/**
			 * ���ø��ư�ť������
			 */
			@Override
			public void onClick(View v) {
				if ("ROOT".equals(NowDir.toUpperCase())) {
					RcDebug.N(ActivityFile.this, "Is Root Path, can't do it.");
				} else {
					boolean flag = true;
					for (int i = 0; i < checkBoxList.size(); i++) {
						if (checkBoxList.get(i) == false)
							continue;
						HashMap<String, String> map = list.get(i);
						alertDialogEditText = new EditText(ActivityFile.this);
						alertDialogEditText.setText(map.get("Name"));
						alertDialogEditText.setTag(map.get("Name"));
						new AlertDialog.Builder(ActivityFile.this)
								.setTitle(getResources().getString(R.string.input_copy)).setView(alertDialogEditText)
								.setPositiveButton(getResources().getString(R.string.ok), new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										RcNet.Get().Send(
												RcSendMsg.createFile("COPY\n" + (String) alertDialogEditText.getTag()
														+ "\n" + alertDialogEditText.getText().toString()));
									}
								}).setNegativeButton(getResources().getString(R.string.cancel), null).show();
						flag = false;
						break;
					}
					if (flag) {
						RcDebug.N(ActivityFile.this, "No Selected File");
					}
				}
			}
		});
		((Button) findViewById(R.id.delete)).setOnClickListener(new Button.OnClickListener() {
			/**
			 * �����ļ�ɾ����ť������
			 */
			@Override
			public void onClick(View v) {
				if ("ROOT".equals(NowDir.toUpperCase())) {
					RcDebug.N(ActivityFile.this, "Is Root Path, can't do it.");
				} else {
					List<Integer> ids = new ArrayList<Integer>();
					for (int i = 0; i < checkBoxList.size(); i++) {
						if (checkBoxList.get(i) == true) {
							ids.add(i);
						}
					}
					if (ids.isEmpty()) {
						RcDebug.N(ActivityFile.this, "No select file or dir.");
					} else {
						alertDialogTextView = new TextView(ActivityFile.this);
						alertDialogTextView.setTag(ids);
						alertDialogTextView.setText("Total " + ids.size() + " file or dir.");
						new AlertDialog.Builder(ActivityFile.this)
								.setTitle(getResources().getString(R.string.input_delete)).setView(alertDialogTextView)
								.setPositiveButton(getResources().getString(R.string.ok), new OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog, int which) {
										@SuppressWarnings("unchecked")
										ArrayList<Integer> check_list = (ArrayList<Integer>) alertDialogTextView
												.getTag();
										for (int id : check_list) {
											RcNet.Get().Send(
													RcSendMsg.createFile("DELETE\n" + list.get(id).get("Name")));
										}
									}
								}).setNegativeButton(getResources().getString(R.string.cancel), null).show();
					}
				}
			}
		});
		((TextView) findViewById(R.id.title)).setText(NowDir);
		findViewById(R.id.title).setOnClickListener(new View.OnClickListener() {
			/**
			 * ���õ�������¼�
			 */
			@Override
			public void onClick(View v) {
				RcDebug.N(ActivityFile.this, ((TextView) v).getText().toString());
			}
		});

		messageHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case DateUpdata:
					UpdateData();
					break;
				}
			}
		};
		if ("".equals(NowDir)) {
			RcNet.Get().Send(RcSendMsg.createFile("get\nROOT"));
		}
	}

	/**
	 * ����ҳ��˵�
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.file, menu);
		return true;
	}

	/**
	 * ��дҳ���̨�����¼�
	 */
	@Override
	protected void onPause() {
		super.onPause();
		isPause = true;
	}

	/**
	 * ��дҳ��ָ��¼�
	 */
	@Override
	protected void onResume() {
		super.onResume();
		isPause = false;
	}

	/**
	 * ��д�˵�ѡ���¼�
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_file_get_root:
			listStack.clear();
			dirStack.clear();
			addStack = false;
			RcNet.Get().Send(RcSendMsg.createFile("get\nROOT"));
			break;
		case R.id.menu_file_mkdir:
			if ("ROOT".equals(NowDir.toUpperCase())) {
				RcDebug.N(ActivityFile.this, "Is Root Path, can't do it.");
			} else {
				alertDialogEditText = new EditText(this);
				new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.input_dir_name))
						.setView(alertDialogEditText)
						.setPositiveButton(getResources().getString(R.string.ok), new OnClickListener() {
							/**
							 * ���������ļ���
							 */
							@Override
							public void onClick(DialogInterface dialog, int which) {
								RcNet.Get().Send(
										RcSendMsg.createFile("MKDIR\n" + NowDir + "\\"
												+ alertDialogEditText.getText().toString()));
							}

						}).setNegativeButton(getResources().getString(R.string.cancel), null).show();
			}
			break;
		case R.id.menu_file_mkfile:
			if ("ROOT".equals(NowDir.toUpperCase())) {
				RcDebug.N(ActivityFile.this, "Is Root Path, can't do it.");
			} else {
				alertDialogEditText = new EditText(this);
				new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.input_file_name))
						.setView(alertDialogEditText)
						.setPositiveButton(getResources().getString(R.string.ok), new OnClickListener() {
							/**
							 * �����������ļ�
							 */
							@Override
							public void onClick(DialogInterface dialog, int which) {
								RcNet.Get().Send(
										RcSendMsg.createFile("MKFILE\n" + NowDir + "\\"
												+ alertDialogEditText.getText().toString()));
							}
						}).setNegativeButton(getResources().getString(R.string.cancel), null).show();
			}
			break;
		case R.id.menu_file_run_cmd:
			alertDialogEditText = new EditText(this);
			new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.input_file_name))
					.setView(alertDialogEditText)
					.setPositiveButton(getResources().getString(R.string.ok), new OnClickListener() {
						/**
						 * ����ִ�������ļ���������
						 */
						@Override
						public void onClick(DialogInterface dialog, int which) {
							RcNet.Get().Send(RcSendMsg.createFile(alertDialogEditText.getText().toString()));
						}
					}).setNegativeButton(getResources().getString(R.string.cancel), null).show();
			break;
		case R.id.menu_file_go_back:
			finish();
			break;
		case R.id.menu_download:
			startActivity(new Intent(ActivityFile.this, ActivityDownload.class));
			break;
		}
		return true;
	}

	/**
	 * ��������ʱ�䣬���޸ķ��ؼ��Ĳ���
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && dirStack.isEmpty() == false) {
			list = listStack.pop();
			NowDir = dirStack.pop();
			UpdateData();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * ͨ���߳��������ļ����б��޸�ҳ����ʾ���ݣ���һ�������֮ǰ���ݼ����ջ
	 * 
	 * @param map
	 *            ��Ӧ�ļ��������б�
	 * @param parent
	 *            ��Ӧ�ļ���Ŀ¼����
	 */
	public static void setList(List<HashMap<String, String>> map, String parent) {
		if ("".equals(NowDir) == false && parent.equals(NowDir) == false) {
			if (dirStack.isEmpty() == false && parent.equals(dirStack.peek())) {
				listStack.pop();
				dirStack.pop();
			} else {
				if (addStack) {
					listStack.push(list);
					dirStack.push(NowDir);
				} else {
					addStack = true;
				}
			}
		}
		list = map;
		NowDir = parent;
		if (instance != null && isPause == false) {
			Message msg = new Message();
			msg.what = DateUpdata;
			instance.messageHandler.sendMessage(msg);
		}
	}

	/**
	 * ���½����б����ݼ�
	 */
	private void UpdateData() {
		checkBoxList = new ArrayList<Boolean>(list.size());
		for (int i = 0; i < list.size(); i++) {
			checkBoxList.add(false);
		}
		((TextView) findViewById(R.id.title)).setText(NowDir);
		((FileAdapter) ((ListView) findViewById(R.id.file_manager_listView)).getAdapter()).notifyDataSetChanged();
	}

	/**
	 * �ļ������Ž���
	 * 
	 * @author loveyu
	 * 
	 */
	class FileAdapter extends BaseAdapter {
		/**
		 * ��ȡ�ļ��б�����
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
		 * ��ȡ��Ӧ���б�����
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			HashMap<String, String> map = list.get(position);
			View view = null;
			String name = map.get("BaseName");
			if ("".equals(name)) {
				name = map.get("Name");
			}
			if ("DIR".equals(map.get("Type").toUpperCase())) {
				view = View.inflate(ActivityFile.this, R.layout.activity_file_item_dir, null);
				view.setOnClickListener(new View.OnClickListener() {
					/**
					 * �����ļ��е���¼����Է������ݽ���ĳһ�ļ���
					 */
					@Override
					public void onClick(View v) {
						String str = ((TextView) v.findViewById(R.id.a_f_t_f_name)).getText().toString();
						if ("ROOT".equals(NowDir.toUpperCase())) {
							RcNet.Get().Send(RcSendMsg.createFile("Get\n" + str));
						} else {
							RcNet.Get().Send(RcSendMsg.createFile("Get\n" + NowDir + "\\" + str));
						}
					}
				});
				if (map.containsKey("Size")) {
					((TextView) view.findViewById(R.id.a_f_t_f_size)).setText(RcFile.ShowSize(
							Double.parseDouble(map.get("Size")), 2));
				} else {
					((TextView) view.findViewById(R.id.a_f_t_f_size)).setText("");
				}
			} else {
				view = View.inflate(ActivityFile.this, R.layout.activity_file_item_file, null);
				((TextView) view.findViewById(R.id.a_f_t_f_size)).setText(RcFile.ShowSize(
						Double.parseDouble(map.get("Size")), 2));
			}
			((TextView) view.findViewById(R.id.a_f_t_f_date)).setText(map.get("ShowDate"));
			((TextView) view.findViewById(R.id.a_f_t_f_name)).setText(name);
			((CheckBox) view.findViewById(R.id.a_f_t_f_checkbox)).setOnCheckedChangeListener(new MyCheckBoxListener(
					position));
			if (checkBoxList.size() - 1 >= position)
				((CheckBox) view.findViewById(R.id.a_f_t_f_checkbox)).setChecked(checkBoxList.get(position));
			return view;
		}

		/**
		 * �ļ����ļ���ѡ����¼�
		 * 
		 * @author loveyu
		 * 
		 */
		class MyCheckBoxListener implements OnCheckedChangeListener {
			/**
			 * ��ǰѡ���ID
			 */
			int position;

			/**
			 * ��ʼ��һ���������������ö�Ӧ��ID
			 * 
			 * @param position
			 */
			public MyCheckBoxListener(int position) {
				this.position = position;
			}

			/**
			 * ����¼����޸��б��е�ֵ
			 */
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				checkBoxList.set(position, isChecked);
			}
		}
	}
}
