function getRelatedEntityCollection(navigationData) {
    var HashMap = Java.type('java.util.HashMap');
    var HashSet = Java.type('java.util.HashSet');
    var ArrayList = Java.type('java.util.ArrayList');
	var sysOut=Java.type('java.lang.System');
    var WTPart = Java.type('wt.part.WTPart');
    var ObjectReference = Java.type('wt.fc.ObjectReference');
    var Collections = Java.type('java.util.Collections');
    var WTArrayList = Java.type('wt.fc.collections.WTArrayList');
    var WTOrganization = Java.type('wt.org.WTOrganization');
    var SupplierHelper = Java.type('com.ptc.windchill.suma.supplier.SupplierHelper');
	
	var map = new HashMap();
    var targetName = navigationData.getTargetSetName();
	var supplierOrganisationList = new ArrayList();//annotationsList
	
	var sourceObjectsList = new WTArrayList(navigationData.getSourceObjects());
	if ("SupplierOrganization".equals(targetName)) { 
		for(var i = 0; i < sourceObjectsList.size(); i++) {
            var sourceObject = sourceObjectsList.getPersistable(i);
			sysOut.out.println("sourceObject => "+sourceObject+" sourceObject.getOrganization() => "+sourceObject.getOrganization());
			//var SupplierOrganization = SupplierHelper.service.getSupplierOrganization(sourceObject);
			var supplierOrganization =  sourceObject.getOrganization();
			//var annotations  = ViewMarkUpHelper.service.getMarkUps(sourceObject);
			//while(annotations.hasMoreElements()){
				supplierOrganisationList.add(supplierOrganization);
			//}
			map.put(sourceObject, supplierOrganisationList);
		}
    }
	return map;
	
}