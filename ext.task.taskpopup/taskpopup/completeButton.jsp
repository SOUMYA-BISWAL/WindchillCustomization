<%@ page import="java.util.HashMap,
                 com.ptc.netmarkets.util.misc.NmAction,
                 com.ptc.netmarkets.util.misc.NmActionServiceHelper,
                 com.ptc.netmarkets.work.NmWorkItem ,
				 com.ptc.netmarkets.work.NmWorkItemCommands,
				 com.ptc.netmarkets.model.NmNamedObject,
				 wt.workflow.worklist.worklistResource,
				 wt.util.WTMessage,
				 java.util.Locale,
				 java.util.ResourceBundle,
                 com.ptc.netmarkets.workflow.util.SSOCheckUtility,
				 com.ptc.netmarkets.util.misc.NmHTMLActionModel,
				 com.ptc.netmarkets.util.misc.NmContext,
                 wt.session.SessionHelper,
                 wt.util.EncodingConverter,
				 java.lang.System,
				 wt.workflow.work.WorkItem,
				 com.ptc.windchill.suma.npi.WTPartRequest,
				 wt.fc.collections.WTCollection,
				 com.ptc.windchill.suma.npi.NPIHelper,
				 ext.task.taskpopup.TaskCompletePopup"
				 
%>
<%@ page errorPage="/netmarkets/jsp/util/error.jsp" %>
<jsp:useBean id="modelBean" class="com.ptc.netmarkets.util.beans.NmModelBean" scope="request"/>
<jsp:useBean id="localeBean" class="com.ptc.netmarkets.util.beans.NmLocaleBean" scope="request"/>
<jsp:useBean id="nmcontext" class="com.ptc.netmarkets.util.beans.NmContextBean" scope="request"/>
<jsp:useBean id="urlFactoryBean" class="com.ptc.netmarkets.util.beans.NmURLFactoryBean"  scope="request"/>
<jsp:useBean id="commandBean" class="com.ptc.netmarkets.util.beans.NmCommandBean" scope="request"/>
<jsp:useBean id="actionBean" class="com.ptc.netmarkets.util.beans.NmActionBean" scope="request"/>
<jsp:useBean id="objectBean" class="com.ptc.netmarkets.util.beans.NmObjectBean" scope="request"/>
<jsp:useBean id="checkBoxBean" class="com.ptc.netmarkets.util.beans.NmCheckBoxBean"  scope="request"/>
<jsp:useBean id="textBoxBean"  class="com.ptc.netmarkets.util.beans.NmTextBoxBean"  scope="request"/>
<jsp:useBean id="radioButtonBean" class="com.ptc.netmarkets.util.beans.NmRadioButtonBean"  scope="request"/>
<jsp:useBean id="textAreaBean" class="com.ptc.netmarkets.util.beans.NmTextAreaBean"  scope="request"/>
<jsp:useBean id="comboBoxBean" class="com.ptc.netmarkets.util.beans.NmComboBoxBean"  scope="request"/>
<jsp:useBean id="dateBean"    class="com.ptc.netmarkets.util.beans.NmDateBean"    scope="request"/>
<jsp:useBean id="stringBean" class="com.ptc.netmarkets.util.beans.NmStringBean"  scope="request"/>
<jsp:useBean id="linkBean"    class="com.ptc.netmarkets.util.beans.NmLinkBean"    scope="request"/>
<jsp:useBean id="sessionBean" class="com.ptc.netmarkets.util.beans.NmSessionBean" scope="request"/>	

<%!
    private static final String RESOURCE = "wt.workflow.worklist.worklistResource";
	private static final String WORK_RESOURCE = "com.ptc.netmarkets.work.workResource";
	Boolean isShow =Boolean.TRUE;
%>

<%
     ResourceBundle WorklistResourceRb = ResourceBundle.getBundle(RESOURCE, localeBean.getLocale());
     boolean isSSoEnabled=SSOCheckUtility.isSSOEnable();
%>


<%
		//Custom call code for conform box
		com.ptc.netmarkets.util.beans.NmCommandBean nmCommandBean = new com.ptc.netmarkets.util.beans.NmCommandBean();
		nmCommandBean.setRequest(request);
		nmCommandBean.setSessionBean(sessionBean);
		NmWorkItem nmWorkItem = (NmWorkItem)NmWorkItemCommands.view(nmCommandBean);
		if("Master Data Validation".equals(nmWorkItem.getName())){
			WorkItem workItem = (WorkItem) new wt.fc.ReferenceFactory().getReference(nmWorkItem.getOid().toString()).getObject();
			if(workItem.getPrimaryBusinessObject().getObject() instanceof WTPartRequest  ){
				WTPartRequest partRequest  = (WTPartRequest)workItem.getPrimaryBusinessObject().getObject();
				WTCollection relatedParts = (WTCollection) NPIHelper.service.getRelatedParts(partRequest);
				TaskCompletePopup euCommodityCodeHelper = new TaskCompletePopup();
				String confirmMessage = euCommodityCodeHelper.getValidEUcommodityCodes(relatedParts);
				request.setAttribute("confirmMessage", confirmMessage);
			}
		}
