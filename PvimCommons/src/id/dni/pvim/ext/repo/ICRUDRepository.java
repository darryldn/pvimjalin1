/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo;

import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;

/**
 *
 * @author darryl.sulistyan
 * @param <T>
 */
public interface ICRUDRepository<T> extends IRepository<T> {
    
    public boolean insert(T obj) throws PvExtPersistenceException;
    
    public boolean delete(T obj) throws PvExtPersistenceException;
    
    public boolean update(T obj) throws PvExtPersistenceException;
    
}
