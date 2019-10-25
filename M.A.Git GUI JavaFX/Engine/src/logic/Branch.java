package logic;

import java.io.File;
import java.io.IOException;

public abstract class Branch
{
	private final String m_BranchName;
	private String m_KeyOfCommit;
	
	public Branch(String i_BranchName, String i_KeyOfCommit)
	{
		m_BranchName = i_BranchName;
		m_KeyOfCommit = i_KeyOfCommit;
	}
	
	public String getName()
	{
		return m_BranchName;
	}
	
	public String getKeyOfCommit()
	{
		return m_KeyOfCommit;
	}
	
	public void setKeyOfCommit(String keyOfCommit)
	{
		this.m_KeyOfCommit = keyOfCommit;
	}
	
	public abstract void createMagitFile(String pathOfRepository) throws IOException;
	
	public abstract void removeMagitFile(String pathOfRepository) throws IOException;
	
	public abstract Branch createBranchFromFile(File branchFile) throws IOException;
	
	
}
