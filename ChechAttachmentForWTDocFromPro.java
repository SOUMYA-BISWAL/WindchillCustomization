package ext.formProcesser;

import java.util.List;
import java.util.Vector;

import com.ptc.core.components.forms.FormProcessingStatus;
import com.ptc.core.components.forms.FormResult;
import com.ptc.core.components.util.FeedbackMessage;
import com.ptc.netmarkets.util.beans.NmCommandBean;
import com.ptc.windchill.enterprise.doc.forms.CreateDocFormProcessor;

import wt.content.ContentHelper;
import wt.content.ContentHolder;
import wt.doc.WTDocument;
import wt.fc.Persistable;
import wt.util.WTException;

/**
 * 
 * @author 40002294 (Soumya Ranjan Biswal)
 * 
 * */

import com.ptc.core.components.beans.ObjectBean;

public class documentModification extends CreateDocFormProcessor{

	/**
	 * 
	 * @ Mandatory Attachment set while creating WTDocumnet
	 * 
	 * */
	public FormResult postProcess(NmCommandBean paramNmCommandBeans, List<ObjectBean> paramList) throws WTException {
		
		/**
		 * 
		 * @variable Declaration
		 * 
		 * */
		
		FormResult fR = super.postProcess(paramNmCommandBeans, paramList);
		Persistable context=null;
		FormResult result=null;
		FeedbackMessage fdMessage=null;
		ContentHolder content = null;
		Vector<?> vcontent=null;
		String noContentMsg="Need to upload Primary Attachment for this";
		String successMsg="Document Created Successful";
		
		/**
		 * 
		 * @ Execution Start From Here
		 * 
		 * */
		
		try {
			for (ObjectBean thisBean : paramList) {
				context = (Persistable)thisBean.getObject();
				if(context instanceof WTDocument) {
					
					/**
					 * 
					 * @ Fetch Secondary Content From WTDocument
					 * 
					 * */
					
					content = ContentHelper.service.getContents((ContentHolder)context);
					vcontent= ContentHelper.getApplicationData(content); 
					
					/**
					 * 
					 * @ execute this block if no Secondary content
					 * 
					 * */
					
					if(vcontent.size() == 0){
						result = new FormResult(FormProcessingStatus.FAILURE);
						fdMessage= new FeedbackMessage();
						fdMessage.addMessage(noContentMsg+" "+context);
						result.addFeedbackMessage(fdMessage);
						return result;
						
						/**
						 * 
						 * @ execute this block if Secondary content are Attached
						 * 
						 * */
						
					}else if(vcontent.size() > 0){
						result = new FormResult(FormProcessingStatus.SUCCESS);
						fdMessage= new FeedbackMessage();
						fdMessage.addMessage(context+" "+successMsg);
						result.addFeedbackMessage(fdMessage);
						return result;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fR;
	}
}
