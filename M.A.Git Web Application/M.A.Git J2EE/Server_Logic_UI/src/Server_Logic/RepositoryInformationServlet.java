package Server_Logic;

import Logic.RepositoryInformation;
import Logic.User;
import Logic.WorkingCopyInformation;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="repositoryInformation", urlPatterns = "/repositoryInformation")
public class RepositoryInformationServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException{
		getRepositoryInformation(request,response);
	}
	
	private void getRepositoryInformation(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson= new Gson();
		String repositoryName=request.getParameter("repository-name");
		String userName=request.getHeader("Authorization");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		RepositoryInformation repositoryInformation = user.getRepositoryInformation(repositoryName);
		
		PrintWriter out=response.getWriter();
		response.setContentType("application/json");
		String branchInformationListJson=gson.toJson(repositoryInformation);
		out.println(branchInformationListJson);
		out.flush();
	}
}
