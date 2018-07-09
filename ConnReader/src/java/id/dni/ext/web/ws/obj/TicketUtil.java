/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.web.ws.obj;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author darryl.sulistyan
 */
public class TicketUtil {
    
    public static Map<String, Object> convert(RestTicketDto ticket) {
        Map<String, Object> ticketMap = new HashMap<>();
        ticketMap.put("ticketNumber",ticket.getTicketNumber());
        ticketMap.put("ticketState",ticket.getTicketState());
        ticketMap.put("summary",ticket.getSummary());
        ticketMap.put("description",ticket.getDescription());
        ticketMap.put("machineNumber",ticket.getMachineNumber());
        ticketMap.put("categoryName",ticket.getCategoryName());
        ticketMap.put("incidentName",ticket.getIncidentName());
        ticketMap.put("priority",ticket.getPriority());
        ticketMap.put("reportedTime",ticket.getReportedTime());
        ticketMap.put("createTime",ticket.getCreateTime());
        ticketMap.put("assigneeGroup",ticket.getAssigneeGroup());
        ticketMap.put("dispatchTime",ticket.getDispatchTime());
        ticketMap.put("assignee",ticket.getAssignee());
        ticketMap.put("assignTime",ticket.getAssignTime());
        ticketMap.put("expectedResponseTime",ticket.getExpectedResponseTime());
        ticketMap.put("startWorkingTime",ticket.getStartWorkingTime());
        ticketMap.put("suspendBeginTime",ticket.getSuspendBeginTime());
        ticketMap.put("suspendEndTime",ticket.getSuspendEndTime());
        ticketMap.put("expectedFixTime",ticket.getExpectedFixTime());
        ticketMap.put("fixTime",ticket.getFixTime());
        ticketMap.put("closeTime",ticket.getCloseTime());
        ticketMap.put("ETA",ticket.getETA());
        ticketMap.put("note",ticket.getNote());
        ticketMap.put("refId",ticket.getRefId());
        return ticketMap;
    }
    
}
