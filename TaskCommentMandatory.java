package ext.workflowcode;

import wt.util.WTException;
import wt.workflow.engine.ProcessData;
import wt.workflow.work.WfAssignedActivity;

/**
 * 
 * @author 40002294
 *
 */

public class TaskCommentMandatory {
	
	/**
	 * Write Below 2 Lines of code in workflow Acrtivity Robot
	 * don't put workflow call code in Try catch block
	 * wt.workflow.work.WfAssignedActivity activity = (wt.workflow.work.WfAssignedActivity) self.getObject();
	 * call this function with "ext.workflowcode.TaskCommentMandatory.commentManadatoryForTask(activity);"
	 * @param WfAssignedActivity
	 * @throws WTException 
	 */
	
	public static void commentManadatoryForTask(WfAssignedActivity activity) throws WTException {

		System.out.println("Method Call");
		ProcessData actData =null;
		String detailedDescription;
		String commentLine =  "Please enter a detailed description in the Comments field.";
	
	
		actData = activity.getContext();
	
	
		detailedDescription = actData.getTaskComments();
		System.out.println("Size of the Comment:=> "+detailedDescription.trim());
	
		if (detailedDescription.trim().length() < 1) {
	
			throw new wt.util.WTException(commentLine);
		}

		System.out.println("Method Execute Successfull");
	}

}
