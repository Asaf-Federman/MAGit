package Components.AppWindowComponent;

import Components.AppWindowComponent.Layout.CommitTreeLayout;
import Components.TreeNodeComponent.CommitNode;
import com.fxgraph.edges.Edge;
import com.fxgraph.graph.Graph;
import com.fxgraph.graph.ICell;
import com.fxgraph.graph.Model;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.paint.Color;
import logic.Branch;
import logic.Commit;
import logic.RemoteBranch;
import logic.RepositoryManager;

import java.util.*;
import java.util.stream.Collectors;

public class BuildTree
{
	private RepositoryManager magitLogic;
	private Graph graph;
	private SimpleStringProperty isNodeClicked;
	private int xMeasures;
	private List<CommitTreeInfo> commitTreeInfoList;
	private Map<String,CommitTreeInfo> commitTreeInfoMap;
	private String chosenColor;
	
	public BuildTree(RepositoryManager magitLogic, Graph graph, SimpleStringProperty isNodeClicked)
	{
		xMeasures=15;
		chosenColor="-fx-color: #ff3333";
		this.magitLogic = magitLogic;
		this.graph = graph;
		this.isNodeClicked = isNodeClicked;
		this.isNodeClicked.addListener(event->changeColorOfBranch());
		createCommits();
	}
	
	private void changeColorOfBranch()
	{
		boolean isCommitInBranch;
		String color;
		for(Branch branch : magitLogic.getRepository().getBranches().values())
		{
			color="-fx-color: #DBDBDB";
			changeColor(color,branch.getKeyOfCommit());
		}
		
		for(Branch branch : magitLogic.getRepository().getBranches().values())
		{
			isCommitInBranch=isCommitInBranch(branch.getKeyOfCommit());
			if(isCommitInBranch)
			{
				color ="-fx-color: #337AB7";
				changeColor(color, branch.getKeyOfCommit());
			}
		}
	}
	
	private void changeColor(String color,String keyOfCommit)
	{
		Commit commit = magitLogic.getRepository().getCommits().get(keyOfCommit);
		CommitTreeInfo commitTreeInfo = commitTreeInfoMap.get(keyOfCommit);
		if (commitTreeInfo != null)
		{
			CommitNode c = (CommitNode) commitTreeInfo.getNode();
			if(commitTreeInfo.getEncryptionKey().equals(isNodeClicked.getValue()))
			{
				graph.getGraphic(c).setStyle(chosenColor);
			}
			else
			{
				graph.getGraphic(c).setStyle(color);
			}
			
		}
		
		for (String previousCommitKey : commit.getPreviousCommitKeys())
		{
			changeColor(color, previousCommitKey);
		}
	}
	
	private boolean isCommitInBranch(String keyOfCommit)
	{
		Commit commit = magitLogic.getRepository().getCommits().get(keyOfCommit);
		if(isNodeClicked.getValue().equals(keyOfCommit))
		{
			return true;
		}
		else if(commit.getPreviousCommitKeys().size()!=0)
		{
			boolean isCommitFound=false;
			for (String previousCommitKey : commit.getPreviousCommitKeys())
			{
				isCommitFound=isCommitInBranch(previousCommitKey) || isCommitFound;
			}
			
			return isCommitFound;
		}
		else
		{
			return false;
		}
	}
	
	private void createCommits()
	{
		final Model model = graph.getModel();
		graph.beginUpdate();
		commitTreeInfoList = new ArrayList<>();
		
		createCells(model);
		createEdges(commitTreeInfoList, model);
		
		graph.endUpdate();
		graph.layout(new CommitTreeLayout());
	}
	
	private void createEdges(List<CommitTreeInfo> commitTreeInfoList, Model model)
	{
		Map<String, CommitTreeInfo> commitMap;
		String encryptKey;
		
		commitMap = commitTreeInfoList.stream().collect(Collectors.toMap(CommitTreeInfo::getEncryptionKey, commitTreeInfo -> commitTreeInfo));
		
		for (Map.Entry<String, Branch> branchEntry : magitLogic.getRepository().getBranches().entrySet())
		{
			encryptKey = branchEntry.getValue().getKeyOfCommit();
			Commit currentCommit = magitLogic.getRepository().getCommits().get(encryptKey);
			makeTreeListRec(currentCommit, commitMap, encryptKey, null, model);
		}
	}
	
