package pcmSolverDemo;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.handlers.IHandlerService;

//import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.DebugUITools;


public class SolverThread implements Runnable {
	// log4J的日志记录器
	private static final Logger logger = Logger.getLogger(SolverThread.class);
	// 与请求解析客户类的线程进行通信的数组，数组中第一元素是命令（lunch的类名##配置名），
	// 第二个元素是执行结果（值为"1"表示是成功完成，"-1"表示发生异常，"-2"表示超时）
	private ArrayList<String> sharedQ = null;
	// 执行管理器
	private ILaunchManager launchManager = null;

	public SolverThread(ArrayList<String> sharedQ2) {
		this.sharedQ = sharedQ2;

	}

	@Override
	public void run() {
		synchronized (sharedQ) {
			while (sharedQ.size() == 0) {// 数组中没有命令，则等待
				try {
					// logger.debug("has no cmd, waiting");
					sharedQ.wait();
				} catch (InterruptedException ex) {
					logger.error("SolverThread.run方法等待数组中有命令时发生异常\n", ex);
				}
			}
			String[] cmdStrs = sharedQ.get(0).split("##");
			String lchClsName = cmdStrs[0];
			String cfgName = cmdStrs[1];
			//logger.debug("开始执行命令：" + lchClsName + "：" + cfgName);
			String state = executeCmd(lchClsName, cfgName);// 执行命令
			sharedQ.add(state);
			sharedQ.notify();
		}
	}

//	public String executeCmd(String lchClsName, String cfgName) {
//		launchManager = DebugPlugin.getDefault().getLaunchManager();
//		ILaunchConfigurationType lchCfgType = launchManager.getLaunchConfigurationType(lchClsName);
//		String state = null;
//		ILaunchConfiguration[] lchCfgs = null;
//		try {
//			lchCfgs = launchManager.getLaunchConfigurations(lchCfgType);// 运行配置
//			for (ILaunchConfiguration lchCfg : lchCfgs) {
//				String name = lchCfg.getName().trim();
//				if (name != null && name.equals(cfgName.trim())) {// 找到对应的运行配置
//					state = doLaunch(lchCfg);// 发起lunch,执行命令
//					break;
//				}
//			}
//		} catch (CoreException e) {
//			logger.error("获取运行配置" + lchClsName + ": " + cfgName + "发生异常\n", e);
//			state = "-1";
//		}
///*
//		if (state.equals("1")) {
//			logger.debug("执行命令" + lchClsName + ": " + cfgName + "成功\n");
//		} else {
//	
//			if (state.equals("-2")) {
//				logger.debug("执行命令" + lchClsName + ": " + cfgName + "超时\n");
//			}
//		}
//		*/
//		return state;
//	}
	
	public String executeCmd(String lchClsName, String cfgName){
		IHandlerService handlerService = (IHandlerService)get
	}

	private String doLaunch(ILaunchConfiguration lchCfg) {
		// 执行命令后处于状态，"1"表示是成功完成，"-1"表示发生异常，"-2"表示超时
		String state = null;
		boolean timeout = false;// 执行命令超时
		boolean isExp = false;// 发生异常
		Monitor monitor = new Monitor();
		try {
			DebugUITools.buildAndLaunch(lchCfg, "run", monitor);
		} catch (CoreException e2) {
			isExp = true;
			logger.error ("DebugUITools.buildAndLaunch异常\n", e2);
		}
		long strtTime = System.currentTimeMillis();
		long endTime = 0;
		while (monitor.getState() != 1) {// -1、0和1分别表示Launch的未发起、发起、成功完成
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				isExp = true;
				logger.error("doLaunch方法休眠发生异常", e);
			}
			endTime = System.currentTimeMillis();
			if (endTime - strtTime > 120 * 1000) {// 超时时间为2分钟
				timeout = true;
				break;
			}
		}
		if (timeout) {
			state = "-2";// 超时

		} else {
			if (isExp) {
				state = "-1";// 异常
			} else {
				state = "1";// 执行成功
			}
		}
		return state;
	}
}
