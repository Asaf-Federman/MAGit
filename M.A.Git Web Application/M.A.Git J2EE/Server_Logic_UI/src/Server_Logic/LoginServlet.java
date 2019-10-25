package Server_Logic;

import Logic.User;
import Logic.UserManager;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="login", urlPatterns = "/login")
public class LoginServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		login(request,response);
	}
	
	private void login(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson = new Gson();
		User result;
		
		UserManager userManager=GetUserList.getUsersList(getServletContext());
		User user = gson.fromJson(request.getParameter("userName"),User.class);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		try{
			userManager.isUserExists(user);
			result=user;
			String res=gson.toJson(result);
			out.println(res);
		}
		catch(Exception e){
			out.println(e.getMessage());
		}
		finally{
			out.flush();
		}
	}
}
