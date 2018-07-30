package com.philips.processaop.dao;

import java.util.List;

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

public interface ProcessAOPDAO {

    List<AOPDetails> getAOPDetailsList();

    NextVersion getNextVersion();


    List<ActiveSector> getActiveSectorRecordList();
    List<ActiveProductDivision> getActiveProductDivisionList();
    List<ActiveBusinessRecord> getActiveBusinessRecordList();
    List<ActiveBusinessGroup> getActiveBusinessGroupList();
    List<ActiveMainArticleGroup> getActiveMainArticleGroupList();
    List<ActiveArticleGroup> getActiveArticleGroupList();    
    
    ProductTreeRoot getCurrentYearProductTree();
    ProductTreeRoot getNextYearProductTree();
    Long getRecordCount(String sTableName); 
    

    List<HierarchyDetails> getHierarchyDetailsList(String baseObjectName, String hierarchyCode,
            boolean activeOnly);

    void createBatchDeleteEntries(String baseObjectName, String toHierarchy);
    void TruncateData(String baseObjectName);
    
    
    ParentChildInfo getParentChildInfo(String relTable, String parentTable, String childTable,
            String rowidObject);

}
