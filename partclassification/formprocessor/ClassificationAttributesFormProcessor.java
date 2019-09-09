package ext.partclassification.formprocessor;

/**
 * @formatter:off
 * Last Changed    ::                      $Date: 2017-08-02 10:17:00 +0530 (Wed, 02 Aug 2017) $:
 * Last Changed By ::                      $Author: $:
 * Last Changed Rev::                      $Rev: 235 $:
 * Latest Head URL ::                      $HeadURL: http://mc0wbaac:90/svn/GPDM/trunk/eclipse_config/Eclipse_codetemplates.xml $:
 * @formatter:on
**/

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.AttributeTypeIdentifier;
import com.ptc.core.meta.common.DisplayOperationIdentifier;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.csm.common.CsmConstants;
import com.ptc.windchill.enterprise.part.forms.CreatePartAndCADDocFormProcessor;

import wt.facade.classification.ClassificationFacade;
import wt.part.WTPart;
import wt.util.WTException;


/**
 * @author 40002294
 *
 */

public class ClassificationAttributesFormProcessor extends CreatePartAndCADDocFormProcessor {


	public FormResult postProcess(NmCommandBean paramNmCommandBeans, List<ObjectBean> paramList) throws WTException {
		
		FormResult fR = super.postProcess(paramNmCommandBeans, paramList);
		System.out.println("POST PROCESS");
        WTPart part = null;
        for (ObjectBean bean : paramList) {
            if (bean.getObject() instanceof WTPart) {
                part = (WTPart) bean.getObject();
                PersistableAdapter pa = new PersistableAdapter(part, null, Locale.US,
                        new DisplayOperationIdentifier());
                pa.load("CLASSIFICATION");
                System.out.println("CLASSIFICATION VALUE : %s"+pa.get("CLASSIFICATION"));

                List<String> attributes = getClassificationAttributeNames("" + pa.get("CLASSIFICATION"));
                System.out.println("attributes: %s"+attributes);

                PersistableAdapter persistableAdapter = new PersistableAdapter(part, null, Locale.getDefault(),
                        new DisplayOperationIdentifier());

                persistableAdapter.load(attributes);
                for (int i = 0; i < attributes.size(); i++) {
                    String attributeName = attributes.get(i);
                    Object attributeValue = persistableAdapter.get(attributeName);
                    System.out.println("Attribute Name : " + attributeName + "::Attribute Value : " + attributeValue
                            + "::Attribute Value Class : " + attributeValue.getClass().getName());
                
                }
            }
        }

        return fR;
    }

    private List<String> getClassificationAttributeNames(String bindingAttributeValue) throws WTException {
    	 System.out.println("getClassificationAttributeNames"+"bindingAttributeName"+ bindingAttributeValue);
        //String bindingAttributeValue = getBindingAttributeValue(bindingAttributeName);
        ClassificationFacade facadeInstance = ClassificationFacade.getInstance();
        Set<AttributeTypeIdentifier> atis = facadeInstance.getClassificationAttributes(CsmConstants.NAMESPACE,
                bindingAttributeValue);
        List<String> result = Collections.emptyList();
        if (!atis.isEmpty()) {
            result = new ArrayList<>(atis.size());
            for (AttributeTypeIdentifier ati : atis) {
                String classificationAttributeName = ati.getAttributeName();
                result.add(classificationAttributeName);
            }
        }
        System.out.println("getClassificationAttributeNames"+ result);
        return result;
    }

} 
 
