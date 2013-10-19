package net.loveyu.remotecontrol;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * ���������
 * 
 * @author loveyu
 * 
 */
public class RcNet {
	/**
	 * ���絥��ģʽ
	 */
	private static RcNet instance = new RcNet();

	/**
	 * ˽�й��캯��
	 */
	private RcNet() {

	}

	/**
	 * ��ȡʵ��
	 * 
	 * @return ����ʵ��
	 */
	public static RcNet Get() {
		return instance;
	}

	/**
	 * Udp�ͻ���
	 */
	private DatagramSocket udp;
	/**
	 * Զ��IP��ַ
	 */
	private InetAddress ip;
	/**
	 * Զ�̶˿�
	 */
	private int port;
	/**
	 * ���ݽ����߳�
	 */
	private Thread receiveThread;
	/**
	 * �߳�������ʾ
	 */
	public boolean Runing = false;

	/**
	 * ��������
	 * 
	 * @throws Exception
	 *             �����������κ��쳣
	 */
	public void Start() throws Exception {
		if (RcConfig.Status() == false) {
			throw new Exception("Config status is false");
		}
		udp = new DatagramSocket();
		ip = InetAddress.getByName(RcConfig.GetServerName());
		port = RcConfig.GetSevrePort();
		udp.connect(ip, port);
		receiveThread = new Thread(new Runnable() {

			@Override
			public void run() {
				ReceiveThread();
			}
		});
		Runing = true;
		receiveThread.start();
		RcQueue.Get().Start();
	}

	/**
	 * �������ݰ�
	 * 
	 * @param msg
	 *            �������ݶ���
	 */
	public void Send(RcSendMsg msg) {
		try {
			if (udp.isClosed()) {
				RcDebug.tN("Net is stop! Please re-login.");
				return;
			}
			if (msg.type != SendMsgType.CALLBACK) {
				if (msg.tryNum == 0) {
					RcQueue.Get().Add(msg);
				} else {
					RcQueue.Get().Update(msg);
				}
			}
			udp.send(msg.pack);
		} catch (IOException e) {
		}
	}

	/**
	 * ֹͣ�����������
	 */
	public void Stop() {
		Runing = false;
		udp.close();
		RcQueue.Get().Stop();
	}

	/**
	 * ��������߳�
	 */
	private void ReceiveThread() {
		try {
			while (Runing) {
				byte[] buf = new byte[5120];
				DatagramPacket pack = new DatagramPacket(buf, buf.length);
				udp.receive(pack);
				RcNetRecvieThread runThread = new RcNetRecvieThread(pack);
				new Thread(runThread).start();
			}
		} catch (Exception e) {
			RcDebug.tN("Get a Network Ex:" + e.getMessage());
		}
	}
}

/**
 * ��������̵߳���Ϣ�����߳�
 * 
 * @author loveyu
 * 
 */
class RcNetRecvieThread implements Runnable {
	/**
	 * �յ�����Ϣ
	 */
	DatagramPacket pack;

	/**
	 * ����һ�����ж���
	 * 
	 * @param pack
	 *            �յ������ݰ�
	 */
	public RcNetRecvieThread(DatagramPacket pack) {
		this.pack = pack;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		new RcProcess().Run(pack);
	}
}