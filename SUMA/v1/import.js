function action_createSupplier (data, params) {
	
	var ActionResult = Java.type("com.ptc.odata.core.entity.processor.ActionResult");
	var System = Java.type('java.lang.System');
    var Manufacturer = Java.type('com.ptc.windchill.suma.supplier.Manufacturer');
    var PersistableAdapter = Java.type('com.ptc.core.lwc.server.PersistableAdapter');
    var UpdateOperationIdentifier = Java.type('com.ptc.core.meta.common.UpdateOperationIdentifier');
    var ObjectIdentifier = Java.type('wt.fc.ObjectIdentifier');
    var PersistenceHelper = Java.type('wt.fc.PersistenceHelper');
    var WTOrganization = Java.type('wt.org.WTOrganization');
    var QuerySpec = Java.type('wt.query.QuerySpec');
    var SearchCondition = Java.type('wt.query.SearchCondition');
    var QueryResult = Java.type('wt.fc.QueryResult');
    var WTContainer = Java.type('wt.inf.container.WTContainer');
    var OrganizationServicesHelper = Java.type('wt.org.OrganizationServicesHelper');
    var URL = Java.type('java.net.URL');
	
	System.out.println("Inside soumyAMethod");
	var orgName = params.get('orgName').getValue();
	var supplierID = params.get('supplierID').getValue();
	System.out.println("orgName--> "+orgName+"supplierID--> "+supplierID);
	
	var org=WTOrganization.newWTOrganization(orgName);
	org.setClassification("soumya");
	org.setConferencingURL(new URL("http://googleConferencingURL.com"));
	org.setDescription("Soumya created");
	org.setWebSite(new URL("http://googlewebsite.com"));
	org = OrganizationServicesHelper.manager.createPrincipal(org);
		
	PersistenceHelper.manager.refresh(org);
	
	var oid = ObjectIdentifier.newObjectIdentifier("wt.inf.container.OrgContainer:71768");
	var container = PersistenceHelper.manager.refresh(oid);
	System.out.println("container--> "+container+"Organization name --> "+org);
	
	var manf= Manufacturer.newManufacturer(org, container);
	
	var obj = new PersistableAdapter(manf, null, null, new UpdateOperationIdentifier());
	System.out.println("fetch SupplierID Attribute");
	obj.load("SupplierID");
	obj.set("SupplierID",supplierID);
	System.out.println("Update SupplierID Attribute");
	obj.apply();
	
	PersistenceHelper.manager.store(manf); 
	
    updatedmf = PersistenceHelper.manager.refresh(manf);
	var updatedmf = data.getProcessor().toEntity(updatedmf, data);
	var result = new ActionResult();
	result.setReturnedObject(updatedmf);
	
	return result;
}


