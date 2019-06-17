package ext.kal.helper;

import java.beans.PropertyVetoException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import wt.change2.WTChangeRequest2;
import wt.content.ApplicationData;
import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.content.ContentRoleType;
import wt.content.ContentServerHelper;
import wt.content.FormatContentHolder;
import wt.doc.WTDocument;
import wt.enterprise.Master;
import wt.epm.EPMDocument;
import wt.epm.build.EPMBuildRule;
import wt.fc.ObjectReference;
import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.fc.PersistenceServerHelper;
import wt.fc.QueryResult;
import wt.fc.collections.WTArrayList;
import wt.fc.collections.WTHashSet;
import wt.fc.collections.WTList;
import wt.fc.collections.WTSet;
import wt.folder.Folder;
import wt.folder.FolderEntry;
import wt.folder.FolderHelper;
import wt.iba.definition.litedefinition.AttributeDefDefaultView;
import wt.iba.definition.litedefinition.StringDefView;
import wt.iba.definition.service.IBADefinitionHelper;
import wt.iba.value.DefaultAttributeContainer;
import wt.iba.value.IBAHolder;
import wt.iba.value.litevalue.AbstractValueView;
import wt.iba.value.litevalue.StringValueDefaultView;
import wt.iba.value.service.IBAValueDBService;
import wt.iba.value.service.IBAValueHelper;
import wt.inf.container.WTContained;
import wt.inf.container.WTContainer;
import wt.inf.container.WTContainerRef;
import wt.lifecycle.LifeCycleException;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleTemplateReference;
import wt.lifecycle.State;
import wt.org.WTUser;
import wt.part.WTPart;
import wt.part.WTPartDescribeLink;
import wt.part.WTPartUsageLink;
import wt.pds.StatementSpec;
import wt.pom.PersistenceException;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.series.MultilevelSeries;
import wt.series.SeriesIncrementInvalidException;
import wt.session.SessionHelper;
import wt.util.WTException;
import wt.util.WTInvalidParameterException;
import wt.util.WTPropertyVetoException;
import wt.vc.IterationIdentifier;
import wt.vc.VersionControlException;
import wt.vc.VersionControlHelper;
import wt.vc.VersionIdentifier;
import wt.vc.Versioned;
import wt.vc.views.View;
import wt.vc.wip.NonLatestCheckoutException;
import wt.vc.wip.WorkInProgressException;
import wt.vc.wip.WorkInProgressHelper;

import com.ptc.core.foundation.type.server.impl.TypeHelper;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.windchill.pdmlink.proimigration.server.PartHelper;


public class MethodHelper
{
	//Reads each line in the file and add it to LinkedHashSet and returns the Set
	public static LinkedHashSet<String> readFile(String filePath)
	{
		BufferedReader br = null;
		String line = "";
		LinkedHashSet<String> numberList = new LinkedHashSet<String>();

		try 
		{			
			br = new BufferedReader(new FileReader(filePath));  

			while ((line = br.readLine()) != null) 
			{
				line = line.trim();
				//System.out.println("Line read:"+line);
				numberList.add(line);
			} 

		}
		catch (FileNotFoundException e) {
			System.out.println("File Not Found. Please Check the .csv file path");
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
		finally 
		{
			if (br != null)
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}	
		}
		return numberList;
	}
//Create a Group inside a Particular Organization
	public static File downloadContent()
		{
		String groupName = "TestAPIGroup";
		String targetOrgName = "Demo Organization";
		try {
			Enumeration orgs = OrganizationServicesHelper.manager.findLikeOrganizations(WTOrganization.NAME, targetOrgName, 
					((ExchangeContainer)WTContainerHelper.getExchangeRef().getContainer()).getContextProvider());
			 WTOrganization targetOrg = null;
			 if (orgs.hasMoreElements()){
				  targetOrg = (WTOrganization) orgs.nextElement();
				  OrgContainer org = WTContainerHelper.service.getOrgContainer(targetOrg);
				  DirectoryContextProvider dcp = WTContainerHelper.service.getPublicContextProvider(org,WTGroup.class);
				  WTGroup group = WTGroup.newWTGroup(groupName, dcp);
				  group.setContainer(org);
				  OrganizationServicesHelper.manager.createPrincipal(group);
			  }
		} catch (Exception e) {
			  e.printStackTrace();
		}
	}

