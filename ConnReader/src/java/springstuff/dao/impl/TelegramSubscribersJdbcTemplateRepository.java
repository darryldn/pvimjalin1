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
import id.dni.pvim.ext.telegram.repo.ITelegramSuscribersRepository;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;
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
@Repository("telegramSubscribersRepository")
public class TelegramSubscribersJdbcTemplateRepository implements ITelegramSuscribersRepository {

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
                return new TelegramSubscriberVo();
            }
        });
    }
    
    public TelegramSubscribersJdbcTemplateRepository() {
    }
    
    @Override
    public TelegramSubscriberVo querySingleResult(ISpecification specification) throws PvExtPersistenceException {
        List<TelegramSubscriberVo> result = query(specification);
        if (result.size() == 1) {
            return result.get(0);
        }
        
        if (result.isEmpty()) {
            return null;
        }
        
        throw new PvExtPersistenceException("More than one result is available!");
    }

    @Override
    public boolean insert(TelegramSubscriberVo obj) throws PvExtPersistenceException {
        return repo.insert(obj);
    }

    @Override
    public boolean delete(TelegramSubscriberVo obj) throws PvExtPersistenceException {
        return repo.delete(obj);
    }

    @Override
    public boolean update(TelegramSubscriberVo obj) throws PvExtPersistenceException {
        return repo.update(obj);
    }

    @Override
    public List<TelegramSubscriberVo> query(ISpecification specification) throws PvExtPersistenceException {
        List<TelegramSubscriberVo> tel = new ArrayList<>();
        List<ITableDescriptorVo> tab = repo.query(specification);
        for (ITableDescriptorVo t : tab) {
            tel.add((TelegramSubscriberVo) t);
        }
        return tel;
    }
    
}
