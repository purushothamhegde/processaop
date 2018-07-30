package com.philips.processaop.service;

import java.security.Security;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.philips.processaop.dao.ProcessAOPDAO;
import com.philips.processaop.dao.ProcessAOPDAOImpl;
import com.philips.processaop.model.AOPDetails;
import com.philips.processaop.model.ActiveArticleGroup;
import com.philips.processaop.model.ActiveBusinessGroup;
import com.philips.processaop.model.HierarchyDetails;
import com.philips.processaop.model.ActiveBusinessRecord;
import com.philips.processaop.model.ActiveMainArticleGroup;
import com.philips.processaop.model.ActiveProductDivision;
import com.philips.processaop.model.ActiveSector;
import com.philips.processaop.model.NextVersion;
import com.philips.processaop.model.ParentChildInfo;
import com.philips.processaop.model.ProductTreeRoot;
import com.siperian.common.util.StringUtil;
import com.siperian.sif.client.SiperianClient;
import com.siperian.sif.message.Field;
import com.siperian.sif.message.Password;
import com.siperian.sif.message.Record;
import com.siperian.sif.message.RecordKey;
import com.siperian.sif.message.SiperianObjectType;
import com.siperian.sif.message.hm.AddRelationshipRequest;
import com.siperian.sif.message.hm.AddRelationshipResponse;
import com.siperian.sif.message.hm.DeleteRelationshipRequest;
import com.siperian.sif.message.hm.DeleteRelationshipResponse;
import com.siperian.sif.message.mrm.ExecuteBatchDeleteRequest;
import com.siperian.sif.message.mrm.ExecuteBatchDeleteResponse;
import com.siperian.sif.message.mrm.ExecuteBatchRecalculateBoRequest;
import com.siperian.sif.message.mrm.ExecuteBatchRecalculateBoResponse;
import com.siperian.sif.message.mrm.PutRequest;
import com.siperian.sif.message.mrm.PutResponse;

@Service
public class ProcessAOPServiceImpl implements ProcessAOPService {

    private static Logger log = LoggerFactory.getLogger(ProcessAOPServiceImpl.class);

    @Autowired
    private ProcessAOPDAO processAOPDAO;

    @Autowired
    private SiperianClient sipClient;

    @Value("${sipclient.username}")
    private String sipClientUser;

    @Value("${sipclient.password}")
    private String sipClientpassword;

    
    @Value("${sipclient.encrypted.password}")
    private String sipClientEncryptedPassword;

    public ProcessAOPServiceImpl() {
        Security.setProperty("ssl.SocketFactory.provider", "");
        Security.setProperty("ssl.ServerSocketFactory.provider", "");
        System.setProperty("https.protocols", "TLSv1.2, TLSv1.1");
    }

    private void UpdateBOfromXref(String baseObjectName) {

        try {

        	
            ExecuteBatchRecalculateBoRequest executeBatchRecalculateRequest = new ExecuteBatchRecalculateBoRequest();
            executeBatchRecalculateRequest.setTableName(baseObjectName);
            executeBatchRecalculateRequest.setUsername(sipClientUser);

            Password password = new Password();

//            password.setPassword(StringUtil.blowfishDecrypt(sipClientEncryptedPassword));
            password.setPassword(sipClientpassword);
            password.setEncrypted(false);

            executeBatchRecalculateRequest.setPassword(password);

            ExecuteBatchRecalculateBoResponse executeBatchRecalculateResponse = (ExecuteBatchRecalculateBoResponse) sipClient
                    .process(executeBatchRecalculateRequest);

            log.info("ExecuteBatchRecalculateBoResponse : " + executeBatchRecalculateResponse.getMessage());

        } catch (Exception e) {
            log.info("Error in ExecuteBatchRecalculateBoResponse : " + e.getMessage(), e);
            throw new RuntimeException("Error in UpdateBOfromXref : " + e.getMessage());
        }
    }

    
    
