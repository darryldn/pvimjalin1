/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package springstuff.dao.spec;

import id.dni.pvim.ext.repo.db.pagination.IPaginator;
import id.dni.pvim.ext.repo.db.pagination.Mssql2012Paginator;
import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import springstuff.model.ComponentStateVo;

/**
 *
 * @author darryl.sulistyan
 */
public class GetAllComponentStateWithDeviceDescrSpec implements ISqlSpecification {
    
    private static final String SQL = 
            new StringBuilder()
                    .append("select ")
                    .append("f.").append(ComponentStateVo.FIELD_DEVICEID).append(",")
                    .append("p.").append(ComponentStateVo.FIELD_DEVICEDESCR).append(",")
                    .append("f.").append(ComponentStateVo.FIELD_COMPONENT).append(",")
                    .append("f.").append(ComponentStateVo.FIELD_COMPONENTSTATE).append(",")
                    .append("f.").append(ComponentStateVo.FIELD_LATITUDE).append(",")
                    .append("f.").append(ComponentStateVo.FIELD_LONGITUDE).append(" ")
                    .append(" from ").append(ComponentStateVo.TABLE_NAME).append(" f, deviceprofile p")
                    .append(" where ").append("f.devicetype = p.devicetype")
//                    .append(" where ").append(ComponentStateVo.FIELD_COMPONENT).append(" is not null ")
                    .toString();
    
    private final int pageSize;
    private final int pageNum;
    public GetAllComponentStateWithDeviceDescrSpec() {
        this(-1, -1);
    }
    
    public GetAllComponentStateWithDeviceDescrSpec(int pageSize, int pageNum) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
    }
    
    //select  f.deviceid , p.devicedescr , f.messagetext , f.devicefailstate , f.latitude , f.longitude 
    //from v_device_fail f, deviceprofile p, (select deviceid from device order by deviceid OFFSET 10 ROWS FETCH NEXT 5 ROWS ONLY) as d where  
    //f.devicetype = p.devicetype and d.deviceid = f.deviceid;
    private String getCompleteSql() {
        String devicePaginatedSql;
        IPaginator paginator = new Mssql2012Paginator.Builder()
                .setSql("select deviceid from device")
                .setOrderByColumn("deviceid")
                .setPageNumber(pageNum)
                .setPageSize(pageSize)
                .build();
        devicePaginatedSql = paginator.getPaginatedSql();
        
        String rawSql = new StringBuilder()
                .append("select ")
                    .append("f.").append(ComponentStateVo.FIELD_DEVICEID).append(",")
                    .append("p.").append(ComponentStateVo.FIELD_DEVICEDESCR).append(",")
                    .append("f.").append(ComponentStateVo.FIELD_COMPONENT).append(",")
                    .append("f.").append(ComponentStateVo.FIELD_COMPONENTSTATE).append(",")
                    .append("f.").append(ComponentStateVo.FIELD_LATITUDE).append(",")
                    .append("f.").append(ComponentStateVo.FIELD_LONGITUDE).append(" ")
                .append(" from ")
                    .append(ComponentStateVo.TABLE_NAME).append(" f,")
                    .append(" deviceprofile p,")
                    .append(" (").append(devicePaginatedSql).append(") as d")
                .append(" where ")
                    .append("f.devicetype = p.devicetype").append(" and ")
                    .append("d.deviceid = f.deviceid")
//                    .append(" where ").append(ComponentStateVo.FIELD_COMPONENT).append(" is not null ")
                .toString();
        
        return rawSql;
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        if (this.pageNum > 0 && this.pageSize > 0) {
            return getCompleteSql();
            
        } else {
            return SQL;
            
        }
    }

    @Override
    public Object[] getSqlParams() {
        return null;
    }
    
}
