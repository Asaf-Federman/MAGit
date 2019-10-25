package Server_Logic;

import Logic.*;
import Logic.MessageComponents.IMessage;
import Logic.MessageComponents.PRDeclineStatus;
import Logic.MessageComponents.PRStatus;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@WebServlet(name="prStatusInformation", urlPatterns = "/prStatusInformation")
public class PRStatusInformationServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		getPRStatus(request,response);
	}

	private void getPRStatus(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson= new Gson();
		String repositoryName=request.getParameter("repository-name");
		String userName=request.getHeader("Authorization");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		Collection<IMessage> prStatuses=user.getPRStatuses(repositoryName);

		PrintWriter out=response.getWriter();
		response.setContentType("application/json");
		String branchInformationListJson=gson.toJson(prStatuses);
		out.println(branchInformationListJson);
		out.flush();
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		try
		{
			changePRStatus(request,response);
			getPRStatus(request,response);
		}
		catch (Exception e)
		{
			response.getWriter().println(e.getMessage());
			response.getWriter().flush();
		}
	}
	
	private void changePRStatus(HttpServletRequest request, HttpServletResponse response) throws Exception
	{
		Gson gson= new Gson();
		PRStatus prStatus;
		String repositoryName=request.getParameter("repository-name");
		String userName=request.getHeader("Authorization");
		String status=request.getParameter("state");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		if(status.equals("Accepted")){
			prStatus=gson.fromJson(request.getReader(),PRStatus.class);
			user.merge(repositoryName,prStatus.getBaseBranchName(),prStatus.getTargetBranchName());
		}else{
			prStatus=gson.fromJson(request.getReader(), PRDeclineStatus.class);
		}
		
		user.changePRStatus(repositoryName,prStatus);
		user.addMessage(prStatus);
	}
}