	public static File downloadContent(WTDocument previousIter, String filePath)
	{
		File  primaryFile = null;

		File outputFile = new File(filePath);			

		FormatContentHolder formatContentHolder;

		try {
			formatContentHolder = (FormatContentHolder) ContentHelper.service.getContents(previousIter);
			ContentRoleType primaryRoleType = ContentRoleType.toContentRoleType("PRIMARY");
			QueryResult primaryContentResult = ContentHelper.service.getContentsByRole(formatContentHolder, primaryRoleType);
			while(primaryContentResult.hasMoreElements()){
				ApplicationData primaryData = (ApplicationData)primaryContentResult.nextElement();	        	
				//if the primary file doesn't exist in the vault proceed with next document
				try{
					primaryFile = new File(outputFile, primaryData.getFileName());
					ContentServerHelper.service.writeContentStream(primaryData, primaryFile.getCanonicalPath());
				}catch(Exception e){
					e.printStackTrace();
					continue;
				}
			}
		} catch (WTException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (PropertyVetoException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return primaryFile;


	}

	public static void attachFiletoDynamicDocument(WTDocument epmdoc, File contentFile) throws WTException
	{
		try {

			InputStream inputStream0 = new FileInputStream(contentFile);

			FormatContentHolder fcHolder = epmdoc;
			ContentHolder contentHolder = ContentHelper.service.getContents(epmdoc);
			ApplicationData applicationData = ApplicationData.newApplicationData(contentHolder);
			applicationData.setRole(ContentRoleType.PRIMARY);
			applicationData.setFileName(contentFile.getName());

			ContentServerHelper.service.updatePrimary(fcHolder, applicationData, inputStream0);
			ContentServerHelper.service.updateHolderFormat(fcHolder);					

			inputStream0.close();			
			//contentFile.delete();

		} catch (Exception e) {
			throw new WTException(e);

		}       
	}

	// Queries the latest version of the WTPart given the view number
	public static WTPart getLatestWTPartByView(String partNumber,Long viewNum) throws Exception
	{
		WTPart part = null;   

		QuerySpec querySpec = new wt.query.QuerySpec(WTPart.class);
		querySpec.appendWhere(new SearchCondition(WTPart.class, "master>number", SearchCondition.EQUAL, partNumber, true), new int[]{0});
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(WTPart.class,"view.key.id",SearchCondition.EQUAL,viewNum), new int[]{0});
		querySpec.appendAnd();
		querySpec.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest","TRUE"), new int[]{0});
		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec)querySpec);  
		
