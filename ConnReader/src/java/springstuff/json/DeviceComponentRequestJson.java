/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.json;

import id.dni.pvim.ext.web.in.PVIMAuthToken;
import id.dni.pvim.ext.web.in.PaginationRequest;

/**
 *
 * @author darryl.sulistyan
 */
public class DeviceComponentRequestJson {
    
    private PVIMAuthToken auth;
    private PaginationRequest page;
    private String id;

    public PVIMAuthToken getAuth() {
        return auth;
    }

    public void setAuth(PVIMAuthToken auth) {
        this.auth = auth;
    }

    public PaginationRequest getPage() {
        return page;
    }

    public void setPage(PaginationRequest page) {
        this.page = page;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    
}