    @Override
    public void doProductTreeAOP() {

    	
    	//1. Let us get all the Future records from XREF to BO.
    	
    	log.info("Transfer the Records from XREF to BO based on Effective Start Dates.");
    	
    	log.info("Transfering Business Records...");
    	UpdateBOfromXref("C_BUSINESS");
    	log.info("Transfering Business Group Records...");
    	UpdateBOfromXref("C_BUSINESS_GROUP");
    	log.info("Transfering Main Article Group Records...");
    	UpdateBOfromXref("C_MAIN_ARTICLE_GROUP");
    	log.info("Transfering Article Group Records...");
    	UpdateBOfromXref("C_ARTICLE_GROUP");
    	
    	
    	log.info("Now update the Version in all the Product Tree Objects to Version of Next Year.");    	
    	
        NextVersion nextVersion = processAOPDAO.getNextVersion();

        List<ActiveSector> activesectorList = processAOPDAO
                .getActiveSectorRecordList();

        log.info("Starting update version for Sector to :" + nextVersion.getVersion());
        
        for (ActiveSector activesector : activesectorList) {

        	
            updateProductTreeVersion("C_SECTOR", "FOR",
                    activesector.getRowidObject(), nextVersion.getVersion(), "", "", "",null,null, "");
        }

        log.info("Starting update version for Product Division");
        
        List<ActiveProductDivision> activeproductdivisionList = processAOPDAO
                .getActiveProductDivisionList();

        for (ActiveProductDivision activeproductdivison : activeproductdivisionList) {

            updateProductTreeVersion("C_PRODUCT_DIVISION", "FOR",
            		activeproductdivison.getRowidObject(), nextVersion.getVersion(), "", "", "",null,null, "");
        }
        
        log.info("Starting update version for Business");
        
        List<ActiveBusinessRecord> activeBusinessRecordList = processAOPDAO
                .getActiveBusinessRecordList();

        for (ActiveBusinessRecord activeBusinessRecord : activeBusinessRecordList) {

            updateProductTreeVersion("C_BUSINESS", activeBusinessRecord.getRowidSystem(),
                    activeBusinessRecord.getRowidObject(), nextVersion.getVersion(), "", "", "",
                    activeBusinessRecord.getPeriodStartDate(),
                    activeBusinessRecord.getPeridoEndDate(), activeBusinessRecord.getRowidXref());
        }

        log.info("Starting update version for Business Group");

        List<ActiveBusinessGroup> activeBusinessgroupRecordList = processAOPDAO
                .getActiveBusinessGroupList();

        for (ActiveBusinessGroup activeBusinessgroup : activeBusinessgroupRecordList) {

            updateProductTreeVersion("C_BUSINESS_GROUP", activeBusinessgroup.getRowidSystem(),
            		activeBusinessgroup.getRowidObject(), nextVersion.getVersion(), "", "", "",
            		activeBusinessgroup.getPeriodStartDate(),
            		activeBusinessgroup.getPeridoEndDate(), activeBusinessgroup.getRowidXref());
        }

        log.info("Starting update version for Main Article Group");
        
        List<ActiveMainArticleGroup> activeMainArticleGroupList = processAOPDAO
                .getActiveMainArticleGroupList();

        for (ActiveMainArticleGroup activeMainArticleGroup : activeMainArticleGroupList) {

            updateProductTreeVersion("C_MAIN_ARTICLE_GROUP", activeMainArticleGroup.getRowidSystem(),
            		activeMainArticleGroup.getRowidObject(), nextVersion.getVersion(), "", "", "",
            		activeMainArticleGroup.getPeriodStartDate(),
            		activeMainArticleGroup.getPeridoEndDate(), activeMainArticleGroup.getRowidXref());
        }
        
        
        log.info("Starting update version for Article Group");
        
        List<ActiveArticleGroup> activeArticleGroupList = processAOPDAO
                .getActiveArticleGroupList();

        for (ActiveArticleGroup activeArticleGroup : activeArticleGroupList) {

            updateProductTreeVersion("C_ARTICLE_GROUP", activeArticleGroup.getRowidSystem(),
            		activeArticleGroup.getRowidObject(), nextVersion.getVersion(), "", "", "",
            		activeArticleGroup.getPeriodStartDate(),
            		activeArticleGroup.getPeridoEndDate(), activeArticleGroup.getRowidXref());
        }
        
        
        log.info("Change the Current Year Flag in Product Tree and change LC =3 ");
        
        ProductTreeRoot currentpt = new ProcessAOPDAOImpl().getCurrentYearProductTree();
        updateProductTreeVersion("C_PRODUCT_TREE", "FOR",currentpt.getRowidObject(), "", "N", "Y", "2",null,null, "");
        
        log.info("Change the Future Year Flag to Current and change LC =2 in Product Tree");
        
        ProductTreeRoot nextpt = new ProcessAOPDAOImpl().getNextYearProductTree();
        updateProductTreeVersion("C_PRODUCT_TREE", "FOR",currentpt.getRowidObject(), "", "Y", "N", "3",null,null, "");
        
            
    }
    
    

