/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.dao.impl;

import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.ISlmUserTokenRepository;
import id.dni.pvim.ext.repo.db.vo.ITableDescriptorVo;
import id.dni.pvim.ext.repo.db.vo.ITableVoFactory;
import id.dni.pvim.ext.repo.db.vo.SlmUserTokenVo;
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
@Repository("SlmUserTokenJdbcTemplateRepository")
public class SlmUserTokenJdbcTemplateRepository implements ISlmUserTokenRepository {

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
                return new SlmUserTokenVo();
            }
        });
    }
    
    public SlmUserTokenJdbcTemplateRepository() {
    }

    @Override
    public boolean insert(SlmUserTokenVo obj) throws PvExtPersistenceException {
        return repo.insert(obj);
    }

    @Override
    public boolean delete(SlmUserTokenVo obj) throws PvExtPersistenceException {
        return repo.delete(obj);
    }

    @Override
    public boolean update(SlmUserTokenVo obj) throws PvExtPersistenceException {
        return repo.update(obj);
    }

    @Override
    public List<SlmUserTokenVo> query(ISpecification specification) throws PvExtPersistenceException {
        List<SlmUserTokenVo> tel = new ArrayList<>();
        List<ITableDescriptorVo> tab = repo.query(specification);
        for (ITableDescriptorVo t : tab) {
            tel.add((SlmUserTokenVo) t);
        }
        return tel;
    }
    
}
