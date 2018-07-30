package com.philips.processaop.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

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

@Repository
public class ProcessAOPDAOImpl implements ProcessAOPDAO {

    private static Logger log = LoggerFactory.getLogger(ProcessAOPDAOImpl.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<AOPDetails> getAOPDetailsList() {

        String sql = "SELECT BASE_OBJECT_NAME, CURRENT_HIERARCHY_CODE, AOP_HIERARCHY_CODE, AOP_STAGE,ROWID_OBJECT FROM C_AOP_CONTROLLER WHERE LOCKED = 'N'";

        return this.jdbcTemplate.query(sql, new RowMapper<AOPDetails>() {

            public AOPDetails mapRow(ResultSet rs, int rowNum) throws SQLException {

                AOPDetails aopDetails = new AOPDetails();

                aopDetails.setBaseObjectName(rs.getString("BASE_OBJECT_NAME"));
                aopDetails.setCurrentHierarchyCode(rs.getString("CURRENT_HIERARCHY_CODE"));
                aopDetails.setAopHierarchyCode(rs.getString("AOP_HIERARCHY_CODE"));
                aopDetails.setAopStage(rs.getString("AOP_STAGE"));
                aopDetails.setRowidObject(rs.getString("ROWID_OBJECT"));

                return aopDetails;
            }
        });
    }

    @Override
    public NextVersion getNextVersion() {

        String sql = "SELECT VERSION, ID, VALID_FROM FROM FOR_ORS.C_PRODUCT_TREE WHERE IS_NEXT_YEAR = 'Y'";

        return this.jdbcTemplate.queryForObject(sql, new RowMapper<NextVersion>() {

            public NextVersion mapRow(ResultSet rs, int rowNum) throws SQLException {

                NextVersion nextVersion = new NextVersion();

                nextVersion.setVersion(rs.getString("VERSION"));
                nextVersion.setId(rs.getString("ID"));
                nextVersion.setValidFrom(rs.getDate("VALID_FROM"));

                return nextVersion;
            }
        });
    }

    @Override
    public Long getRecordCount(String sTableName) {

        String sql = "SELECT COUNT(*) CNT FROM FOR_ORS."+ sTableName ;

        return this.jdbcTemplate.queryForObject(sql, new RowMapper<Long>() {

            public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getLong("CNT");
            }
        });
    }

    
    @Override
    public List<ActiveBusinessRecord> getActiveBusinessRecordList() {
    	

        String sql = " SELECT OUTER_BU.ROWID_SYSTEM ROWID_SYSTEM, OUTER_BU.ROWID_OBJECT ROWID_OBJECT, OUTER_BU.PERIOD_START_DATE PERIOD_START_DATE, OUTER_BU.PERIOD_END_DATE PERIOD_END_DATE, OUTER_BU.ROWID_XREF ROWID_XREF "
                +    " FROM C_BUSINESS_XREF OUTER_BU INNER JOIN C_BUSINESS ON (C_BUSINESS.ROWID_OBJECT=OUTER_BU.ROWID_OBJECT AND C_BUSINESS.LAST_ROWID_SYSTEM=OUTER_BU.ROWID_SYSTEM) "
                + 	  " WHERE OUTER_BU.SRC_LUD = (SELECT MAX(SRC_LUD ) FROM C_BUSINESS_XREF INNER_BU WHERE OUTER_BU.ROWID_OBJECT = INNER_BU.ROWID_OBJECT AND OUTER_BU.ROWID_SYSTEM = INNER_BU.ROWID_SYSTEM ) AND OUTER_BU.HUB_STATE_IND = 1 ";

        return this.jdbcTemplate.query(sql, new RowMapper<ActiveBusinessRecord>() {

            public ActiveBusinessRecord mapRow(ResultSet rs, int rowNum) throws SQLException {

                ActiveBusinessRecord activeBusinessRecord = new ActiveBusinessRecord();

                activeBusinessRecord.setRowidSystem(rs.getString("ROWID_SYSTEM"));
                activeBusinessRecord.setRowidObject(rs.getString("ROWID_OBJECT"));
                activeBusinessRecord.setPeriodStartDate(rs.getDate("PERIOD_START_DATE"));
                activeBusinessRecord.setPeridoEndDate(rs.getDate("PERIOD_END_DATE"));
                activeBusinessRecord.setRowidXref(rs.getString("ROWID_XREF"));

                return activeBusinessRecord;
            }
        });
    }

    @Override
    public void TruncateData(String baseObjectName) {
    	String sql = "DELETE FROM " + baseObjectName; 

        log.info("Query to Delete Temp table " + sql);

        this.jdbcTemplate.update(sql);
        
    	
    }
    
