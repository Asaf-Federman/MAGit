package Logic;

import java.io.File;
import java.io.IOException;

public interface Node
{
	Node Clone();
	void createWorkingCopyFile(File file) throws IOException;
	void createMagitFile(String repositoryPath, String encryptionKey) throws IOException;
}
