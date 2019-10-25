package Server_Logic;

import Logic.*;
import Logic.MessageComponents.PRBasicMessage;
import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name="pr-files", urlPatterns = "/pr-files")
public class PrFilesService extends HttpServlet
{
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		getFileStatus(request,response);
	}
	
	private void getFileStatus(HttpServletRequest request, HttpServletResponse response) throws IOException
	{
		Gson gson = new Gson();
		String UserName=request.getHeader("Authorization");
		String repositoryName=request.getParameter("repository-name");
		String id=request.getParameter("id");
		User user=GetUserList.getUsersList(getServletContext()).getUsers().get(UserName);
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		
		try{
		PRBasicMessage prStatus= (PRBasicMessage) user.getPRStatus(repositoryName,Integer.parseInt(id));
		List<StatusInformation> statusInformationList=user.showPRFileStatus(prStatus.getBaseBranchName(),prStatus.getTargetBranchName(),repositoryName);
		String statusInformationString=gson.toJson(statusInformationList);
		out.println(statusInformationString);
		}
		catch(Exception e){
			out.println(e.getMessage());
		}
		finally{
			out.flush();
		}
	}
}