%>


<SCRIPT LANGUAGE="JavaScript">

var busySign = new Ext.LoadMask( Ext.getBody(), { msg: bundleHandler.get( "com.ptc.core.ui.navigationRB.LOADING" ) } );
var completeTaskButton;

handleSubmitResult = handleSubmitResult.wrap(
       function(orig, status, nextAction, js, URL, dynamicRefreshInfo) {
          if(status===1 && '<%=isSSoEnabled%>'=='true'){
              document.getElementById('launchReauthPopup').value = true;
          }
		  busySign.hide();
          orig(status, nextAction, js, URL, dynamicRefreshInfo); 
       });

function teamPickerCallback(objects, pickerID, attr, displayFieldId ) {
	var  updateHiddenField2= document.getElementsByName(pickerID)[0];
	var updateDisplayField = document.getElementsByName(displayFieldId)[0];
	
	var oid = objects.pickedObject[0].oid;
	var displayAttr =objects.pickedObject[0][attr];

	updateDisplayField.value=displayAttr;
	updateHiddenField2.value=oid;

}

function listenEnterOnPassword(evt) {
	 if (!evt) evt = window.event; // for IE
	 if (evt.keyCode) code = evt.keyCode
	 if (evt.which) code = evt.which
	
	 if(code != Event.KEY_RETURN) {
	 	return;
	 }
	 clickCompleteButton();
	 Event.stop(evt);
	 return false;
}

function clickCompleteButton() {
	 if(document.getElementsByName("complete")) {
		 var completeButton = document.getElementsByName("complete")[0];
		 completeButton.click();
	 }
} 

function validateAdhocAssignee() {
    var shouldSubmitResponse = false;
	var pathname = window.location.pathname;
    var splitPathname = pathname.split("/");
    var wcHome = window.location.protocol + "//" + window.location.host + "/" + splitPathname[1] + "/";
    var jspName = 'netmarkets/jsp/workitem/validateAdhocAssignee.jsp';
    var fullURL = wcHome + jspName;

    // want to get no. of elements from the tbody rows
    var tbody = document.getElementById('tb__Adhoc_Act_Id');
    var noOfAssignee = tbody.getElementsByTagName('tr').length;
    var selection = '';
	var adhocAssigneeCount = 0;
	var adhocAssigneeNameCount = 0;

	for (var i = 0; i < noOfAssignee; i++)
    {
        var selectedAssignee = document.getElementsByName("___AD_HOC_ASSIGNEE" + i +"___textbox")[0].value;
		var selectedActvityName = document.getElementsByName("___AD_HOC_ACTIVITY_NAME" + i +"___textbox")[0].value;

		if(selectedActvityName.length > 0){
				adhocAssigneeNameCount++;
		}

        if (selectedAssignee.length > 0) {
        	adhocAssigneeCount++;
            selection += selectedAssignee;
			
			if(selection.length > 0 && i < noOfAssignee)
				selection += ',';
        }
    }
	//alert('Name :: '+adhocAssigneeNameCount+'----Assignee :: '+adhocAssigneeCount)
    if(adhocAssigneeNameCount == 0 || adhocAssigneeCount ==0){
		JCAAlert('wt.workflow.worklist.worklistResource.ADHOC_VALIDATION_ERROR1');
		return shouldSubmitResponse;
	}
	if(adhocAssigneeNameCount != adhocAssigneeCount){
		JCAAlert('wt.workflow.worklist.worklistResource.ADHOC_VALIDATION_ERROR2');
		return shouldSubmitResponse;
	}
	if(adhocAssigneeCount > 0)
    {
    	selection = selection.substring(0, selection.length-1);
    }	

	var loader = new ajax.ContentLoader(fullURL, null, null, 'POST', '&adhocAssignees='+selection+'&oid='+getParamFromQueryString('oid'));
    var resText = null;
	if (loader.req.readyState == 4 && loader.req.status == 200) {
        resText = loader.req.responseText;
		
		if(resText == null || resText.length <= 0){
			shouldSubmitResponse = true;
		}
		else {
			alert(resText); 
		}
    }
    return shouldSubmitResponse;
}

