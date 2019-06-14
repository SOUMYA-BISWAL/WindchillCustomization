/**
 * @auther Soumya Ranjan Biswal
 */
package ext;

import java.beans.PropertyVetoException;
import java.lang.ExceptionInInitializerError;
import java.lang.reflect.InvocationTargetException;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentServerHelper;
import wt.doc.WTDocument;

import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.util.WTException;

/**
 * @author Shirish Morkhade
 *
 */
public class AllDocumentWithAttachmentInformation implements RemoteAccess {

	/**
	 * @param args
	 * @throws WTException 
	 * @throws IOException 
	 * @throws PropertyVetoException 
	 * @throws InvocationTargetException 
	 */
	public static void main(String[] args) throws WTException, PropertyVetoException, IOException, InvocationTargetException {

		// we have set the Username and password for Method Server so it will not ask for credentials again and again.  
		RemoteMethodServer rms = RemoteMethodServer.getDefault();
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");

		rms.invoke("docInfo", "ext.AllDocumentWithAttachmentInformation", null, null, null);
	}

	public static void docInfo() throws WTException, PropertyVetoException, IOException{

		String currfileName = null;
		ApplicationData appData = null;
		WTDocument doc = null;

		QuerySpec qs = new QuerySpec(WTDocument.class);
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);

		System.out.println("Total Number of Objects retrieved -> "+ qr.size());

		while (qr.hasMoreElements()) {

			doc = (WTDocument)qr.nextElement();
			String number = doc.getNumber();

			ContentHolder content = ContentHelper.service.getContents((ContentHolder)doc);
			Vector<?> vcontent = ContentHelper.getApplicationData(content); // this will return Secondary Attachments

			if(vcontent.size() > 0){

				System.out.println("Document " + number + " has no of secondary attachments -> " + vcontent.size());

				for(int i=0; i<vcontent.size(); i++) {
					System.out.println("***** VContent -> " + vcontent.get(i).toString());					 

					appData = (ApplicationData)vcontent.get(i);
					currfileName = appData.getFileName();
					// System.out.println("Current File Name -> " + currfileName);

					File saveAsFile = new java.io.File("D:/downloads",currfileName); // input your location and file name
					// System.out.println("saveAsFile -> " + saveAsFile.getCanonicalPath());

					try {
						ContentServerHelper.service.writeContentStream((ApplicationData)appData, saveAsFile.getCanonicalPath());
					} catch (ExceptionInInitializerError eii) {
						eii.printStackTrace();
					}		
				}
			}
			else
				System.out.println("Document " + number + " has no of secondary attachments -> " + vcontent.size());
		}
	}
}
