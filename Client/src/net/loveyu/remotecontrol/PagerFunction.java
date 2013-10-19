package net.loveyu.remotecontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

/**
 * ���ù���ҳ��
 * 
 * @author loveyu
 * 
 */
public class PagerFunction extends PagerEmpty {
	/**
	 * ����һ������ҳ���ʵ��
	 * 
	 * @param content
	 *            Ψһ����
	 * @return ҳ��ʵ��
	 */
	public static PagerFunction newInstance(String content) {
		PagerFunction fragment = new PagerFunction();
		fragment.mContent = content;
		return fragment;
	}

	/**
	 * �б�����
	 */
	private ListView lv;
	/**
	 * ������Ϣ�б�
	 */
	private HashMap<String, String> funcMap = new HashMap<String, String>();
	/**
	 * ���ڴ洢button���б�
	 */
	private List<String> list;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.loveyu.remotecontrol.PagerEmpty#onCreateView(android.view.LayoutInflater
	 * , android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		CreateFuncMap();
		lv = new ListView(getActivity());
		FunctionListViewAdapter adapter = new FunctionListViewAdapter();
		lv.setAdapter(adapter);

		return lv;
	}

	/**
	 * ��ӹ���ͼ
	 */
	private void CreateFuncMap() {
		funcMap.put("�رշ���������", "close");
		funcMap.put("�������Ϣ", "info");
		funcMap.put("������������Ϣ", "config");
		funcMap.put("�ļ����������Ϣ", "file");
		funcMap.put("��Ϣ�������", "msg");
		funcMap.put("�������汾��Ϣ", "version");
		list = new ArrayList<String>(funcMap.keySet());
	}

	/**
	 * ���ڹ����б���б��Ž���
	 * 
	 * @author loveyu
	 * 
	 */
	class FunctionListViewAdapter extends BaseAdapter {
		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getCount()
		 */
		@Override
		public int getCount() {
			return list.size();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItem(int)
		 */
		@Override
		public Object getItem(int position) {
			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getItemId(int)
		 */
		@Override
		public long getItemId(int position) {
			return 0;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.widget.Adapter#getView(int, android.view.View,
		 * android.view.ViewGroup)
		 */
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Button button = new Button(getActivity());
			button.setText(list.get(position));
			button.setOnClickListener(new Button.OnClickListener() {
				@Override
				public void onClick(View v) {
					String txt = ((Button) v).getText().toString();
					// Notice("T:" + txt + ",S:" + funcMap.get(txt));
					RcNet.Get().Send(RcSendMsg.createFunction(funcMap.get(txt)));
				}
			});
			return button;
		}
	}
}
