/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.service.impl;

import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.ISlmUserRepository;
import id.dni.pvim.ext.repo.db.spec.impl.GetPvimUserByEmailSpecification;
import id.dni.pvim.ext.repo.db.spec.impl.GetPvimUserByLoginNameSpecification;
import id.dni.pvim.ext.repo.db.vo.SlmUserVo;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.web.in.Commons;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import springstuff.service.UserService;

/**
 *
 * @author darryl.sulistyan
 */
@Service
public class UserServiceImpl implements UserService {

    private ISlmUserRepository userRepo;
    
    @Autowired
    public void setSlmUserRepository(ISlmUserRepository userRepo) {
        this.userRepo = userRepo;
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

    
}
