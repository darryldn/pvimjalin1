/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo;

import id.dni.pvim.ext.repo.ICRUDRepository;
import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;

/**
 *
 * @author darryl.sulistyan
 */
public interface ITelegramSuscribersRepository extends ICRUDRepository<TelegramSubscriberVo> {
    
    public TelegramSubscriberVo querySingleResult(ISpecification specification) throws PvExtPersistenceException;
    
//    public long queryLatestLastprocessedTimestamp() throws PvExtPersistenceException;
    
//    public void setLatestLastprocessedTimestamp(long lastupdated) throws PvExtPersistenceException;
    
}