    @Override
    public void doProcessing(AOPDetails aopDetails) {

        // Delete the current AOP Relationships

        List<HierarchyDetails> currentAOPHierarchyList = processAOPDAO.getHierarchyDetailsList(
                aopDetails.getBaseObjectName(), aopDetails.getAopHierarchyCode(), false);

        for (HierarchyDetails currentAOPHierarchy : currentAOPHierarchyList) {

            deleteRelationship(currentAOPHierarchy);

        }

        // Hard delete anything with Hub State -1.

        processAOPDAO.createBatchDeleteEntries(aopDetails.getBaseObjectName(),
                aopDetails.getAopHierarchyCode());

        log.info("Starting Batch Delete for cleaning old deleted records");

        
        if (processAOPDAO.getRecordCount("C_L_AOP_BATCH_DELETE")>0) {
        
        	truncateOldRelationship("C_L_AOP_BATCH_DELETE", aopDetails.getBaseObjectName());

            log.info("Completed Batch Delete for cleaning old deleted records");

        }
        
        processAOPDAO.TruncateData("C_L_AOP_BATCH_DELETE");
        
        // Get the existing Relationships

        List<HierarchyDetails> currentLiveHierarchyList = processAOPDAO.getHierarchyDetailsList(
                aopDetails.getBaseObjectName(), aopDetails.getCurrentHierarchyCode(), true);

        for (HierarchyDetails currentLiveHierarchy : currentLiveHierarchyList) {
            String sROWIDXREF="";
			String sRel_Type_Code="";
			String system="";
		
			sROWIDXREF=currentLiveHierarchy.getRowidXref();
			sRel_Type_Code=currentLiveHierarchy.getRelType();
			system=currentLiveHierarchy.getRowidSystem().trim();
			Date startdate=currentLiveHierarchy.getPeriodStartDate();
			


			Record myrecord= new Record();
			RecordKey myreckey = new RecordKey();
			RecordKey myBO1recordkey =new RecordKey();
			RecordKey myBO2recordkey=new RecordKey();
			
			Field f1= new Field();
			f1.setName("HIERARCHY_LEVEL");
			f1.setValue(currentLiveHierarchy.getHierarchyLevel());
			myrecord.setField(f1);
			
			Field f2= new Field();
			f2.setName("LINE_NUMBER");
			f2.setValue(currentLiveHierarchy.getLineNumber());
			myrecord.setField(f2);
			

			Field f3= new Field();
			f3.setName("PARENT_NODE");
			f3.setValue(currentLiveHierarchy.getParentNode());
			myrecord.setField(f3);
			
			Field f4= new Field();
			f4.setName("CHILD_NODE");
			f4.setValue(currentLiveHierarchy.getChildNode());
			myrecord.setField(f4);
			
			myreckey.setSystemName(currentLiveHierarchy.getRowidSystem().trim());
			myreckey.setSourceKey(currentLiveHierarchy.getPkeySrcObject().replace(aopDetails.getCurrentHierarchyCode(),aopDetails.getAopHierarchyCode()));
			
			Date enddate= currentLiveHierarchy.getPeriodEndDate();		
			
			String ParentTable="";
			String ChildTable="";
			String RelTable="";
			if (sRel_Type_Code.equals("MRUGRP-MRU")){
				ParentTable="C_MAN_REPORTING_UNIT_GRP";
				ChildTable="C_MAN_REPORTING_UNIT";
				RelTable="C_MAN_REP_UNIT_GROUPING";
			}
			else if(sRel_Type_Code.equals("MRUGRP-MRUGRP")){

				ParentTable="C_MAN_REPORTING_UNIT_GRP";
				ChildTable="C_MAN_REPORTING_UNIT_GRP";
				RelTable="C_MAN_REP_UNIT_GRP_GRPNG";
			}

			else if(sRel_Type_Code.equals("ORUGRP-ORU")){
				ParentTable="C_ORG_REP_UNIT_GROUP";
				ChildTable="C_ORG_REP_UNIT";
				RelTable="C_ORG_REP_UNIT_GROUPING";
			}	

			else if(sRel_Type_Code.equals("ORUGRP-ORUGRP")){
				ParentTable="C_ORG_REP_UNIT_GROUP";
				ChildTable="C_ORG_REP_UNIT_GROUP";
				RelTable="C_ORU_GROUP_GROUPING";
			}
			
			else if(sRel_Type_Code.equals("CCH-CCH")){
				ParentTable="C_COST_CENTER_HIERARCHY";
				ChildTable="C_COST_CENTER_HIERARCHY";
				RelTable="C_COS_CEN_HIE_GROUPING";
			}

			ParentChildInfo parentChildInfo = processAOPDAO.getParentChildInfo(RelTable, ParentTable,
					ChildTable, currentLiveHierarchy.getRowidObject());

			
			myBO1recordkey.setRowid(parentChildInfo.getParentRowidObject());
			myBO2recordkey.setRowid(parentChildInfo.getChildRowidObject());

			AddRelationship(aopDetails.getAopHierarchyCode(), myrecord, myreckey, myBO1recordkey, myBO2recordkey, sRel_Type_Code,startdate,enddate);
			

        }

    }

