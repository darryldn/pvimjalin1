/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.db.trx.impl;

import id.dni.pvim.ext.db.trx.IProViewTrx;
import id.dni.pvim.ext.repo.exceptions.PvExtPersistenceException;
import java.sql.SQLException;
import javax.sql.DataSource;

/**
 *
 * @author darryl.sulistyan
 */
public class ProViewJdbcTrxImpl implements IProViewTrx {
    
    private final PVTxObject tx;
    
    public ProViewJdbcTrxImpl(DataSource ds) {
        tx = new PVTxObject(ds);
    }
    
    @Override
    public Object getTrxConnection() {
        return tx.getConnection();
    }

    @Override
    public void begin() throws PvExtPersistenceException {
        try {
            tx.begin();
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
    }

    @Override
    public void close() throws PvExtPersistenceException {
        try {
            tx.close();
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
    }

    @Override
    public void commit() throws PvExtPersistenceException {
        try {
            tx.commit();
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
    }

    @Override
    public void rollback() throws PvExtPersistenceException {
        try {
            tx.rollback();
        } catch (SQLException ex) {
            throw new PvExtPersistenceException(ex);
        }
    }
    
}
