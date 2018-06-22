/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.db.trx;

import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;

/**
 *
 * @author darryl.sulistyan
 */
public interface IProViewTrx {
    
    /**
     * 
     * @return the object to maintain the transaction in implementation
     * Can be anything, such as java.sql.Connection or EntityManager
     * @throws id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException
     */
    public Object getTrxConnection() throws PvExtPersistenceException;
    
    public void begin() throws PvExtPersistenceException;
    
    public void close() throws PvExtPersistenceException;
    
    public void commit() throws PvExtPersistenceException;
    
    public void rollback() throws PvExtPersistenceException;
    
}
