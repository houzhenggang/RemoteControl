package net.loveyu.remotecontrol;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * ������Ϣҳ��
 * 
 * @author loveyu
 * 
 */
public class PagerSendNotice extends PagerEmpty {
	/**
	 * ����һ��ҳ��
	 * 
	 * @param content
	 *            ҳ�����
	 * @return ҳ��ʵ��
	 */
	public static PagerSendNotice newInstance(String content) {
		PagerSendNotice fragment = new PagerSendNotice();
		fragment.mContent = content;
		return fragment;
	}

	/**
	 * ҳ��view
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
					RcDebug.N(getActivity(), "Notice message is Empty!");
					return;
				}
				RcNet.Get().Send(RcSendMsg.createNotice(txt));
			}
		});

		return view;
	}
}