function getParamFromQueryString(paramToGet) {
	var hostUrl = window.location.search.substring(1);
	var paramValue = new Array();
	var paramValuePairs = hostUrl.split("&");

	for (i=0;i<paramValuePairs.length;i++) {
		paramValue = paramValuePairs[i].split("=");
		if (paramValue[0] == paramToGet) {
			var valueToReturn = paramValue[1];
			valueToReturn = valueToReturn.replace(/%3A/g, ':');
			return valueToReturn;
		}
	}
}

function completeTaskEnableTextBox(){
	
	//Custom conform box enable
	var confirmMessage  = "<%= request.getAttribute("confirmMessage") %>";
	if (confirmMessage != 'null'){
		var confrimResponse = confirm(confirmMessage);
		if(confrimResponse == true){
			var result=EnableTextBox();
		}
	}
	else{
		var result=EnableTextBox();
	}
    
    if(document.getElementById('launchReauthPopup').value=='true'){
        window.open('reauthsecure/SSOReAuthentication.jsp?buttonId='+completeTaskButton, 'SSO', 'height=500,width=500');
        return false;
    }
    
    if(result){
        busySign.show();
    }
    
    return result;
}

function EnableTextBox(){
	
    var inputVar = $$('input');
    for(var i=0; i<inputVar.length; i++){
            if (inputVar[i].disabled && inputVar[i].type == 'text') {
                inputVar[i].disabled = false;
                inputVar[i].readonly = false;
            }
    }
    
    return PTC.wizard.checkRequired(false,false,getMainForm());
}

function clearUserPickerTextBox(pickerId)
{
    var condition = "input[id*='" + pickerId + "']";
  	var ele=Ext.DomQuery.select(condition);
	
	Ext.DomQuery.select(condition).each(function(ele){
    ele.value="";	
 });

}

function goBackToPreviousPage(){
    
  if (Ext.History.getToken()) {
       Ext.History.back();
   } else {
         //if there is no history, just go to the home page
       PTC.navigation.goHome();
   }
}

</SCRIPT>
	           
<%
  nmcontext.setRequest(request);
  nmcontext.setResponse(response);
  nmcontext.adjustContext(request);
  com.ptc.netmarkets.util.beans.NmCommandBean cb = new com.ptc.netmarkets.util.beans.NmCommandBean();
  cb.setCompContext(nmcontext.getContext().toString());
  cb.setRequest(request);
  cb.setSessionBean(sessionBean);
  
  
  NmWorkItem myNmWorkItem = (NmWorkItem) NmWorkItemCommands.view(cb);
  
  boolean signFlag = myNmWorkItem.isSigningRequired() && isSSoEnabled;


	String curUser = SessionHelper.getPrincipal().getName();					
	Boolean disAllowFlag = Boolean.FALSE;
	Boolean isPM =Boolean.FALSE;
	HashMap additionInfo = myNmWorkItem.getAdditionalInfo();
	if(additionInfo!=null&&additionInfo.containsKey("DisallowFlag")&&additionInfo.containsKey("ProjectMangerFlag"))
	{
		disAllowFlag = (Boolean)additionInfo.get("DisallowFlag");
		isPM = (Boolean)additionInfo.get("ProjectMangerFlag");
		if(disAllowFlag.equals(Boolean.TRUE))
		{  
			String workItemOwnerName = null;
			if(additionInfo.containsKey("CurrentOwner"))
				workItemOwnerName = (String)additionInfo.get("CurrentOwner");
			if(workItemOwnerName==null || workItemOwnerName.equals(curUser))
				disAllowFlag = Boolean.FALSE;
		
			if(isPM.equals(Boolean.TRUE))
			{
				if(disAllowFlag.equals(Boolean.FALSE))
					isShow=Boolean.TRUE;
				else
					isShow=Boolean.FALSE;
			}
			else
			{
				if(disAllowFlag.equals(Boolean.FALSE))
					isShow=Boolean.TRUE;
			}
		}

	}

  %>                

  

<div id="completButtonId">                  
<table class="pp" cellspacing="1" cellpadding="1" height="5">
<tr  class="basefont" align="right" height="5">
<td align="center" height="5">
<%
Locale locale = localeBean.getLocale();
response.setCharacterEncoding("UTF-8");
	NmHTMLActionModel am = null;
	 String show= request.getParameter("showAdhocComponent");

