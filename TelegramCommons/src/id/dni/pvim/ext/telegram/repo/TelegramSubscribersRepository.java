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
    
//    private static final String PAR_NAME_LATEST_LASTPROCESSED = "LATEST_LASTPROCESSED";
//    private final ITelegramSubscribersParameterRepository parameterRepo;
    
    public TelegramSubscribersRepository() {
        repo = new GenericSqlRepository(PVIMDBConnectionFactory.getInstance().getDataSource(), new ITableVoFactory() {
            @Override
            public ITableDescriptorVo create() {
                return new TelegramSubscriberVo();
            }
        });
//        parameterRepo = new TelegramSubscribersParameterRepository();
        //repo = null;
        //sim = new TelegramSubscribersRepositorySim();
    }

    @Override
    public boolean insert(TelegramSubscriberVo obj) throws PvExtPersistenceException {
        // don't care whether insert is successful or not, the chat IS ALREADY processed
        // by this time. If insert errors, the user MUST send the chat again!
//        setLatestLastprocessedTimestamp(obj.getLastupdate());
        return repo.insert(obj);
//        return sim.insert(obj);
    }

    @Override
    public boolean delete(TelegramSubscriberVo obj) throws PvExtPersistenceException {
        // ditto insert
//        setLatestLastprocessedTimestamp(obj.getLastupdate());
        return repo.delete(obj);
//        return sim.delete(obj);
    }

    @Override
    public boolean update(TelegramSubscriberVo obj) throws PvExtPersistenceException {
        // ditto insert
//        setLatestLastprocessedTimestamp(obj.getLastupdate());
        return repo.update(obj);
//        return sim.update(obj);
    }

    @Override
    public List<TelegramSubscriberVo> query(ISpecification specification) throws PvExtPersistenceException {
        List<TelegramSubscriberVo> tel = new ArrayList<>();
        List<ITableDescriptorVo> tab = repo.query(specification);
        for (ITableDescriptorVo t : tab) {
            tel.add((TelegramSubscriberVo) t);
        }
        return tel;
//        return sim.query(specification);
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
//        return sim.querySingleResult(specification);
    }
    
//    private TelegramSubscriberParameterVo getLatestLastProcessed() throws PvExtPersistenceException {
//        
//        List<TelegramSubscriberParameterVo> query = 
//                parameterRepo.query(new TelegramSubscribersParameterSpec(PAR_NAME_LATEST_LASTPROCESSED));
//        if (query == null || query.isEmpty()) {
//            return null;
//        }
//        
//        if (query.size() != 1) {
//            throw new PvExtPersistenceException("More than one result for " + PAR_NAME_LATEST_LASTPROCESSED);
//        }
//        
//        return query.get(0);
//    }

//    @Override
//    public long queryLatestLastprocessedTimestamp() throws PvExtPersistenceException {
//        
//        TelegramSubscriberParameterVo q = getLatestLastProcessed();
//        if (q != null) {
//            return Long.parseLong(q.getParValue());
//        } else {
//            return 0;
//        }
//    }
//
////    @Override
//    public void setLatestLastprocessedTimestamp(long lastupdated) throws PvExtPersistenceException {
//        
//        TelegramSubscriberParameterVo q = getLatestLastProcessed();
//        if (q != null) {
//            q.setParValue("" + lastupdated);
//            parameterRepo.update(q);
//        } else {
//            q = new TelegramSubscriberParameterVo();
//            q.setParName(PAR_NAME_LATEST_LASTPROCESSED);
//            q.setParValue("" + lastupdated);
//            q.setToCurrentTimestamp();
//            parameterRepo.insert(q);
//        }
//    }
    
}
