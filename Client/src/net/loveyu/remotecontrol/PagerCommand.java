package net.loveyu.remotecontrol;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * ִ������ҳ��
 * 
 * @author loveyu
 * 
 */
public class PagerCommand extends PagerEmpty {
	/**
	 * ����һ��ִ�������ҳ��
	 * 
	 * @param content
	 *            ҳ�����
	 * @return һ��ҳ���ʵ��
	 */
	public static PagerCommand newInstance(String content) {
		PagerCommand fragment = new PagerCommand();
		fragment.mContent = content;
		return fragment;
	}

	/**
	 * ҳ�����ͼ����
	 */
	private View view;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.loveyu.remotecontrol.PagerEmpty#onCreateView(android.view.LayoutInflater
	 * , android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.pager_command, container, false);
		view.findViewById(R.id.pager_command_button_clear).setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				((EditText) view.findViewById(R.id.pager_command_editText)).setText("");
			}
		});
		view.findViewById(R.id.pager_command_button_send).setOnClickListener(new Button.OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText et = (EditText) view.findViewById(R.id.pager_command_editText);
				String txt = et.getText().toString();
				if (txt == null || "".equals(txt)) {
					RcDebug.N(getActivity(), "Command is Empty!");
					return;
				}
				RcNet.Get().Send(RcSendMsg.createCommand(txt));
			}
		});

		return view;
	}
}
