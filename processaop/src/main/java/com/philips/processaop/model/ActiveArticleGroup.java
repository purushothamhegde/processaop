package com.philips.processaop.model;

import java.util.Date;

public class ActiveArticleGroup extends LoggingObject{
	private String rowidObject;
	private Date periodStartDate;
    private Date peridoEndDate;
    private String rowidXref;
    private String rowidSystem;
    
	public String getRowidObject() {
		return rowidObject;
	}
	public void setRowidObject(String rowidObject) {
		this.rowidObject = rowidObject;
	}
	public Date getPeriodStartDate() {
		return periodStartDate;
	}
	public void setPeriodStartDate(Date periodStartDate) {
		this.periodStartDate = periodStartDate;
	}
	public Date getPeridoEndDate() {
		return peridoEndDate;
	}
	public void setPeridoEndDate(Date peridoEndDate) {
		this.peridoEndDate = peridoEndDate;
	}
	public String getRowidXref() {
		return rowidXref;
	}
	public void setRowidXref(String rowidXref) {
		this.rowidXref = rowidXref;
	}
	public String getRowidSystem() {
		return rowidSystem;
	}
	public void setRowidSystem(String rowidSystem) {
		this.rowidSystem = rowidSystem;
	}
	

    
}
