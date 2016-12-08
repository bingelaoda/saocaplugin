package pcmSolverDemo;

//import org.apache.log4j.Logger;
import org.eclipse.core.runtime.IProgressMonitor;

public class Monitor implements IProgressMonitor {
	//private static final Logger logger = Logger.getLogger(Monitor.class);
	private int state = -1;// -1、0和1分别表示Launch的未发起、发起、成功完成

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Monitor() {

	}

	@Override
	public void beginTask(String name, int totalWork) {
		state = 0;
		//logger.debug("start task " + name + ",共有" + totalWork + "项工作");
	}

	@Override
	public void done() {
		state = 1;
		//logger.debug("in monitor:I can detect done!");
	}

	@Override
	public void internalWorked(double work) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isCanceled() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setCanceled(boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setTaskName(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void subTask(String name) {
		// TODO Auto-generated method stub
	}

	@Override
	public void worked(int work) {
		// TODO Auto-generated method stub
	}

}
