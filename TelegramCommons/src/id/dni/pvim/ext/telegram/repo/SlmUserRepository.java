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
import id.dni.pvim.ext.telegram.repo.db.vo.SlmUserVo;
import id.dni.pvim.ext.telegram.repo.spec.SlmUserIsMobileExistSpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
public class SlmUserRepository implements ISlmUserRepository {

    private final GenericSqlRepository repo;
    
    public SlmUserRepository() {
        repo = new GenericSqlRepository(
                PVIMDBConnectionFactory.getInstance().getDataSource(), 
                new ITableVoFactory() {
            @Override
            public ITableDescriptorVo create() {
                return new SlmUserVo();
            }
        });
        //repo = null;
    }
    
    @Override
    public boolean isMobileExist(String mobile) throws PvExtPersistenceException {
        List l = this.query(new SlmUserIsMobileExistSpec(mobile));
        return l != null && !l.isEmpty();
        //return true;
    }

    @Override
    public List<SlmUserVo> query(ISpecification specification) throws PvExtPersistenceException {
        List<SlmUserVo> tel = new ArrayList<>();
        List<ITableDescriptorVo> tab = repo.query(specification);
        for (ITableDescriptorVo t : tab) {
            tel.add((SlmUserVo) t);
        }
        return tel;
        //return Collections.EMPTY_LIST;
    }
    
}
