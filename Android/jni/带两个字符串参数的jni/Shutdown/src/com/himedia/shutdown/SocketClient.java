package com.himedia.shutdown;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.util.Log;

public class SocketClient {

	// Service Name
	 static final String SOCKET_NAME = "configserver";

	// The end of the command whether to perform
	 boolean running = false;

	// Socket
	 LocalSocket s = null;

	// Socket address
	 LocalSocketAddress l;

	// Input stream
	 InputStream is;

	// Output stream
	 OutputStream os;

	// Data output stream
	 DataOutputStream dos;

	// Command return value
	 String rec_data = null;


	public SocketClient() {
		running = true;
		connect();

	}

	/**
	 * Socket connect
	 */
	public void connect() {
		try {
		s = new LocalSocket();
		l = new LocalSocketAddress(SOCKET_NAME,
		LocalSocketAddress.Namespace.RESERVED);
		s.connect(l);
		is = s.getInputStream();
		os = s.getOutputStream();
		System.out.println(os);

		} catch (IOException e) {
		e.printStackTrace();
		}

	}


	public void writeMess(String s) {
		try {
		Log.w("STR", s);
		dos = new DataOutputStream(os);
		int strLen = s.getBytes().length;
		System.out.println(strLen);
		byte[] sendLen = intToBytes2(strLen);
		byte[] allLen = new byte[s.getBytes().length + 4];

		byte[] srcLen = s.getBytes();

		for (int i = 0; i < (s.getBytes().length + 4); i++) {
		if (i < 4) {
		allLen[i] = sendLen[i];
		System.out.println(i);
		} else {
		System.out.println("=" + i);
		allLen[i] = srcLen[i - 4];
		}
		}
		dos.write(allLen);
		dos.flush();
		} catch (IOException e) {
		e.printStackTrace();
		}
	}

	/**
	 * int change to byte
	 * @param n int data
	 * @return byte data
	 */
	private byte[] intToBytes2(int n) {
		byte[] b = new byte[4];
		for (int i = 3; i >= 0; i--) {
		b[i] = (byte) (n >> (i * 8));
		}
		return b;
	}

	/*Access to the command execution results*/
	/*CNcomment:同步获得命令执行结果*/
		public void readNetResponseSync() {
		try {
		InputStream m_Rece = s.getInputStream();
		byte[] data;
		int receiveLen = 0;
		while (running) {
		receiveLen = m_Rece.available();
		data = new byte[receiveLen];
		if (receiveLen != 0) {
		m_Rece.read(data);
		rec_data = new String(data);
		Log.w("TAG", rec_data);
		// success
		if (rec_data.contains("execute ok")) {
		running = false;
		}
		// fail
		else if (rec_data.contains("failed execute")) {
		running = false;
		}
		}

		try {
		Thread.sleep(50);
		} catch (InterruptedException e) {
		e.printStackTrace();
		}
		}
		m_Rece.close();
		close();
		} catch (IOException e) {
		e.printStackTrace();
		}
		}

	/**
	 * Socket Disconnect
	 */
	public void close() {
		try {
		dos.close();
		is.close();
		os.close();
		s.close();
		} catch (IOException e) {
		e.printStackTrace();
		}

	}
}