    @Override
    public List<HierarchyDetails> getHierarchyDetailsList(String baseObjectName,
            String hierarchyCode, boolean activeOnly) {

        String sql = "SELECT ROWID_XREF,ROWID_OBJECT,S_PARENT_1,S_PARENT_2,HIERARCHY_CODE,HIERARCHY_LEVEL,"
                + "REL_TYPE_CODE,PERIOD_START_DATE,PERIOD_END_DATE,ROWID_SYSTEM,PKEY_SRC_OBJECT, LINE_NUMBER,nvl(PARENT_NODE,' ')PARENT_NODE,nvl(CHILD_NODE,' ')CHILD_NODE FROM "
                + baseObjectName + "_XREF WHERE HIERARCHY_CODE='" + hierarchyCode + "' ";

        if (activeOnly) {
            sql += "AND HUB_STATE_IND = 1";
        }

        log.info("SQL : " + sql);

        return this.jdbcTemplate.query(sql, new RowMapper<HierarchyDetails>() {

            public HierarchyDetails mapRow(ResultSet rs, int rowNum) throws SQLException {

                HierarchyDetails aopHierarchy = new HierarchyDetails();

                aopHierarchy.setRowidXref(rs.getString("ROWID_XREF"));
                aopHierarchy.setRowidObject(rs.getString("ROWID_OBJECT"));
                aopHierarchy.setParent1(rs.getString("S_PARENT_1"));
                aopHierarchy.setParent2(rs.getString("S_PARENT_2"));
                aopHierarchy.setHierarchyCode(rs.getString("HIERARCHY_CODE"));
                aopHierarchy.setHierarchyLevel(rs.getString("HIERARCHY_LEVEL"));
                aopHierarchy.setRelType(rs.getString("REL_TYPE_CODE"));
                aopHierarchy.setPeriodStartDate(rs.getDate("PERIOD_START_DATE"));
                aopHierarchy.setPeriodEndDate(rs.getDate("PERIOD_END_DATE"));
                aopHierarchy.setRowidSystem(rs.getString("ROWID_SYSTEM"));
                aopHierarchy.setPkeySrcObject(rs.getString("PKEY_SRC_OBJECT"));
                aopHierarchy.setLineNumber(rs.getString("LINE_NUMBER"));
                aopHierarchy.setParentNode(rs.getString("PARENT_NODE"));
                aopHierarchy.setChildNode(rs.getString("CHILD_NODE"));

                return aopHierarchy;
            }
        });
    }

    @Override
    public void createBatchDeleteEntries(String baseObjectName, String toHierarchy) {

        String sql = "INSERT INTO C_L_AOP_BATCH_DELETE (ROWID_XREF,LAST_UPDATE_DATE) SELECT ROWID_XREF,SYSDATE FROM "
                + baseObjectName + "_XREF WHERE HUB_STATE_IND = -1 AND HIERARCHY_CODE = '"
                + toHierarchy + "'";

        log.info("Query to Insert into Temp table with XREF  :: " + sql);

        this.jdbcTemplate.update(sql);
    }

    
    
    
    
    @Override
    public ParentChildInfo getParentChildInfo(String relTable, String parentTable,
            String childTable, String rowidObject) {

        String sql = "SELECT PARENT.ROWID_OBJECT AS PARENT_ROWID_OBJECT, CHILD.ROWID_OBJECT AS CHILD_ROWID_OBJECT FROM "
                + relTable + "," + parentTable + " PARENT, " + childTable + " CHILD " + " WHERE "
                + relTable + ".HUB_STATE_IND = 1  AND " + relTable + ".ROWID_OBJECT='" + rowidObject
                + "' AND " + relTable + ".PARENT_1=PARENT.ROWID_OBJECT AND " + relTable
                + ".PARENT_2 = CHILD.ROWID_OBJECT ";
        
        log.info("ParentChildInfo Query ::" + sql);
        return this.jdbcTemplate.queryForObject(sql, new RowMapper<ParentChildInfo>() {

            public ParentChildInfo mapRow(ResultSet rs, int rowNum) throws SQLException {

                ParentChildInfo parentChildInfo = new ParentChildInfo();

                parentChildInfo.setParentRowidObject(rs.getString("PARENT_ROWID_OBJECT"));
                parentChildInfo.setChildRowidObject(rs.getString("CHILD_ROWID_OBJECT"));

                return parentChildInfo;
            }
        });
    }

