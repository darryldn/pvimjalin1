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
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberParameterVo;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
class TelegramSubscribersParameterRepository implements ITelegramSubscribersParameterRepository {

    private final GenericSqlRepository repo;
    
    public TelegramSubscribersParameterRepository() {
        repo = new GenericSqlRepository(PVIMDBConnectionFactory.getInstance().getDataSource(), new ITableVoFactory() {
            @Override
            public ITableDescriptorVo create() {
                return new TelegramSubscriberParameterVo();
            }
        });
    }
    
    @Override
    public boolean insert(TelegramSubscriberParameterVo obj) throws PvExtPersistenceException {
        return repo.insert(obj);
    }

    @Override
    public boolean delete(TelegramSubscriberParameterVo obj) throws PvExtPersistenceException {
        return repo.delete(obj);
    }

    @Override
    public boolean update(TelegramSubscriberParameterVo obj) throws PvExtPersistenceException {
        return repo.update(obj);
    }

    @Override
    public List<TelegramSubscriberParameterVo> query(ISpecification specification) throws PvExtPersistenceException {
        List<TelegramSubscriberParameterVo> tel = new ArrayList<>();
        List<ITableDescriptorVo> tab = repo.query(specification);
        for (ITableDescriptorVo t : tab) {
            tel.add((TelegramSubscriberParameterVo) t);
        }
        return tel;
    }
    
}
