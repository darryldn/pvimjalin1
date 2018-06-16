/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo.spec;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.telegram.repo.db.vo.SlmUserVo;

/**
 *
 * @author darryl.sulistyan
 */
public class SlmUserIsMobileExistSpec implements ISqlSpecification {

    private static final String SQL =
            new StringBuilder()
            .append("select 1 from ").append(SlmUserVo.TABLE_NAME)
            .append(" where ").append(SlmUserVo.FIELD_MOBILE).append("=?")
            .toString();
    private final String mobile;    
    
    public SlmUserIsMobileExistSpec(String mobile) {
        this.mobile = mobile;
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL;
    }

    @Override
    public Object[] getSqlParams() {
        return (Object[])(new String[]{mobile});
    }
    
}
