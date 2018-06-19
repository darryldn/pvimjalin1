/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo.sim;

import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.repo.ITelegramSuscribersRepository;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;
import id.dni.pvim.ext.telegram.repo.spec.TelegramSubscribersByChatIDSpec;
import id.dni.pvim.ext.telegram.repo.spec.TelegramSubscribersPhoneNumSpecification;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author darryl.sulistyan
 */
class TelegramSubscribersRepositorySim implements ITelegramSuscribersRepository {

    private static final Map<String, TelegramSubscriberVo> db = new HashMap<>();
    private static final Map<String, TelegramSubscriberVo> dbByPhoneNum = new HashMap<>();
    private static final Map<String, TelegramSubscriberVo> dbByChatId = new HashMap<>();
    private static long latestLastupdated;
    
    @Override
    public TelegramSubscriberVo querySingleResult(ISpecification specification) throws PvExtPersistenceException {
        System.out.println("querySingleResult");
        if (specification instanceof TelegramSubscribersByChatIDSpec) {
            TelegramSubscribersByChatIDSpec t = (TelegramSubscribersByChatIDSpec) specification;
            String par = (Long) t.getSqlParams()[0] + "";
            System.out.println("par=" + par);
            return dbByChatId.get(par);
            
        } else if (specification instanceof TelegramSubscribersPhoneNumSpecification) {
            TelegramSubscribersPhoneNumSpecification t = (TelegramSubscribersPhoneNumSpecification) specification;
            String ph = (String) t.getSqlParams()[0];
            System.out.println("ph=" + ph);
            return dbByPhoneNum.get(ph);
            
        } else {
            return null;
        }
    }

    @Override
    public boolean insert(TelegramSubscriberVo obj) throws PvExtPersistenceException {
        System.out.println("insert obj " + obj);
        db.put(obj.getSubs_id(), obj);
        dbByChatId.put(obj.getChat_id()+"", obj);
        dbByPhoneNum.put(obj.getPhone_num(), obj);
        if (obj.getLastupdate() > latestLastupdated) {
            latestLastupdated = obj.getLastupdate();
        } else {
            System.out.println("!!! ERROR !!! insert, getLastupdate <= latestLastupdated!");
        }
        return true;
    }

    @Override
    public boolean delete(TelegramSubscriberVo obj) throws PvExtPersistenceException {
        db.remove(obj.getSubs_id());
        dbByChatId.remove(obj.getChat_id() + "");
        dbByPhoneNum.remove(obj.getPhone_num());
        if (obj.getLastupdate() > latestLastupdated) {
            latestLastupdated = obj.getLastupdate();
        } else {
            System.out.println("!!! ERROR !!! delete, getLastupdate <= latestLastupdated!");
        }
        return true;
    }

    @Override
    public boolean update(TelegramSubscriberVo obj) throws PvExtPersistenceException {
        db.put(obj.getSubs_id(), obj);
        dbByChatId.put(obj.getChat_id()+"", obj);
        dbByPhoneNum.put(obj.getPhone_num(), obj);
        if (obj.getLastupdate() > latestLastupdated) {
            latestLastupdated = obj.getLastupdate();
        } else {
            System.out.println("!!! ERROR !!! update, getLastupdate <= latestLastupdated!");
        }
        return true;
    }

    @Override
    public List<TelegramSubscriberVo> query(ISpecification specification) throws PvExtPersistenceException {
        System.out.println("Query list!");
        return Collections.EMPTY_LIST;
    }

//    @Override
    public long queryLatestLastprocessedTimestamp() throws PvExtPersistenceException {
        return latestLastupdated;
    }

//    @Override
    public void setLatestLastprocessedTimestamp(long lastupdated) throws PvExtPersistenceException {
        if (lastupdated > latestLastupdated) {
            latestLastupdated = lastupdated;
        } else {
            System.out.println("!!! ERROR !!! setLatestLastupdated, getLastupdate <= latestLastupdated!");
        }
    }
    
}
