/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.ext.web.ws.obj.firebase;

/**
 *
 * @author darryl.sulistyan
 */
public class FbPvimSlmUserJson {
    
    private String userId;
    private String mobile;
    private String email;
    private String userType;
    private String loginName;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    @Override
    public String toString() {
        return "FbPvimSlmUserJson{" + "userId=" + userId + ", mobile=" + mobile + ", email=" + email + ", userType=" + userType + ", loginName=" + loginName + '}';
    }
    
    
    
}
