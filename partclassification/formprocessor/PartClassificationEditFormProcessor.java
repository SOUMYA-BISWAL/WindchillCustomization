


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
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import com.ptc.core.components.beans.ObjectBean;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.lwc.server.PersistableAdapter;
import com.ptc.core.meta.common.TypeIdentifierHelper;
import com.ptc.core.meta.common.UpdateOperationIdentifier;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.part.forms.EditPartFormProcessor;

import wt.fc.PersistenceHelper;
import wt.part.WTPart;
import wt.util.WTException;

import ext.generic.constants.GenericUtilConstants;

//import ext.vestas.workflow.newpartrequest.PartRequestWorkflowHelper;

/**
 * @author 40007080
 *
 */
public class PartClassificationEditFormProcessor extends EditPartFormProcessor {

    private static final String classification = "CLASSIFICATION";
    private static WTPart part = null;
    private static String classificationValues = null;
    private PersistableAdapter persiststable = null;
    private String additionalInformationValue = StringUtils.EMPTY;
    private final PartClassificationCreateFormProcessor createFormProcessorInstance = new PartClassificationCreateFormProcessor();

    @Override
    public FormResult postProcess(NmCommandBean paramNmCommandBean, List<ObjectBean> paramList) throws WTException {

        for (ObjectBean bean : paramList) {
            if (bean.getObject() instanceof WTPart) {
                part = (WTPart) bean.getObject();
                if (TypeIdentifierHelper.getType(part).toString().indexOf("net.vestas.Vestas") > 0) {
                    try {
                        classificationValues = createFormProcessorInstance.fetchClassification(part, classification);
                        classificationValues = classificationValues.substring(0,
                                classificationValues.lastIndexOf(GenericUtilConstants.COMMA_DELIM));
                        persiststable = new PersistableAdapter(part, null, Locale.US, new UpdateOperationIdentifier());
                        persiststable.load("LONG_DESCRIPTION", "COMMERCIAL_DESCRIPTION");
                        persiststable.set("LONG_DESCRIPTION", classificationValues + GenericUtilConstants.COMMA_DELIM
                                + GenericUtilConstants.BLANK_SPACE + additionalInformationValue);
                        persiststable.set("COMMERCIAL_DESCRIPTION", classificationValues);
                        persiststable.apply();
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

}