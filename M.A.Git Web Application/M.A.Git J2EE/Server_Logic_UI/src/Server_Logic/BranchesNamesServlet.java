package Server_Logic;

import Logic.*;
import Logic.MessageComponents.PRBasicMessage;
import Logic.MessageComponents.PRStatus;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name ="branchesNames", urlPatterns = "/branchesNames")
public class BranchesNamesServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		getBranchesNames(request,response);
	}
	
	private void getBranchesNames(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson= new Gson();
		String repositoryName=request.getParameter("repository-name");
		String userName=request.getHeader("Authorization");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		BranchesNames branchesNames =user.getBranchesNames(repositoryName);
		
		PrintWriter out=response.getWriter();
		response.setContentType("application/json");
		String branchInformationListJson=gson.toJson(branchesNames);
		out.println(branchInformationListJson);
		out.flush();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{
		Gson gson= new Gson();
		String repositoryName=request.getParameter("repository-name");
		String userName=request.getHeader("Authorization");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		String remoteUserName=user.getRemoteUserName(repositoryName);
		User remoteUser=GetUserList.getUsersList(getServletContext()).getUsers().get(remoteUserName);
		PRBasicMessage prBasicMessage=gson.fromJson(request.getReader(),PRBasicMessage.class);
		prBasicMessage.initialize();
		
		push(user,repositoryName,prBasicMessage.getTargetBranchName());
		addPRStatus(prBasicMessage,remoteUser,repositoryName);
		addPRAlert(prBasicMessage,remoteUser);
	}
	
	private void addPRAlert(PRBasicMessage prBasicMessage, User user)
	{
		PRBasicMessage prAlert= new PRBasicMessage(prBasicMessage);
		user.addMessage(prAlert);
	}
	
	private void push(User user,String repositoryName, String branchName) throws IOException
	{
		user.push(repositoryName,branchName);
	}
	
	private void addPRStatus(PRBasicMessage prBasicMessage, User user, String repositoryName) throws IOException
	{
		PRStatus prStatus=new PRStatus(prBasicMessage);
		user.addPRStatus(prStatus,repositoryName);
	}
	
}
