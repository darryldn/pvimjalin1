/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.web.ws.obj;

import com.wn.econnect.inbound.wsi.ticket.ObjectFactory;
import com.wn.econnect.inbound.wsi.ticket.TicketDto;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import javax.xml.bind.JAXBElement;

/**
 *
 * @author darryl.sulistyan
 */
public class RestTicketDto implements Serializable {

    private String ETA;
    private String assignTime;
    private String assignee;
    private String assigneeGroup;
    private String categoryName;
    private String closeTime;
    private String createTime;
    private String description;
    private String dispatchTime;
    private String expectedFixTime;
    private String expectedResponseTime;
    private String fixTime;
    private String incidentName;
    private String machineNumber;
    private String note;
    private String priority;
    private String refId;
    private String reportedTime;
    private String startWorkingTime;
    private String summary;
    private String suspendBeginTime;
    private String suspendEndTime;
    private String ticketNumber;
    private String ticketState;

    public RestTicketDto() {

    }
    
    public static RestTicketDto convert(Map<String, Object> ticketMap) {
        RestTicketDto rest = new RestTicketDto();
        rest.setTicketNumber((String)ticketMap.get("ticketNumber"));
        rest.setTicketState((String)ticketMap.get("ticketState"));
        rest.setSummary((String)ticketMap.get("summary"));
        rest.setDescription((String)ticketMap.get("description"));
        rest.setMachineNumber((String)ticketMap.get("machineNumber"));
        rest.setCategoryName((String)ticketMap.get("categoryName"));
        rest.setIncidentName((String)ticketMap.get("incidentName"));
        rest.setPriority((String)ticketMap.get("priority"));
        rest.setReportedTime((String)ticketMap.get("reportedTime"));
        rest.setCreateTime((String)ticketMap.get("createTime"));
        rest.setAssigneeGroup((String)ticketMap.get("assigneeGroup"));
        rest.setDispatchTime((String)ticketMap.get("dispatchTime"));
        rest.setAssignee((String)ticketMap.get("assignee"));
        rest.setAssignTime((String)ticketMap.get("assignTime"));
        rest.setExpectedResponseTime((String)ticketMap.get("expectedResponseTime"));
        rest.setStartWorkingTime((String)ticketMap.get("startWorkingTime"));
        rest.setSuspendBeginTime((String)ticketMap.get("suspendBeginTime"));
        rest.setSuspendEndTime((String)ticketMap.get("suspendEndTime"));
        rest.setExpectedFixTime((String)ticketMap.get("expectedFixTime"));
        rest.setFixTime((String)ticketMap.get("fixTime"));
        rest.setCloseTime((String)ticketMap.get("closeTime"));
        rest.setETA((String)ticketMap.get("ETA"));
        rest.setNote((String)ticketMap.get("note"));
        rest.setRefId((String)ticketMap.get("refId"));
        return rest;
    }

    public RestTicketDto(RestTicketDto other) {
        ETA = other.ETA;
        assignTime = other.assignTime;
        assignee = other.assignee;
        assigneeGroup = other.assigneeGroup;
        categoryName = other.categoryName;
        closeTime = other.closeTime;
        createTime = other.createTime;
        description = other.description;
        dispatchTime = other.dispatchTime;
        expectedFixTime = other.expectedFixTime;
        expectedResponseTime = other.expectedResponseTime;
        fixTime = other.fixTime;
        incidentName = other.incidentName;
        machineNumber = other.machineNumber;
        note = other.note;
        priority = other.priority;
        refId = other.refId;
        reportedTime = other.reportedTime;
        startWorkingTime = other.startWorkingTime;
        summary = other.summary;
        suspendBeginTime = other.suspendBeginTime;
        suspendEndTime = other.suspendEndTime;
        ticketNumber = other.ticketNumber;
        ticketState = other.ticketState;
    }

    private String getValue(JAXBElement<String> data, String defVal) {
        if (data != null) {
            return data.getValue();
        } else {
            return defVal;
        }
    }

