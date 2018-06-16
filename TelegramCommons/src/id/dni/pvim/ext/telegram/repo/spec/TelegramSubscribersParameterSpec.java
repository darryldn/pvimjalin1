/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo.spec;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberParameterVo;

/**
 *
 * @author darryl.sulistyan
 */
public class TelegramSubscribersParameterSpec implements ISqlSpecification {

    private static final String SQL = 
            new StringBuilder()
            .append("select ")
                .append(TelegramSubscriberParameterVo.FIELD_PAR_NAME).append(", ")
                .append(TelegramSubscriberParameterVo.FIELD_PAR_VALUE).append(", ")
                .append(TelegramSubscriberParameterVo.FIELD_LASTUPDATE).append(" ")
            .append("from ").append(TelegramSubscriberParameterVo.TABLE_NAME).append(" ")
            .append("where ").append(TelegramSubscriberParameterVo.FIELD_PAR_NAME).append("=?")
            .toString();
    
    private final String parameterName;
    public TelegramSubscribersParameterSpec(String parameterName) {
        this.parameterName = parameterName;
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        return SQL;
    }

    @Override
    public Object[] getSqlParams() {
        return new Object[]{parameterName};
    }
    
}
