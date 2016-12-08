package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SASolver {

	private String pcmSolver = "PCMSolver";
	private String pcmRaliability = "PCMReliability";
	private Socket socket = null;
	private PrintWriter os = null;
	private BufferedReader is = null;
	private static SASolver saSolver = null;
	public static long time;
	public static int solverTimes;

	// private BufferedReader is = null;
	/**
	 * Socket通信连接
	 */
	public SASolver() {// 只能实例化一次，建立一次连接
		try {
			socket = new Socket("127.0.0.1", 4700);
			os = new PrintWriter(socket.getOutputStream());
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (Exception e) {
			System.out.println("current  Error" + e);
		}
	}

	public static SASolver getInstance() {
		if (saSolver == null) {
			saSolver = new SASolver();
		}
		return saSolver;
	}

	/**
	 * 调用PCM Solver插件
	 * 
	 * @throws Exception
	 */
	public synchronized void RunPCMSolver() {
		long starttime = System.currentTimeMillis();
		os.println(pcmSolver);
		os.flush();
		String line = "aa";
		try {
			socket.setSoTimeout(300 * 1000);// 设置socket读的超时时间为5分钟
			line = is.readLine();
			while (!(line.equals("success") || line.equals("failure"))) {
				line = is.readLine();
			}
            if(line.equals("failure")){//底层解析异常
            	closeSolver();
//          	ComputerUtil.ShutDown();
            }
		} catch (Exception e) {//socket读超时或读异常
			closeSolver();
//			ComputerUtil.ShutDown();
		}
		long endtime = System.currentTimeMillis();
		
		time += endtime-starttime;
		solverTimes++;

	}

	/**
	 * 调用PCM Reliability Solver插件
	 * 
	 * @throws Exception
	 */
	public synchronized void RunPCMReliability() {
		os.println(pcmRaliability);
		os.flush();
		String line = "aa";
		try {
			socket.setSoTimeout(300 * 1000);// 设置socket读的超时时间为5分钟
			line = is.readLine();
			while (!(line.equals("success") || line.equals("failure"))) {
				line = is.readLine();
			}
            if(line.equals("failure")){//底层解析异常
            	closeSolver();
//           	ComputerUtil.ShutDown();
            }
		} catch (Exception e) {//socket读超时或读异常
			closeSolver();
//			ComputerUtil.ShutDown();
		}
	}

	public synchronized void closeSolver() {
		os.println("end");
		os.flush();
		String line = "aa";
		try {
			line = is.readLine();
			while (!line.equals("end")) {
				line = is.readLine();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws InterruptedException {
		SASolver saSolver = new SASolver();
		for (int i = 0; i < 2; i++) {
			saSolver.RunPCMSolver();
			saSolver.RunPCMReliability();
		}
	}
}
