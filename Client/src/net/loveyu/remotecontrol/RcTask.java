package net.loveyu.remotecontrol;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Զ�������б�ָ���
 * 
 * @author loveyu
 * 
 */
public class RcTask {
	/**
	 * ��ȡ�����б�
	 * 
	 * @param content
	 *            ԭʼ�ļ�������
	 * @return �ָ�õ������б�
	 */
	public static List<HashMap<String, String>> GetTaskList(String content) {
		List<HashMap<String, String>> rt = new ArrayList<HashMap<String, String>>();
		String[] list = content.split("\\n");
		for (String s : list) {
			String[] info = s.split("\\t");
			if (info.length != 5)
				continue;
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("id", info[0]);
			map.put("name", info[1]);
			map.put("memory", info[2]);
			map.put("time", info[3]);
			map.put("path", info[4]);
			rt.add(map);
		}
		return rt;
	}
}
