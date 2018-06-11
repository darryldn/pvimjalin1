/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo;

import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 * @param <T>
 */
public interface IRepository<T> {
    
    public List<T> query(ISpecification specification) throws PvExtPersistenceException;
    
}
