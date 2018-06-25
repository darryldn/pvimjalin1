/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.dao.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.jdbc.core.RowMapper;

/**
 *
 * @author darryl.sulistyan
 */
public class MapMapper implements RowMapper<Map<String, Object>> {

    @Override
    public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        Map<String, Object> row = new HashMap<>();
        int ncol = metaData.getColumnCount();
        for (int i=1; i<=ncol; ++i) {
            row.put(metaData.getColumnName(i), rs.getObject(i));
        }
        return row;
    }
    
}
