/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo.sim;

import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.telegram.repo.ISlmUserRepository;
import id.dni.pvim.ext.telegram.repo.db.vo.SlmUserVo;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
class SlmUserRepositorySim implements ISlmUserRepository {
    
    public SlmUserRepositorySim() {
    }
    
    @Override
    public boolean isMobileExist(String mobile) throws PvExtPersistenceException {
        return true;
    }

    @Override
    public List<SlmUserVo> query(ISpecification specification) throws PvExtPersistenceException {
        return Collections.EMPTY_LIST;
    }
}
