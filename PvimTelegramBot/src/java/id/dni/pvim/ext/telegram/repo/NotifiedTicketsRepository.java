/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo;

import id.dni.pvim.ext.db.config.PVIMDBConnectionFactory;
import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.CommonCruds;
import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.repo.db.vo.NotifiedTicketsVo;
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
class NotifiedTicketsRepository implements INotifiedTicketsRepository {

    private final DataSource pvimDS;
    private final CommonCruds crud;
    
    public NotifiedTicketsRepository() {
        pvimDS = PVIMDBConnectionFactory.getInstance().getDataSource();
        crud = new CommonCruds(pvimDS);
    }
    
    @Override
    public boolean insert(NotifiedTicketsVo obj) throws PvExtPersistenceException {
        try {
            return crud.insert(obj) == 1;
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
    }

    @Override
    public boolean delete(NotifiedTicketsVo obj) throws PvExtPersistenceException {
        try {
            return crud.delete(obj) == 1;
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
    }

    @Override
    public boolean update(NotifiedTicketsVo obj) throws PvExtPersistenceException {
        try {
            return crud.update(obj) == 1;
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
    }

    @Override
    public List<NotifiedTicketsVo> query(ISpecification specification) throws PvExtPersistenceException {
        
        ISqlSpecification sqlSpec = (ISqlSpecification) specification;
        String sql = sqlSpec.toParameterizedSqlQuery();
        Object[] params = sqlSpec.getSqlParams();
        
        MapListHandler handler = new MapListHandler();
        QueryRunner runner = new QueryRunner(this.pvimDS);
        
        try {
            List<Map<String, Object>> result;
            if (params == null || params.length == 0) {
                result = runner.query(sql, handler);
            } else {
                result = runner.query(sql, handler, params);
            }
            List<NotifiedTicketsVo> ln = new ArrayList<>();
            for (Map<String, Object> e : result) {
                NotifiedTicketsVo vo = new NotifiedTicketsVo();
                vo.fillDataFromMap(e);
                ln.add(vo);
            }
            return ln;
            
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
        
    }

    @Override
    public int remove(ISpecification spec) throws PvExtPersistenceException {
        ISqlSpecification sqlSpec = (ISqlSpecification) spec;
        String sql = sqlSpec.toParameterizedSqlQuery();
        Object[] params = sqlSpec.getSqlParams();
        
        QueryRunner runner = new QueryRunner(this.pvimDS);
        
        try {
            int result;
            if (params == null || params.length == 0) {
                result = runner.update(sql);
            } else {
                result = runner.update(sql, params);
            }
            return result;
            
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
        
    }
    
}
