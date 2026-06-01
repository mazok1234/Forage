package connection;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;

public class MySqlCleanupListener implements ServletContextListener {
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			AbandonedConnectionCleanupThread.checkedShutdown();
		} catch (Exception ex) {
			// Ignore shutdown cleanup errors
		}
	}
}
