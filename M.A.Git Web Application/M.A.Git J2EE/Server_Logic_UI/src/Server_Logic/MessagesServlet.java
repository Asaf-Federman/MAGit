package Server_Logic;

import Logic.MessageComponents.IMessage;
import Logic.User;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name="messages", urlPatterns = "/messages")
public class MessagesServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		getMessages(request,response);
	}
	
	private void getMessages(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson = new Gson();
		String UserName=request.getHeader("Authorization");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(UserName);
//		int typeValue=Integer.parseInt(request.getParameter("type"));
//		ArrayList<IMessage> messages=user.getCurrentMessages(MessagesManager.eMessageManager.getType(typeValue));
		ArrayList<IMessage> messages=user.getCurrentMessages();
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		String gsonResponse=gson.toJson(messages);
		out.println(gsonResponse);
		out.flush();
	}
}
