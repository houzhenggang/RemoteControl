package net.loveyu.remotecontrol;

import java.util.HashMap;

import android.text.format.Time;

/**
 * ��Ϣ������
 * 
 * @author loveyu
 * 
 */
public class RcQueue {
	/**
	 * ���е���
	 */
	private static RcQueue instance = new RcQueue();
	/**
	 * ��Ϣ�����б�
	 */
	private HashMap<String, RcSendMsg> queue = new HashMap<String, RcSendMsg>();

	/**
	 * ˽�й��캯��
	 */
	private RcQueue() {
	}

	/**
	 * �����߳�
	 */
	private Thread thread;
	/**
	 * �߳�����״̬
	 */
	private volatile boolean threadRuning = true;
	/**
	 * ����Դ���
	 */
	private final int maxTry = 5;

	/**
	 * ��ȡ���ж���ʵ��
	 * 
	 * @return ʵ��
	 */
	public static RcQueue Get() {
		return instance;
	}

	/**
	 * �������д���
	 */
	public void Start() {
		threadRuning = true;
		thread = new Thread(new Runnable() {

			@Override
			public void run() {
				ProcessThread();
			}
		});
		thread.start();
		RcDebug.v("debug", "queue start");
	}

	/**
	 * ֹͣ���д���
	 */
	public void Stop() {
		threadRuning = false;
		synchronized (queue) {
			queue.clear();
		}
	}

	/**
	 * ���д����߳�
	 */
	private void ProcessThread() {
		while (threadRuning) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
			}
			String[] ls = GetQueueHead();
			if (ls.length == 0) {
				continue;
			}
			Time time = new Time();
			time.setToNow();
			Time add = new Time();
			add.second = 2;
			time.after(add);
			for (String hd : ls) {
				boolean f = false;
				synchronized (queue) {
					f = queue.containsKey(hd);
				}
				if (f) {
					RcSendMsg msg;
					synchronized (queue) {
						msg = queue.get(hd);
					}
					if (Time.compare(time, msg.time) < 0) {
						// RcDebug.v("debug", time.toString() + ":::" +
						// msg.time.toString());
						continue;
					}
					if (msg.tryNum < maxTry) {
						RcNet.Get().Send(msg);
						RcDebug.v("debug", "Try Send:" + msg.IdString);
					} else {
						Remove(msg.IdString);
					}
				}
			}
		}
	}

	/**
	 * ��ȡ���е�ͷ�б�
	 * 
	 * @return �ַ�������
	 */
	private String[] GetQueueHead() {
		String[] rt;
		synchronized (queue) {
			rt = new String[queue.keySet().size()];
			int i = 0;
			for (Object ob : queue.keySet().toArray()) {
				rt[i++] = (String) ob;
			}
		}
		return rt;
	}

	/**
	 * ͳ���б�
	 * 
	 * @return ����
	 */
	public int Count() {
		synchronized (queue) {
			return queue.size();
		}
	}

	/**
	 * ���һ���ѷ��͵���Ϣ������
	 * 
	 * @param smsg
	 *            ���͵���Ϣ
	 */
	public void Add(RcSendMsg smsg) {
		synchronized (queue) {
			queue.put(smsg.IdString, smsg);
		}
	}

	/**
	 * ����һ���ظ����͵���Ϣ
	 * 
	 * @param smsg
	 *            ��Ϣ
	 */
	public void Update(RcSendMsg smsg) {
		synchronized (queue) {
			if (queue.containsKey(smsg.IdString)) {
				++smsg.tryNum;
				smsg.time.setToNow();
				queue.put(smsg.IdString, smsg);
			}
		}
		RcDebug.v("debug", "queue update:" + smsg);
	}

	/**
	 * ����ID�Ƴ���Ϣ
	 * 
	 * @param id
	 *            ��ϢID
	 */
	public void Remove(String id) {
		synchronized (queue) {
			if (queue.containsKey(id)) {
				queue.remove(id);
			}
		}
		RcDebug.v("debug", "queue remove:" + id);
	}
}
