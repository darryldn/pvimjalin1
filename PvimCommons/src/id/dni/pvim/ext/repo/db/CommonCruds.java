/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db;

import id.dni.pvim.ext.repo.db.vo.FieldData;
import id.dni.pvim.ext.repo.db.vo.ITableDescriptorVo;
import id.dni.pvim.ext.web.in.Commons;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.dbutils.QueryRunner;

/**
 *
 * @author darryl.sulistyan
 */
public class CommonCruds {
    
    private final Connection conn;
    
    public CommonCruds(Connection conn) {
        this.conn = conn;
    }
    
    Pair buildInsert(ITableDescriptorVo t) {
        String tblName = t.getTableName();
        Map<String, FieldData> tbl = t.getFieldDescriptor();
        
        if (Commons.isEmptyStrIgnoreSpaces(tblName) || tbl == null) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("insert into ").append(tblName).append(" ");
        
        List<String> fieldNames = new ArrayList<>();
        List<FieldData> fields = new ArrayList<>();
        for (Map.Entry<String, FieldData> e : tbl.entrySet()) {
            fieldNames.add(e.getKey());
            fields.add(e.getValue());
        }
        
        if (fieldNames.isEmpty()) {
            return null;
            
        } else {
            sb.append("(");
            sb.append(fieldNames.get(0));
            for (int i=1; i<fieldNames.size(); ++i) {
                sb.append(", ").append(fieldNames.get(i));
            }
            sb.append(") ");
        }
        sb.append("values ");
        
        sb.append("(?");
        for (int i=1; i<fieldNames.size(); ++i) {
            sb.append(", ?");
        }
        sb.append(") ");
        
        Pair p = new Pair();
        p.sql = sb.toString();
        p.fieldOrdering = new ArrayList<>();
        p.fieldOrdering.addAll(fields);
        
        return p;
    }
    
    public int insert(ITableDescriptorVo t) throws SQLException {
        
        QueryRunner runner = new QueryRunner();
        try {
            Pair pair = buildInsert(t);
            if (pair == null) {
                return 0;
            }
            
            String sql = pair.sql;
            if (sql == null) {
                return 0;
            }
            
            List params = new ArrayList();
            for (FieldData f : pair.fieldOrdering) {
                if (f != null) {
                    params.add(f.getValue());
                } else {
                    params.add(null);
                }
            }
            int updated = runner.update(conn, sql, params.toArray());
            return updated;
            
        } finally {
            
            
        }   
        
    }
    
    static class Pair {
        List<FieldData> fieldOrdering;
        String sql;
    }
    
    Pair buildUpdate(ITableDescriptorVo t) {
        String tblName = t.getTableName();
        Map<String, FieldData> tblFields = t.getFieldDescriptor();
        
        if (Commons.isEmptyStrIgnoreSpaces(tblName) || tblFields == null || tblFields.isEmpty()) {
            return null;
        }
        
        List<FieldData> commonFields = new ArrayList<>();
        List<FieldData> pkFields = new ArrayList<>();
        
        StringBuilder sb = new StringBuilder();
        sb.append("update ").append(tblName).append(" set ");
        
        for (Map.Entry<String, FieldData> field : tblFields.entrySet()) {
            FieldData fieldData = field.getValue();
            
            if (!fieldData.isIsPartOfPk()) {
                sb.append(" ").append(fieldData.getFieldName()).append(" = ?,");
                commonFields.add(fieldData);
            } else {
                pkFields.add(fieldData);
            }
        }
        
        if (commonFields.isEmpty()) {
            return null; // all are PK? They cannot be updated then...
        }
        
        sb.setLength(sb.length()-1); // remove the last comma
        sb.append(" where ");
        
        if (pkFields.isEmpty()) {
            return null; // no primary keys?
        }
        
        sb.append(pkFields.get(0).getFieldName()).append("=? ");
        for (int i=1; i<pkFields.size(); ++i) {
            sb.append(" and ").append(pkFields.get(i).getFieldName()).append("=? ");
        }
        
        Pair p = new Pair();
        p.sql = sb.toString();
        p.fieldOrdering = new ArrayList<>();
        p.fieldOrdering.addAll(commonFields);
        p.fieldOrdering.addAll(pkFields);
        return p;
    }
    
    
    public int update(ITableDescriptorVo t) throws SQLException {
        
        QueryRunner runner = new QueryRunner();
        try {
            Pair pair = buildUpdate(t);
            if (pair == null) {
                return 0;
            }
            
            String sql = pair.sql;
            if (sql == null) {
                return 0;
            }
            
            List params = new ArrayList();
            for (FieldData f : pair.fieldOrdering) {
                if (f != null) {
                    params.add(f.getValue());
                } else {
                    params.add(null);
                }
            }
            int updated = runner.update(conn, sql, params.toArray());
            return updated;
            
        } finally {
            
            
        }   
    }
    
    Pair buildDelete(ITableDescriptorVo t) {
        String tblName = t.getTableName();
        Map<String, FieldData> tblFields = t.getFieldDescriptor();
        
        if (Commons.isEmptyStrIgnoreSpaces(tblName) || tblFields == null || tblFields.isEmpty()) {
            return null;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("delete from ").append(tblName).append(" ");
        
        List<FieldData> params = new ArrayList<>();
        for (Map.Entry<String, FieldData> f : tblFields.entrySet()) {
            if (f.getValue() == null) {
                return null; // error configuration here!
            }
            params.add(f.getValue());
        }
        
        if (params.isEmpty()) {
            return null; // delete from table?? All of them??
        }
        
        sb.append("where ");
        sb.append(params.get(0).getFieldName()).append("=? ");
        for (int i=1; i<params.size(); ++i) {
            sb.append(" and ").append(params.get(i).getFieldName()).append("=? ");
        }
        
        Pair p = new Pair();
        p.sql = sb.toString();
        p.fieldOrdering = new ArrayList<>();
        p.fieldOrdering.addAll(params);
        return p;
    }
    
    public int delete(ITableDescriptorVo t) throws SQLException {
        QueryRunner runner = new QueryRunner();
        try {
            Pair pair = buildDelete(t);
            if (pair == null) {
                return 0;
            }
            
            String sql = pair.sql;
            if (sql == null) {
                return 0;
            }
            
            List params = new ArrayList();
            for (FieldData f : pair.fieldOrdering) {
                if (f != null) {
                    params.add(f.getValue());
                } else {
                    params.add(null);
                }
            }
            int updated = runner.update(conn, sql, params.toArray());
            return updated;
            
        } finally {
            
            
        }   
    }
    
}
