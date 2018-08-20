/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.ISlmUserRepository;
import id.dni.pvim.ext.repo.db.ISlmUserTokenRepository;
import id.dni.pvim.ext.repo.db.spec.impl.GetPvimUserByEmailSpecification;
import id.dni.pvim.ext.repo.db.spec.impl.GetPvimUserByLoginNameSpecification;
import id.dni.pvim.ext.repo.db.spec.impl.GetPvimUserTokenByIdSpecification;
import id.dni.pvim.ext.repo.db.vo.SlmUserTokenVo;
import id.dni.pvim.ext.repo.db.vo.SlmUserVo;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.web.in.Commons;
import id.dni.pvim.ext.web.in.PVIMAuthToken;
import id.dni.pvim.ext.web.in.UserToken;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springstuff.service.UserService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
public class UserServiceImpl implements UserService {

    private ISlmUserRepository userRepo;
    private ISlmUserTokenRepository userTokenRepo;
    
    @Autowired
    public void setSlmUserRepository(ISlmUserRepository userRepo) {
        this.userRepo = userRepo;
    }
    
    @Autowired
    public void setSlmUserTokenRepository(ISlmUserTokenRepository userTokenRepo) {
        this.userTokenRepo = userTokenRepo;
    }
    
    @Override
    public SlmUserVo checkUser(String username, String password) {
        return new SlmUserVo(); // for now, bypass this
    }
    
    private SlmUserVo getUserBySpec(ISpecification spec) {
        try {
            List<SlmUserVo> users = userRepo.query(spec);
            if (!users.isEmpty()) {
                return users.get(0);
            }
        } catch (PvExtPersistenceException ex) {
            Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public SlmUserVo getUserDataByUsername(String username) {
        if (!Commons.isEmptyStrIgnoreSpaces(username)) {
            return getUserBySpec(new GetPvimUserByLoginNameSpecification(username));
        } else {
            return null;
        }
    }

    @Override
    public SlmUserVo getUserDataByEmail(String email) {
        if (!Commons.isEmptyStrIgnoreSpaces(email)) {
            return getUserBySpec(new GetPvimUserByEmailSpecification(email));
        } else {
            return null;
        }
    }

    @Override
    public SlmUserVo checkUser(PVIMAuthToken token) {
        return this.checkUser(token.getUsername(), token.getPassword());
    }

    @Transactional(transactionManager = "pvimTransactionManager", rollbackFor = PvExtPersistenceException.class)
    @Override
    public SlmUserTokenVo setOrUpdateUserToken(UserToken userToken) {
        
        if (userToken == null) {
            return null;
        }
        
        if (Commons.isEmptyStrIgnoreSpaces(userToken.getUserId()) || 
                Commons.isEmptyStrIgnoreSpaces(userToken.getUserToken())) {
            return null;
        }
        
        try {
            List<SlmUserTokenVo> tok = this.userTokenRepo.query(new 
                    GetPvimUserTokenByIdSpecification(userToken.getUserId()));
            boolean result;
            
            SlmUserTokenVo dbToken;
            if (tok.isEmpty()) {
                dbToken = new SlmUserTokenVo();
                dbToken.setLastUpdated(System.currentTimeMillis());
                dbToken.setMessageToken(userToken.getUserToken());
                dbToken.setUserID(userToken.getUserId());
                result = this.userTokenRepo.insert(dbToken);
                
            } else {
                dbToken = tok.get(0);
                dbToken.setMessageToken(userToken.getUserToken());
                dbToken.setLastUpdated(System.currentTimeMillis());
                result = this.userTokenRepo.update(dbToken);
                
            }
            
            if (result) {
                return dbToken;
            }
            
            return null;
            
        } catch (PvExtPersistenceException ex) {
            Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
            
        }
    }

    @Transactional(transactionManager = "pvimTransactionManager", rollbackFor = PvExtPersistenceException.class)
    @Override
    public SlmUserTokenVo deleteUserToken(UserToken userToken) {
        if (userToken == null) {
            return null;
        }
        
        if (Commons.isEmptyStrIgnoreSpaces(userToken.getUserId()) || 
                Commons.isEmptyStrIgnoreSpaces(userToken.getUserToken())) {
            return null;
        }
        
        try {
            List<SlmUserTokenVo> tok = this.userTokenRepo.query(new 
                    GetPvimUserTokenByIdSpecification(userToken.getUserId()));
            boolean result = false;
            
            SlmUserTokenVo dbToken = null;
            if (!tok.isEmpty()) {
                dbToken = tok.get(0);
                result = this.userTokenRepo.delete(dbToken);
            }
            
            if (result) {
                return dbToken;
            }
            
            return null;
            
        } catch (PvExtPersistenceException ex) {
            Logger.getLogger(UserServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
            return null;
            
        }
    }

    
}
