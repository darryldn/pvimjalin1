/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.impl;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.db.config.PVDBConnectionFactory;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.repo.db.vo.DBMachineBasedataVo;
import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.IDBMachineBasedataRepository;
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
public class DBMachineBasedataRepository implements IDBMachineBasedataRepository {

    private final DataSource ds;
    
    public DBMachineBasedataRepository() {
        this.ds = PVDBConnectionFactory.getInstance().getDataSource();
    }
    
    public DBMachineBasedataRepository(DataSource ds) {
        this.ds = ds;
    }
    
    @Override
    public List<DBMachineBasedataVo> query(ISpecification specification) throws PvExtPersistenceException {
        ISqlSpecification sqlSpec = (ISqlSpecification) specification;
        String sql = sqlSpec.toParameterizedSqlQuery();
        Object[] params = sqlSpec.getSqlParams();
        
        MapListHandler handler = new MapListHandler();
        QueryRunner runner = new QueryRunner(this.ds);
        try {
            List<Map<String, Object>> result = runner.query(sql, handler, (Object[]) params);
            List<DBMachineBasedataVo> basedata = new ArrayList<>();
            for (Map<String, Object> mp : result) {
                DBMachineBasedataVo dao = new DBMachineBasedataVo();
                dao.setDeviceID((String) mp.get("ID"));
                dao.setIdType((Integer) mp.get("IDTYPE"));
                dao.setReference((Integer) mp.get("REFERENCE"));
                dao.setValue((String)mp.get("VALUE"));
                basedata.add(dao);
            }
            return basedata;
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
    }
    
}
