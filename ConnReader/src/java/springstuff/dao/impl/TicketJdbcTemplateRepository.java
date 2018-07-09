/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.dao.impl;

import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.ITicketRepository;
import id.dni.pvim.ext.repo.db.vo.ITableDescriptorVo;
import id.dni.pvim.ext.repo.db.vo.ITableVoFactory;
import id.dni.pvim.ext.repo.db.vo.TicketVo;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import springstuff.dao.util.GenericSqlJdbcTemplateRepository;

/**
 *
 * @author darryl.sulistyan
 */
@Repository("TicketJdbcTemplateRepository")
public class TicketJdbcTemplateRepository implements ITicketRepository {

    private DataSource pvimDS;
    private JdbcTemplate jdbcTemplate;
    private GenericSqlJdbcTemplateRepository repo;
    
    @Autowired
    @Qualifier(value = "pvimDataSource")
    public void setPvimDS(DataSource pvimDS) {
        this.pvimDS = pvimDS;
        this.jdbcTemplate = new JdbcTemplate(pvimDS);
        repo = new GenericSqlJdbcTemplateRepository(jdbcTemplate, new ITableVoFactory() {
            @Override
            public ITableDescriptorVo create() {
                return new TicketVo();
            }
        });
    }
    
    public TicketJdbcTemplateRepository() {
        
    }
    
    @Override
    public List<TicketVo> query(ISpecification specification) throws PvExtPersistenceException {
        List<TicketVo> result = new ArrayList<>();
        List<ITableDescriptorVo> tab = repo.query(specification);
        for (ITableDescriptorVo s : tab) {
            result.add((TicketVo) s);
        }
        return result;
    }
    
}
