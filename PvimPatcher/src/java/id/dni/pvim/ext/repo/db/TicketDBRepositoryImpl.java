/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db;

import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.vo.ITableDescriptorVo;
import id.dni.pvim.ext.repo.db.vo.ITableVoFactory;
import id.dni.pvim.ext.repo.db.vo.TicketVo;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import id.dni.pvim.ext.repo.impl.GenericSqlRepository;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author darryl.sulistyan
 */
public class TicketDBRepositoryImpl implements ITicketRepository {

    private final GenericSqlRepository repo;
    
    public TicketDBRepositoryImpl(Connection conn) {
        repo = new GenericSqlRepository(
//                PVIMDBConnectionFactory.getInstance().getDataSource(), 
                conn,
                new ITableVoFactory() {
            @Override
            public ITableDescriptorVo create() {
                return new TicketVo();
            }
        });
    }
    
    @Override
    public List<TicketVo> query(ISpecification specification) throws PvExtPersistenceException {
        List<TicketVo> tel = new ArrayList<>();
        List<ITableDescriptorVo> tab = repo.query(specification);
        for (ITableDescriptorVo t : tab) {
            tel.add((TicketVo) t);
        }
        return tel;
    }
    
}
