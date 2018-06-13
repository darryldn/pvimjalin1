/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.telegram.repo.spec;

import id.dni.pvim.ext.repo.db.spec.ISqlSpecification;
import id.dni.pvim.ext.telegram.repo.db.vo.TelegramSubscriberVo;
import id.dni.pvim.ext.web.in.Commons;

/**
 *
 * @author darryl.sulistyan
 */
public class TelegramSubscribersByListOfChatIDSpec implements ISqlSpecification {

    private static final String SQL_SELECT_PREFIX = 
            new StringBuilder()
                    .append("SELECT ").append(TelegramSubscriberVo.FIELD_SUBS_ID).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_CHAT_ID).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_PHONE_NUM).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_LASTUPDATE).append(", ")
                                      .append(TelegramSubscriberVo.FIELD_PASSKEY).append(" ")
                    .append("FROM ")  .append(TelegramSubscriberVo.TABLE_NAME).append(" ")
                    .append("WHERE ") .append(TelegramSubscriberVo.FIELD_CHAT_ID).append(" in ")
            .toString();
    
    private final String sqlQuery;
    private final long[] chatID;
    
    public TelegramSubscribersByListOfChatIDSpec(long[] chatIDs) {
        this.chatID = chatIDs;
        StringBuilder sb = new StringBuilder(SQL_SELECT_PREFIX);
        sb.append("(");
        if (chatIDs != null) {
            sb.append(Commons.concatQuestionMarks(chatIDs.length));
        }
        sb.append(")");
        this.sqlQuery = sb.toString();
    }
    
    @Override
    public String toParameterizedSqlQuery() {
        return sqlQuery;
    }

    @Override
    public Object[] getSqlParams() {
        Object[] o = new Object[chatID.length];
        for (int i=0; i<o.length; ++i) {
            o[i] = chatID[i];
        }
        return o;
    }
    
}
