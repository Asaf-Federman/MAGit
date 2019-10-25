package Server_Logic;

import Logic.CommitInformation;
import Logic.FileInformation;
import Logic.User;
import com.google.gson.Gson;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;

@WebServlet(name="fileInformation", urlPatterns = "/fileInformation")
public class FileInformationServlet extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException
	{
		Gson gson= new Gson();
		String repositoryName=request.getParameter("repository-name");
		String commitEncryptionKey=request.getParameter("commit-key");
		String userName=request.getHeader("Authorization");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(userName);
		FileInformation fileInformation=user.getFileInformation(repositoryName,commitEncryptionKey);
		
		PrintWriter out=response.getWriter();
		response.setContentType("application/json");
		String branchInformationListJson=gson.toJson(fileInformation);
		out.println(branchInformationListJson);
		out.flush();
	}
}
