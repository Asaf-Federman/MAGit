package Server_Logic;

import Logic.MessageComponents.ChatMessage;
import Logic.MessageComponents.IMessage;
import Logic.UserManager;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebEndpoint;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@WebServlet(name="chatUsers", urlPatterns = "/chatUsers")
public class ChatUsersServlet extends HttpServlet
{
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String userName=request.getReader().readLine();
		
		addUser(userName);
		addLoginMessage(userName);
		getLastVersion(response);
		getUsers(request,response);
	}
	
	private void addLoginMessage(String userName)
	{
		ChatMessage chatMessage=new ChatMessage(userName,"Logged In", ChatMessage.eMessageState.Alert);
		UserManager userManager=GetUserList.getUsersList(getServletContext());
		userManager.addChatMessage(chatMessage);
	}
	
	private void getLastVersion(HttpServletResponse response)
	{
		UserManager userManager=GetUserList.getUsersList(getServletContext());
		int lastVersion=userManager.getChatVersion();

		response.setHeader("version",Integer.toString(lastVersion));
	}
	
	private void addUser(String userName) throws IOException
	{
		UserManager userManager=GetUserList.getUsersList(getServletContext());
		userManager.addChatUser(userName);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		getUsers(request,response);
	}
	
	private void getUsers(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson= new Gson();
		UserManager userManager=GetUserList.getUsersList(getServletContext());
		Collection<String> users=userManager.getChatUsers();
		
		PrintWriter out=response.getWriter();
		response.setContentType("application/json");
		String usersJson=gson.toJson(users);
		out.println(usersJson);
		out.flush();
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		deleteUser(request);
		deleteMessage(request);
	}
	
	private void deleteMessage(HttpServletRequest request)
	{
		ChatMessage chatMessage=new ChatMessage(request.getParameter("userName"),"Logged Out", ChatMessage.eMessageState.Alert);
		UserManager userManager=GetUserList.getUsersList(getServletContext());
		userManager.addChatMessage(chatMessage);
	}
	
	private void deleteUser(HttpServletRequest request)
	{
		UserManager userManager=GetUserList.getUsersList(getServletContext());
		String userName=request.getParameter("userName");
		userManager.removeChatUser(userName);
	}
}
