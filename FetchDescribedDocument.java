package ext.pdmlink;

import ext.generic.logger.Logger;
import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.WhereExpression;

public class FetchDescribedDocument implements RemoteAccess {

	public static void main(String[] args) throws Exception   {
		RemoteMethodServer rms= RemoteMethodServer.getDefault(); 
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		System.out.println("logged in Windchill");   
		 
		Class<?> argTypes[]={String.class};
		Object argValues[]={"soumya"};
		try {
			System.out.println("Call to Create Document Method");
			rms.invoke("fetchDesWTDocumentFormPart", "ext.pdmlink.FetchDescribedDocument", null, argTypes, argValues); 
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	} 
	
	public static void fetchDesWTDocumentFormPart(String str) {
		System.out.println("Method Calling");
		try {
			QuerySpec querySpec = null;
			SearchCondition searchCondition = null;
			QueryResult queryResult = null;
			QueryResult describeDocument = null;
			WTPart part = null;
			String partNumber = "GC000001";
			
			querySpec = new QuerySpec(WTPart.class);
			searchCondition = new SearchCondition(WTPart.class, "master>number", SearchCondition.EQUAL, partNumber, true);
			querySpec.appendWhere((WhereExpression) searchCondition, new int[]{0});
			queryResult = PersistenceHelper.manager.find((StatementSpec)querySpec);
			part = (WTPart)queryResult.nextElement();
			Logger.print("////////Part Name==> "+part.getName());
			
			describeDocument = PersistenceHelper.manager.navigate(part,WTPartDescribeLink.DESCRIBED_BY_ROLE, wt.part.WTPartDescribeLink.class,false);
			
			Logger.print(part.getName()+" has "+describeDocument.size()+" Descibe Document.");
			while(describeDocument.hasMoreElements()) {
				WTPartDescribeLink describeLink = (WTPartDescribeLink)describeDocument.nextElement();
				WTDocument document = describeLink.getDescribedBy();
				Logger.print("Described By Document Name...."+ document.getName());
				Logger.print("Described By Document State ...."+ document.getState());
			}
			
		}catch (Exception e) { 
			System.out.println("Catch Execution");
			e.printStackTrace();
		}
	}
}
