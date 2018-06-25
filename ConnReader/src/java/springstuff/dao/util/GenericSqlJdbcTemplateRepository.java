/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.dao.util;

import id.dni.pvim.ext.repo.ICRUDRepository;
import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.repo.db.vo.ITableDescriptorVo;
import id.dni.pvim.ext.repo.db.vo.ITableVoFactory;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author darryl.sulistyan
 */
public class GenericSqlJdbcTemplateRepository implements ICRUDRepository<ITableDescriptorVo> {

    private final JdbcTemplate jdbcTemplate;
    private final ITableVoFactory factory;
    private final CommonJdbcTemplateCruds crud;
    
    public GenericSqlJdbcTemplateRepository(JdbcTemplate jdbcTemplate, ITableVoFactory factory) {
        this.jdbcTemplate = jdbcTemplate;
        this.factory = factory;
        this.crud = new CommonJdbcTemplateCruds(jdbcTemplate);
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
        
        try {
            List<Map<String, Object>> result;
            if (params == null || params.length == 0) {
                result = jdbcTemplate.queryForList(sql);
            } else {
                result = jdbcTemplate.queryForList(sql, params);
            }
            List<ITableDescriptorVo> ln = new ArrayList<>();
            for (Map<String, Object> e : result) {
                ITableDescriptorVo vo = this.factory.create();
                vo.fillDataFromMap(e);
                ln.add(vo);
            }
            return ln;
            
        } catch (PvExtPersistenceException | DataAccessException ex) {
            throw new PvExtPersistenceException(ex);
        }
        
    }
    
}
