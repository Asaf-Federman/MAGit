package Server_Logic;

import Logic.User;
import Logic.UserManager;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@WebServlet(name="AddUser",urlPatterns = "/getUsers")
public class AddUser extends HttpServlet
{
	protected void SignUp(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson = new Gson();
		User result;
		
		UserManager userManager=GetUserList.getUsersList(getServletContext());
		User user = gson.fromJson(request.getReader(),User.class);
		user.initialize();
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		try{
			userManager.isUserValid(user);
			userManager.addUser(user.getUserName(),user);
			result=user;
			String res=gson.toJson(result);
			out.println(res);
		}
		catch(Exception e){
			out.println(e.getMessage());
		}
		finally
		{
			out.flush();
		}
	}
	
	protected void fetchList(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson = new Gson();
		UserManager userManager=GetUserList.getUsersList(getServletContext());
		Collection<String> usersSet=userManager.getActiveUsersNames();
		PrintWriter out = response.getWriter();
		String gsonList=gson.toJson(usersSet);
		out.println(gsonList);
		out.flush();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		SignUp(request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		fetchList(request, response);
	}
}
