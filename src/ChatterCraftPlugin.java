import java.util.logging.Level;
import java.util.logging.Logger;


public class ChatterCraftPlugin extends Plugin {

	private ChatterCraftListener listener = new ChatterCraftListener();
	private Logger log;
	String name = "ChatterCraftPlugin";
	String version = "0.9b";
	
	public void initialize() {
		etc.getLoader().addListener(PluginLoader.Hook.CHAT, listener, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, listener, this, PluginListener.Priority.MEDIUM);
	}
	
	@Override
	public void disable() {
		log.info(name + " shutting down...");
		listener.disable();
		etc.getInstance().removeCommand(ChatterCraftServer.USER_LIST_COMMAND);
		log.log(Level.INFO, name + " stopped.");
	}

	@Override
	public void enable() {
		log = Logger.getLogger("Minecraft");
		listener.logger = log;
		listener.enable();
		etc.getInstance().addCommand(ChatterCraftServer.USER_LIST_COMMAND, "List users that are chatting from the web.");
		log.log(Level.INFO, name + " version " + version + " started.");
		
	}

}
