/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo;

import id.dni.pvim.ext.db.config.PVIMDBConnectionFactory;
import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.vo.ITableDescriptorVo;
import id.dni.pvim.ext.repo.db.vo.ITableVoFactory;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.repo.impl.GenericSqlRepository;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
public class TelegramSubscribersRepository implements ITelegramSuscribersRepository {
    
    private final GenericSqlRepository repo;
    
    public TelegramSubscribersRepository() {
        repo = new GenericSqlRepository(PVIMDBConnectionFactory.getInstance().getDataSource(), new ITableVoFactory() {
            @Override
            public ITableDescriptorVo create() {
                return new TelegramSubscriberVo();
            }
        });
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
    
}
