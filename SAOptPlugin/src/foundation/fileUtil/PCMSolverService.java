package foundation.fileUtil;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;

import pcmSolverDemo.SolveService;

public class PCMSolverService {
	private static PCMSolverService instance = null;

	private PCMSolverService() {

	}

	public static PCMSolverService getInstance() {
		if (instance == null) {
			instance = new PCMSolverService();
		}
		return instance;
	}

	public synchronized void startService() {

		new Thread() {
			public void run() {
				SolveService slvSrv = SolveService.getInstance();
				long strtTime=System.currentTimeMillis();
				int slvNum=2;//总的解析次数
				int slvFailNum=-1;//在哪一次发生异常
				for (int i = 0; i < slvNum; i++) {
					slvSrv.solveSA("PCMSolver");
					int state = slvSrv.getState();
					if (state == -1) {
						info("解析SA发生异常!");
						slvFailNum=i;
						break;
					}
					slvSrv.solveSA("PCMReliability");
					state = slvSrv.getState();
					if (state == -1) {
						info("解析SA发生异常!");
						slvFailNum=i;
						break;
					}
				}
				long endTime=System.currentTimeMillis();
				String fileNm="D:\\slvRlt.txt";
				File file=new File(fileNm);
				String content="solve "+slvNum+"SAs"+" and time consuming is "+String.valueOf(endTime-strtTime)+"ms";
				if(slvFailNum==-1){
					content+="\n"+"has no exception.";
				}
				else{
					content+="\n"+"the exception occurs in "+slvFailNum+"st";
				}
	
				try {
					TxtFileUtil.writeTxtFile(content, file);
				} catch (Exception e) {
					e.printStackTrace();
				}
				//info(String.valueOf(endTime-strtTime));
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
