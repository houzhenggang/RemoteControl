package net.loveyu.remotecontrol;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

/**
 * ����ҳ��
 * @author loveyu
 *
 */
public class ActivityHelp extends Activity {
	/**
	 * ��д��������������xml����
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);
		setHelpContent();
	}

	/**
	 * ���ð����ĵ�����
	 */
	private void setHelpContent() {
		String html = "";

		ArrayList<HashMap<String, ArrayList<String>>> list = getData();
		for (HashMap<String, ArrayList<String>> map : list) {
			for (String name : map.keySet()) {
				html += "<div><h3>" + name + "</h3>";
				for (String item : map.get(name)) {
					html += "<p>" + item + "</p>";
				}
				html += "</div>";
			}
		}
		TextView tv = ((TextView) findViewById(R.id.help));
		tv.setMovementMethod(ScrollingMovementMethod.getInstance());
		tv.setText(Html.fromHtml(html));
	}

	/**
	 * ���ذ������ݵ��б���Ϣ
	 * @return ʹ�ö����б�
	 */
	private ArrayList<HashMap<String, ArrayList<String>>> getData() {
		ArrayList<HashMap<String, ArrayList<String>>> rt = new ArrayList<HashMap<String, ArrayList<String>>>();

		HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
		ArrayList<String> list = new ArrayList<String>();
		list.add("һ�����׵�Զ�̿������,��������������ܡ�");
		list.add("�汾��" + getVersion());
		list.add("�������Bug�����Է���һ�¡�");
		map.put("������Ϣ", list);
		rt.add(map);

		map = new HashMap<String, ArrayList<String>>();
		list = new ArrayList<String>();
		list.add("��¼ǰ��Ҫ֪����������IP��ַ����Ȼ����ʹ������������ܹ�����");
		list.add("�û��������������ȵ��������趨��������Ĭ�����롣");
		list.add("����ò����ļ����ط������������д�ļ����������IP���˿ڡ�");
		list.add("�����ļ������浽Ӧ�ó���Ŀ¼����ROOTȨ�����������޷����ʣ���Ȼ��δ���ܡ�");
		map.put("��¼˵��", list);
		rt.add(map);

		map = new HashMap<String, ArrayList<String>>();
		list = new ArrayList<String>();
		list.add("�ύ���������Զ�̵��µ�DOS������ִ��");
		list.add("�������᷵���κ�������ݵ����ƶˣ������Ҫʹ���ն�����");
		list.add("�����Ҫ��������ͬʱִ�У�����ʹ�û��зָ�����&�ָ���������������һ��");
		map.put("ִ������˵��", list);
		rt.add(map);

		map = new HashMap<String, ArrayList<String>>();
		list = new ArrayList<String>();
		list.add("��Ϣ������������ִ��״̬���أ�������գ�������ݹ������ֶ����");
		list.add("������Ϣָ������޷�ִ��ѡ�����󣬻��߳��ִ��������ص�״������������һ����ʾ��Ϣ");
		map.put("��Ϣ��������Ϣ˵��", list);
		rt.add(map);

		map = new HashMap<String, ArrayList<String>>();
		list = new ArrayList<String>();
		list.add("���Ƿ����������õ�һϵ�й��ܣ����Է���һ������Ϣ");
		list.add("����رշ�������û���κ���ʾ����ʱ������Ծ��˳�����");
		map.put("���ù��ܺ���˵��", list);
		rt.add(map);

		map = new HashMap<String, ArrayList<String>>();
		list = new ArrayList<String>();
		list.add("����Ի�ȡ������������ʵʱͼƬ�����ҷ��ص�ǰ���б�");
		list.add("�б����һ��������СͼƬ������ͼ");
		list.add("���ֶ�ָ������ͼ��ȣ���������쳣������Ĭ��100�Ŀ��ֵ");
		list.add("�˴����غ�ɵ����ع����в鿴");
		map.put("��ͼ����˵��", list);
		rt.add(map);

		map = new HashMap<String, ArrayList<String>>();
		list = new ArrayList<String>();
		list.add("������Ϣָ�ڼ�������浯��һ����ʾ������ʾ��ر�ʱ�᷵�عر���Ϣ");
		map.put("������Ϣ����˵��", list);
		rt.add(map);

		map = new HashMap<String, ArrayList<String>>();
		list = new ArrayList<String>();
		list.add("���;���ֵ��������ʾ���������Ϣ������");
		map.put("���;��湦��˵��", list);
		rt.add(map);

		map = new HashMap<String, ArrayList<String>>();
		list = new ArrayList<String>();
		list.add("��������ǽ�windows�������е���������ֶη��͹���");
		list.add("�����ƽ������򣬳���ѡ��һ��������������");
		list.add("û�������ܣ�����ĳһ����󷵻�״̬��֮����Ҫ�ֶ�ˢ���б�");
		list.add("���б���ʱʱ����");
		map.put("���������˵��", list);
		rt.add(map);

		map = new HashMap<String, ArrayList<String>>();
		list = new ArrayList<String>();
		list.add("�ṩ�б�����ʾ���п��г��̷�");
		list.add("���Ҫ���ļ����в��������ȹ�ѡ֮����ʹ�ð�ť");
		list.add("֧��ɾ���������������ļ��ĸ��Ʋ��������Ҫ���ļ��и�����Ҫʹ��DOS���");
		list.add("�ڲ˵��п���ѡ�񴴽����ļ����ļ���");
		list.add("���Ե���ִ���ļ��������֧��,get,delete,rename,move������ÿ��һ����������Ŀ¼ʹ��ROOT�����get�����Ǹ��ļ����Ǹ�����ִ��һ�������ļ�����");
		map.put("�ļ�������˵��", list);
		rt.add(map);

		map = new HashMap<String, ArrayList<String>>();
		list = new ArrayList<String>();
		list.add("�ù��ܲ������κ�״̬��Ϣ������������������");
		list.add("�����б����ֻ����ʾ�������ؽ��ȣ���ӵ����񲻻�һ����ȫ�����");
		list.add("������ɺ��ܹ�����ϵͳ���ļ�");
		list.add("�ļ�����Ŀ¼ΪSD��RemoteControl��");
		list.add("�о�û���Զ���ı�Ҫ���ѵ��������������ش��ļ�ô���о�����ʵ������Ȼ���ԡ�");
		list.add("�ļ�����û����ͣ��ȡ�����ܣ���Ҫ�Ļ��Լ�������������");
		map.put("���ع�����˵��", list);
		rt.add(map);

		map = new HashMap<String, ArrayList<String>>();
		list = new ArrayList<String>();
		list.add("����:<strong>����</strong>");
		list.add("���͵�ַ:<a href=\"http://www.loveyu.org\">http://www.loveyu.org</a>");
		list.add("��Ŀ��ַ[������]:<a href=\"http://www.loveyu.net/RemoteControl\">http://www.loveyu.net/RemoteControl</a>");
		map.put("������Ϣ", list);
		rt.add(map);

		return rt;
	}
	
	/**
	 * ��ȡ����汾��
	 * @return �޷���ȡʱ����1.0
	 */
	private String getVersion() {
		try {
			return getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			return "1.0";
		}
	}
}
