package saoptplugin.handlers;

import org.apache.log4j.Logger;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;

import pcmSolverDemo.CommandExcuteServer;
//import pcmSolverDemo.SolverThread;


/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SAOptHandler extends AbstractHandler {
	// log4J的日志记录器
		private static final Logger logger = Logger.getLogger("D");
	
	/**
	 * The constructor.
	 */
	public SAOptHandler() {
	}

	/**
	 * the command has been executed, so extract extract the needed information
	 * from the application context.
	 */
	public Object execute(ExecutionEvent event) {

		CommandExcuteServer server = CommandExcuteServer.getInstance();
	     logger.debug("启动Socket通信服务");
		server.startService();
		return null;
		
		
		/*PCMSolverService srv = PCMSolverService.getInstance();
		srv.startService();
		return null;*/
		
	}
}
