/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.db.config;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author darryl.sulistyan
 */
public class DBConnectionFactory {
    
    private static final DBConnectionFactory INSTANCE = new DBConnectionFactory();
    
    private DBConnectionFactory() {
        
    }
    
    public static DBConnectionFactory getInstance() {
        return INSTANCE;
    }
    
    public DataSource getDataSource(String jndiName) {
        try {
            Context ctx = (Context) new InitialContext();
            return (DataSource) ctx.lookup(jndiName);
        } catch (NamingException ex) {
            Logger.getLogger(PVIMDBConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
}
