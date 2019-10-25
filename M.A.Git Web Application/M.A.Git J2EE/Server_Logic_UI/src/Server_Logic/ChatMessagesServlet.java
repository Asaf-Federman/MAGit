package Server_Logic;

import Logic.BranchInformation;
import Logic.MessageComponents.ChatMessage;
import Logic.MessageComponents.IMessage;
import Logic.User;
import Logic.UserManager;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

@WebServlet(name="chatMessages", urlPatterns = "/chatMessages")
public class ChatMessagesServlet extends HttpServlet
{
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		addChatMessage(request,response);
		getLastVersion(response);
		getChatMessages(request,response);
	}
	
	private void addChatMessage(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson= new Gson();
		ChatMessage message=gson.fromJson(request.getReader(), ChatMessage.class);
		message.initializeDate();
		UserManager userManager=GetUserList.getUsersList(getServletContext());
		userManager.addChatMessage(message);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		getLastVersion(response);
		getChatMessages(request,response);
	}
	
	private void getChatMessages(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson= new Gson();
		String lastVersion=request.getParameter("last-version");
		UserManager userManager=GetUserList.getUsersList(getServletContext());
		Collection<IMessage> messages=userManager.getChatMessages(Integer.parseInt(lastVersion));
		
		PrintWriter out=response.getWriter();
		response.setContentType("application/json");
		String messagesJson=gson.toJson(messages);
		out.println(messagesJson);
		out.flush();
	}
	
	private void getLastVersion(HttpServletResponse response)
	{
		UserManager userManager=GetUserList.getUsersList(getServletContext());
		int lastVersion=userManager.getChatVersion();
		
		response.setHeader("version",Integer.toString(lastVersion));
	}
}
