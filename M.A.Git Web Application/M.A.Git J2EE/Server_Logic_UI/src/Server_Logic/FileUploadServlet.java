package Server_Logic;

import Logic.*;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@WebServlet(name="XMLUpload" , urlPatterns = "/XMLFile")
public class FileUploadServlet extends HttpServlet
{
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		uploadFile(request, response);
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException
	{
		getInformation(request, response);
	}
	
	private void getInformation(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String username=request.getHeader("authorization");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(username);
		FileUploadServlet.getRepositoriesInformation(response,user);
	}
	
	public static void getRepositoriesInformation(HttpServletResponse response, User user) throws IOException
	{
		Gson gson = new Gson();
		PrintWriter out = response.getWriter();
		RepositoryDetails repositoryDetails = new RepositoryDetails();
		
		Collection<RepositoryManager> repositoryManagers = user.getRepositoryManagerMap().values();
		Collection<RepositoryDetail> repositoryDetailCollection=repositoryDetails.transformer(repositoryManagers);
		response.setContentType("application/json");
		String gsonResponse=gson.toJson(repositoryDetailCollection);
		out.println(gsonResponse);
		out.flush();
	}
	
	private void uploadFile(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		String username=request.getHeader("authorization");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(username);
		Utilz.TwoParametersResult validationRes;
		
		try
		{
			validationRes = user.validateXML(request.getReader());
			if(validationRes.m_IsValid)
			{
				user.fetchXMLRepository();
				getInformation(request, response);
			}
			else
			{
				warningMessage(response,validationRes.m_String);
			}
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}
	
	private void warningMessage(HttpServletResponse response,String message) throws IOException
	{
		Gson gson = new Gson();
		PrintWriter out = response.getWriter();
		String gsonContent=gson.toJson(message);
		out.println(gsonContent);
		out.flush();
	}
}
