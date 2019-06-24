package ext.suma;

import java.net.MalformedURLException;
import java.net.URL;

import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.windchill.suma.supplier.Manufacturer;
import com.ptc.windchill.suma.supplier.Supplier;
import com.ptc.windchill.suma.supplier.SupplierHelper;
import com.sun.xml.ws.transport.tcp.pool.LifeCycle;

import wt.fc.ObjectIdentifier;
import wt.fc.PersistenceHelper;
import wt.fc.QueryResult;
import wt.inf.container.WTContainer;
import wt.lifecycle.LifeCycleHelper;
import wt.lifecycle.LifeCycleManaged;
import wt.lifecycle.LifeCycleState;
import wt.lifecycle.State;
import wt.method.RemoteAccess;
import wt.method.RemoteMethodServer;
import wt.org.OrganizationServicesHelper;
import wt.org.WTOrganization;
import wt.part.WTPart;
import wt.pds.StatementSpec;
import wt.query.QuerySpec;
import wt.query.SearchCondition;
import wt.query.WhereExpression;
import wt.util.WTException;  
import wt.util.WTPropertyVetoException;

/**
 * 
 * @author 40002294(Soumya Ranjan Biswal)
 * Supplier/Manufacture Create, Modify,set_State and delete
 *
 */

public class CreateSupplier implements RemoteAccess {
	
	/**
	 * 
	 * @param args
	 * @throws WTPropertyVetoException
	 * @throws WTException
	 */
	
	public static void main(String[] args) throws WTPropertyVetoException, WTException {
		RemoteMethodServer rms= RemoteMethodServer.getDefault(); 
		rms.setUserName("wcadmin");
		rms.setPassword("wcadmin");
		System.out.println("logged in Windchill");
		 
		Class<?> argTypes[]={String.class};
		Object argValues[]={"soumya"};
		try {
			System.out.println("Call to Create Supplier Method");
			rms.invoke("createSupplier", "ext.suma.CreateSupplier", null, argTypes, argValues);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		
	} 
	/**
	 * @param str
	 * @throws WTException
	 * @throws WTPropertyVetoException
	 * @throws MalformedURLException
	 */
	public static void createSupplier(String str) throws WTException, WTPropertyVetoException, MalformedURLException {
	
		
		/**
		 * 
		 * Variable Declaration
		 * 
		 */
		WTOrganization organization = null;
		ObjectIdentifier objectId = null;
		WTContainer container =null;
		Manufacturer manufacturer = null;
		PersistableAdapter persistableAttribute =null;
		String organizationName= "soumyaTest164";
		String conferencingURL = "http://googleConferencingURL.com";
		String description = "Created By Soumya";
		String classificationName = "soumya164";
		String url = "http://google.com";
		String containerID = "wt.inf.container.OrgContainer:71768";
		String state = "RELEASED";
		
		
		organization= WTOrganization.newWTOrganization(organizationName);
		organization.setClassification(classificationName);
		organization.setConferencingURL(new URL(conferencingURL));
		organization.setDescription(description);
		organization.setWebSite(new URL(url));
		organization = (WTOrganization) OrganizationServicesHelper.manager.createPrincipal(organization);
		PersistenceHelper.manager.refresh(organization);
		
		objectId = ObjectIdentifier.newObjectIdentifier(containerID);
		container = (WTContainer) PersistenceHelper.manager.refresh(objectId);
        System.out.println("Successfull Execute:-> "+organization.getContainer());
        
		/**
		 * 
		 * Below Code For creating Manufacturer
		 * 
		 */
        
        manufacturer = Manufacturer.newManufacturer(organization, container);
        System.out.println("Manufacture Set");
        LifeCycleHelper.service.setLifeCycleState((LifeCycleManaged) manufacturer, State.toState(state));
        System.out.println("State Set");
        persistableAttribute = new PersistableAdapter(manufacturer, null, null, new UpdateOperationIdentifier());
        persistableAttribute.load("SupplierID");
        persistableAttribute.set("SupplierID","soumya");
		System.out.println("Update SupplierID Att");
		persistableAttribute.apply();
		PersistenceHelper.manager.store(manufacturer);
		
		/**
		 * 
		 * Below Code For creating Supplier
		 * 
		 */

		/*
		Supplier supplier = null;
		supplier = Supplier.newSupplier(organization, container);
        persistableAttribute = new PersistableAdapter(supplier, null, null, new UpdateOperationIdentifier());
        persistableAttribute.load("SupplierID");
        persistableAttribute.set("SupplierID","soumya");
		System.out.println("Update SupplierID Att");
		persistableAttribute.apply();
		PersistenceHelper.manager.store(supplier);
		*/
	
		/**
		 * 
		 * Below Code For Fetch Supplier/Manufacture
		 * 
		 */
		
		/*
		String OrganizationName="test2";
		QuerySpec querySpec = null;
		SearchCondition searchCondition = null;
		QueryResult queryResult = null;
		WTOrganization organization =null;
		querySpec = new wt.query.QuerySpec(WTOrganization.class);
		searchCondition = new SearchCondition(WTOrganization.class, "", SearchCondition.EQUAL, OrganizationName, true);
		querySpec.appendWhere((WhereExpression) searchCondition, new int[]{0});
		queryResult = PersistenceHelper.manager.find((StatementSpec)querySpec);
		organization = (WTOrganization)queryResult.nextElement();
		System.out.println("Organization Name:=> "+organization.getName());
		//Supplier supplier = SupplierHelper.service.getManufacturer(organization, container); //Find Supplier From WTOrganization 
		*/
		
		
	}
}
