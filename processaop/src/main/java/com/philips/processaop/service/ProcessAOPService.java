package com.philips.processaop.service;

import com.philips.processaop.model.AOPDetails;

public interface ProcessAOPService {
    
    void doProductTreeAOP();
    
    void doProcessing(AOPDetails aopDetails);
    
    void lockAOPRecord(String rowidObject);
}
