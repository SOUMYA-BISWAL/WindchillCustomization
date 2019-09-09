


/**
 * @formatter:off
 * Last Changed    ::                      $Date: 2017-08-02 10:17:00 +0530 (Wed, 02 Aug 2017) $:
 * Last Changed By ::                      $Author: $:
 * Last Changed Rev::                      $Rev: 235 $:
 * Latest Head URL ::                      $HeadURL: http://mc0wbaac:90/svn/GPDM/trunk/eclipse_config/Eclipse_codetemplates.xml $:
 * @formatter:on
**/
package ext.partclassification.formprocessor;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.lwc.common.AttributeTemplateFlavor;
import com.ptc.core.lwc.common.view.AttributeDefinitionReadView;
import com.ptc.core.lwc.common.view.ConstraintDefinitionReadView;
import com.ptc.core.lwc.common.view.ConstraintDefinitionReadView.RuleDataObject;
import com.ptc.core.lwc.common.view.EnumerationDefinitionReadView;
import com.ptc.core.lwc.common.view.EnumerationEntryReadView;
import com.ptc.core.lwc.common.view.EnumerationMembershipReadView;
import com.ptc.core.lwc.common.view.TypeDefinitionReadView;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.lwc.server.TypeDefinitionServiceHelper;
import com.ptc.core.lwc.server.cache.EnumerationDefinitionManager;
import com.ptc.core.meta.common.DisplayOperationIdentifier;
import com.ptc.core.meta.common.TypeIdentifierHelper;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.netmarkets.search.utils.SearchNumericUtils;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.csm.client.utils.CSMUtils;
import com.ptc.windchill.csm.common.CsmConstants;
import com.ptc.windchill.enterprise.part.forms.CreatePartAndCADDocFormProcessor;

import wt.fc.Persistable;
import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.units.FloatingPointWithUnits;
import wt.util.WTException;

import ext.generic.constants.GenericUtilConstants;

/**
 * @author 40007080
 *
 */
public class PartClassificationCreateFormProcessor extends CreatePartAndCADDocFormProcessor {

    private boolean globalEnumFlag = false;
    private static final String classification = "CLASSIFICATION";
    private static final String measurmentUnitProperty = "measurementSystemForAutoNaming";
    private static WTPart part = null;
    private static String classificationValues = null;
    private PersistableAdapter persistable = null;
    private String nodeClassValue = StringUtils.EMPTY;
    private String additionalInformationValue = StringUtils.EMPTY;
    private TypeDefinitionReadView localTypeDefinitionReadView = null;

    @Override
    public FormResult postProcess(NmCommandBean paramNmCommandBean, List<ObjectBean> paramList) throws WTException {

        for (ObjectBean bean : paramList) {
            if (bean.getObject() instanceof WTPart) {
                part = (WTPart) bean.getObject();
                if (TypeIdentifierHelper.getType(part).toString().indexOf("net.vestas.Vestas") > 0) {
                    try {
                        classificationValues = fetchClassification(part, classification);
                        classificationValues = classificationValues.substring(0,
                                classificationValues.lastIndexOf(GenericUtilConstants.COMMA_DELIM));
                        persistable = new PersistableAdapter(part, null, Locale.US, new UpdateOperationIdentifier());
                        persistable.load("LONG_DESCRIPTION", "COMMERCIAL_DESCRIPTION");
                        persistable.set("LONG_DESCRIPTION", classificationValues + GenericUtilConstants.COMMA_DELIM
                                + GenericUtilConstants.BLANK_SPACE + additionalInformationValue);
                        persistable.set("COMMERCIAL_DESCRIPTION", classificationValues);
                        persistable.apply();
                        PersistenceHelper.manager.save(part);
                    } catch (RemoteException | ParseException e) {
                    	System.out.println(e.getMessage());
                    }
                } else {
                    return super.postProcess(paramNmCommandBean, paramList);
                }
            }
        }
        return super.postProcess(paramNmCommandBean, paramList);
    }

