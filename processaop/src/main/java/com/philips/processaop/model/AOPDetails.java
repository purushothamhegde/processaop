package com.philips.processaop.model;

public class AOPDetails extends LoggingObject {
    
    private String baseObjectName;
    private String currentHierarchyCode;
    private String aopHierarchyCode;
    private String aopStage;
    private String rowidObject;
    
    public String getBaseObjectName() {
        return baseObjectName;
    }
    
    public void setBaseObjectName(String baseObjectName) {
        this.baseObjectName = baseObjectName;
    }
    
    public String getCurrentHierarchyCode() {
        return currentHierarchyCode;
    }
    
    public void setCurrentHierarchyCode(String currentHierarchyCode) {
        this.currentHierarchyCode = currentHierarchyCode;
    }
    
    public String getAopHierarchyCode() {
        return aopHierarchyCode;
    }
    
    public void setAopHierarchyCode(String aopHierarchyCode) {
        this.aopHierarchyCode = aopHierarchyCode;
    }
    
    public String getAopStage() {
        return aopStage;
    }
    
    public void setAopStage(String aopStage) {
        this.aopStage = aopStage;
    }
    
    public String getRowidObject() {
        return rowidObject;
    }
    
    public void setRowidObject(String rowidObject) {
        this.rowidObject = rowidObject;
    }
}
