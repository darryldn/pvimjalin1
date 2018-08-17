/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service;

import id.dni.pvim.ext.repo.db.vo.SlmUserVo;

/**
 *
 * @author darryl.sulistyan
 */
public interface UserService {
    
    public SlmUserVo checkUser(String username, String password);
    
    public SlmUserVo getUserDataByUsername(String username);
    
    public SlmUserVo getUserDataByEmail(String email);
    
}