    public RestTicketDto(TicketDto soap) {
        if (soap == null) {
            return;
        }
        
        ETA = getValue(soap.getETA(), "");
        assignTime = getValue(soap.getAssignTime(), "");
        assignee = getValue(soap.getAssignee(), "");
        assigneeGroup = getValue(soap.getAssigneeGroup(), "");
        categoryName = getValue(soap.getCategoryName(), "");
        closeTime = getValue(soap.getCloseTime(), "");
        createTime = getValue(soap.getCreateTime(), "");
        description = getValue(soap.getDescription(), "");
        dispatchTime = getValue(soap.getDispatchTime(), "");
        expectedFixTime = getValue(soap.getExpectedFixTime(), "");
        expectedResponseTime = getValue(soap.getExpectedResponseTime(), "");
        fixTime = getValue(soap.getFixTime(), "");
        incidentName = getValue(soap.getIncidentName(), "");
        machineNumber = getValue(soap.getMachineNumber(), "");
        note = getValue(soap.getNote(), "");
        priority = getValue(soap.getPriority(), "");
        refId = getValue(soap.getRefId(), "");
        reportedTime = getValue(soap.getReportedTime(), "");
        startWorkingTime = getValue(soap.getStartWorkingTime(), "");
        summary = getValue(soap.getSummary(), "");
        suspendBeginTime = getValue(soap.getSuspendBeginTime(), "");
        suspendEndTime = getValue(soap.getSuspendEndTime(), "");
        ticketNumber = getValue(soap.getTicketNumber(), "");
        ticketState = getValue(soap.getTicketState(), "");

    }
    
    public TicketDto convert() {
        TicketDto dto = new TicketDto();
        ObjectFactory objFactory = new ObjectFactory();
        JAXBElement<String> xETA = objFactory.createTicketDtoETA(this.ETA); dto.setETA(xETA);
        JAXBElement<String> xassignTime = objFactory.createTicketDtoAssignTime(this.assignTime); dto.setAssignTime(xassignTime);
        JAXBElement<String> xassignee = objFactory.createTicketDtoAssignee(this.assignee); dto.setAssignee(xassignee);
        JAXBElement<String> xassigneeGroup = objFactory.createTicketDtoAssigneeGroup(this.assigneeGroup); dto.setAssigneeGroup(xassigneeGroup);
        JAXBElement<String> xcategoryName = objFactory.createTicketDtoCategoryName(this.categoryName); dto.setCategoryName(xcategoryName);
        JAXBElement<String> xcloseTime = objFactory.createTicketDtoCloseTime(this.closeTime); dto.setCloseTime(xcloseTime);
        JAXBElement<String> xcreateTime = objFactory.createTicketDtoCreateTime(this.createTime); dto.setCreateTime(xcreateTime);
        JAXBElement<String> xdescription = objFactory.createTicketDtoDescription(this.description); dto.setDescription(xdescription);
        JAXBElement<String> xdispatchTime = objFactory.createTicketDtoDispatchTime(this.dispatchTime); dto.setDispatchTime(xdispatchTime);
        JAXBElement<String> xexpectedFixTime = objFactory.createTicketDtoExpectedFixTime(this.expectedFixTime); dto.setExpectedFixTime(xexpectedFixTime);
        JAXBElement<String> xexpectedResponseTime = objFactory.createTicketDtoExpectedResponseTime(this.expectedResponseTime); dto.setExpectedResponseTime(xexpectedResponseTime);
        JAXBElement<String> xfixTime = objFactory.createTicketDtoFixTime(this.fixTime); dto.setFixTime(xfixTime);
        JAXBElement<String> xincidentName = objFactory.createTicketDtoIncidentName(this.incidentName); dto.setIncidentName(xincidentName);
        JAXBElement<String> xmachineNumber = objFactory.createTicketDtoMachineNumber(this.machineNumber); dto.setMachineNumber(xmachineNumber);
        JAXBElement<String> xnote = objFactory.createTicketDtoNote(this.note); dto.setNote(xnote);
        JAXBElement<String> xpriority = objFactory.createTicketDtoPriority(this.priority); dto.setPriority(xpriority);
        JAXBElement<String> xrefId = objFactory.createTicketDtoRefId(this.refId); dto.setRefId(xrefId);
        JAXBElement<String> xreportedTime = objFactory.createTicketDtoReportedTime(this.reportedTime); dto.setReportedTime(xreportedTime);
        JAXBElement<String> xstartWorkingTime = objFactory.createTicketDtoStartWorkingTime(this.startWorkingTime); dto.setStartWorkingTime(xstartWorkingTime);
        JAXBElement<String> xsummary = objFactory.createTicketDtoSummary(this.summary); dto.setSummary(xsummary);
        JAXBElement<String> xsuspendBeginTime = objFactory.createTicketDtoSuspendBeginTime(this.suspendBeginTime); dto.setSuspendBeginTime(xsuspendBeginTime);
        JAXBElement<String> xsuspendEndTime = objFactory.createTicketDtoSuspendEndTime(this.suspendEndTime); dto.setSuspendEndTime(xsuspendEndTime);
        JAXBElement<String> xticketNumber = objFactory.createTicketDtoTicketNumber(this.ticketNumber); dto.setTicketNumber(xticketNumber);
        JAXBElement<String> xticketState = objFactory.createTicketDtoTicketState(this.ticketState); dto.setTicketState(xticketState);
        return dto;
    }

