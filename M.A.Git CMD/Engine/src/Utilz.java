import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
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
	
	public static String getCurrentTime()
	{
		DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy-hh:mm:ss:SSS");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	public static boolean validateString(String stringInput)
	{
		if (stringInput == null || stringInput.length() < 2)
		{
			return false;
		}
		
		return true;
	}
	
	public static String readFileAsString(String fileName) throws IOException
	{
		String data = "";
		data = new String(Files.readAllBytes(Paths.get(fileName)));
		
		return data;
	}
	
	public static void writeFileAsString(String source, String content)
	{
		BufferedWriter writer = null;
		try
		{
			writer = new BufferedWriter(new FileWriter(source));
			writer.write(content);
		}
		catch (IOException e)
		{
		}
		finally
		{
			try
			{
				if (writer != null)
				{
					writer.close();
				}
			}
			catch (IOException e)
			{
			}
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
	
	
	public static boolean checkExistenceOfMagit(String repositoryPath)
	{
		File file = new File(repositoryPath + "\\.magit");
		return !file.exists();
	}
	
	public static Utilz.TwoParametersResult isValidPath(String path)
	{
		boolean isValidPath= Utilz.checkExistenceOfMagit(path);
		if (!isValidPath)
		{
			return new Utilz.TwoParametersResult(false, "The XML fetch location already contains a M.A.Git folder");
		}
		
		return new Utilz.TwoParametersResult(true, "There's no M.A.Git repository at that location");
	}
	
	public static boolean isEmpty(String path)
	{
		File file = new File(path);
		
		if (file.exists())
		{
			return file.list().length <= 0;
		}
		
		return true;
	}
	
	public static boolean isExists(String path)
	{
		File file= new File(path);
		
		return file.exists();
	}
	
}