    public String fetchClassification(Persistable persistObj, String bindingAttribute)
            throws WTException, RemoteException, ParseException {

        String valueString = StringUtils.EMPTY;
        PersistableAdapter persistableAdapter = null;
        String nodeValue = null;

        Collection<AttributeDefinitionReadView> allAttributeList = null;
        Iterator<AttributeDefinitionReadView> iterationValuelist = null;

        persistableAdapter = new PersistableAdapter(persistObj, null, Locale.US, new DisplayOperationIdentifier());
        persistableAdapter.load(bindingAttribute);
        nodeValue = persistableAdapter.get(bindingAttribute).toString();
        localTypeDefinitionReadView = TypeDefinitionServiceHelper.service
                .getTypeDefView(AttributeTemplateFlavor.LWCSTRUCT, CsmConstants.NAMESPACE, nodeValue);
        localTypeDefinitionReadView.getDisplayName().substring(11);
        allAttributeList = localTypeDefinitionReadView.getAllAttributes();
        iterationValuelist = allAttributeList.iterator();

        while (iterationValuelist.hasNext()) {
            AttributeDefinitionReadView classificationAttribute = iterationValuelist.next(); //oobj
            Collection<ConstraintDefinitionReadView> constDefReadViewCol = classificationAttribute.getAllConstraints();
            String attributeInternalName = classificationAttribute.getName();
            String attributeDisplayName = classificationAttribute.getDisplayName();
            RuleDataObject ruleDataObject = null;
            globalEnumFlag = false;
            for (ConstraintDefinitionReadView constDefReadView : constDefReadViewCol) {
                ruleDataObject = constDefReadView.getRuleDataObj();
                if (ruleDataObject != null && null != ruleDataObject.getEnumDef()) {
                    globalEnumFlag = true;
                    break;
                }
            }
            if (globalEnumFlag) {
                EnumerationDefinitionReadView enumDefReadView = ruleDataObject.getEnumDef();
                PersistableAdapter pa = new PersistableAdapter(persistObj, null, Locale.getDefault(),
                        new DisplayOperationIdentifier());
                pa.load(attributeInternalName);
                String valueInternalName = (String) pa.get(attributeInternalName);
                String valueDisplayName = getGlobalEnumMap(enumDefReadView.getName(), false, valueInternalName);
                valueString = valueString + attributeDisplayName + GenericUtilConstants.COLON
                        + GenericUtilConstants.BLANK_SPACE + valueDisplayName + GenericUtilConstants.COMMA_DELIM
                        + GenericUtilConstants.BLANK_SPACE;
            } else {
                String dataType = classificationAttribute.getDatatype().getName();
                if (dataType.equals("wt.units.FloatingPointWithUnits")) {
                    String floatValues = fetchClassificationFloatAttributes(persistObj, attributeInternalName);
                    valueString = valueString + attributeDisplayName + GenericUtilConstants.COLON
                            + GenericUtilConstants.BLANK_SPACE + floatValues + GenericUtilConstants.COMMA_DELIM
                            + GenericUtilConstants.BLANK_SPACE;
                }
                if (dataType.equals("java.lang.String")) {
                    if ("CLASS_NODE".equals(attributeInternalName)) {
                        nodeClassValue = fetchClassificationStringAttributes(persistObj, attributeInternalName);
                    } else if ("ADDITIONAL_INFORMATION".equals(attributeInternalName)) {
                        additionalInformationValue = fetchClassificationStringAttributes(persistObj,
                                attributeInternalName);
                    } else {
                        String stringValues = fetchClassificationStringAttributes(persistObj, attributeInternalName);
                        valueString = valueString + attributeDisplayName + GenericUtilConstants.COLON
                                + GenericUtilConstants.BLANK_SPACE + stringValues + GenericUtilConstants.COMMA_DELIM
                                + GenericUtilConstants.BLANK_SPACE;
                    }
                }
            }
        }
        return nodeClassValue + GenericUtilConstants.COMMA_DELIM + GenericUtilConstants.BLANK_SPACE + valueString;
    }

    public String fetchClassificationFloatAttributes(Persistable PersistableObj, String attrName)
            throws WTException, ParseException {
        AttributeDefinitionReadView attributeDefinationReadView = localTypeDefinitionReadView
                .getAttributeByName(attrName);
        PersistableAdapter perstableAdp = new PersistableAdapter(PersistableObj, null, Locale.US,
                new DisplayOperationIdentifier());
        perstableAdp.load(attrName);
        FloatingPointWithUnits floatingValue = (FloatingPointWithUnits) perstableAdp.get(attrName);
        String unitString = localTypeDefinitionReadView.getPropertyValueByName(measurmentUnitProperty)
                .getValueAsString();
        if (unitString == null || StringUtils.EMPTY.equals(unitString)) {
            return floatingValue.toString();
        } else {
            String measurementSystemName = CSMUtils.getMeasurementSystemName(unitString);
            Object object = new SearchNumericUtils().getConvertedValue(floatingValue, attributeDefinationReadView,
                    Locale.getDefault(), measurementSystemName);
            return object.toString();
        }
    }

    public String fetchClassificationStringAttributes(Persistable paramPersistable, String attrName)
            throws WTException {
        PersistableAdapter per = null;
        String stringAttributeValue = null;

        per = new PersistableAdapter(paramPersistable, null, Locale.US, new UpdateOperationIdentifier());
        per.load(attrName);
        stringAttributeValue = (String) per.get(attrName);
        return stringAttributeValue;

    }

    public String getGlobalEnumMap(String internalName, boolean includeNonSelectable, String internalValue)
            throws WTException {
        System.out.println("Entering getGlobalEnumMap with Arguments: \n");
        System.out.println("internalName:" + internalName);
        System.out.println("includeNonSelectable:" + includeNonSelectable);
        Map<String, String> enumerationEntries = new HashMap<>();
        EnumerationDefinitionManager manager = EnumerationDefinitionManager.getEnumerationDefinitionManagerInstance();
        EnumerationDefinitionReadView edv = manager.getEnumerationDefView(internalName);
        Map<String, EnumerationEntryReadView> entryViewMap = edv.getAllEnumerationEntries();
        for (String key : entryViewMap.keySet()) {
            EnumerationMembershipReadView emv = edv.getMembershipByName(key);
            EnumerationEntryReadView member = emv.getMember();
            if (includeNonSelectable == Boolean.TRUE) {
                Object displayName = member.getPropertyValueByName("displayName").getValue();
                enumerationEntries.put(key, displayName.toString());
            } else {
                boolean selectable = Boolean
                        .parseBoolean(member.getPropertyValueByName("selectable").getValue().toString());
                if (selectable) {
                    Object desc = member.getPropertyValueByName("displayName").getValue();
                    enumerationEntries.put(key, desc.toString());
                }
            }
        }
        System.out.println("Exiting getGlobalEnumMap with result:" + enumerationEntries);
        return enumerationEntries.get(internalValue);
    }
}
