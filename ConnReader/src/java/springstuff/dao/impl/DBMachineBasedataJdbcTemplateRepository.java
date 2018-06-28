/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.dao.impl;

import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.IDBMachineBasedataRepository;
import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.repo.db.vo.DBMachineBasedataVo;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 *
 * @author darryl.sulistyan
 */
@Repository("DBMachineBasedataJdbcTemplateRepository")
public class DBMachineBasedataJdbcTemplateRepository implements IDBMachineBasedataRepository {

    private DataSource pvDS;
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
    @Qualifier(value = "pvDataSource")
    public void setDataSource(DataSource ds) {
        this.pvDS = ds;
        this.jdbcTemplate = new JdbcTemplate(ds);
    }
    
    @Override
    public List<DBMachineBasedataVo> query(ISpecification specification) throws PvExtPersistenceException {
        ISqlSpecification sqlSpec = (ISqlSpecification) specification;
        String sql = sqlSpec.toParameterizedSqlQuery();
        Object[] params = sqlSpec.getSqlParams();
        
        List<Map<String, Object>> result;
        try {
            if (params != null) {
                result = jdbcTemplate.queryForList(sql, params);
            } else {
                result = jdbcTemplate.queryForList(sql);
            }
        } catch (DataAccessException e) {
            throw new PvExtPersistenceException(e);
        }

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
    }
    
}
