package Server_Logic;

import Logic.BranchInformation;
import Logic.CommitInformation;
import Logic.User;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.List;

@WebServlet(name="commitInformation",urlPatterns = "/commitInformation")
public class CommitInformationServlet extends HttpServlet
{
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response){
	
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		getCommitInformation(request,response);
	}
	
	private void getCommitInformation(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson= new Gson();
		String repositoryName=request.getParameter("repository-name");
		String userName=request.getHeader("Authorization");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		Collection<CommitInformation> commitInformationCollection=user.getCommitInformationCollection(repositoryName);
		
		PrintWriter out=response.getWriter();
		response.setContentType("application/json");
		String branchInformationListJson=gson.toJson(commitInformationCollection);
		out.println(branchInformationListJson);
		out.flush();
	}
}
