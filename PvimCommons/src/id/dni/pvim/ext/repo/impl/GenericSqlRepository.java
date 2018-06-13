/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.impl;

import id.dni.pvim.ext.repo.ICRUDRepository;
import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.CommonCruds;
import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.repo.db.vo.ITableDescriptorVo;
import id.dni.pvim.ext.repo.db.vo.ITableVoFactory;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

/**
 *
 * @author darryl.sulistyan
 */
public class GenericSqlRepository implements ICRUDRepository<ITableDescriptorVo>{

    private final CommonCruds crud;
    private final DataSource ds;
    private final ITableVoFactory factory;
    
    public GenericSqlRepository(DataSource ds, ITableVoFactory factory) {
        this.ds = ds;
        this.crud = new CommonCruds(ds);
        this.factory = factory;
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
    
    @Override
    public List<ITableDescriptorVo> query(ISpecification specification) throws PvExtPersistenceException {
        
        ISqlSpecification sqlSpec = (ISqlSpecification) specification;
        String sql = sqlSpec.toParameterizedSqlQuery();
        Object[] params = sqlSpec.getSqlParams();
        
        MapListHandler handler = new MapListHandler();
        QueryRunner runner = new QueryRunner(this.ds);
        
        try {
            List<Map<String, Object>> result;
            if (params == null || params.length == 0) {
                result = runner.query(sql, handler);
            } else {
                result = runner.query(sql, handler, params);
            }
            List<ITableDescriptorVo> ln = new ArrayList<>();
            for (Map<String, Object> e : result) {
                ITableDescriptorVo vo = this.factory.create();
                vo.fillDataFromMap(e);
                ln.add(vo);
            }
            return ln;
            
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
        
    }
    
}