    @Override
    public void lockAOPRecord(String rowidObject) {

        PutRequest putRequest = new PutRequest();
        Record putRecord = new Record();
        RecordKey putRecordKey = new RecordKey();

        putRecord.setSiperianObjectUid(SiperianObjectType.BASE_OBJECT.makeUid("C_AOP_CONTROLLER"));
        log.info("Inside Lock AOP Record");

        putRecordKey.setSystemName("FOR");
        putRecordKey.setRowid(rowidObject);

        addStringField(putRecord, "LOCKED", "Y");

        putRequest.setRecordKey(putRecordKey);
        putRequest.setRecord(putRecord);

        putRequest.setUsername(sipClientUser);

        Password password = new Password();

        //password.setPassword(StringUtil.blowfishDecrypt(sipClientEncryptedPassword));
        password.setPassword(sipClientpassword);
        password.setEncrypted(false);

        putRequest.setPassword(password);

        try {
            PutResponse response = (PutResponse) sipClient.process(putRequest);
            log.info("PutResponse : " + response.getMessage());

        } catch (Exception e) {
            log.info("Error in PutRequest : " + e.getMessage(), e);
            throw new RuntimeException("Error in PutRequest : " + e.getMessage());
        }
    }

    private void updateProductTreeVersion(String baseObjectName, String rowidSystem,
            String rowidObject, String version, String isCurrentYear, String isNextYear,
            String lifecyclePhase, Date periodStartDate, Date periodEndDate, String rowidXref) {

        PutRequest putRequest = new PutRequest();
        Record putRecord = new Record();
        RecordKey putRecordKey = new RecordKey();

        putRecord.setSiperianObjectUid(SiperianObjectType.BASE_OBJECT.makeUid(baseObjectName));

        log.info("Inside UpdateProductTreeVersion Method");

        log.info("UpdateProductTreeVersion.rowidSystem::" + rowidSystem.trim());
        log.info("UpdateProductTreeVersion.BaseObjectName::" + baseObjectName);
        log.info("UpdateProductTreeVersion.ROWID_OBJECT::" + rowidObject);
        log.info("UpdateProductTreeVersion.Version::" + version);
        log.info("UpdateProductTreeVersion.Period Start date::" + periodStartDate);
        log.info("UpdateProductTreeVersion.Period End date::" + periodEndDate);

        putRecordKey.setSystemName(rowidSystem.trim());

        log.info("Before SetROWID");
        
        putRecordKey.setRowid(rowidObject);

        
        log.info("Before rowidXref");
        
        if (!("".equals(rowidXref.trim()))) {
            log.info("RowId Xref::" + rowidXref);
            putRecordKey.setRowidXref(rowidXref);
        }

        
        log.info("Before Add Version");
        
        if (!("".equals(version.trim()))) {
        	log.info("Add Version");
            
            addStringField(putRecord, "VERSION", version);        	
        }
        
        
        
        if (!("".equals(isCurrentYear.trim()))) {
        	log.info("Current Year");
            addStringField(putRecord, "IS_CURRENT_YEAR", isCurrentYear);        	
        }
        
        

        
        if (!("".equals(isNextYear.trim()))) {
            log.info("Next Year");
        	addStringField(putRecord, "IS_NEXT_YEAR", isNextYear);        	
        }
        
        
        if (!("".equals(lifecyclePhase.trim()))) {
            log.info("lifecyclePhase");
        	addStringField(putRecord, "LIFE_CYCLE_PHASE", lifecyclePhase);        	
        }
        
        

        if (periodStartDate != null) {
            putRequest.setPeriodStartDate(periodStartDate);

            log.info("Request Start Date ::" + putRequest.getPeriodStartDate());

        }

        if (periodEndDate != null) {
            putRequest.setPeriodEndDate(periodEndDate);
            log.info("Request End Date ::" + putRequest.getPeriodEndDate());
        }

        putRequest.setRecordKey(putRecordKey);
        putRequest.setRecord(putRecord);

        putRequest.setUsername(sipClientUser);

        Password password = new Password();

        //password.setPassword(StringUtil.blowfishDecrypt(sipClientEncryptedPassword));
        password.setPassword(sipClientpassword);
        
        password.setEncrypted(false);

        putRequest.setPassword(password);

        try {
            PutResponse putResponse = (PutResponse) sipClient.process(putRequest);
            log.info("PUT Response" + putResponse.getMessage());

        } catch (Exception e) {
            log.info("Error in PutRequest : " + e.getMessage(), e);
            throw new RuntimeException("Error in PutRequest : " + e.getMessage());
        }
    }

