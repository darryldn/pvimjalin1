/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.db.config;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 *
 * @author darryl.sulistyan
 */
public class PVDBConnectionFactory {
    private static final PVDBConnectionFactory INSTANCE = new PVDBConnectionFactory();
    
    private DataSource dataSource;
    
    private PVDBConnectionFactory() {
        dataSource = getDataSource();
    }
    
    public static PVDBConnectionFactory getInstance() {
        return INSTANCE;
    }
    
    public final synchronized DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = DBConnectionFactory.getInstance().getDataSource(
                    DBConnectionConfig.getPVDataSourceJDNIName());
        }
        
        return dataSource;
    }
    
    public Connection getConnection() throws SQLException {
        DataSource ds = getDataSource();
        return ds.getConnection();
    }
}
