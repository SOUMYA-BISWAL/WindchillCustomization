package ext.pdmlink;

import java.util.Vector;

import wt.doc.WTDocument;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.WhereExpression;
import wt.util.WTException;
import wt.workflow.work.WorkItem;
import wt.workflow.work.WorkflowHelper;


public class FetchTaskAndComplete implements RemoteAccess {

	/**
	 * @author 40002294(Soumya Ranjan Biswal)
	 */
	
	public static void main(String[] args) throws Exception   {
		RemoteMethodServer rms= RemoteMethodServer.getDefault(); 
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		System.out.println("Windchill Login Successfully");   
		 
		Class<?> argTypes[]={String.class};
		Object argValues[]={"soumya"};
		try {
			System.out.println("Call to Create Document Method");
			rms.invoke("getTaskList", "ext.pdmlink.FetchTaskAndComplete", null, argTypes, argValues); 
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	} 
	 

	public static void getTaskList(String str)throws Exception {
		System.out.println("Method Calling");
		try {

			QuerySpec querySpec = null;
			SearchCondition searchCondition = null;
			QueryResult queryResult = null;
			WTDocument doc = null;
			String docNumber ="0000000162";

			querySpec = new QuerySpec(WTDocument.class);
			searchCondition = new SearchCondition(WTDocument.class, "master>number", SearchCondition.EQUAL, docNumber, true);
			querySpec.appendWhere((WhereExpression) searchCondition, new int[]{0});
			queryResult = PersistenceHelper.manager.find((StatementSpec)querySpec);
			doc = (WTDocument)queryResult.nextElement();
			
			/**
			 * Get Task based on WindChill Object
			 */
			
			QueryResult listOfTask = WorkflowHelper.service.getWorkItems(doc);
			System.out.println(doc.getName()+" has "+listOfTask.size()+" number of Task");
			while(listOfTask.hasMoreElements()) {
				WorkItem task = (WorkItem) listOfTask.nextElement();
				
				/**
				 * If You want to complete this task call completeTask Method
				 */
				
				completeTask(task);
			}
		}catch (Exception e) { 
			System.out.println("Catch Execution");
			e.printStackTrace();
		}
	}
	
	/**
	 * @param task
	 * @throws WTException
	 */
	
	public static void completeTask(WorkItem task) throws WTException {
		
		Vector<String> routeList = new Vector<String>();
		routeList.addElement("Rejected");
		WorkflowHelper.service.workComplete(task, task.getOwnership().getOwner(), routeList);
		System.out.println("Task Completed Successfully");
	}
	
}
