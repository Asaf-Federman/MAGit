package Server_Logic;

import Logic.Branch;
import Logic.BranchInformation;
import Logic.User;
import Logic.WorkingCopyInformation;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name="push", urlPatterns = "/push")
public class PushServlet extends HttpServlet
{
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		push(request,response);
	}
	
	private void push(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson= new Gson();
		String repositoryName=request.getReader().readLine();
		String userName=request.getHeader("Authorization");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		
		PrintWriter out=response.getWriter();
		try
		{
			user.push(repositoryName);
			List<BranchInformation> branchInformation=user.getBranchInformationList(repositoryName);
			response.setContentType("application/json");
			String branchInformationListJson=gson.toJson(branchInformation);
			out.println(branchInformationListJson);
		}
		catch (Exception e)
		{
			out.println(e.getMessage());
		}
		finally
		{
			out.flush();
		}
	}
}
