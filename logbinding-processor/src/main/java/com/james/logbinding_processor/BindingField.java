package com.james.logbinding_processor;

import javax.lang.model.type.TypeMirror;

public class BindingField {

    private TypeMirror type;

    private int id;

    private String value;

    private String fieldName;

    public BindingField(TypeMirror type, int id, String value, String fieldName) {
        this.type = type;
        this.id = id;
        this.value = value;
        this.fieldName = fieldName;
    }

    public TypeMirror getType() {
        return type;
    }

    public void setType(TypeMirror type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }
}