	@Override
	public List<ActiveSector> getActiveSectorRecordList() {

        String sql = "SELECT ROWID_OBJECT FROM FOR_ORS.C_SECTOR WHERE HUB_STATE_IND =1 AND LIFE_CYCLE_PHASE IN ('2','3') ";
        		
        return this.jdbcTemplate.query(sql, new RowMapper<ActiveSector>() {

            public ActiveSector mapRow(ResultSet rs, int rowNum) throws SQLException {

            	ActiveSector activesector = new ActiveSector();
            	activesector.setRowidObject(rs.getString("ROWID_OBJECT"));
                return activesector;
            }
        });

	}

	@Override
	public List<ActiveProductDivision> getActiveProductDivisionList() {

		String sql = "SELECT ROWID_OBJECT FROM FOR_ORS.C_PRODUCT_DIVISION WHERE HUB_STATE_IND =1 AND LIFE_CYCLE_PHASE IN ('2','3')";
		
        return this.jdbcTemplate.query(sql, new RowMapper<ActiveProductDivision>() {

            public ActiveProductDivision mapRow(ResultSet rs, int rowNum) throws SQLException {

            	ActiveProductDivision activeproductdivision = new ActiveProductDivision();
            	activeproductdivision.setRowidObject(rs.getString("ROWID_OBJECT"));
                return activeproductdivision;
            }
        });
	}

	@Override
	public List<ActiveBusinessGroup> getActiveBusinessGroupList() {

		  String sql = " SELECT OUTER_BG.ROWID_SYSTEM ROWID_SYSTEM, OUTER_BG.ROWID_OBJECT ROWID_OBJECT, OUTER_BG.PERIOD_START_DATE PERIOD_START_DATE, OUTER_BG.PERIOD_END_DATE PERIOD_END_DATE, OUTER_BG.ROWID_XREF ROWID_XREF  "
	                 + " FROM C_BUSINESS_GROUP_XREF OUTER_BG  INNER JOIN C_BUSINESS_GROUP ON (OUTER_BG.ROWID_OBJECT=C_BUSINESS_GROUP.ROWID_OBJECT AND C_BUSINESS_GROUP.LAST_ROWID_SYSTEM= OUTER_BG.ROWID_SYSTEM) "
	                 + " WHERE SRC_LUD = (SELECT MAX(SRC_LUD ) FROM C_BUSINESS_GROUP_XREF INNER_BG WHERE OUTER_BG.ROWID_OBJECT=INNER_BG.ROWID_OBJECT AND OUTER_BG.ROWID_SYSTEM =INNER_BG.ROWID_SYSTEM )AND OUTER_BG.HUB_STATE_IND=1  ";

	        return this.jdbcTemplate.query(sql, new RowMapper<ActiveBusinessGroup>() {

	            public ActiveBusinessGroup mapRow(ResultSet rs, int rowNum) throws SQLException {

	            	ActiveBusinessGroup activebusinessgrouprecord = new ActiveBusinessGroup();

	            	activebusinessgrouprecord.setRowidSystem(rs.getString("ROWID_SYSTEM"));
	            	activebusinessgrouprecord.setRowidObject(rs.getString("ROWID_OBJECT"));
	            	activebusinessgrouprecord.setPeriodStartDate(rs.getDate("PERIOD_START_DATE"));
	            	activebusinessgrouprecord.setPeridoEndDate(rs.getDate("PERIOD_END_DATE"));
	            	activebusinessgrouprecord.setRowidXref(rs.getString("ROWID_XREF"));

	                return activebusinessgrouprecord;
	            }
	        });
	  }

	@Override
	public List<ActiveMainArticleGroup> getActiveMainArticleGroupList() {

		  String sql = " SELECT OUTER_MAG.ROWID_SYSTEM, OUTER_MAG.ROWID_OBJECT, OUTER_MAG.PERIOD_START_DATE, OUTER_MAG.PERIOD_END_DATE, OUTER_MAG.ROWID_XREF  FROM C_MAIN_ARTICLE_GROUP_XREF OUTER_MAG INNER JOIN C_MAIN_ARTICLE_GROUP ON (C_MAIN_ARTICLE_GROUP.ROWID_OBJECT = OUTER_MAG.ROWID_OBJECT AND OUTER_MAG.ROWID_SYSTEM=C_MAIN_ARTICLE_GROUP.LAST_ROWID_SYSTEM) "
	                 + " WHERE SRC_LUD = (SELECT MAX(SRC_LUD ) FROM C_MAIN_ARTICLE_GROUP_XREF INNER_MAG WHERE OUTER_MAG.ROWID_OBJECT=INNER_MAG.ROWID_OBJECT AND OUTER_MAG.ROWID_SYSTEM =INNER_MAG.ROWID_SYSTEM ) AND OUTER_MAG.HUB_STATE_IND =1 ";
	                 

	        return this.jdbcTemplate.query(sql, new RowMapper<ActiveMainArticleGroup>() {

	            public ActiveMainArticleGroup mapRow(ResultSet rs, int rowNum) throws SQLException {

	            	ActiveMainArticleGroup activemainarticlegroup = new ActiveMainArticleGroup();

	            	activemainarticlegroup.setRowidSystem(rs.getString("ROWID_SYSTEM"));
	            	activemainarticlegroup.setRowidObject(rs.getString("ROWID_OBJECT"));
	            	activemainarticlegroup.setPeriodStartDate(rs.getDate("PERIOD_START_DATE"));
	            	activemainarticlegroup.setPeridoEndDate(rs.getDate("PERIOD_END_DATE"));
	            	activemainarticlegroup.setRowidXref(rs.getString("ROWID_XREF"));

	                return activemainarticlegroup;
	            }
	        });
	        
	}

