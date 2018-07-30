package com.philips.processaop.model;

public class ParentChildInfo extends LoggingObject {
    
    private String parentRowidObject;
    private String childRowidObject;
    
    public String getParentRowidObject() {
        return parentRowidObject;
    }
    
    public void setParentRowidObject(String parentRowidObject) {
        this.parentRowidObject = parentRowidObject;
    }
    
    public String getChildRowidObject() {
        return childRowidObject;
    }
    
    public void setChildRowidObject(String childRowidObject) {
        this.childRowidObject = childRowidObject;
    }
}
