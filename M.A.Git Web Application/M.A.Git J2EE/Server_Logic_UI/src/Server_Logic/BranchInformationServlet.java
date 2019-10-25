package Server_Logic;

import Logic.BranchInformation;
import Logic.User;
import Logic.UserManager;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name="branchInformation",urlPatterns = "/branchInformation")
public class BranchInformationServlet extends HttpServlet
{
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		changeHead(request,response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		getBranchInformation(request,response);
	}
	
	@Override
	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException{
		newBranch(request,response);
	}
	
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException{
		deleteBranch(request,response);
	}
	
	private void deleteBranch(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String repositoryName = request.getParameter("repository-name");
		String userName = request.getHeader("Authorization");
		String deleteBranchName = request.getParameter("delete-branch-name");
		UserManager userManager = GetUserList.getUsersList(getServletContext());
		userManager.deleteBranch(userName,repositoryName,deleteBranchName);
		getBranchInformation(request, response);
	}
	
	private void newBranch(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String repositoryName=request.getParameter("repository-name");
		String branchName=request.getParameter("branch-name");
		String userName=request.getHeader("Authorization");
		String newBranchName=request.getReader().readLine();
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		
		try
		{
			user.newBranch(repositoryName,branchName,newBranchName);
			getBranchInformation(request,response);
		}
		catch (Exception e)
		{
			PrintWriter out = response.getWriter();
			out.println(e.getMessage());
			out.flush();
		}
	}
	
	private void getBranchInformation(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson= new Gson();
		String repositoryName=request.getParameter("repository-name");
		String userName=request.getHeader("Authorization");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		List<BranchInformation> branchInformationList=user.getBranchInformationList(repositoryName);
		
		PrintWriter out=response.getWriter();
		response.setContentType("application/json");
		String branchInformationListJson=gson.toJson(branchInformationList);
		out.println(branchInformationListJson);
		out.flush();
	}
	
	private void changeHead(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String repositoryName=request.getParameter("repository-name");
		String userName=request.getHeader("Authorization");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		
		try
		{
			String branchName=request.getReader().readLine();
			user.changeHead(repositoryName,branchName);
			getBranchInformation(request,response);
		}
		catch(Exception e){
			PrintWriter out = response.getWriter();
			out.println(e.getMessage());
			out.flush();
		}
	}
}
