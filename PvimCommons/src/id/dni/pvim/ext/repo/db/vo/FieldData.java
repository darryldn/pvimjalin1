/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package id.dni.pvim.ext.repo.db.vo;

/**
 *
 * @author darryl.sulistyan
 */
public class FieldData {
    // left empty now. Dunno what to fill
    
    public static class Builder {
        
        private String fieldName = null;
        private boolean partOfPk = false;
        private Object value = null;
        
        public Builder() {
        }
        
        public Builder setFieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }
        
        public Builder setPartOfPk(boolean p) {
            this.partOfPk = p;
            return this;
        }
        
        public Builder setValue(Object v) {
            this.value = v;
            return this;
        }
        
        public FieldData build() {
            FieldData f = new FieldData();
            f.fieldName = this.fieldName;
            f.isPartOfPk = this.partOfPk;
            f.value = this.value;
            
            return f;
        }
        
    }
    
    private String fieldName = null;
    private boolean isPartOfPk = false;
    private Object value = null;
    
    public String getFieldName() {
        return fieldName;
    }

    public boolean isIsPartOfPk() {
        return isPartOfPk;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
    
}