    private void addStringField(Record record, String columnName, String columnValue) {

        if (columnValue != null) {
            record.setField(new Field(columnName, columnValue.trim()));
        }
    }

    private void deleteRelationship(HierarchyDetails aopHierarchy) {

        log.info("Inside SIF Call DeleteRelationshipRequest:");
        DeleteRelationshipRequest deleteRelationshipRequest = new DeleteRelationshipRequest();

        RecordKey deleteRelationshipRecordKey = new RecordKey();
        deleteRelationshipRecordKey.setRowidXref(aopHierarchy.getRowidXref());
        deleteRelationshipRecordKey.setSystemName(aopHierarchy.getRowidSystem());
        deleteRelationshipRequest.setRecordKey(deleteRelationshipRecordKey);

        deleteRelationshipRequest
                .setRelTypeUid("HM_RELATIONSHIP_TYPE.".concat(aopHierarchy.getRelType()));

        deleteRelationshipRequest.setHmConfigurationUid("HM_CONFIGURATION.Default|Master");

        deleteRelationshipRequest.setUsername(sipClientUser);

        Password password = new Password();

        //password.setPassword(StringUtil.blowfishDecrypt(sipClientEncryptedPassword));
        password.setPassword(sipClientpassword);
        password.setEncrypted(false);

        deleteRelationshipRequest.setPassword(password);

        try {
            DeleteRelationshipResponse deleteRelationshipResponse = (DeleteRelationshipResponse) sipClient
                    .process(deleteRelationshipRequest);
            log.info("DeleteRelationshipResponse : " + deleteRelationshipResponse.getMessage());

        } catch (Exception e) {
            log.info("Error in DeleteRelationshipRequest : " + e.getMessage(), e);
            throw new RuntimeException("Error in DeleteRelationshipRequest : " + e.getMessage());
        }
    }

