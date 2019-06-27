package ext.pdmlink;

import ext.generic.logger.Logger;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.part.WTPart;
import wt.part.WTPartHelper;
import wt.part.WTPartMaster;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.WhereExpression;
import wt.util.WTException;

/**
 * 
 * @author 40002294
 *
 */

public class FetchBOM implements RemoteAccess {
	
	/**
	 * 
	 * @param args
	 * @throws Exception
	 */
	
	public static void main(String[] args) throws Exception   {
		RemoteMethodServer rms= RemoteMethodServer.getDefault(); 
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		System.out.println("logged in Windchill");   
		 
		Class<?> argTypes[]={String.class};
		Object argValues[]={"soumya"};
		try {
			System.out.println("Call to Create Document Method");
			rms.invoke("fetchBOMStructure", "ext.pdmlink.FetchBOM", null, argTypes, argValues); 
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		} 
	 
	/**
	 * 
	 * @param str
	 * @throws Exception
	 * This Code is for Fetching all part structure
	 */
	
	public static void fetchBOMStructure(String str)throws Exception {
		System.out.println("Method Calling");
		try {
			String partNumber = "GC000001";
			checkSubPartBOM(partNumber);
		}catch (Exception e) { 
			System.out.println("Catch Execution");
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param partNumber
	 * @throws WTException
	 */
	
	public static void checkSubPartBOM(String partNumber) throws WTException {
		QuerySpec querySpec = null;
		SearchCondition searchCondition = null;
		QueryResult queryResult = null;
		WTPart part = null;

		querySpec = new QuerySpec(WTPart.class);
		searchCondition = new SearchCondition(WTPart.class, "master>number", SearchCondition.EQUAL, partNumber, true);
		querySpec.appendWhere((WhereExpression) searchCondition, new int[]{0});
		queryResult = PersistenceHelper.manager.find((StatementSpec)querySpec);
		part = (WTPart)queryResult.nextElement();
		Logger.print("////////Part Name==> "+part.getName());
		queryResult = WTPartHelper.service.getUsesWTPartMasters(part);
		Logger.print(part.getName()+" SIZE IS ---> "+queryResult.size());
		
		/**
		 * 
		 * if part has sub part this it will call IterationBOM method
		 *  
		 */
		
		if(queryResult.size()>0) {
			Logger.print(" Below Part are Child Part of ---> "+part.getName());
			iterationBOM(queryResult);
		}
	}
	
	/**
	 * 
	 * @param queryResult
	 * @throws WTException
	 */
	
	public static void iterationBOM(QueryResult queryResult) throws WTException {		
		while(queryResult.hasMoreElements()) {
			WTPartUsageLink ul = (WTPartUsageLink) queryResult.nextElement();
		    WTPartMaster part1= (WTPartMaster) ul.getUses();
		    
		    /**
		     *
		     * it will send PartMaster Number to check tis part master has sub Part or not
		     * 
		     */
		    
		    checkSubPartBOM(part1.getNumber());
		}
	}

}

