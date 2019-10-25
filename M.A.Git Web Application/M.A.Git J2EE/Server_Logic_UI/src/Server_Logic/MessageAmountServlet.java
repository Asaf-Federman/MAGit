package Server_Logic;

import Logic.User;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="messageAmount", urlPatterns = "/messageAmount")
public class MessageAmountServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		getAmountOfMessages(request,response);
	}
	
	private void getAmountOfMessages(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson = new Gson();
		String UserName=request.getHeader("Authorization");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(UserName);
		int amountOfMessages=user.getAmountOfMessages();
		response.setContentType("application/json");
		PrintWriter out=response.getWriter();
		String gsonResponse=gson.toJson(amountOfMessages);
		out.println(gsonResponse);
		out.flush();
	}
}
