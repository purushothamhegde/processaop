package com.philips.processaop.model;

import java.util.Date;

public class HierarchyDetails extends LoggingObject {

    private String rowidXref;
    private String rowidObject;
    private String parent1;
    private String parent2;
    private String hierarchyCode;
    private String hierarchyLevel;
    private String relType;
    private Date periodStartDate;
    private Date periodEndDate;
    private String rowidSystem;
    private String pkeySrcObject;
    private String lineNumber;
    private String parentNode;
    public String getParentNode() {
		return parentNode;
	}

	public void setParentNode(String parentNode) {
		this.parentNode = parentNode;
	}

	public String getChildNode() {
		return childNode;
	}

	public void setChildNode(String childNode) {
		this.childNode = childNode;
	}

	private String childNode;
    
    public String getRowidXref() {
        return rowidXref;
    }

    public void setRowidXref(String rowidXref) {
        this.rowidXref = rowidXref;
    }

    public String getRowidObject() {
        return rowidObject;
    }

    public void setRowidObject(String rowidObject) {
        this.rowidObject = rowidObject;
    }

    public String getParent1() {
        return parent1;
    }

    public void setParent1(String parent1) {
        this.parent1 = parent1;
    }

    public String getParent2() {
        return parent2;
    }

    public void setParent2(String parent2) {
        this.parent2 = parent2;
    }

    public String getHierarchyCode() {
        return hierarchyCode;
    }

    public void setHierarchyCode(String hierarchyCode) {
        this.hierarchyCode = hierarchyCode;
    }

    public String getHierarchyLevel() {
        return hierarchyLevel;
    }

    public void setHierarchyLevel(String hierarchyLevel) {
        this.hierarchyLevel = hierarchyLevel;
    }

    public String getRelType() {
        return relType;
    }

    public void setRelType(String relType) {
        this.relType = relType;
    }

    public Date getPeriodStartDate() {
        return periodStartDate;
    }

    public void setPeriodStartDate(Date periodStartDate) {
        this.periodStartDate = periodStartDate;
    }

    public Date getPeriodEndDate() {
        return periodEndDate;
    }

    public void setPeriodEndDate(Date periodEndDate) {
        this.periodEndDate = periodEndDate;
    }

    public String getRowidSystem() {
        return rowidSystem;
    }

    public void setRowidSystem(String rowidSystem) {
        this.rowidSystem = rowidSystem;
    }

    public String getPkeySrcObject() {
        return pkeySrcObject;
    }

    public void setPkeySrcObject(String pkeySrcObject) {
        this.pkeySrcObject = pkeySrcObject;
    }

    public String getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(String lineNumber) {
        this.lineNumber = lineNumber;
    }
}