		WTPart latestVersion = null;
		Boolean initial = true;
		while(queryResult.hasMoreElements())
		{
			part = (WTPart) queryResult.nextElement();
			if(initial == true)
			{
				latestVersion = part;
				initial = false;
			}
			
			String revision = part.getVersionIdentifier().getValue();
			int partRev = Integer.valueOf(revision);
			String latestVersionRevision = latestVersion.getVersionIdentifier().getValue();
			int latestVersionRev = Integer.valueOf(latestVersionRevision);
			//System.out.println("Part Rev = "+partRev);
			//System.out.println("Latest Revision = "+latestVersionRev);
			//System.out.println();
			if(latestVersionRev<partRev)
			{
				//System.out.println("Inside If");
				latestVersion = part;
			}
			//System.out.println("Part Version = "+part.getVersionIdentifier().getValue()+"."+part.getIterationIdentifier().getValue()+"("+part.getViewName()+")");
		}
		return latestVersion;
	}
	
	// Queries the latest version of the WTPart given the view number
		public static WTPart getLatestWTPart(String partNumber) throws Exception
		{
			WTPart part = null;   

			QuerySpec querySpec = new wt.query.QuerySpec(WTPart.class);
			querySpec.appendWhere(new SearchCondition(WTPart.class, "master>number", SearchCondition.EQUAL, partNumber, true), new int[]{0});
			querySpec.appendAnd();
			querySpec.appendWhere(new SearchCondition(WTPart.class, "iterationInfo.latest","TRUE"), new int[]{0});
			QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec)querySpec);  
			
			if(queryResult.hasMoreElements())
			{
				part = (WTPart) queryResult.nextElement();
				part = (WTPart) PartHelper.getLatestVersion(part);
			}
			return part;
		}
	
	public static WTContainer getContainer(String containerName) throws Exception
	{
		QuerySpec queryspec3 = new QuerySpec(WTContainer.class);
		SearchCondition searchcondition3 = new SearchCondition(WTContainer.class, "containerInfo.name", SearchCondition.EQUAL, containerName);
		queryspec3.appendWhere(searchcondition3, new int[]{0});
		QueryResult qr3 = PersistenceHelper.manager.find((StatementSpec) queryspec3);
		
		WTContainer container = null;
		if(qr3!=null)
			container = (WTContainer) qr3.nextElement();
		else
			throw new Exception("Container not found = "+containerName);
		
		return container;
	}

	public static WTDocument getLatestWTDocument(String docNumber) throws Exception
	{
		WTDocument doc = null;   

		QuerySpec querySpec = new wt.query.QuerySpec(WTDocument.class);
		querySpec.appendWhere(new wt.query.SearchCondition(WTDocument.class, "master>number", wt.query.SearchCondition.EQUAL, docNumber, true), new int[]{0});

		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec)querySpec);  
		if(queryResult.hasMoreElements())
		{
			doc = (WTDocument) PartHelper.getLatestVersion((WTDocument) queryResult.nextElement());
		}
		else
		{
			throw new Exception("Document not found = "+doc);
		}

		return doc;
	}
	
	public static WTChangeRequest2 getChangeRequest(String chrNo) throws Exception
	{
		WTChangeRequest2 doc = null;   

		QuerySpec querySpec = new wt.query.QuerySpec(WTChangeRequest2.class);
		querySpec.appendWhere(new wt.query.SearchCondition(WTChangeRequest2.class, "master>number", wt.query.SearchCondition.EQUAL, chrNo, true), new int[]{0});

		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec)querySpec);  
		if(queryResult.hasMoreElements())
		{
			doc = (WTChangeRequest2) PartHelper.getLatestVersion((WTChangeRequest2) queryResult.nextElement());
		}
		else
		{
			throw new Exception("Document not found = "+doc);
		}

		return doc;
	}
	
	public static QueryResult getAllObjectVersions(String docNumber,Class classData) throws Exception
	{
		QuerySpec querySpec = new wt.query.QuerySpec(classData);
		querySpec.appendWhere(new wt.query.SearchCondition(classData, "master>number", wt.query.SearchCondition.EQUAL, docNumber, true), new int[]{0});

		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec)querySpec);  
		
		return queryResult;
	}

	public static WTDocument getWTDocumentByVersion(String docNumber, String revision, String iteration) throws Exception
	{
		WTDocument doc = null; 

		QuerySpec querySpec = new wt.query.QuerySpec(WTDocument.class);
		querySpec.appendWhere(new wt.query.SearchCondition(WTDocument.class, "master>number", wt.query.SearchCondition.EQUAL, docNumber, true), new int[]{0});

		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec)querySpec);  
		while(queryResult.hasMoreElements())
		{
			doc = (WTDocument) queryResult.nextElement();
			String revisionLatest  = doc.getVersionIdentifier().getValue();
			String iterationLatest = doc.getIterationIdentifier().getValue();
			if(revisionLatest.equalsIgnoreCase(revision) && iterationLatest.equalsIgnoreCase(iteration))
				break;
			doc = null;
		}
		System.out.println("Document Version = "+doc.getVersionIdentifier().getValue()+"."+doc.getIterationIdentifier().getValue());
		return doc;
	}
	
	public static EPMDocument getEPMDocByVersion(String docNumber, String revision) throws Exception
	{
		EPMDocument doc = null; 

		QuerySpec querySpec = new wt.query.QuerySpec(EPMDocument.class);
		querySpec.appendWhere(new wt.query.SearchCondition(EPMDocument.class, "master>number", wt.query.SearchCondition.EQUAL, docNumber, true), new int[]{0});

		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec)querySpec);  
		while(queryResult.hasMoreElements())
		{
			doc = (EPMDocument) queryResult.nextElement();
			String revisionLatest  = doc.getVersionIdentifier().getValue();
			if(revisionLatest.equalsIgnoreCase(revision))
				break;
			doc = null;
		}
		//System.out.println("Document = "+doc);
		doc = (EPMDocument) VersionControlHelper.service.getLatestIteration(doc, false);
		//System.out.println("Document Version = "+doc.getVersionIdentifier().getValue()+"."+doc.getIterationIdentifier().getValue());
		return doc; 
	}
	
	public static EPMDocument getLatestEPMDocument(String docNumber) throws Exception 
	{
		EPMDocument doc = null;   

		QuerySpec querySpec = new wt.query.QuerySpec(EPMDocument.class);
		querySpec.appendWhere(new wt.query.SearchCondition(EPMDocument.class, "master>number", wt.query.SearchCondition.EQUAL, docNumber, true), new int[]{0});

		QueryResult queryResult = PersistenceHelper.manager.find((StatementSpec)querySpec);  
		if(queryResult.hasMoreElements())
		{
			doc = (EPMDocument) PartHelper.getLatestVersion((EPMDocument) queryResult.nextElement());
		}
		
		return doc;
	}
	
	public static WTUser getUser(String username) throws Exception 
	{
		WTUser user = null;
		QuerySpec qs = new QuerySpec(WTUser.class);
		qs.appendWhere(new wt.query.SearchCondition(WTUser.class, WTUser.NAME,wt.query.SearchCondition.LIKE, username, false), new int[] { 0 });
		QueryResult qr = PersistenceHelper.manager.find((StatementSpec) qs);
		if(qr.hasMoreElements())
			user = (WTUser)qr.nextElement();
		
		return user;
	}


	//get the view number by passing view name
	public static Long getView(String viewName) throws WTException 
	{
		QuerySpec viewQuery = new wt.query.QuerySpec(View.class);
		viewQuery.appendWhere(new wt.query.SearchCondition(View.class,"name",wt.query.SearchCondition.EQUAL,viewName,true), new int[]{0});
		QueryResult views = PersistenceHelper.manager.find((StatementSpec)viewQuery);
		View partView = null;
		if(views.hasMoreElements()) 
		{
			partView = (View)views.nextElement();
		}

		if(partView==null)
			return null;

		String viewObid = partView.toString();
		viewObid = viewObid.substring(viewObid.lastIndexOf(":") + 1, viewObid.length());
		Long viewNum = Long.parseLong(viewObid);
		return viewNum;
	}


	//Append Manufacturing Location to the checked out part and replace old values with new ones
	@SuppressWarnings("unchecked")
	public static WTPart appendMFGLocWithCoCi(WTPart coPart, String mfgLoc) throws WTException
	{
		PersistableAdapter pa = new PersistableAdapter(coPart,null,Locale.US,new UpdateOperationIdentifier());
		pa.load("ManufacturingLocation");

		if(pa.get("ManufacturingLocation")!=null)
		{
			//System.out.println("inside MFG Loc..");
			Object value = pa.get("ManufacturingLocation");

			if(value.getClass().isArray())
			{
				Object[] mfgLocation = (Object[]) value;
				//System.out.println("Manufacturing Location Existing= "+Arrays.toString(mfgLocation));
				List<Object> list = new ArrayList<Object>(Arrays.asList(mfgLocation));

				//replace old manufacturing location values with new one
				//System.out.println("List Data before Replace = "+list);
				Collections.replaceAll(list, "Nogales-MX", "Nogales-MX-US11");
				Collections.replaceAll(list, "EDESA_Monterry-MX", "EDESA_Monterry-MX-US13");
				Collections.replaceAll(list, "Bielsko_Biala-PL", "Bielsko-Biala-ED-PL02");
				//System.out.println("List Data after Replace = "+list);
				
				if(!list.contains(mfgLoc))
					list.add(mfgLoc);
				
				Object[] obj = list.toArray();
				//System.out.println("Manufacturing Location Array= "+Arrays.toString(obj));
				pa.set("ManufacturingLocation",obj);
			}
			else
			{
				String temp = (String) value;

				//replace old manufacturing location values with new one
				//System.out.println("String data before replace"+temp);
				if(temp.equalsIgnoreCase("Nogales-MX"))
					temp = "Nogales-MX-US11";
				else if(temp.equalsIgnoreCase("EDESA_Monterry-MX"))
					temp = "EDESA_Monterry-MX-US13";
				else if(temp.equalsIgnoreCase("Bielsko_Biala-PL"))
					temp = "Bielsko-Biala-ED-PL02";
				//System.out.println("String data after replace"+temp);
				
				/*temp.replaceAll("Nogales-MX", "Nogales-MX-US11");
				temp.replaceAll("EDESA_Monterry-MX", "EDESA_Monterry-MX-US13");
				temp.replaceAll("Bielsko_Biala-PL", "Bielsko-Biala-ED-PL02");*/
				
				// Append new value.
				//System.out.println("Manufacturing Location Existing= "+temp);
				if(!temp.equals(mfgLoc))
				{
					String[] strArray = new String[] {temp, mfgLoc};
					//System.out.println("Manufacturing Location String Array= "+Arrays.toString(strArray));
					pa.set("ManufacturingLocation",strArray );
				}
				else
				{
					pa.set("ManufacturingLocation",temp);
				}
			}

		}
		else
		{
			pa.set("ManufacturingLocation",mfgLoc );
		}
		pa.apply();		
		PersistenceHelper.manager.modify(coPart);

		return coPart;
	}

	// Append Manufacturing Location Without Check Out Check In
	public static WTPart appendMFGLocWithOutCoCi(IBAHolder ibaholder, String ibaName, String ibaValue) throws RemoteException, WTException, WTPropertyVetoException 
	{

		AbstractValueView    aabstractvalueview[] = null;
		DefaultAttributeContainer     defaultattributecontainer = null;
		IBAValueDBService ibaDbService =null;
		Object constraintParam = null;
		AttributeDefDefaultView attribute = null;
		StringDefView stringAttribute = null;
		StringValueDefaultView   attString  = null;

		//System.out.println(" $$$$  IBAHolder :-->"+((WTPart)ibaholder).getNumber()+"::: IBAName :--->"+ibaName+":::: IBA Value :--->"+ibaValue);

		ibaholder = IBAValueHelper.service.refreshAttributeContainerWithoutConstraints(ibaholder);
		defaultattributecontainer = (DefaultAttributeContainer) ibaholder.getAttributeContainer();
		ibaDbService = new IBAValueDBService();
		constraintParam = defaultattributecontainer.getConstraintParameter();
		if (defaultattributecontainer != null) 
		{
			aabstractvalueview = defaultattributecontainer.getAttributeValues();			
			//System.out.println("aabstractvalueview length = "+aabstractvalueview.length);
		}

		for (int i = 0; i < aabstractvalueview.length; i++)
		{
			if (aabstractvalueview[i].getDefinition().getName().equals(ibaName)) 
			{

				if(aabstractvalueview[i].getLocalizedDisplayString().equalsIgnoreCase("Nogales-MX"))
				{
					//System.out.println("Update attribute ---->"+"Iba Name = "+aabstractvalueview[i].getDefinition().getName()+" Iba value = "+"Nogales-MX");
					((StringValueDefaultView) aabstractvalueview[i]).setValue("Nogales-MX-US11");
					defaultattributecontainer.updateAttributeValue((StringValueDefaultView) aabstractvalueview[i]);
				}

				if(aabstractvalueview[i].getLocalizedDisplayString().equalsIgnoreCase("EDESA_Monterry-MX"))
				{
					//System.out.println("Update attribute ---->"+"Iba Name = "+aabstractvalueview[i].getDefinition().getName()+" Iba value = "+"EDESA_Monterry-MX");
					((StringValueDefaultView) aabstractvalueview[i]).setValue("EDESA_Monterry-MX-US13");
					defaultattributecontainer.updateAttributeValue((StringValueDefaultView) aabstractvalueview[i]);
				}

				if(aabstractvalueview[i].getLocalizedDisplayString().equalsIgnoreCase("Bielsko_Biala-PL"))
				{
					//System.out.println("UPDATE attribute ---->"+"Iba Name = "+aabstractvalueview[i].getDefinition().getName()+" Iba value = "+"Bielsko_Biala-PL");
					((StringValueDefaultView) aabstractvalueview[i]).setValue("Bielsko-Biala-ED-PL02");
					defaultattributecontainer.updateAttributeValue((StringValueDefaultView) aabstractvalueview[i]);
				}

			}
		}

		//System.out.println("ADD attribute ---->"+"Iba Name = "+ibaName+" Iba value = "+ibaValue);
		attribute = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
		if (attribute != null) 
		{
			stringAttribute = (StringDefView) attribute;
			attString = new StringValueDefaultView(stringAttribute, ibaValue);
			defaultattributecontainer.addAttributeValue(attString);
		}

		ibaholder.setAttributeContainer(defaultattributecontainer);
		ibaDbService.updateAttributeContainer(ibaholder, constraintParam, null, null);

		return (WTPart) ibaholder;
	}

	// Update IBA WithOut Check Out Check In
	public static WTPart updateIBAWithOutCoCi(IBAHolder ibaholder, String ibaName, String ibaValue) throws RemoteException, WTException, WTPropertyVetoException 
	{
		//System.out.println("Inside updateIBA");
		AbstractValueView    aabstractvalueview[] = null;
		DefaultAttributeContainer     defaultattributecontainer = null;
		IBAValueDBService ibaDbService =null;
		Object constraintParam = null;
		AttributeDefDefaultView attribute = null;
		StringDefView stringAttribute = null;
		StringValueDefaultView   attString  = null;


		//System.out.println(" $$$$  IBAHolder :-->"+((WTPart)ibaholder).getNumber()+"::: IBAName :--->"+ibaName+":::: IBA Value :--->"+ibaValue);

		ibaholder = IBAValueHelper.service.refreshAttributeContainerWithoutConstraints(ibaholder);
		defaultattributecontainer = (DefaultAttributeContainer) ibaholder.getAttributeContainer();
		ibaDbService = new IBAValueDBService();
		constraintParam = defaultattributecontainer.getConstraintParameter();
		boolean exist = false;
		if (defaultattributecontainer != null) 
		{
			//System.out.println("Inside If");
			aabstractvalueview = defaultattributecontainer.getAttributeValues();
			//System.out.println("aabstractvalueview length = "+aabstractvalueview.length);
		}

		for (int i = 0; i < aabstractvalueview.length; i++)
		{
			//System.out.println("Update attribute ---->"+"Iba Name = "+aabstractvalueview[i].getDefinition().getName()+" Iba value = "+aabstractvalueview[i].getLocalizedDisplayString());

			if (aabstractvalueview[i].getDefinition().getName().equals(ibaName)) 
			{
				((StringValueDefaultView) aabstractvalueview[i]).setValue(ibaValue);
				defaultattributecontainer.updateAttributeValue((StringValueDefaultView) aabstractvalueview[i]);
				exist = true;
			}
		}

		if(!exist)
		{
			//System.out.println("ADD attribute ---->"+"Iba Name = "+ibaName+" Iba value = "+ibaValue);
			attribute = IBADefinitionHelper.service.getAttributeDefDefaultViewByPath(ibaName);
			if (attribute != null) 
			{
				stringAttribute = (StringDefView) attribute;
				attString = new StringValueDefaultView(stringAttribute, ibaValue);
				defaultattributecontainer.addAttributeValue(attString);

			}
		}

		ibaholder.setAttributeContainer(defaultattributecontainer);
		ibaDbService.updateAttributeContainer(ibaholder, constraintParam, null, null);

		return (WTPart) ibaholder;
	}

	//add or replace iba value based on append flag
	public static WTPart updateIBAWithCiCo(WTPart partObj, String ibaName, String ibaValue, boolean append ) throws NonLatestCheckoutException, WorkInProgressException, WTPropertyVetoException, PersistenceException, WTException 
	{		
		Locale locale = SessionHelper.getLocale();
		PersistableAdapter obj = new PersistableAdapter(partObj,null,locale,new UpdateOperationIdentifier());
		obj.load(ibaName);

		String iba = (String) obj.get(ibaName);
		if(append)
		{
			if(iba==null)
				obj.set(ibaName,ibaValue);	
			else
				obj.set(ibaName,iba+";"+ibaValue);	
		}			
		else
			obj.set(ibaName,ibaValue);	

		obj.apply();		
		partObj = (WTPart) PersistenceHelper.manager.save(partObj);
		partObj = (WTPart) PersistenceHelper.manager.refresh(partObj);

		return partObj;
	}

	// Set Part Version same as another Part and Set state to Released/Production
	public static Versioned setVersionAndState(Versioned partVerTobeSet, Versioned partVerUsed, String CheckInComments) throws SeriesIncrementInvalidException, VersionControlException, WTPropertyVetoException, WTException
	{

		VersionIdentifier mwpVid = partVerUsed.getVersionIdentifier();																// get MWP version identifier
		IterationIdentifier mwpIid = partVerUsed.getIterationIdentifier();														    // get MWP iteration identifier
		VersionIdentifier sapVid = partVerTobeSet.getVersionIdentifier();																// get SAP version identifier


		if(!mwpVid.getValue().equalsIgnoreCase(sapVid.getValue()))																	// check if revision of SAP and MWP matches then set only iteration
		{
			partVerTobeSet = (WTPart)VersionControlHelper.service.newVersion(partVerTobeSet, mwpVid, mwpIid, true);				// if revision doesn't match create a new version same as MWP version										

			partVerTobeSet = (WTPart) PersistenceHelper.manager.save(partVerTobeSet); 

			Locale locale1 = SessionHelper.getLocale();
			PersistableAdapter obj1 = new PersistableAdapter(partVerTobeSet,null,locale1,null);
			obj1.load("state.state");
			String sapState = (String) obj1.get("state.state");
			//LOGGER.debug("SAP state after revise = "+sapState);

			if(sapState.equalsIgnoreCase("INWORK"))
			{
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) partVerTobeSet, State.toState("RELEASED"));
				//LOGGER.debug("State set to = RELEASED");
			}

			if(sapState.equalsIgnoreCase("PRODUCTIONINWORK"))
			{
				LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) partVerTobeSet, State.toState("PRODUCTION"));
				//LOGGER.debug("State set to = PRODUCTION");
			}

		}   

		return partVerTobeSet;

	}
	
	public static Versioned setRevision(Versioned partObj, String revision) throws SeriesIncrementInvalidException, VersionControlException, WTPropertyVetoException, WTException
	{
		
		VersionIdentifier vid = partObj.getVersionIdentifier();																// get SAP version identifier
		IterationIdentifier id = IterationIdentifier.newIterationIdentifier("1");
		

		if(!vid.getValue().equalsIgnoreCase(revision))																	// check if revision of SAP and MWP matches then set only iteration
		{
			Master master = (Master) partObj.getMaster();
			String series = master.getSeries();		
			VersionIdentifier verID = VersionIdentifier.newVersionIdentifier(MultilevelSeries.newMultilevelSeries(series,revision));
			
			partObj = VersionControlHelper.service.newVersion(partObj, verID, id, true);				// if revision doesn't match create a new version same as MWP version										
			partObj = (Versioned) PersistenceHelper.manager.save(partObj);  
		}   

		return partObj;

	}

	//Add describe link to a part same as another Part
	public static WTPart addDescLinks(WTPart partToAssocLink, WTPart partHavingLink) throws WTException
	{
		QueryResult qr = PersistenceHelper.manager.navigate(partHavingLink, WTPartDescribeLink.DESCRIBED_BY_ROLE, WTPartDescribeLink.class,false); // navigate describe links of MWP part

		while(qr.hasMoreElements())																											
		{
			WTPartDescribeLink link = (WTPartDescribeLink) qr.nextElement();															    // get the 	describe link
			if(link != null)
			{
				WTDocument doc = link.getDescribedBy();																						// get the document object
				WTPartDescribeLink link1 = WTPartDescribeLink.newWTPartDescribeLink(partToAssocLink, doc);										// create describe by link between the SAP Part and document 
				PersistenceHelper.manager.save(link1);																						// save the link
				partToAssocLink = (WTPart) PersistenceHelper.manager.save(partToAssocLink);
				partToAssocLink = (WTPart) PersistenceHelper.manager.refresh(partToAssocLink);
			}
		}
		return partToAssocLink;	
	}
	
	//Move to container
	public static void moveToContainer(FolderEntry sapPartObj3, WTContainer wtContainer, String folerPath) throws WTException 
	{
		WTContainerRef ref = WTContainerRef.newWTContainerRef(wtContainer);				// get target container reference 
		Folder folder = null;


		folder = FolderHelper.service.getFolder(folerPath, ref);			// Fetch the folder from the new location 																					
		if (folder != null)
		{
			FolderHelper.service.changeFolder(sapPartObj3, folder);		// move the SAP Part to the new location 
			System.out.println("Move Folder is done to "+folder.getName());
		}


	}
	
	

	//Add describe link to a document same as another document
	public static WTDocument addDescLinks(WTDocument docToAssocLink, WTDocument docHavingLink, String comments) throws WTException, WTPropertyVetoException
	{
		QueryResult qr = PersistenceHelper.manager.navigate(docHavingLink, WTPartDescribeLink.DESCRIBES_ROLE, WTPartDescribeLink.class,false); // navigate describe links of MWP part
		System.out.println("Describe links count = "+qr.size());
		System.out.println("docToAssocLink version = "+docToAssocLink.getVersionIdentifier().getValue()+"."+docToAssocLink.getIterationIdentifier().getValue());
		while(qr.hasMoreElements())																											
		{
			WTPartDescribeLink link = (WTPartDescribeLink) qr.nextElement();															    // get the 	describe link
			if(link != null)
			{
				WTPart part = link.getDescribes();	
				part = (WTPart) VersionControlHelper.service.getLatestIteration(part, false);// get the document object
				part = (WTPart) WorkInProgressHelper.service.checkout(part, WorkInProgressHelper.service.getCheckoutFolder(),"Checked out").getWorkingCopy();			
				WTPartDescribeLink link1 = WTPartDescribeLink.newWTPartDescribeLink(part, docToAssocLink);										// create describe by link between the SAP Part and document 
				PersistenceHelper.manager.save(link1);																						// save the link
				part = (WTPart) PersistenceHelper.manager.save(part);
				part = (WTPart) PersistenceHelper.manager.refresh(part);
				part = (WTPart) WorkInProgressHelper.service.checkin(part, comments);
			}
		}
		return docToAssocLink;	
	}

	//remove all part desc link
	public static WTPart removeDescLink(WTPart part) throws WTException
	{
		QueryResult qr = PersistenceHelper.manager.navigate(part, WTPartDescribeLink.DESCRIBED_BY_ROLE, WTPartDescribeLink.class,false); // navigate describe links of MWP part

		WTSet partDescribeLink = new WTHashSet();
		partDescribeLink.addAll(qr);

		if (partDescribeLink.size() != 0) 
		{
			PersistenceServerHelper.manager.remove(partDescribeLink);																		// remove describe links from MWP Part
			part = (WTPart) PersistenceHelper.manager.refresh(part);

		}

		return part;
	}
	
	
	public static boolean hasDescribeLink(WTPart part) throws WTException
	{
		boolean hasDescLink = false;
		QueryResult qr = PersistenceHelper.manager.navigate(part, WTPartDescribeLink.DESCRIBED_BY_ROLE, WTPartDescribeLink.class,false); // navigate describe links of MWP part

		WTSet partDescribeLink = new WTHashSet();
		partDescribeLink.addAll(qr);

		if (partDescribeLink.size() != 0) 
		{
			hasDescLink = true;

		}

		return hasDescLink;
	}
	
	public static WTDocument removeDescLink(WTDocument doc) throws WTException
	{
		QueryResult qr = PersistenceHelper.manager.navigate(doc, WTPartDescribeLink.DESCRIBES_ROLE, WTPartDescribeLink.class,false); // navigate describe links of MWP part

		WTSet partDescribeLink = new WTHashSet();
		partDescribeLink.addAll(qr);

		if (partDescribeLink.size() != 0) 
		{
			PersistenceServerHelper.manager.remove(partDescribeLink);																		// remove describe links from MWP Part
		}

		return doc;
	}

	//Add owner or content link based on flag isOwner
	public static WTPart addCADLinks(WTPart part, EPMDocument epm, int linkType) throws WTException, WTPropertyVetoException
	{
		if(epm!=null)
		{
			if(linkType == 7)																		// for first SAP Part isOwnerLink = true
			{
				//LOGGER.debug("owner link set");
				EPMBuildRule link1 = EPMBuildRule.newEPMBuildRule(epm, part, 7);			// create a owner link between SAP Part and EPM Doc	
				PersistenceServerHelper.manager.insert(link1);											// save the link	
				part = (WTPart) PersistenceHelper.manager.save(part);																									   
				part = (WTPart) PersistenceHelper.manager.refresh(part);
			}	
	
			else if(linkType == 2)
			{
				//LOGGER.debug("is Owner Link = "+isOwnerLink);
				EPMBuildRule link1 = EPMBuildRule.newEPMBuildRule(epm, part, 2);			// set content link for all other SAP parts																					  	
				PersistenceServerHelper.manager.insert(link1);											// save the link																					  
				part = (WTPart) PersistenceHelper.manager.save(part);																									   
				part = (WTPart) PersistenceHelper.manager.refresh(part);
			} 
			
			else if(linkType == 4)
			{
				//LOGGER.debug("is Owner Link = "+isOwnerLink);
				EPMBuildRule link1 = EPMBuildRule.newEPMBuildRule(epm, part, 4);			// set content link for all other SAP parts																					  	
				PersistenceServerHelper.manager.insert(link1);										// save the link																					  
				part = (WTPart) PersistenceHelper.manager.save(part);																									   
				part = (WTPart) PersistenceHelper.manager.refresh(part);
			}
		}
		return part;
	}


	//remove owner link
	public static EPMDocument removeOwnerLink(EPMDocument epm) throws Exception 
	{
		if(epm!=null)
		{
			QueryResult qr = PersistenceHelper.manager.navigate((Persistable)epm, EPMBuildRule.BUILD_TARGET_ROLE,EPMBuildRule.class, false); // navigate links from EPM doc to parts

			while(qr.hasMoreElements())																											
			{
				EPMBuildRule link = (EPMBuildRule) qr.nextElement();																		
				if(link.getBuildType() == 7)																								// check if link is owner link
				{
					PersistenceServerHelper.manager.remove(link);																			// remove link
					//LOGGER.debug("Owner Link removed");
				}

			}
		}

		return epm; 
	}
	
	public static EPMDocument removeCADLinks(EPMDocument epm) throws Exception 
	{
		if(epm!=null)
		{
			QueryResult qr = PersistenceHelper.manager.navigate((Persistable)epm, EPMBuildRule.BUILD_TARGET_ROLE,EPMBuildRule.class, false); // navigate links from EPM doc to parts
			
			while(qr.hasMoreElements())																											
			{
				EPMBuildRule link = (EPMBuildRule) qr.nextElement();																		
				if(link.getBuildType() == 7)																								// check if link is owner link
				{
					PersistenceServerHelper.manager.remove(link);																			// remove link
				}

			}
		}
		return epm;
		
	}
	
	

	//Set state
	public static LifeCycleManaged setLCState(LifeCycleManaged part, String state) throws WTInvalidParameterException, LifeCycleException, WTException
	{
		part = LifeCycleHelper.service.setLifeCycleState(part, State.toState(state));
		return part;
	}

	//get EPM associated to Part
	public static EPMDocument getEPMAssocToPart(WTPart part, int linkType) throws Exception
	{
		QueryResult qr = PersistenceHelper.manager.navigate((Persistable)part, EPMBuildRule.BUILD_SOURCE_ROLE,EPMBuildRule.class, false); 
		EPMDocument epm = null;
		while(qr.hasMoreElements())																											
		{
			EPMBuildRule link = (EPMBuildRule) qr.nextElement();																		
			if(link.getBuildType() == linkType)																								// check if link is owner link
			{
				epm = (EPMDocument) link.getRoleAObject();
			}
		}
		
		
		return epm;


	}
	
		
	//changes part type 
	public static WTPart changePartType(WTPart partObj, String fullyQualifiedType) throws WTException
	{
		TypeHelper.setType((Persistable)partObj, TypeHelper.getTypeIdentifier(fullyQualifiedType));		// set the type of SAP Part to Custom Part
		PersistenceServerHelper.update(partObj);
		partObj = (WTPart)PersistenceHelper.manager.refresh(partObj);
		return partObj;
	}
	
	public static WTPart changeLifecycle(WTPart sapPartObj1, String lifeCycleName, String state) throws WTInvalidParameterException, LifeCycleException, WTException 
	{
		WTContainerRef cr=((WTContained)sapPartObj1).getContainerReference();														// get SAP part container reference
		LifeCycleTemplateReference lcTemplate = LifeCycleHelper.service.getLifeCycleTemplateReference(lifeCycleName, cr);			// get GEISCustomPart lifecycle template reference 
		WTPart partObj = null;

		if(state.equalsIgnoreCase("INWORK"))																						
		{
			WTList list = new WTArrayList();				
			list.add(sapPartObj1);
			WTList partObj1= LifeCycleHelper.service.reassign( list, lcTemplate, cr, State.toState("INWORK"));						// reassign LC and state
			partObj = (WTPart) ((ObjectReference)partObj1.get(0)).getObject();
		}

		if(state.equalsIgnoreCase("SUPERSEDED"))
		{
			WTList list = new WTArrayList();				
			list.add(sapPartObj1);
			WTList partObj1= LifeCycleHelper.service.reassign( list, lcTemplate, cr, State.toState("SUPERSEDED"));					// reassign LC and state
			partObj = (WTPart) ((ObjectReference)partObj1.get(0)).getObject();
		}

		if(state.equalsIgnoreCase("RELEASED"))
		{
			WTList list = new WTArrayList();				
			list.add(sapPartObj1);
			WTList partObj1= LifeCycleHelper.service.reassign( list, lcTemplate, cr, State.toState("RELEASED"));					// reassign LC and state
			partObj = (WTPart) ((ObjectReference)partObj1.get(0)).getObject();
		}

		if(state.equalsIgnoreCase("OBSOLETE"))
		{
			WTList list = new WTArrayList();				
			list.add(sapPartObj1);
			WTList partObj1= LifeCycleHelper.service.reassign( list, lcTemplate, cr, State.toState("OBSOLETE"));					// reassign LC and state
			partObj = (WTPart) ((ObjectReference)partObj1.get(0)).getObject();
		}

		if(state.equalsIgnoreCase("PRODUCTION"))
		{
			WTList list = new WTArrayList();				
			list.add(sapPartObj1);
			WTList partObj1= LifeCycleHelper.service.reassign( list, lcTemplate, cr, State.toState("PRODUCTION"));					// reassign LC and state
			partObj = (WTPart) ((ObjectReference)partObj1.get(0)).getObject();
		}

		return partObj;
	}
	
	public static WTPart deleteBOMLinks(WTPart partObj) throws WTException 
	{
		QueryResult qr = PersistenceHelper.manager.navigate(partObj, WTPartUsageLink.USES_ROLE, WTPartUsageLink.class,false);      // navigate the part usage links  

		WTSet partUsageLinks = new WTHashSet();
		partUsageLinks.addAll(qr);																									

		if (partUsageLinks.size() != 0) 
		{
			PersistenceServerHelper.manager.remove(partUsageLinks);																	 // remove the links
		}
		return partObj;

	}

}

