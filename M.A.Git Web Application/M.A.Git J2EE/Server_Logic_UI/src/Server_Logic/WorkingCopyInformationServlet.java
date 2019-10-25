package Server_Logic;

import Logic.StatusInformation;
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

@WebServlet(name = "workingCopyInformation", urlPatterns = "/workingCopyInformation")
public class WorkingCopyInformationServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		getWorkingCopyInformation(request,response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		workingCopyInformationUpdate(request,response);
	}
	
	private void workingCopyInformationUpdate(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson= new Gson();
		String repositoryName=request.getParameter("repository-name");
		String action=request.getParameter("action");
		String userName=request.getHeader("Authorization");
		WorkingCopyInformation newWorkingCopyInformation=gson.fromJson(request.getReader(),WorkingCopyInformation.class);
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		user.changeWorkingCopyInformation(repositoryName,newWorkingCopyInformation,action);
		WorkingCopyInformation workingCopyInformation=user.getWorkingCopyInformation(repositoryName);
		
		PrintWriter out=response.getWriter();
		response.setContentType("application/json");
		String branchInformationListJson=gson.toJson(workingCopyInformation);
		out.println(branchInformationListJson);
		out.flush();
	}
	
	private void getWorkingCopyInformation(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson = new Gson();
		String repositoryName = request.getParameter("repository-name");
		String userName = request.getHeader("Authorization");
		User user = GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		WorkingCopyInformation workingCopyInformation = user.getWorkingCopyInformation(repositoryName);
		
		PrintWriter out = response.getWriter();
		response.setContentType("application/json");
		String branchInformationListJson = gson.toJson(workingCopyInformation);
		out.println(branchInformationListJson);
		out.flush();
	}
}
