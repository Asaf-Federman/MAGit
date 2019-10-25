package logic;

import com.sun.deploy.security.SelectableSecurityManager;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Utilz
{
	
	public static class TwoParametersResult
	{
		public boolean m_IsValid;
		public String m_String;
		
		public TwoParametersResult(boolean isValid, String message)
		{
			this.m_IsValid = isValid;
			this.m_String = message;
		}
		
		public TwoParametersResult()
		{
		
		}
	}
	
	public static void deleteFile(String path)
	{
		File file = new File(path);
		if(file.exists())
		{
			file.delete();
		}
	}
	
	public static String getCurrentTime()
	{
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss:SSS");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static String readFileAsString(String fileName) throws IOException
	{
		String data = "";
		data = new String(Files.readAllBytes(Paths.get(fileName)));
		
		return data;
	}
	
	public static void writeFileAsString(String source, String content)
	{
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(source)))
		{
			writer.write(content);
		}
		catch (IOException ignored)
		{
		}
	}
	
	public static void printMap(Map<String, String> toPrint)
	{
		for (Map.Entry<String, String> resultMap : toPrint.entrySet())
		{
			System.out.println(resultMap.getValue() + " " + resultMap.getKey());
		}
		System.out.println("===================================================");
	}
	
	public static void zipFile(String source, String destination) throws IOException
	{
		FileOutputStream fos = new FileOutputStream((destination));
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		File fileToZip = new File(source);
		FileInputStream fis = new FileInputStream(fileToZip);
		ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while ((length = fis.read(bytes)) >= 0)
		{
			zipOut.write(bytes, 0, length);
		}
		
		zipOut.close();
		fis.close();
		fos.close();
		fileToZip.delete();
	}
	
	public static void unZip(String source, String destination) throws IOException
	{
		File destDir = new File(destination);
		byte[] buffer = new byte[1024];
		ZipInputStream zis = new ZipInputStream(new FileInputStream(source));
		ZipEntry zipEntry = zis.getNextEntry();
		while (zipEntry != null)
		{
			File newFile = newFile(destDir, zipEntry);
			FileOutputStream fos = new FileOutputStream(newFile);
			int len;
			while ((len = zis.read(buffer)) > 0)
			{
				fos.write(buffer, 0, len);
			}
			fos.close();
			zipEntry = zis.getNextEntry();
		}
		zis.closeEntry();
		zis.close();
	}
	
	private static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException
	{
		File destFile = new File(destinationDir, zipEntry.getName());
		
		String destDirPath = destinationDir.getCanonicalPath();
		String destFilePath = destFile.getCanonicalPath();
		
		if (!destFilePath.startsWith(destDirPath + File.separator))
		{
			throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
		}
		
		return destFile;
	}
	
	public static String readZippedFile(String source, String destination, String fileName) throws IOException
	{
		unZip(source, destination);
		String fileContent = readFileAsString(destination + fileName);
		fileErasure(destination + fileName);
		return fileContent;
	}
	
	public static void fileErasure(String pathToErase)
	{
		File fileToErase = new File(pathToErase);
		fileToErase.delete();
	}
	
	
	public static void deleteMagitDirectory(File fileToDelete)
	{
		for (File file : Objects.requireNonNull(fileToDelete.listFiles(), "File collection can't be null"))
		{
			if (file.isFile())
			{
				file.delete();
			}
			else if (file.isDirectory() && !file.getName().equals(".magit"))
			{
				deleteMagitDirectory(file);
				file.delete();
			}
		}
	}
	
	public static void deleteMagitDirectory(String path)
	{
		deleteMagitDirectory(new File(path));
	}
	
	public static void deleteDirectory(String path)
	{
		try
		{
			FileUtils.deleteDirectory(new File(path));
		}
		catch (IOException e)
		{
		}
	}
	
	
	public static boolean checkExistenceOfMagit(String repositoryPath)
	{
		File file = new File(repositoryPath + "\\.magit");
		return file.exists();
	}
	
	public static TwoParametersResult isValidPath(String path)
	{
		boolean isMagitPathExists= Utilz.checkExistenceOfMagit(path);
		if (isMagitPathExists)
		{
			return new TwoParametersResult(false, "The XML fetch location already contains a M.A.Git folder");
		}
		
		return new TwoParametersResult(true, "There's no M.A.Git repository at that location");
	}
	
	public static boolean isEmpty(String path)
	{
		File file = new File(path);
		
		if (file.exists())
		{
			return Objects.requireNonNull(file.list()).length == 0;
		}
		
		return true;
	}
	
	public static boolean isExists(String path)
	{
		File file= new File(path);
		
		return file.exists();
	}
	
	public static String validDate(String date)
	{
		StringTokenizer	splittedDate= new StringTokenizer(date,"-//.//:");
		List<String> stringList = new LinkedList<>();
		StringBuilder newString= new StringBuilder();
		
		while(splittedDate.hasMoreElements())
		{
			stringList.add(splittedDate.nextToken());
		}
		
		for(int i=0;i<2;i++)
		{
			if(stringList.get(i).length()==1)
			{
				newString.append("0");
			}
			
			newString.append(stringList.get(i)).append(".");
		}
		newString.append(stringList.get(2)).append("-");
		for(int i=3; i<6;i++)
		{
			if(stringList.get(i).length()!=2)
			{
				newString.append("0");
			}
			
			newString.append(stringList.get(i)).append(":");
		}
		
		if(stringList.get(6).length()==1)
		{
			newString.append("00");
		}
		else if(stringList.get(6).length()==2)
		{
			newString.append("0");
		}
		
		newString.append(stringList.get(6));
		
		return newString.toString();
	}
	
	public static int DateTimeComparator(String thisCreationDate,String otherCreationDate)
	{
		DateTimeFormatter pattern = DateTimeFormatter.ofPattern( "dd.MM.yyyy-HH:mm:ss:SSS" ) ;
		LocalDateTime thisDateTime = LocalDateTime.parse(thisCreationDate, pattern);
		LocalDateTime otherDateTime =LocalDateTime.parse(otherCreationDate, pattern);
		return otherDateTime.compareTo(thisDateTime);
	}
	
	public static void createNewFile(String path, String content)
	{
		File file = new File(path);
		if(!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				return;
			}
		}
		
		writeFileAsString(path,content);
	}
	
	public static void moveFiles(String sourcePath,String destinationPath) throws IOException
	{
		File sourceFile = new File(sourcePath);
		File destinationFile = new File(destinationPath);
		FileUtils.copyDirectory(sourceFile,destinationFile);
	}
}
