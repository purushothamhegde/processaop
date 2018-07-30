package com.philips.processaop.main;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.philips.processaop.controller.ProcessAOPController;
import com.philips.processaop.controller.ProcessAOPControllerImpl;

@Component
public class ApplicationRunner implements CommandLineRunner {
    
    private static Logger log = LoggerFactory.getLogger(ApplicationRunner.class);

    @Autowired
    private ProcessAOPController processAOPController;
    
    @Override
    public void run(String... strings) throws Exception {
        
        processAOPController.processAOP();
    }
}