    // // GPS information, not added to PVIM webservice
    // private double latitude;
//
    // // GPS information, NOT ADDED to PVIM Webservice
    // private double longitude;
//
    // public double getLatitude() {
    //     return latitude;
    // }
//
    // public void setLatitude(double latitude) {
    //     this.latitude = latitude;
    // }
//
    // public double getLongitude() {
    //     return longitude;
    // }
//
    // public void setLongitude(double longitude) {
    //     this.longitude = longitude;
    // }
    public String getETA() {
        return ETA;
    }

    public void setETA(String ETA) {
        this.ETA = ETA;
    }

    public String getAssignTime() {
        return assignTime;
    }

    public void setAssignTime(String assignTime) {
        this.assignTime = assignTime;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getAssigneeGroup() {
        return assigneeGroup;
    }

    public void setAssigneeGroup(String assigneeGroup) {
        this.assigneeGroup = assigneeGroup;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDispatchTime() {
        return dispatchTime;
    }

    public void setDispatchTime(String dispatchTime) {
        this.dispatchTime = dispatchTime;
    }

    public String getExpectedFixTime() {
        return expectedFixTime;
    }

    public void setExpectedFixTime(String expectedFixTime) {
        this.expectedFixTime = expectedFixTime;
    }

    public String getExpectedResponseTime() {
        return expectedResponseTime;
    }

    public void setExpectedResponseTime(String expectedResponseTime) {
        this.expectedResponseTime = expectedResponseTime;
    }

    public String getFixTime() {
        return fixTime;
    }

    public void setFixTime(String fixTime) {
        this.fixTime = fixTime;
    }

    public String getIncidentName() {
        return incidentName;
    }

    public void setIncidentName(String incidentName) {
        this.incidentName = incidentName;
    }

    public String getMachineNumber() {
        return machineNumber;
    }

    public void setMachineNumber(String machineNumber) {
        this.machineNumber = machineNumber;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getReportedTime() {
        return reportedTime;
    }

    public void setReportedTime(String reportedTime) {
        this.reportedTime = reportedTime;
    }

    public String getStartWorkingTime() {
        return startWorkingTime;
    }

    public void setStartWorkingTime(String startWorkingTime) {
        this.startWorkingTime = startWorkingTime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSuspendBeginTime() {
        return suspendBeginTime;
    }

    public void setSuspendBeginTime(String suspendBeginTime) {
        this.suspendBeginTime = suspendBeginTime;
    }

    public String getSuspendEndTime() {
        return suspendEndTime;
    }

    public void setSuspendEndTime(String suspendEndTime) {
        this.suspendEndTime = suspendEndTime;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getTicketState() {
        return ticketState;
    }

    public void setTicketState(String ticketState) {
        this.ticketState = ticketState;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.ticketNumber);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RestTicketDto other = (RestTicketDto) obj;
        if (!Objects.equals(this.ETA, other.ETA)) {
            return false;
        }
        if (!Objects.equals(this.assignTime, other.assignTime)) {
            return false;
        }
        if (!Objects.equals(this.assignee, other.assignee)) {
            return false;
        }
        if (!Objects.equals(this.assigneeGroup, other.assigneeGroup)) {
            return false;
        }
        if (!Objects.equals(this.categoryName, other.categoryName)) {
            return false;
        }
        if (!Objects.equals(this.closeTime, other.closeTime)) {
            return false;
        }
        if (!Objects.equals(this.createTime, other.createTime)) {
            return false;
        }
        if (!Objects.equals(this.description, other.description)) {
            return false;
        }
        if (!Objects.equals(this.dispatchTime, other.dispatchTime)) {
            return false;
        }
        if (!Objects.equals(this.expectedFixTime, other.expectedFixTime)) {
            return false;
        }
        if (!Objects.equals(this.expectedResponseTime, other.expectedResponseTime)) {
            return false;
        }
        if (!Objects.equals(this.fixTime, other.fixTime)) {
            return false;
        }
        if (!Objects.equals(this.incidentName, other.incidentName)) {
            return false;
        }
        if (!Objects.equals(this.machineNumber, other.machineNumber)) {
            return false;
        }
        if (!Objects.equals(this.note, other.note)) {
            return false;
        }
        if (!Objects.equals(this.priority, other.priority)) {
            return false;
        }
        if (!Objects.equals(this.refId, other.refId)) {
            return false;
        }
        if (!Objects.equals(this.reportedTime, other.reportedTime)) {
            return false;
        }
        if (!Objects.equals(this.startWorkingTime, other.startWorkingTime)) {
            return false;
        }
        if (!Objects.equals(this.summary, other.summary)) {
            return false;
        }
        if (!Objects.equals(this.suspendBeginTime, other.suspendBeginTime)) {
            return false;
        }
        if (!Objects.equals(this.suspendEndTime, other.suspendEndTime)) {
            return false;
        }
        if (!Objects.equals(this.ticketNumber, other.ticketNumber)) {
            return false;
        }
        if (!Objects.equals(this.ticketState, other.ticketState)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "PVTicketDTO{"
                + "ETA='" + ETA + '\''
                + ", assignTime='" + assignTime + '\''
                + ", assignee='" + assignee + '\''
                + ", assigneeGroup='" + assigneeGroup + '\''
                + ", categoryName='" + categoryName + '\''
                + ", closeTime='" + closeTime + '\''
                + ", createTime='" + createTime + '\''
                + ", description='" + description + '\''
                + ", dispatchTime='" + dispatchTime + '\''
                + ", expectedFixTime='" + expectedFixTime + '\''
                + ", expectedResponseTime='" + expectedResponseTime + '\''
                + ", fixTime='" + fixTime + '\''
                + ", incidentName='" + incidentName + '\''
                + ", machineNumber='" + machineNumber + '\''
                + ", note='" + note + '\''
                + ", priority='" + priority + '\''
                + ", refId='" + refId + '\''
                + ", reportedTime='" + reportedTime + '\''
                + ", startWorkingTime='" + startWorkingTime + '\''
                + ", summary='" + summary + '\''
                + ", suspendBeginTime='" + suspendBeginTime + '\''
                + ", suspendEndTime='" + suspendEndTime + '\''
                + ", ticketNumber='" + ticketNumber + '\''
                + ", ticketState='" + ticketState + '\''
                + '}';
    }
}
