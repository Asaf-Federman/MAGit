package Server_Logic;

import Logic.UserManager;

import javax.servlet.ServletContext;

public class GetUserList
{
	public static final String USER_MANAGER_ATTRIBUTE="users";
	public static Object userManagerLock= new Object();
	
	public static UserManager getUsersList(ServletContext servletContext) {
		if(servletContext.getAttribute(USER_MANAGER_ATTRIBUTE) == null)
		{
			synchronized (userManagerLock)
			{
				if (servletContext.getAttribute(USER_MANAGER_ATTRIBUTE) == null)
				{
					servletContext.setAttribute(USER_MANAGER_ATTRIBUTE, new UserManager());
				}
			}
		}
		
		return (UserManager) servletContext.getAttribute(USER_MANAGER_ATTRIBUTE);
	}
}
