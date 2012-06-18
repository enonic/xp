package com.enonic.wem.core.jcr;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcAccountsRetriever
{
    private static final Logger LOG = LoggerFactory.getLogger( JdbcAccountsRetriever.class );

    public static final String USER_INFO_FIELDS_MAP = "FIELDSMAP";

    private JdbcTemplate jdbcTemplate;


    public void fetchUserStores( ImportDataCallbackHandler handler )
    {
        final String sql = "SELECT DOM_LKEY, DOM_BISDELETED, DOM_SNAME, DOM_BDEFAULTSTORE, DOM_SCONFIGNAME, DOM_XMLDATA " +
            "FROM TDOMAIN WHERE DOM_BISDELETED = 0";

        List<Map<String, Object>> userStoreRows = jdbcTemplate.queryForList( sql );
        LOG.info( userStoreRows.size() + " user stores retrieved." );

        for ( Map<String, Object> userStoreRow : userStoreRows )
        {
            handler.processDataEntry( userStoreRow );
        }
    }

    public void fetchUsers( ImportDataCallbackHandler handler )
    {
        final String sql = "SELECT USR_HKEY, USR_SUID, USR_SFULLNAME, USR_DTETIMESTAMP, USR_UT_LKEY, " +
            "USR_DOM_LKEY, USR_SSYNCVALUE, USR_SEMAIL, USR_SPASSWORD, USR_GRP_HKEY, USR_PHOTO, USR_BISDELETED FROM TUSER WHERE USR_BISDELETED = 0";

        List<Map<String, Object>> userRows = jdbcTemplate.queryForList( sql );
        LOG.info( userRows.size() + " users retrieved." );

        for ( Map<String, Object> userRow : userRows )
        {
            String userKey = (String) userRow.get( "USR_HKEY" );
            Map<String, Object> userFields = fetchUserFields( userKey );
            userRow.put( USER_INFO_FIELDS_MAP, userFields );
            handler.processDataEntry( userRow );
        }
    }

    public void fetchGroups( ImportDataCallbackHandler handler )
    {
        final String sql = "SELECT GRP_HKEY, GRP_SDESCRIPTION, GRP_SNAME, GRP_BRESTRICTED, GRP_SSYNCVALUE, " +
                "GRP_LTYPE, GRP_DOM_LKEY, GRP_BISDELETED FROM TGROUP WHERE GRP_BISDELETED = 0";

        List<Map<String, Object>> userRows = jdbcTemplate.queryForList( sql );
        LOG.info( userRows.size() + " groups retrieved." );

        for ( Map<String, Object> userRow : userRows )
        {
            // TODO fetch members
            String groupKey = (String) userRow.get( "GRP_HKEY" );
//            Map<String, Object> userFields = fetchUserFields( userKey );
//            userRow.put( USER_INFO_FIELDS_MAP, userFields );
            handler.processDataEntry( userRow );
        }
    }

    private Map<String, Object> fetchUserFields( String userKey )
    {
        final String sql = "SELECT USF_NAME, USF_VALUE FROM TUSERFIELD WHERE USF_USR_HKEY = ?";
        List<Map<String, Object>> userFieldsResults = jdbcTemplate.queryForList( sql, userKey );

        Map<String, Object> userFields = new HashMap<String, Object>();
        for ( Map<String, Object> row : userFieldsResults )
        {
            userFields.put( (String) row.get( "USF_NAME" ), row.get( "USF_VALUE" ) );
        }
        return userFields;
    }

    @Autowired
    public void setDataSource( DataSource dataSource )
    {
        this.jdbcTemplate = new JdbcTemplate( dataSource );
    }
}