//fetch all bom structure

if((latPart.isEndItem())) {
				System.out.println("This current part is end item");
				QueryResult qr = PersistenceHelper.manager.navigate(latPart, WTPartUsageLink.USES_ROLE, WTPartUsageLink.class,false); // navigate describe links of MWP part
				System.out.println("Query Execute");
				System.out.println("Result size = "+qr.size());
				while(qr.hasMoreElements())																											
				{
					WTPartUsageLink link = (WTPartUsageLink) qr.nextElement();	
					WTPartMaster linkPartb = link.getUses();//(WTPart)VersionControlHelper.service.allVersionsOf((WTPartMaster) link.getUses()).nextElement();	// get the document object
					System.out.println(linkPartb.getName());
				}

			}else
				System.out.println("This current part is not end item");
		
		}
/*CREATE CUSTOM ATTRIBUTE

		Locale locale = SessionHelper.getLocale(); PersistableAdapter obj = new PersistableAdapter("com.acme.AcmePart",locale,new CreateOperationIdentifier());
		obj.load("name","number");
		obj.set("name","my name");
		obj.set("number","12345");
		obj.persist();

		RETRIEVE CUSTOM ATTRIBUTE
		PersistableAdapter obj = new PersistableAdapter(my_persistable,null,locale,null);
		obj.load("name","number");
		Object nameValue = obj.get("name");
		Object numberValue = obj.get("number");

		UPDATE CUSTOM ATTRIBUTE
		PersistableAdapter obj = new PersistableAdapter(my_persistable,null,locale,new UpdateOperationIdentifier());
		obj.load("attributeA","attribtueB");
		obj.set("attributeA",Boolean.TRUE);
		obj.set("attribtueB","PURPLE");
		obj.apply();
*/

