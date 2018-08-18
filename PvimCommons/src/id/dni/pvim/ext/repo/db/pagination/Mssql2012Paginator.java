/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.pagination;

/**
 *
 * @author darryl.sulistyan
 */
public class Mssql2012Paginator implements IPaginator {

    public static class Builder {
        
        private String sql;
        private String tblColumn;
        private int pageSize;
        private int pageNum;
        
        public Builder() {
            
        }
        
        public Builder setSql(String sql) {
            this.sql = sql;
            return this;
        }
        
        public Builder setOrderByColumn(String tblColumn) {
            this.tblColumn = tblColumn;
            return this;
        }
        
        public Builder setPageSize(int pageSize) {
            this.pageSize = pageSize;
            return this;
        }
        
        /**
         * pageNum is 1-based, remember this!
         * @param pageNum
         * @return 
         */
        public Builder setPageNumber(int pageNum) {
            this.pageNum = pageNum;
            return this;
        }
        
        public IPaginator build() {
            //  SELECT * FROM TICKET order by ticket_id OFFSET 0 ROWS FETCH NEXT 10 ROWS ONLY;
            StringBuilder sb = new StringBuilder(sql);
            sb.append(" ORDER BY ").append(this.tblColumn);
            
            if (pageSize <= 0 || pageNum <= 0) {
                sb.append(" OFFSET ").append("?").append(" ROWS");
                sb.append(" FETCH NEXT ").append("?").append(" ROWS ONLY");
            } else {
                sb.append(" OFFSET ").append(pageSize * (pageNum-1)).append(" ROWS");
                sb.append(" FETCH NEXT ").append(pageSize).append(" ROWS ONLY");
            }
            
            return new Mssql2012Paginator(sb.toString());
        }
        
    }
    
    private final String sql;
    
    private Mssql2012Paginator(String sql) {
        this.sql = sql;
    }
    
    @Override
    public String getPaginatedSql() {
        return sql;
    }
    
}
