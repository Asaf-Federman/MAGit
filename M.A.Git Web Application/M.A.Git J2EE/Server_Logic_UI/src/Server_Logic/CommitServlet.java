package Server_Logic;

import Logic.StatusInformation;
import Logic.User;
import Logic.WorkingCopyInformation;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name="commit", urlPatterns = "/commit")
public class CommitServlet extends HttpServlet
{
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		commit(request,response);
	}
	
	private void commit(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String repositoryName=request.getParameter("repository-name");
		String userName=request.getHeader("Authorization");
		String commitMessage=request.getReader().readLine();
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		user.commit(repositoryName,commitMessage);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		getStatus(request,response);
	}
	
	private void getStatus(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson= new Gson();
		String repositoryName=request.getParameter("repository-name");
		String userName=request.getHeader("Authorization");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		List<StatusInformation> statusInformationList =user.getStatusInformation(repositoryName);
		
		PrintWriter out=response.getWriter();
		response.setContentType("application/json");
		String branchInformationListJson=gson.toJson(statusInformationList);
		out.println(branchInformationListJson);
		out.flush();
	}
}
