/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.impl;

import id.dni.pvim.ext.repo.ICRUDRepository;
import id.dni.pvim.ext.repo.db.CommonCruds;
import id.dni.pvim.ext.repo.db.vo.ITableDescriptorVo;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 *
 * @author darryl.sulistyan
 */
public abstract class GenericRepository implements ICRUDRepository<ITableDescriptorVo>{

    private final CommonCruds crud;
    private final DataSource ds;
    
    public GenericRepository(DataSource ds) {
        this.ds = ds;
        this.crud = new CommonCruds(ds);
    }
    
    @Override
    public boolean insert(ITableDescriptorVo obj) throws PvExtPersistenceException {
        try {
            return crud.insert(obj) == 1;
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
    }

    @Override
    public boolean delete(ITableDescriptorVo obj) throws PvExtPersistenceException {
        try {
            return crud.delete(obj) == 1;
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
    }

    @Override
    public boolean update(ITableDescriptorVo obj) throws PvExtPersistenceException {
        try {
            return crud.update(obj) == 1;
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
    }
    
}
