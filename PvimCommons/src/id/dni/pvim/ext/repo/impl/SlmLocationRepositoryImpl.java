/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.impl;

import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.ISlmLocationRepository;
import id.dni.pvim.ext.repo.db.vo.ITableDescriptorVo;
import id.dni.pvim.ext.repo.db.vo.ITableVoFactory;
import id.dni.pvim.ext.repo.db.vo.SlmLocationVo;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
public class SlmLocationRepositoryImpl implements ISlmLocationRepository {

    private final GenericSqlRepository repo;
    
    public SlmLocationRepositoryImpl(Connection conn) {
        repo = new GenericSqlRepository(
                conn,
                new ITableVoFactory() {
            @Override
            public ITableDescriptorVo create() {
                return new SlmLocationVo();
            }
        });
    }

    
    @Override
    public boolean insert(SlmLocationVo obj) throws PvExtPersistenceException {
        return repo.insert(obj);
    }

    @Override
    public boolean delete(SlmLocationVo obj) throws PvExtPersistenceException {
        return repo.delete(obj);
    }

    @Override
    public boolean update(SlmLocationVo obj) throws PvExtPersistenceException {
        return repo.update(obj);
    }

    @Override
    public List<SlmLocationVo> query(ISpecification specification) throws PvExtPersistenceException {
        List<SlmLocationVo> tel = new ArrayList<>();
        List<ITableDescriptorVo> tab = repo.query(specification);
        for (ITableDescriptorVo t : tab) {
            tel.add((SlmLocationVo) t);
        }
        return tel;
    }
    
}
