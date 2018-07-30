package com.philips.processaop.controller;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.philips.processaop.dao.ProcessAOPDAO;
import com.philips.processaop.dao.ProcessAOPDAOImpl;
import com.philips.processaop.model.AOPDetails;
import com.philips.processaop.service.ProcessAOPService;
import com.philips.processaop.service.ProcessAOPServiceImpl;

import org.apache.commons.lang.StringUtils;

@Service
public class ProcessAOPControllerImpl implements ProcessAOPController {

    private static Logger log = LoggerFactory.getLogger(ProcessAOPControllerImpl.class);

    @Autowired
    private ProcessAOPDAO processAOPDAO;

    @Autowired
    private ProcessAOPService processAOPService;

    @Override
    public void processAOP() throws Exception {

    	System.out.println("Inside ProcessAOP in Controller Impl");
        List<AOPDetails> aopDetailsList = processAOPDAO.getAOPDetailsList();
    	
    	
        for (AOPDetails aopDetails : aopDetailsList) {

            if (StringUtils.equals(aopDetails.getAopStage().trim(), "PRE")) {

                processAOPService.doProcessing(aopDetails);
                processAOPService.lockAOPRecord(aopDetails.getRowidObject());

            } else if (StringUtils.equals(aopDetails.getAopStage().trim(), "POST")) {

                if (StringUtils.equals(aopDetails.getBaseObjectName().trim(), "PRODUCT_TREE")) {
                    log.info("Inside AOP Post processing for Product Tree");

                    processAOPService.doProductTreeAOP();

                } else {

                    processAOPService.doProcessing(aopDetails);

                }

                processAOPService.lockAOPRecord(aopDetails.getRowidObject());
            }
        }
    }
}
