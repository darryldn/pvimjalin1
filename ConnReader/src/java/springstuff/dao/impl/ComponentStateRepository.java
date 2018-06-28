/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.dao.impl;

import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.vo.ITableDescriptorVo;
import id.dni.pvim.ext.repo.db.vo.ITableVoFactory;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import springstuff.dao.IComponentStateRepository;
import springstuff.dao.util.GenericSqlJdbcTemplateRepository;
import springstuff.model.ComponentStateVo;

/**
 *
 * @author darryl.sulistyan
 */
@Repository("componentStateRepository")
public class ComponentStateRepository implements IComponentStateRepository {

    private DataSource pvDS;
    private JdbcTemplate jdbcTemplate;
    private GenericSqlJdbcTemplateRepository repo;
    
    @Autowired
    @Qualifier(value = "pvDataSource")
    public void setPvimDS(DataSource pvDS) {
        this.pvDS = pvDS;
        this.jdbcTemplate = new JdbcTemplate(pvDS);
        repo = new GenericSqlJdbcTemplateRepository(jdbcTemplate, new ITableVoFactory() {
            @Override
            public ITableDescriptorVo create() {
                return new ComponentStateVo();
            }
        });
    }
    
    @Override
    public List<ComponentStateVo> query(ISpecification specification) throws PvExtPersistenceException {
        List<ComponentStateVo> list = new ArrayList<>();
        List<ITableDescriptorVo> result = repo.query(specification);
        for (ITableDescriptorVo i : result) {
            list.add((ComponentStateVo) i);
        }
        return list;
    }
    
}
