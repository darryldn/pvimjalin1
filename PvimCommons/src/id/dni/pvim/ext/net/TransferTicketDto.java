/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.net;

import java.util.List;
import java.util.Map;

/**
 *
 * @author darryl.sulistyan
 */
public class TransferTicketDto {
    private long lastupdated;
    private Map<String, Object> ticketMap;
    private String ticketId;
    private List<String> accountList;

    public List<String> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<String> accountList) {
        this.accountList = accountList;
    }

    public TransferTicketDto(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTicketId() {
        return ticketId;
    }

    public long getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(long lastupdated) {
        this.lastupdated = lastupdated;
    }

    public Map<String, Object> getTicketMap() {
        return ticketMap;
    }

    public void setTicketMap(Map<String, Object> ticketMap) {
        this.ticketMap = ticketMap;
    }

    @Override
    public String toString() {
        return "TransferTicketDto{" + "lastupdated=" + lastupdated + ", ticketMap=" + ticketMap + '}';
    }
    
    
}
