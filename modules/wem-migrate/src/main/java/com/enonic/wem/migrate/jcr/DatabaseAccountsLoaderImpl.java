package com.enonic.wem.migrate.jcr;

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
public class DatabaseAccountsLoaderImpl
    implements DatabaseAccountsLoader
{
    private static final Logger LOG = LoggerFactory.getLogger( DatabaseAccountsLoaderImpl.class );

    static final String USER_INFO_FIELDS_MAP = "FIELDSMAP";

    private JdbcTemplate jdbcTemplate;

    @Override
    public void loadUserStores( ImportDataCallbackHandler handler )
    {
        final String sql = "SELECT DOM_LKEY, DOM_BISDELETED, DOM_SNAME, DOM_BDEFAULTSTORE, DOM_SCONFIGNAME, DOM_XMLDATA " +
            "FROM TDOMAIN WHERE DOM_BISDELETED = 0";

        final List<Map<String, Object>> userStoreRows = jdbcTemplate.queryForList( sql );
        LOG.info( userStoreRows.size() + " user stores retrieved." );

        for ( Map<String, Object> userStoreRow : userStoreRows )
        {
            handler.processDataEntry( userStoreRow );
        }
    }

    @Override
    public void loadUsers( ImportDataCallbackHandler handler )
    {
        final String sql = "SELECT USR_HKEY, USR_SUID, USR_SFULLNAME, USR_DTETIMESTAMP, USR_UT_LKEY, " +
            "USR_DOM_LKEY, USR_SSYNCVALUE2, USR_SEMAIL, USR_SPASSWORD, USR_GRP_HKEY, USR_PHOTO, USR_BISDELETED FROM TUSER WHERE USR_BISDELETED = 0";

        final List<Map<String, Object>> userRows = jdbcTemplate.queryForList( sql );
        LOG.info( userRows.size() + " users retrieved." );

        for ( Map<String, Object> userRow : userRows )
        {
            final String userKey = (String) userRow.get( "USR_HKEY" );
            final Map<String, Object> userFields = fetchUserFields( userKey );
            userRow.put( USER_INFO_FIELDS_MAP, userFields );
            handler.processDataEntry( userRow );
        }
    }

    @Override
    public void loadGroups( ImportDataCallbackHandler handler )
    {
        final String sql = "SELECT GRP_HKEY, GRP_SDESCRIPTION, GRP_SNAME, GRP_BRESTRICTED, GRP_SSYNCVALUE, " +
            "GRP_LTYPE, GRP_DOM_LKEY, GRP_BISDELETED FROM TGROUP WHERE GRP_BISDELETED = 0";

        final List<Map<String, Object>> userRows = jdbcTemplate.queryForList( sql );
        LOG.info( userRows.size() + " groups retrieved." );

        for ( Map<String, Object> userRow : userRows )
        {
            handler.processDataEntry( userRow );
        }
    }

    @Override
    public void loadMemberships( ImportDataCallbackHandler handler )
    {
        final String sql = "SELECT GGM_MBR_GRP_HKEY, GGM_GRP_HKEY FROM TGRPGRPMEMBERSHIP";

        final List<Map<String, Object>> membershipRows = jdbcTemplate.queryForList( sql );
        LOG.info( membershipRows.size() + " memberships retrieved." );

        for ( Map<String, Object> membership : membershipRows )
        {
            handler.processDataEntry( membership );
        }
    }

    private Map<String, Object> fetchUserFields( String userKey )
    {
        final String sql = "SELECT USF_NAME, USF_VALUE FROM TUSERFIELD WHERE USF_USR_HKEY = ?";
        final List<Map<String, Object>> userFieldsResults = jdbcTemplate.queryForList( sql, userKey );

        final Map<String, Object> userFields = new HashMap<String, Object>();
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
