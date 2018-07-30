package com.philips.processaop.model;

import java.util.Date;

public class NextVersion extends LoggingObject {
    
    private String version;
    private String id;
    private Date validFrom;
    
    public String getVersion() {
        return version;
    }
    
    public void setVersion(String version) {
        this.version = version;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public Date getValidFrom() {
        return validFrom;
    }
    
    public void setValidFrom(Date validFrom) {
        this.validFrom = validFrom;
    }

}
