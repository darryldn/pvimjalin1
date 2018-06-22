/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.db.trx.impl;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 *
 * @author darryl.sulistyan
 */
public class PVTxObject {
    
    private final DataSource ds;
    private Connection conn;
    private boolean originalAutoCommit;
    private boolean beginSuccess;

    public PVTxObject(DataSource ds) {
        this.ds = ds;
        this.beginSuccess = false;
    }
    
    public Connection getConnection() {
        return conn;
    }
    
    public void begin() throws SQLException {
        conn = ds.getConnection();
        originalAutoCommit = conn.getAutoCommit();
        conn.setAutoCommit(false);
        this.beginSuccess = true;
    }
    
    public void commit() throws SQLException {
        conn.commit();
    }
    
    public void rollback() throws SQLException {
        conn.rollback();
    }
    
    public void close() throws SQLException {
        if (this.beginSuccess) {
            conn.setAutoCommit(originalAutoCommit);
        }
        conn.close();
    }
    
}
