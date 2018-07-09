/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db;

import id.dni.pvim.ext.repo.IRepository;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.repo.db.vo.SlmUserVo;

/**
 *
 * @author darryl.sulistyan
 */
public interface ISlmUserRepository extends IRepository<SlmUserVo>{
    
    public boolean isMobileExist(String mobile) throws PvExtPersistenceException;
    
}
