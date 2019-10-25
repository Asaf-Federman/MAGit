
import org.apache.commons.io.FileUtils;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class UI
{
	private RepositoryManager m_MyRepManager;
	private Scanner m_Reader;
	
	public UI()
	{
		m_Reader = new Scanner(System.in);
		m_MyRepManager = new RepositoryManager();
		mainMenu();
	}
	
	private void mainMenu()
	{
		int counter;
		boolean toContinue;
		
		do
		{
			counter = 0;
			System.out.println();
			System.out.println("Please choose one option from the options below (enter it's number):");
			System.out.println(++counter + ". Update user's name");
			System.out.println(++counter + ". Read repository's data from XML");
			System.out.println(++counter + ". Load or Switch a repository");
			System.out.println(++counter + ". Show all the files of the current commit");
			System.out.println(++counter + ". Show status");
			System.out.println(++counter + ". Commit");
			System.out.println(++counter + ". Show all the branches in the system");
			System.out.println(++counter + ". Create a new branch");
			System.out.println(++counter + ". Remove a branch");
			System.out.println(++counter + ". Choose a new active branch");
			System.out.println(++counter + ". Show the history of the active branch");
			System.out.println(++counter + ". Change active commit");
			System.out.println(++counter + ". Create a new local repository");
			System.out.println(++counter + ". EXIT");
			System.out.println("Enter your choice: ");
			toContinue = getInputFromMainMenu(counter);
		}
		while (toContinue);
		
	}
	
	private int getValidOption(int numberOfOptions)
	{
		int userInput = -1;
		boolean isValid;
		
		do
		{
			isValid = true;
			try
			{
				userInput = Integer.parseInt(m_Reader.nextLine());
				if (userInput < 1 || userInput > numberOfOptions)
				{
					System.out.println("Please enter a number within the range of 1 to " + numberOfOptions);
					isValid = false;
				}
				else if (userInput == 1 || (userInput >= 4 && userInput <= 12))
				{
					if (m_MyRepManager.getRepository() == null)
					{
						System.out.println("You need to initialize a repository for that option");
						isValid = false;
					}
				}
			}
			catch (Exception e)
			{
				System.out.println("Please enter a valid number");
				isValid = false;
			}
		}
		while (!isValid);
		
		return userInput;
	}
	
	private boolean getInputFromMainMenu(int numberOfOptions)
	{
		int userInput = getValidOption(numberOfOptions);
		
		switch (userInput)
		{
			case 1:
				updateUserName();
				return true;
			case 2:
				fetchInformationFromXML();
				return true;
			case 3:
				changeRepository();
				return true;
			case 4:
				showBranchHistoricData();
				return true;
			case 5:
				showStatus();
				return true;
			case 6:
				commit();
				return true;
			case 7:
				showAllBranches();
				return true;
			case 8:
				createNewBranch();
				return true;
			case 9:
				deleteBranch();
				return true;
			case 10:
				chooseNewHeadBranch();
				return true;
			case 11:
				showActiveBranchHistory();
				return true;
			case 12:
				changeCommitOfHeadBranch();
				return true;
			case 13:
				createLocalRepository();
				return true;
			case 14:
				exit();
				return false;
			default:
				System.out.println("Invalid choice");
				return true;
		}
	}
	
	private void updateUserName()
	{
		String username;
		System.out.println("Please enter a new user's name (with atleast 2 letters):");
		username = m_Reader.nextLine();
		
		if (!Utilz.validateString(username))
		{
			System.out.println("Failed to change the username! The username u entered is invalid");
		}
		else
		{
			System.out.println("Successfully changed the username!");
			m_MyRepManager.setUsername(username);
		}
	}
	
	//C:\Users\ASAF\Desktop\XMLcheck\ex1-small.xml
	//C:\Users\ASAF\Desktop\XMLcheck\ex1-medium.xml
	//C:\Users\ASAF\Desktop\XMLcheck\ex1-large.xml
	//C:\Users\ASAF\Desktop\XMLcheck\ex1-error1-3.2.xml
	//C:\Users\ASAF\Desktop\XMLcheck\ex1-error2-3.3.xml
	//C:\Users\ASAF\Desktop\XMLcheck\ex1-error3-3.7.xml
	//C:\Users\ASAF\Desktop\XMLcheck\ex1-error4-3.9.xml
	private void fetchInformationFromXML()
	{
		String XMLpath;
		boolean flagToDelete = false;
		System.out.println("Please enter the XML's path:");
		XMLpath = m_Reader.nextLine();
		
		if (!Utilz.validateString(XMLpath))
		{
			System.out.println("The fetch of information from XML path failed! The path u entered is invalid");
			return;
		}
		
		XMLFetcher fetcher = new XMLFetcher();
		
		try
		{
			if (!fetcher.preValidateXML(XMLpath).m_IsValid)
			{
				System.out.println(fetcher.preValidateXML(XMLpath).m_String);
				return;
			}
			
			if (!fetcher.isValidPath().m_IsValid)
			{
				int numberOfOption;
				System.out.println("Repository already exists in this location, please choose an option:");
				System.out.println("1. Delete the old repository and fetch the new XML data");
				System.out.println("2. Do not fetch the new XML data to this location");
				try
				{
					numberOfOption = Integer.parseInt(m_Reader.nextLine());
				}
				catch (Exception e)
				{
					System.out.println("The fetch of information from XML path failed! Invalid string was entered");
					return;
				}
				
				if (numberOfOption != 1 && numberOfOption != 2)
				{
					System.out.println("The fetch of information from XML path failed! Invalid number was entered");
					return;
				}
				else if (numberOfOption == 2)
				{
					System.out.println("The fetch of information from XML path failed! You chose not to fetch the XML data");
					return;
				}
				else
				{
					flagToDelete = true;
				}
			}
			else if (!fetcher.isEmpty())
			{
				System.out.println("The fetch of information from XML path failed! Folder is not empty and therefore could not fetch the XML data");
				return;
			}
			
			fetchXML(fetcher, flagToDelete);
		}
		catch (JAXBException e)
		{
			e.printStackTrace();
		}
	}
	
	private void fetchXML(XMLFetcher fetcher, Boolean flagToDelete)
	{
		try
		{
			Utilz.TwoParametersResult result = fetcher.checkValidationXML();
			if (result.m_IsValid)
			{
				if (flagToDelete)
				{
					FileUtils.deleteDirectory(new File(fetcher.getMagitRepository().getLocation()));
				}
				
				this.m_MyRepManager.setRepository(fetcher.FetchXMLData());
				System.out.println(this.m_MyRepManager.fetchWorkingCopyInfo());
			}
			else
			{
				System.out.println(result.m_String);
			}
		}
		catch (JAXBException | IOException e)
		{
			System.out.println(e.getMessage());
		}
		catch (IllegalArgumentException e)
		{
			System.out.println("The fetch of information from XML path failed! Either the directory does not exist, or it is not a directory");
		}
	}
	
	private void changeRepository()
	{
		String repositoryPath;
		
		System.out.println("Please enter the repository's path:");
		repositoryPath = m_Reader.nextLine();
		
		if (!Utilz.validateString(repositoryPath))
		{
			System.out.println("The change of repository failed! The path u entered is invalid");
		}
		else if (this.m_MyRepManager.getRepository()!=null && this.m_MyRepManager.getRepository().getPath().equals(repositoryPath))
		{
			System.out.println("The change of repository failed! This repository is already active");
		}
		else
		{
			try
			{
				System.out.println(m_MyRepManager.changeRepository(repositoryPath));
			}
			catch (IOException e)
			{
				System.out.println("The change of repository failed! Couldn't fetch the M.A.Git data");
			}
		}
	}
	
	private void showBranchHistoricData()
	{
		List<NodeInformation> historicDataList = new LinkedList<>();
		m_MyRepManager.branchHistoricData(historicDataList);
		if (historicDataList.size() > 0)
		{
			for (NodeInformation node : historicDataList)
			{
				System.out.println(node.toString());
			}
		}
		else
		{
			System.out.println("There are no files to show!");
		}
	}
	
	private void showStatus()
	{
		System.out.println("The path of the current repository is: " + m_MyRepManager.getRepository().getPath());
		System.out.println("The current repository's name is: " + m_MyRepManager.getRepositoryName());
		System.out.println("The name of the current user in the system is: " + m_MyRepManager.getUsername());
		Map<String, String> status = null;
		try
		{
			status = this.m_MyRepManager.showStatus();
			printStatusCommit(status);
		}
		catch (IOException e)
		{
			System.out.println("Failed to show the status! Could not read the files needed");
		}
		
	}
	
	private void commit()
	{
		Map<String, String> result;
		String commitMessage;
		
		System.out.println("Please enter the repositories message (Should include atleast 2 letters):");
		commitMessage = m_Reader.nextLine();
		
		if (!Utilz.validateString(commitMessage))
		{
			System.out.println("The commit failed! The entered message is invalid!");
		}
		else
		{
			try
			{
				result = this.m_MyRepManager.showStatus();
				if (result.size() == 0)
				{
					System.out.println("The commit failed! The current commit is up to date!");
					return;
				}
				
				printStatusCommit(result);
				this.m_MyRepManager.commit(commitMessage);
				System.out.println("The new commit succeeded");
			}
			catch (IOException e)
			{
				System.out.println("The commit failed! Could not read the files needed");
			}
		}
	}
	
	private void printStatusCommit(Map<String, String> result)
	{
		if (result.isEmpty())
		{
			System.out.println("The repository did not change since ur last commit");
		}
		else
		{
			System.out.println("================================================================================================");
			System.out.println("The status of the current repository is: ");
			for (Map.Entry<String, String> resultMap : result.entrySet())
			{
				System.out.println(resultMap.getValue() + " " + resultMap.getKey());
			}
			System.out.println("================================================================================================");
		}
	}
	
	private void showAllBranches()
	{
		System.out.println("Those are the branches and their information in the system:");
		System.out.println(this.m_MyRepManager.showAllBranches());
	}
	
	private void createNewBranch()
	{
		String name;
		boolean activeBranch = false;
		
		System.out.println("Please enter the new branch' name (should include atleast 2 characters):");
		name = m_Reader.nextLine();
		
		if (!Utilz.validateString(name))
		{
			System.out.println("The creation of a new branch failed! The entered name is invalid!");
			return;
		}
		
		System.out.println("Would you like to make this branch the active branch? ");
		System.out.println("1. Yes");
		System.out.println("2. No");
		try
		{
			int numberOfChoice = Integer.parseInt(m_Reader.nextLine());
			if (numberOfChoice == 1)
			{
				activeBranch = true;
			}
			else if (numberOfChoice == 2)
			{
				System.out.println("You chose not to make this branch your active branch");
			}
			else
			{
				System.out.println("Invalid number");
				return;
			}
		}
		catch (NumberFormatException e)
		{
			System.out.println("You entered invalid string");
			return;
		}
		
		try
		{
			Utilz.TwoParametersResult result = this.m_MyRepManager.createBranch(name);
			System.out.println(result.m_String);
			if (!result.m_IsValid)
			{
				return;
			}
			if (activeBranch)
			{
				chooseNewHeadBranch(name);
			}
		}
		catch (IOException e)
		{
			System.out.println("The creation of a new branch failed! could not create new M.A.Git files");
		}
	}
	
	private void deleteBranch()
	{
		String name;
		System.out.println("Please enter the branch's name");
		name = m_Reader.nextLine();
		
		if (!Utilz.validateString(name))
		{
			System.out.println("The erasure of the branch failed! The branch' name you entered is invalid!");
		}
		else
		{
			System.out.println(m_MyRepManager.deleteBranch(name));
		}
	}
	
	private void chooseNewHeadBranch(String... arguements)
	{
		Utilz.TwoParametersResult branchName;
		boolean isUnsavedChanges;
		
		try
		{
			isUnsavedChanges = m_MyRepManager.checkIfExistUnsavedChanges();
		}
		catch (IOException e)
		{
			System.out.println("Could not show status of the current M.A.Git unsaved changes! ");
			return;
		}
		
		if (isUnsavedChanges)
		{
			int counter = 0;
			
			System.out.println("There are unsaved changes in your system. Would you like to save them?");
			System.out.println(++counter + ". Yes");
			System.out.println(++counter + ". No");
			counter = getValidOption(counter);
			
			if (counter == 1)
			{
				System.out.println("Heading to the menu...");
				return;
			}
		}
		
		if (arguements.length == 0)
		{
			branchName = chooseBranch();
		}
		else
		{
			branchName = new Utilz.TwoParametersResult(true, arguements[0]);
		}
		
		if (branchName.m_IsValid)
		{
			try
			{
				System.out.println(m_MyRepManager.changeHeadBranch(branchName.m_String));
			}
			catch (IOException e)
			{
				System.out.println("The switch to new head branch failed! Failed to create M.A.Git files");
			}
		}
	}
	
	private Utilz.TwoParametersResult chooseBranch()
	{
		int counter = 0;
		Utilz.TwoParametersResult result = new Utilz.TwoParametersResult();
		boolean isBranchExist, isBranchActive;
		
		System.out.println("Please enter the name of the branch u want to change to:");
		result.m_String = m_Reader.nextLine();
		
		isBranchExist = m_MyRepManager.getRepository().getBranches().get(result.m_String) != null;
		isBranchActive = result.m_String.equals(m_MyRepManager.getRepository().getHeadBranch().getBranchName());
		result.m_IsValid = true;
		if (!isBranchExist)
		{
			System.out.println("The change of branch failed! The name entered isn't of an existing branch");
			result.m_IsValid = false;
		}
		else if (isBranchActive)
		{
			System.out.println("The change of branch failed! The name entered is of an active branch");
			result.m_IsValid = false;
		}
		
		return result;
	}
	
	private void showActiveBranchHistory()
	{
		String branchHistory = this.m_MyRepManager.branchHistory();
		System.out.println(branchHistory);
	}
	
	private void changeCommitOfHeadBranch()
	{
		boolean isUnsavedChanges;
		String commitKey;
		System.out.println("Please write the encryption key of the commit you want to change to");
		commitKey = m_Reader.nextLine();
		
		if (!m_MyRepManager.checkForExistanceOfCommit(commitKey))
		{
			System.out.println("The commit key you entered does not exist");
			return;
		}
		
		if (m_MyRepManager.commitIsActive(commitKey))
		{
			System.out.println("The commit key you entered is the current active commit");
			return;
		}
		
		try
		{
			isUnsavedChanges = m_MyRepManager.checkIfExistUnsavedChanges();
		}
		catch (IOException e)
		{
			System.out.println("Could not show status of the current M.A.Git unsaved changes! ");
			return;
		}
		
		if (isUnsavedChanges)
		{
			System.out.println("There are open changes, would you like to save them? ");
			System.out.println("1. Yes");
			System.out.println("2. No");
			try
			{
				int numberOfChoice = Integer.parseInt(m_Reader.nextLine());
				if (numberOfChoice == 1)
				{
					System.out.println("Heading to menu.......");
					return;
				}
				else if (numberOfChoice == 2)
				{
					System.out.println("You chose to continue");
				}
				else
				{
					System.out.println("Invalid number");
					return;
				}
			}
			catch (NumberFormatException e)
			{
				System.out.println("You entered invalid string");
				return;
			}
		}
		
		try
		{
			m_MyRepManager.changeActiveCommit(commitKey);
			System.out.println("Successfully changed the active commit!");
			showBranchHistoricData();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}
	}
	
	private void createLocalRepository()
	{
		String repositoryPath;
		String repositoryName;
		boolean validPath;
		boolean validName;
		
		System.out.println("Please enter the repository's path");
		repositoryPath = m_Reader.nextLine();
		validPath = pathValidation(repositoryPath);
		if (!validPath)
		{
			return;
		}
		
		System.out.println("Please enter the repository's name");
		repositoryName = m_Reader.nextLine();
		validName = Utilz.validateString(repositoryName);
		if (!validName)
		{
			System.out.println("Could not create a new local repository! The repository's name is invalid");
			return;
		}
		
		try
		{
			m_MyRepManager.localRepositoryInitialization(repositoryPath, repositoryName);
			System.out.println("Creation of local repository succeeded!");
		}
		catch (IOException e)
		{
			System.out.println("Could not create a new local repository! Failed to create new M.A.Git files");
		}
	}
	
	private boolean pathValidation(String repositoryPath)
	{
		boolean validPath = false;
		boolean validString = Utilz.validateString(repositoryPath);
		if (validString)
		{
			validPath = Utilz.checkExistenceOfMagit(repositoryPath);
		}
		
		if (!validString)
		{
			System.out.println("Failed to create a local repository! The path entered is invalid!");
		}
		else if (!validPath)
		{
			System.out.println("Failed to create a local repository! The path entered already contains a repository!");
		}
		
		return validPath;
	}
	
	private void exit()
	{
		System.out.println("You chose to exit the system... FAREWELL");
	}
}
