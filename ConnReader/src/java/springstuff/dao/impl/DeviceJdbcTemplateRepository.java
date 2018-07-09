/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.dao.impl;

import id.dni.pvim.ext.repo.ISpecification;
import id.dni.pvim.ext.repo.db.IDeviceRepository;
import id.dni.pvim.ext.repo.db.spec.impl.GetDeviceByIdSpecification;
import id.dni.pvim.ext.repo.db.vo.DeviceVo;
import id.dni.pvim.ext.repo.db.vo.ITableDescriptorVo;
import id.dni.pvim.ext.repo.db.vo.ITableVoFactory;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import springstuff.dao.util.GenericSqlJdbcTemplateRepository;

/**
 *
 * @author darryl.sulistyan
 */
@Repository("DeviceJdbcTemplateRepository")
public class DeviceJdbcTemplateRepository implements IDeviceRepository {

    private DataSource pvDS;
    private JdbcTemplate jdbcTemplate;
    private GenericSqlJdbcTemplateRepository repo;
    
    @Autowired
    @Qualifier(value = "pvDataSource")
    public void setPvimDS(DataSource pvDS) {
        this.pvDS = pvDS;
        this.jdbcTemplate = new JdbcTemplate(pvDS);
        repo = new GenericSqlJdbcTemplateRepository(jdbcTemplate, new ITableVoFactory() {
            @Override
            public ITableDescriptorVo create() {
                return new DeviceVo();
            }
        });
    }
    
    @Override
    public boolean isDeviceExist(String deviceId) throws PvExtPersistenceException {
        List x = query(new GetDeviceByIdSpecification(deviceId));
        return (x != null && !x.isEmpty());
    }

    @Override
    public List<DeviceVo> query(ISpecification specification) throws PvExtPersistenceException {
        List<DeviceVo> list = new ArrayList<>();
        List<ITableDescriptorVo> result = repo.query(specification);
        for (ITableDescriptorVo i : result) {
            list.add((DeviceVo) i);
        }
        return list;
    }
    
}
