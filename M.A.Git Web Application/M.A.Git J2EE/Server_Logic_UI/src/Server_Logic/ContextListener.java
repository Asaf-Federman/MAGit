package Server_Logic;


import org.apache.commons.io.FileUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;
import java.io.IOException;

@WebListener
public class ContextListener implements ServletContextListener
{
	private String location="c:\\magit-ex3";
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent)
	{
		File file = new File(location);
		file.mkdirs();
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent servletContextEvent)
	{
		File file = new File(location);
		
		try
		{
			FileUtils.deleteDirectory(file);
		}
		catch (IOException ignored)
		{
		}
	}
}
