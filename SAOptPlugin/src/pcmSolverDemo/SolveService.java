package pcmSolverDemo;

import java.util.ArrayList;
import org.apache.log4j.Logger;

public class SolveService {
	private static SolveService instance = null;
	private static final Logger logger = Logger.getLogger(SolveService.class);
	// 与解析线程进行通信的数组，数组中第一元素是命令（lunch的类名##配置名），
	// 第二个元素是执行结果（值为"1"表示是成功完成，"-1"表示发生异常）
	private ArrayList<String> sharedQ = null;

	private SolverThread slvThd = null;

	private volatile int state = -1;// 1表示是成功完成，-1表示发生异常
	private volatile int signal = 0;// 用于state的信号量，用于阻塞请求方

	public synchronized int getState() {
		while (signal == 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				logger.error("SolveService.getState方法请求等待发生异常\n", e);
			}
		}
		signal--;
		notify();
		return state;
	}

	public synchronized void setState(int state) {
		while (signal != 0) {
			try {
				wait();
			} catch (InterruptedException e) {
				logger.error("SolveService.setState方法请求等待发生异常\n", e);
			}
		}
		this.state = state;
		signal++;
		notify();
	}

	private SolveService() {
		sharedQ = new ArrayList<String>();
		slvThd = new SolverThread(sharedQ);
	}

	public static SolveService getInstance() {
		if (instance == null) {
			instance = new SolveService();
		}
		return instance;
	}

	public synchronized void solveSA(final String cfgName) {
		new Thread() {
			public void run() {
				handle(cfgName);
			}

		}.start();
	}

	private void handle(final String cfgName) {
		//logger.debug("解析" + cfgName);
		String cmdTxt = getCmdTxt(cfgName);// 生成解析命令
		String rlt = exeCmd(cmdTxt);// 执行解析命令
		int retryNum = 3;// 最多可重试的次数

		if (!rlt.equals("1")) {// 超时或异常
			logger.error("解析" + cmdTxt + "超时或异常,正在尝试重新解析");
			boolean success = false;
			for (int i = 0; i < retryNum; i++) {
				rlt = retry(cmdTxt);
				if (rlt.equals("1")) {
					//logger.debug("重新解析" + cmdTxt + "成功");
					success = true;
					setState(1);
					break;
				}
			}
			if (!success) {
				logger.error("重新解析已达到" + retryNum + "次！");
				setState(-1);
			}

		} else {// 解析成功
			synchronized (sharedQ) {
				sharedQ.clear();
			}
			//logger.debug("解析" + cmdTxt + "成功");
			setState(1);
		}
	}

	private String getCmdTxt(String cfgName) {
		String cmdTxt = "";
		if (cfgName.equals("PCMSolver")) {
			cmdTxt = "de.uka.ipd.sdq.dsolver_plugin.PCMSolverLaunchConfigurationType";
			cmdTxt += "##PCMSolver";
		}
		if (cfgName.equals("PCMReliability")) {
			cmdTxt = "de.uka.ipd.sdq.dsolver_plugin.PCMSolverLaunchConfigurationType.Reliability";
			cmdTxt += "##PCMReliability";
		}
		return cmdTxt;
	}

	private String retry(String cmdTxt) {
		synchronized (sharedQ) {
			sharedQ.clear();// 将上次执行的命令和结果删除
		}
		return exeCmd(cmdTxt);
	}

	private String exeCmd(String cmdTxt) {
		String rlt = "-1";
		synchronized (sharedQ) {
			if (sharedQ.size() == 0) {// 初始状态或上一条命令执行结束
				sharedQ.add(cmdTxt);
				//logger.debug("初始状态或上一条命令执行结束");
				new Thread(slvThd).start();
				sharedQ.notify();
			}
			while (sharedQ.size() == 1) {// 解析工作正在进行
				//logger.debug("正在按" + cmdTxt + "解析, waiting");
				try {
					sharedQ.wait();
				} catch (InterruptedException e) {
					logger.error("SolveService.exeCmd方法请求等待sharedQ,发生异常\n", e);
				}
			}
			if (sharedQ.size() == 2) {
				rlt = sharedQ.get(1);
			}
		}
		return rlt;
	}
}
