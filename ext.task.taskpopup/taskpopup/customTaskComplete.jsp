<%@ taglib uri="http://www.ptc.com/windchill/taglib/components" prefix="jca"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="tags"%>

<%@taglib uri="http://www.ptc.com/windchill/taglib/workItem"  prefix="workItem"%>

<%@ include file="/netmarkets/jsp/util/begin.jspf"%>

<workItem:MyWorkItem/>
<c:if test="${myWorkItem != null}">
<SCRIPT LANGUAGE="JavaScript">
alert("You have clicked on Ok Button soumyafrom first.");
</SCRIPT>
<%-- There are two steps to render a custom activity variable on the task page.

1) Place the name of the activity variable that you want to display on the 
page in the comma delimited custom_variables list.

Height and/or width can be specified using CSS style syntax.  
For example "variable_name{height:1;width:2}". 
Specifying "all_activity_variables" will display all visible activity 
variables not named "special_instructions", or "instructions".  

2) To specify where to render the particular variable on the page you must add
the following line.

<tags:taskPanelValue propertyModel="${propertyModel}" attrs="variable_name"/>

Note: by default all_activty_variables are rendered.  If you add a custom 
activity variable and have all_activity_variables the variable will show up twice.

--%>



<tags:workItemInfo custom_variables="all_activity_variables"/>

<BR>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tr>
    <td rowspan="3" width="12"><IMG SRC="netmarkets/images/sp.gif" height="1" width="12"></td>
    <td rowspan="3" class="tableborderbg" width="1"><IMG SRC="netmarkets/images/sp.gif" height="1" width="1"></td>
    <td class="tableborderbg"><IMG SRC="netmarkets/images/sp.gif" height="1" width="1"></td>
    <td rowspan="3" class="tableborderbg" width="1"><IMG SRC="netmarkets/images/sp.gif" height="1" width="1"></td>
    <td rowspan="3" width="12"><IMG SRC="netmarkets/images/sp.gif" height="1" width="12"></td>
  </tr>
  <tr class="detailsboxbg">
    <td>

      <table cellpadding="0" cellspacing="0" border="0" align="right" width=100%>
        <tr>
          <td> &nbsp; </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr class="detailsboxbg">
    <td>
<table border="0" cellpadding="0" cellspacing="0">
  <tr>
  	 <td><tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_detailaction"/> </td>
	 <td width="12"><IMG SRC="netmarkets/images/sp.gif" height="1" width="12"></td>
	 <td><font class="projectnamefont"><IMG SRC="netmarkets/images/open_work.gif">
		<tags:taskPanelValue propertyModel="${propertyModel}" attrs="workitem_activityname"/>
	</td>
  </tr>
</table>
<div align="left">

<table border="0" cellpadding="1" width=100%>
        <!-- Task Information Section -->
	<span class="x-reset">
	<tr>
		<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_instructions"/>
	</tr>
	</span>
	<tr>

	<tr>
		<td align="right" valign="top" nowrap><FONT class=tabledatafont>
			<tags:taskPanelLabel propertyModel="${propertyModel}" attrs="workitem_processname"/></FONT>
		</td>
		<td valign="top"><FONT class=tabledatafont>
                        <tags:taskPanelValue propertyModel="${propertyModel}" attrs="workitem_lightweightprocessmonitor"/>
                        <tags:taskPanelValue propertyModel="${propertyModel}" attrs="workitem_processname"/> 
		</td>
	</tr>

	<tr>
		<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_processinitiator"/>
	</tr>

	<tr>
		<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_assignee"/>
	</tr>
	<tr>
		<td align="right" valign="top" nowrap><FONT class=tabledatafont>
			<tags:taskPanelLabel propertyModel="${propertyModel}" attrs="workitem_role"/></FONT>
		</td>
		<td valign="top"><FONT class=tabledatafont>
			<tags:taskPanelValue propertyModel="${propertyModel}" attrs="workitem_role"/>
		</td>
	</tr>
	<tr>
		<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_priority"/>
	</tr>
	<tr>
		<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_deadline"/>
	</tr>
	<tr>
		<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_pbolink"/>
	</tr>

	<tr>
		<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_state"/>
	</tr>
   
        <tags:taskPanelValue propertyModel="${propertyModel}" attrs="all_activity_variables"/>   
   
<tr>	

	<td valign="middle" colspan="3">
		<FONT class=wizardbuttonfont>
		<tags:adhocAct/></FONT></td>
</tr>


	<tr><td valign="middle" colspan="3"><hr size="1" width="100%"></td></tr>

	<tags:workItemActions />

	<tr>
		<td valign="middle" colspan="3"><hr size="1" width="100%"></td>
	</tr>
        <!-- task completion section -->

	<tr>
		<tags:taskPanel propertyModel="${propertyModel}" attrs="workitem_esignature"/>
	</tr>
	<tr>
		<td></td><td></td><td valign="middle" align="left">
			<FONT class=wizardbuttonfont><jsp:include page="/netmarkets/jsp/customtemplates/completeButton.jsp"/>
							  			 </FONT>
		</td>
  	</tr>
  	
	</table>

	</div>
	</td>
  </tr>
  <tr>
	<td></td><td></td><td class="tableborderbg"><IMG SRC="netmarkets/images/sp.gif" height="1" width="1"></td>
  </tr>
</table>


<!-- PBO Info -->

<table border="0" cellpadding="3" width=100%>
	<tr>
            <td colspan="3">&nbsp;
                <!-- show the entire routing history & reassignment history tables -->
                <tags:routingStatus dispProcess="ALL"/>
                <!-- show the reassignment history within a table -->
                <!-- tags:reassignHistory showRH="table"/ -->
				
                <!-- displayType options are "table" or "link".  This tag only works when PBO implements interface SubjectOfNotebook -->
                <workItem:notebook displayType="table"/>
                
                <!-- displayType options are "table" or "link".  This tag only works when PBO implements interface SubjectOfForum -->
                <workItem:discussions displayType="table"/> 
                
	    </td>
	</tr>	
</table>

<BR>
</c:if>
<%@ include file="/netmarkets/jsp/util/end.jspf"%>

