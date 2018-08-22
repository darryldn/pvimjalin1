/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.net;

import id.dni.pvim.ext.web.in.PVIMAuthToken;
import java.util.List;
import java.util.Map;

/**
 *
 * @author darryl.sulistyan
 */
public class TransferTicketDto {
    private long lastupdated;
    private Map<String, Object> ticketMap;
    private final String ticketId;
    private List<String> accountList;
    private String context;
    private PVIMAuthToken auth;

    public PVIMAuthToken getAuth() {
        return auth;
    }

    public void setAuth(PVIMAuthToken auth) {
        this.auth = auth;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

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
        return "TransferTicketDto{" + "lastupdated=" + lastupdated + ", ticketMap=" + ticketMap + ", ticketId=" + ticketId + ", accountList=" + accountList + ", context=" + context + ", auth=" + auth + '}';
    }
    
    
    
}