	private void createCells(Model model)
	{
		Map<String, Commit> validCommits = magitLogic.validCommits();
		Map<String, String> branchPointers = magitLogic.branchPointers();
		commitTreeInfoMap= new HashMap<>();
		ICell node;
		
		for (Map.Entry<String, Commit> commitEntry : validCommits.entrySet())
		{
			String message = commitEntry.getValue().getMessage();
			String author = commitEntry.getValue().getAuthor();
			String date = commitEntry.getValue().getCreationDate();
			String encryptionKey = commitEntry.getKey();
			List<String> previousEncryptionKeys = commitEntry.getValue().getPreviousCommitKeys();
			if (branchPointers.get(encryptionKey) != null)
			{
				String branchNames=branchPointers.get(encryptionKey);
				CommitNode commitNode=new CommitNode(encryptionKey, date, author, message, branchNames, isNodeClicked);
				for(String branchName : branchNames.split(", "))
				{
					Branch branch = magitLogic.getRepository().getBranches().get(branchName);
					if(branch instanceof RemoteBranch)
					{
						commitNode.setColor(Color.GREEN);
					}
				}
				
				node = commitNode;
			}
			else
			{
				node = new CommitNode(encryptionKey, date, author, message, "No Branch", isNodeClicked);
			}
			
			CommitTreeInfo commitTreeInfo=new CommitTreeInfo(message, date, author, encryptionKey, previousEncryptionKeys, node);
			commitTreeInfoMap.put(commitEntry.getKey(),commitTreeInfo);
			commitTreeInfoList.add(commitTreeInfo);
		}
		
		commitTreeInfoList.sort(CommitTreeInfo::compareTo);
		commitTreeInfoList.forEach(commitTreeInfo -> model.addCell(commitTreeInfo.getNode()));
		sortXByBranch(commitTreeInfoMap);
	}
	
	private void sortXByBranch(Map<String,CommitTreeInfo> commitTreeInfoMap)
	{
		int branchId=0;
		int currentXMeasures;
		Set<String> newSet= new HashSet<>();
		for(Branch branch : magitLogic.getRepository().getBranches().values())
		{
			currentXMeasures=branchId*xMeasures;
			sortXByBranchRec(currentXMeasures,branch.getKeyOfCommit(),commitTreeInfoMap,newSet);
			++branchId;
		}
	}
	
	private void sortXByBranchRec(int currentXMeasures, String keyOfCommit,Map<String,CommitTreeInfo> commitTreeInfoMap,Set<String> set)
	{
		Commit commit = magitLogic.getRepository().getCommits().get(keyOfCommit);
		CommitTreeInfo commitTreeInfo=commitTreeInfoMap.get(keyOfCommit);
		if(commitTreeInfo!=null && !set.contains(keyOfCommit))
		{
			CommitNode c = (CommitNode) commitTreeInfo.getNode();
			graph.getGraphic(c).relocate(currentXMeasures,0);
			set.add(keyOfCommit);
		}
		
		for(String previousCommitKey : commit.getPreviousCommitKeys())
		{
			sortXByBranchRec(currentXMeasures,previousCommitKey,commitTreeInfoMap,set);
		}
	}
	
	
	private void makeTreeListRec(Commit currentCommit, Map<String, CommitTreeInfo> commitMap, String encryptKey, String seniorEncryptionKey, Model model)
	{
		List<String> previousCommitList;
		CommitTreeInfo commitTreeInfo;
		
		if (currentCommit != null)
		{
			if (seniorEncryptionKey != null && commitMap.get(encryptKey) != null)
			{
				ICell seniorNode = commitMap.get(seniorEncryptionKey).getNode();
				commitTreeInfo = commitMap.get(encryptKey);
				Edge edge = new Edge(seniorNode, commitTreeInfo.getNode());
				model.addEdge(edge);
			}
			
			if(isNodeClicked.getValue().equals(encryptKey))
			{
				isNodeClicked.setValue("");
			}
			
			previousCommitList = currentCommit.getPreviousCommitKeys();
			for (String previousCommitKey : previousCommitList)
			{
				currentCommit = magitLogic.getRepository().getCommits().get(previousCommitKey);
				makeTreeListRec(currentCommit, commitMap, previousCommitKey, encryptKey, model);
			}
		}
	}
}
