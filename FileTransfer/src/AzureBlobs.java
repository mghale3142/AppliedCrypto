import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.*;


import org.bouncycastle.util.encoders.Base64;

public class AzureBlobs {
	
	private String storageConnectionString;
	
	private CloudStorageAccount storageAccount;
	
	private CloudBlobClient blobClient;
	
	private boolean connectionSet;
	
	/**
	 * Constructor, creates null.
	 */
	public AzureBlobs() {
		storageConnectionString = "";
		storageAccount = null;
		blobClient = null;
		connectionSet = false;
	}
	
	/**
	 * TBsQFnjFMsY5wmb8RiJYmP1WpPSfVvBa+M/WecDcUbk=
IV:
Ajl8jLDTXKYtMt2Wk18cNg==
	 */
	/**
	 * @param acName: appliedcrypto
	 * @param acKey: ynNyS547X1JcvM1TpojdpRWMgATGemLRawgjJ/UkRmw6Km6GuIvsjpZuwVI4FgE174Q1d82q8oLBxw6EuZ5ScA==
	 * 
	 * Connects to storage account and creates client.
	 */
	public AzureBlobs(String acName, String acKey) {
		
		storageConnectionString = "DefaultEndpointsProtocol=http;" +
			    "AccountName=" + acName + ";" +
			    "AccountKey=" + acKey;
		try {
			// Retrieve storage account from connection-string.
		    storageAccount = CloudStorageAccount.parse(storageConnectionString);

		    // Create the blob client.
		    blobClient = storageAccount.createCloudBlobClient();
		    
		    connectionSet = true;
		}
		catch(Exception e) {
			connectionSet = false;
			// Output the stack trace.
		    // e.printStackTrace();
			System.err.println("Wrong Credentials, try again.");
			System.exit(1);
		}
	}
	
	/**
	 * @return connectionSet
	 */
	public boolean isConnectionSet() {
		return connectionSet;
	}
	
	/**
	 * @param containerName
	 * @return container of that name
	 */
	public CloudBlobContainer getContainer(String containerName) {
		try
		{
		   // Get a reference to a container.
			
		   // The container name must be lower case
		   CloudBlobContainer container = blobClient.getContainerReference(containerName);
		   return container;
		}
		catch (Exception e)
		{
		    // Output the stack trace.
		    e.printStackTrace();
		    System.exit(1);
		    return null;
		}
	}
	/**
	 * 
	 */
	/**
	 * @param filePath
	 * @param containerName
	 */
	public void uploadBlob(String filePath, String containerName) {
		try
		{
			// Retrieve reference to a previously created container.
		    CloudBlobContainer container = getContainer(containerName);//blobClient.getContainerReference("mycontainer");
		    File source = new File(filePath);
		    // Create or overwrite the file blob with contents from a local file.
		    CloudBlockBlob blob = container.getBlockBlobReference(source.getName());
		    blob.upload(new FileInputStream(source), source.length());
		    System.out.println("Successful.");
		}
		catch (Exception e)
		{
		    // Output the stack trace.
		    e.printStackTrace();
		}
	}
	
	/**
	 * @param containerName
	 */
	public void listBlobs(String containerName) {
		try
		{
		    // Retrieve reference to a previously created container.
		    CloudBlobContainer container = getContainer(containerName);//blobClient.getContainerReference("mycontainer");

		    // Loop over blobs within the container and output the URI to each of them.
		    for (ListBlobItem blobItem : container.listBlobs()) {
		       System.out.println(blobItem.getUri());
		   }
		   System.out.println("Successful.");
		}
		catch (Exception e)
		{
		    // Output the stack trace.
		    e.printStackTrace();
		}
	}
	
	/**
	 * @param filepath
	 * @param containerName
	 */
	public void downloadBlob(String blobToDownload, String filepath, String containerName) {
		try
		{
		   // Retrieve reference to a previously created container.
		   CloudBlobContainer container = getContainer(containerName);

		   // Loop through each blob item in the container.
		   for (ListBlobItem blobItem : container.listBlobs()) {
		       // If the item is a blob, not a virtual directory.
		       if (blobItem instanceof CloudBlob) {
		           // Download the item and save it to a file with the same name.
		            CloudBlob blob = (CloudBlob) blobItem;
		            System.out.println(blob.getName());
		            if(blob.getName().equals(blobToDownload)) {
		            	blob.download(new FileOutputStream(filepath + blob.getName()));
		            }
		        }
		    }
		   System.out.println("Successful.");

		}
		catch (Exception e)
		{
		    // Output the stack trace.
		    e.printStackTrace();
		}
	}
	
	public void handleCommands(BufferedReader br) throws IOException {
		System.out.println("logged in");
		int count = 0;
		while(count<=10) {
			System.out.println("Choose action: list, upload, download or logout");
			String command = br.readLine().toLowerCase();
			String inputFilepath;
			String outputFilepath;
			String container;
			count++;
			switch(command) {
			case "list":
				System.out.println("Enter the name of the Azure container (doesn't create a new one if it doesn't exist): ");
				container = br.readLine();
				listBlobs(container);
				break;
			case "upload":
				System.out.println("Enter input filepath (file to encrypt): ");
				inputFilepath = br.readLine();
				System.out.println("Enter output filepath (location to store encrypted file): ");
				outputFilepath = br.readLine();
				
				System.out.println("Enter the name of the Azure container (doesn't create a new one if it doesn't exist: ");
				container = br.readLine();
				EncryptDecrypt.encryptFile(inputFilepath, outputFilepath);
				uploadBlob(outputFilepath, container);
				break;
			case "download":
				System.out.println("Enter name of the blob you want to download (*.txt) : ");
				String blobName = br.readLine();
				System.out.println("Enter download filepath (location to download from container, the name of the blob will be maintained so don't include filename): ");
				inputFilepath = br.readLine();
				System.out.println("Enter output filepath (location to store decrypted file, include filename): ");
				outputFilepath = br.readLine();
				System.out.println("Enter the name of the Azure container (doesn't create a new one if it doesn't exist: ");
				container = br.readLine();
				downloadBlob(blobName,inputFilepath, container);
				System.out.println("Enter the key to decrypt the file");
				byte[] key = Base64.decode(br.readLine());
				System.out.println("Enter the IV to decrypt the file");
				byte[] iv = Base64.decode(br.readLine());
				EncryptDecrypt.decryptFile(inputFilepath+blobName, key, iv, outputFilepath);
				break;
			case "logout":
				System.out.println("Logging out.");
				System.exit(0);
			default:
				System.out.println("Didn't recongnize action.");
				break;
			}
		}
		System.out.println("Only 10 actions can be performed at a time, please start again.");
		System.exit(0);
	}
	
	public static void main(String[] args) {
		// Might have to switch to buffered reader if problem with synchronization.
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
		System.out.println("Enter your Azure account name: ");
        String acName;
		try {
			acName = br.readLine();
			System.out.print("Enter your Azure key: ");
	        String acKey = br.readLine();
	        
	        AzureBlobs ab = new AzureBlobs(acName, acKey);
	        
	        ab.handleCommands(br);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

}