    private void truncateOldRelationship(String tempDeleteTableName, String baseObjectName) {

        try {

        	
            ExecuteBatchDeleteRequest executeBatchDeleteRequest = new ExecuteBatchDeleteRequest();
            executeBatchDeleteRequest.setTableName(baseObjectName);
            executeBatchDeleteRequest.setSourceTableName(tempDeleteTableName);

            executeBatchDeleteRequest.setUsername(sipClientUser);

            Password password = new Password();

//            password.setPassword(StringUtil.blowfishDecrypt(sipClientEncryptedPassword));
            password.setPassword(sipClientpassword);
            password.setEncrypted(false);

            executeBatchDeleteRequest.setPassword(password);

            ExecuteBatchDeleteResponse executeBatchDeleteResponse = (ExecuteBatchDeleteResponse) sipClient
                    .process(executeBatchDeleteRequest);

            log.info("ExecuteBatchDeleteResponse : " + executeBatchDeleteResponse.getMessage());

        } catch (Exception e) {
            log.info("Error in ExecuteBatchDeleteRequest : " + e.getMessage(), e);
            throw new RuntimeException("Error in ExecuteBatchDeleteRequest : " + e.getMessage());
        }
    }

    
    
    
       
    
    
	public void AddRelationship(String shierarchyUid,Record myrecord, RecordKey myrecordkey, RecordKey myBO1recordkey,RecordKey myBO2recordkey,String s_REL_TYPE_CODE, Date startDate, Date enddate){
		
		AddRelationshipRequest request2 = new AddRelationshipRequest();

		request2.setHierarchyUid("HM_HIERARCHY."+ shierarchyUid);
		request2.setRelTypeUid("HM_RELATIONSHIP_TYPE.".concat(s_REL_TYPE_CODE));
		request2.setHmConfigurationUid("HM_CONFIGURATION.Default|Master");
		request2.setStartDate(startDate);
		request2.setEndDate(enddate);
		request2.setRecordKey(myrecordkey);
		request2.setRecord(myrecord);
		request2.setBo1RecordKey(myBO1recordkey);
		request2.setBo2RecordKey(myBO2recordkey);
		request2.setGenerateSourceKey(false);

		request2.setUsername(sipClientUser);

        Password password = new Password();

        //password.setPassword(StringUtil.blowfishDecrypt(sipClientEncryptedPassword));
        password.setPassword(sipClientpassword);
        password.setEncrypted(false);

        request2.setPassword(password);

        

		try
		{	log.info("Inside SIF Call AddRelationship before SIF call:");
		AddRelationshipResponse response2 = (AddRelationshipResponse) sipClient.process(request2);
		log.info("AddRelationshipResponse" + response2.getMessage());

		}

		catch(Exception e)
		{
			log.info("Error in Add Relationship " + e.getMessage());
		}	

	}
    
    
}
