package pcmSolverDemo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

public class CommandExcuteServer {
	private static final Logger logger = Logger.getLogger(CommandExcuteServer.class);
	private int port = 4700;// socket通信约定的端口号
	private ServerSocket server = null;// 服务器端socket
	private BufferedReader is = null;// 服务器端socket的输入流
	private PrintWriter os = null;// 服务器端socket的输出流
	private Socket socket = null;// 客户端socket
	private static CommandExcuteServer instance = null;// 本类的唯一实例，采用单例模式
	private SolveService slvSrv = null;// SA解析服务

	// socket服务器状态{null,readingCmd,exeingCmd}
	private volatile String severState = "null";

	public String getSeverState() {
		return severState;
	}

	public synchronized void setServerState(String state) {
		this.severState = state;
	}

	private CommandExcuteServer() {
		slvSrv = SolveService.getInstance();
	}

	public static CommandExcuteServer getInstance() {
		if (instance == null) {
			instance = new CommandExcuteServer();
		}
		return instance;
	}

	private void initilize() {
		try {
			server = new ServerSocket(port);
			info("server:" + InetAddress.getLocalHost().toString() + " " + port);
			socket = server.accept();
			is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			os = new PrintWriter(socket.getOutputStream());
		} catch (IOException e) {
			logger.error("server sock initialize error\n",e);;
		}
	}

	private void handle() {// 已保证只有唯一的一个客户socket
		String cmdTxt = "";
		try {
			cmdTxt = is.readLine();
			while (!"end".equals(cmdTxt)) {// 如要执行的命令不是结束"end"命令
				setServerState("exeingCmd");
				os.println("exeingCmd");
				os.flush();
				slvSrv.solveSA(cmdTxt);
				int taskState = slvSrv.getState();
				if (taskState == -1) {
					info("解析SA发生异常!");
					logger.error("解析SA发生异常!");
					os.println("failure");
					os.flush();
				} else {
					os.println("success");
					os.flush();

				}
				setServerState("readingCmd");
				cmdTxt = is.readLine();
			}
		} catch (IOException e) {
			logger.error("CommandExcuteServer.handle方法发生读错误\n",e);
		}
		// 接收到"end"命令退出循环，回到"null"状态
		os.println("end");
		os.flush();
		setServerState("null");
		try {
			socket.close();
			socket = null;
			server.close();
			server = null;
			is.close();
			is = null;
			os.close();
			os = null;
		} catch (IOException e) {
			logger.error("关闭服务器端socket发生异常\n",e);
		}
	}

	public synchronized void startService() {
		new Thread() {
			public void run() {
				String tempState = getSeverState();
				if (tempState.equals("null")) {
					initilize();// 初始化服务端和客户端socket
					setServerState("readingCmd");
					handle();
				} else {
					info("the state of server is  " + tempState + ",and connection is refused!");
				}
			}
		}.start();
	}

	private void info(final String message) {
		displayAsync(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openInformation(Display.getDefault().getActiveShell(), "info", message);
			}
		});
	}

	private void displayAsync(Runnable runnable) {
		Display.getDefault().asyncExec(runnable);
		
		
	}
}
