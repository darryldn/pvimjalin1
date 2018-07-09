/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db;

import id.dni.pvim.ext.repo.IRepository;
import id.dni.pvim.ext.repo.db.vo.DeviceVo;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;

/**
 *
 * @author darryl.sulistyan
 */
public interface IDeviceRepository extends IRepository<DeviceVo> {
    
    public boolean isDeviceExist(String deviceId) throws PvExtPersistenceException;
    
}
