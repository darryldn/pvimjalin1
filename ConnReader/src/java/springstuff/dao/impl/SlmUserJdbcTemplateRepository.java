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
import id.dni.pvim.ext.repo.db.ISlmUserRepository;
import id.dni.pvim.ext.repo.db.vo.SlmUserVo;
import id.dni.pvim.ext.repo.db.spec.impl.SlmUserIsMobileExistSpec;
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
@Repository("SlmUserRepository")
public class SlmUserJdbcTemplateRepository implements ISlmUserRepository {

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
                return new SlmUserVo();
            }
        });
    }
    
    public SlmUserJdbcTemplateRepository() {
    }
    
    @Override
    public boolean isMobileExist(String mobile) throws PvExtPersistenceException {
        List l = this.query(new SlmUserIsMobileExistSpec(mobile));
        return l != null && !l.isEmpty();
    }

    @Override
    public List<SlmUserVo> query(ISpecification specification) throws PvExtPersistenceException {
        List<SlmUserVo> tel = new ArrayList<>();
        List<ITableDescriptorVo> tab = repo.query(specification);
        for (ITableDescriptorVo t : tab) {
            tel.add((SlmUserVo) t);
        }
        return tel;
    }
    
}
