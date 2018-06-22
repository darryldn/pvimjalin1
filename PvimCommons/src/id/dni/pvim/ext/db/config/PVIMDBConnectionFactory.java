/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.db.config;

import id.dni.pvim.ext.db.trx.IProViewTrx;
import id.dni.pvim.ext.db.trx.impl.ProViewJdbcTrxImpl;
import javax.sql.DataSource;

/**
 *
 * @author darryl.sulistyan
 */
public class PVIMDBConnectionFactory {
    
    private static final PVIMDBConnectionFactory INSTANCE = new PVIMDBConnectionFactory();
    
    private DataSource dataSource;
    
    private PVIMDBConnectionFactory() {
        dataSource = getDataSource();
    }
    
    public static PVIMDBConnectionFactory getInstance() {
        return INSTANCE;
    }
    
    /**
     * NOT REUSABLE
     * @return 
     */
    public IProViewTrx getTransaction() {
        return new ProViewJdbcTrxImpl(getDataSource());
    }
    
    public final synchronized DataSource getDataSource() {
        if (dataSource == null) {
            dataSource = DBConnectionFactory.getInstance().getDataSource(
                    DBConnectionConfig.getPVIMDataSourceJDNIName());
        }
        
        return dataSource;
    }
    
//    public Connection getConnection() throws SQLException {
//        DataSource ds = getDataSource();
//        Connection c = ds.getConnection();
//        if (c.getAutoCommit()) {
//            c.setAutoCommit(false);
//        }
//        return c;
//    }
    
//    private boolean testConnection() {
//        
//        boolean isSuccess = true;
//        DataSource ds = getDataSource();
//        Connection conn = null;
//        Statement stmt = null;
//        ResultSet rs = null;
//        
//        try {
//            conn = ds.getConnection();
//            stmt = conn.createStatement();
//            //rs = stmt.executeQuery("select * FROM [pvim].[dbo].[TICKET]");
//            rs = stmt.executeQuery("select * FROM TICKET");
//            
//            while (rs.next()) {
//                String ticketID = rs.getString("TICKET_ID");
//                String ticketNum = rs.getString("TICKET_NUM");
//                Logger.getLogger(PVIMDBConnectionFactory.class.getName()).log(
//                        Level.INFO, String.format("Read Ticket %s,%s", 
//                                ticketID, ticketNum));
//            }
//            
//            return isSuccess;
//        } catch (SQLException ex) {
//            Logger.getLogger(PVIMDBConnectionFactory.class.getName()).log(Level.SEVERE, null, ex);
//            
//        } finally {
//            
//            if (conn != null) {
//                try {
//                    conn.close();
//                } catch (SQLException ex) {
//                }
//            }
//            
//            if (stmt != null) {
//                try {
//                    stmt.close();
//                } catch (SQLException ex) {
//                }
//            }
//            
//            if (rs != null) {
//                try {
//                    rs.close();
//                } catch (SQLException ex) {
//                }
//            }
//            
//        }
//        
//        return false;
//        
//    }
//    
}