	@Override
	public List<ActiveArticleGroup> getActiveArticleGroupList() {

		  String sql = " SELECT OUTER_AG.ROWID_SYSTEM ROWID_SYSTEM, OUTER_AG.ROWID_OBJECT ROWID_OBJECT, OUTER_AG.PERIOD_START_DATE PERIOD_START_DATE, OUTER_AG.PERIOD_END_DATE PERIOD_END_DATE, OUTER_AG.ROWID_XREF ROWID_XREF FROM C_ARTICLE_GROUP_XREF OUTER_AG INNER JOIN C_ARTICLE_GROUP ON (OUTER_AG.ROWID_OBJECT=C_ARTICLE_GROUP.ROWID_OBJECT AND OUTER_AG.ROWID_SYSTEM=C_ARTICLE_GROUP.LAST_ROWID_SYSTEM ) "
	                 + " WHERE SRC_LUD = (SELECT MAX(SRC_LUD ) FROM C_ARTICLE_GROUP_XREF INNER_AG WHERE OUTER_AG.ROWID_OBJECT=INNER_AG.ROWID_OBJECT AND OUTER_AG.ROWID_SYSTEM =INNER_AG.ROWID_SYSTEM ) AND OUTER_AG.HUB_STATE_IND =1  ";


	        return this.jdbcTemplate.query(sql, new RowMapper<ActiveArticleGroup>() {

	            public ActiveArticleGroup mapRow(ResultSet rs, int rowNum) throws SQLException {

	            	ActiveArticleGroup activearticlegroup = new ActiveArticleGroup();

	            	activearticlegroup.setRowidSystem(rs.getString("ROWID_SYSTEM"));
	            	activearticlegroup.setRowidObject(rs.getString("ROWID_OBJECT"));
	            	activearticlegroup.setPeriodStartDate(rs.getDate("PERIOD_START_DATE"));
	            	activearticlegroup.setPeridoEndDate(rs.getDate("PERIOD_END_DATE"));
	            	activearticlegroup.setRowidXref(rs.getString("ROWID_XREF"));

	                return activearticlegroup;
	            }
	        });
	        
	}

	@Override
	public ProductTreeRoot getCurrentYearProductTree() {

        String sql = "SELECT ROWID_OBJECT FROM FOR_ORS.C_PRODUCT_TREE WHERE HUB_STATE_IND =1 AND IS_CURRENT_YEAR='Y'";

        return this.jdbcTemplate.queryForObject(sql, new RowMapper<ProductTreeRoot>() {

            public ProductTreeRoot mapRow(ResultSet rs, int rowNum) throws SQLException {

            	ProductTreeRoot producttreeroot = new ProductTreeRoot();

            	producttreeroot.setRowidObject(rs.getString("ROWID_OBJECT"));

                return producttreeroot;
            }
        });
	}

	@Override
	public ProductTreeRoot getNextYearProductTree() {
        String sql = "SELECT ROWID_OBJECT FROM FOR_ORS.C_PRODUCT_TREE WHERE HUB_STATE_IND =1 AND IS_NEXT_YEAR='Y'";

        return this.jdbcTemplate.queryForObject(sql, new RowMapper<ProductTreeRoot>() {

            public ProductTreeRoot mapRow(ResultSet rs, int rowNum) throws SQLException {

            	ProductTreeRoot producttreeroot = new ProductTreeRoot();

            	producttreeroot.setRowidObject(rs.getString("ROWID_OBJECT"));

                return producttreeroot;
            }
        });

		
	}
}
