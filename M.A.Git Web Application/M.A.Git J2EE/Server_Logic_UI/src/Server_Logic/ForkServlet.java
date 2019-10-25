package Server_Logic;

import Logic.MessageComponents.ForkMessage;
import Logic.RepositoryManager;
import Logic.User;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name="fork", urlPatterns = "/fork")
public class ForkServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String userName=request.getParameter("userName");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		FileUploadServlet.getRepositoriesInformation(response,user);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		forkRepositories(request,response);
	}
	
	private void forkRepositories(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String responseMessage;
		PrintWriter out = response.getWriter();
		String fetchFromRepository=request.getReader().readLine();
		String fetchFromUserName=request.getParameter("userName");
		User fetchFromUser=GetUserList.getUsersList(getServletContext()).getUsers().get(fetchFromUserName);
		String fetchToUserName=request.getHeader("Authorization");
		User fetchToUser=GetUserList.getUsersList(getServletContext()).getUsers().get(fetchToUserName);
		RepositoryManager repositoryManager=fetchFromUser.getRepositoryManagerMap().get(fetchFromRepository);
		try
		{
			fetchToUser.cloneRepository(repositoryManager,fetchFromUserName);
			ForkMessage forkMessage=new ForkMessage(fetchFromRepository,fetchToUserName);
			fetchFromUser.addMessage(forkMessage);
			responseMessage="Successfully forked "+fetchFromUserName+"'s repository "+fetchFromRepository;
			out.println(responseMessage);
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