if(!myNmWorkItem.isAdhocInProgress())
{
	
if (myNmWorkItem.isProjectWorkItem() && !myNmWorkItem.isAdhocActivity()) {
		am = NmActionServiceHelper.service.getActionModel("update workitem",myNmWorkItem);
		NmAction updateTask = (NmAction)am.getActions().get(0);
		updateTask.setButton(true);
		updateTask.setIcon(null);
		updateTask.setEnabled((!myNmWorkItem.isCompleted() && !myNmWorkItem.isSuspended() && myNmWorkItem.isMine()));
		//boolean adminTrackCostOverride=;
		//if (adminTrackCostOverride) updateTask.setEnabled(true);
		actionBean.setAction(updateTask);
		NmContext context = nmcontext.getContext();
		context.pushElement(myNmWorkItem);
		NmAction.actionjsp( actionBean,  linkBean,  objectBean,  localeBean,  urlFactoryBean,nmcontext,  sessionBean,  out,  request,  response);
		context.popElement();
		actionBean.setAction(null);
	}else {
		
		if(isShow.equals(Boolean.TRUE))
		{
		am = NmActionServiceHelper.service.getActionModel("save workitem",myNmWorkItem);
		NmAction saveTask = (NmAction)am.getActions().get(0);
		saveTask.setButton(true);
		saveTask.setEnabled((!myNmWorkItem.isCompleted() && !myNmWorkItem.isSuspended() && myNmWorkItem.isMine()));
		actionBean.setAction(saveTask);
		NmContext context2 = nmcontext.getContext();
		context2.pushElement(myNmWorkItem);
		NmAction.actionjsp( actionBean,  linkBean,  objectBean,  localeBean,  urlFactoryBean,nmcontext,  sessionBean,  out,  request,  response);
		context2.popElement();
		actionBean.setAction(null);
		
	}
}
}
%>
</td>
<td align="center" height="5">
<%

	if(!myNmWorkItem.isAdhocActivity() || !(show !=null && show.equals("table")) )
	{
		if(myNmWorkItem.isAdhocActivity() && myNmWorkItem.isAdhocInProgress() )
		{
		
  				    out.println (new WTMessage (RESOURCE, worklistResource.AD_HOC_IN_PROGRESS, null).getLocalizedMessage (locale));
					out.println("<br></br>");
		}
		else 
		{
			if(isShow.equals(Boolean.TRUE))
			{
			am = NmActionServiceHelper.service.getActionModel("complete workitem",myNmWorkItem);
			 NmAction completeTask = (NmAction)am.getActions().get(0);
			completeTask.setButton(true);
			//completeTask.setEnabled(true);
			
			completeTask.setEnabled((!myNmWorkItem.isCompleted() && !myNmWorkItem.isSuspended() && myNmWorkItem.isMine()));
			actionBean.setAction(completeTask);
			NmContext context = nmcontext.getContext();
			context.pushElement(myNmWorkItem);
			NmAction.actionjsp( actionBean,  linkBean,  objectBean,  localeBean,  urlFactoryBean,nmcontext,  sessionBean,  out,  request,  response);
			context.popElement();
			actionBean.setAction(null);
			%>
	        <SCRIPT LANGUAGE="JavaScript">
	             completeTaskButton = '<%=completeTask.getIdName()%>';
	            document.getElementById('launchReauthPopup').value = '<%=signFlag%>';
	        </SCRIPT>
	        <%
		}
	}
	}
	else
	{
		
		am = NmActionServiceHelper.service.getActionModel("start Activities",myNmWorkItem);
		NmAction startTask = (NmAction)am.getActions().get(0);
		startTask.setButton(true);
		startTask.setEnabled((!myNmWorkItem.isCompleted() && !myNmWorkItem.isSuspended() && myNmWorkItem.isMine()));
		actionBean.setAction(startTask);
		NmContext context1 = nmcontext.getContext();
		context1.pushElement(myNmWorkItem);
		NmAction.actionjsp( actionBean,  linkBean,  objectBean,  localeBean,  urlFactoryBean,nmcontext,  sessionBean,  out,  request,  response);
		context1.popElement();
		actionBean.setAction(null);
	}
%>
</td>
</table>
</div>
<input type="hidden" id="requiredMessage" name="requiredMessage" value="<%=WTMessage.getLocalizedMessage(com.ptc.core.ui.errorMessagesRB.class.getName(),com.ptc.core.ui.errorMessagesRB.CREATE_WITHOUT_REQUIRED_FIELDS, null, locale)%>">
<input type="hidden" id="launchReauthPopup" name="launchReauthPopup" value="false"> 
