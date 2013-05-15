package com.enonic.wem.migrate.account;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

final class DatabaseAccountsLoaderImpl
    implements DatabaseAccountsLoader
{
    private static final Logger LOG = LoggerFactory.getLogger( DatabaseAccountsLoaderImpl.class );

    static final String USER_INFO_FIELDS_MAP = "FIELDSMAP";

    private final DataSource dataSource;

    public DatabaseAccountsLoaderImpl( final DataSource dataSource )
    {
        this.dataSource = dataSource;
    }

    @Override
    public void loadUserStores( ImportDataCallbackHandler handler )
        throws Exception
    {
        final String sql = "SELECT DOM_LKEY, DOM_BISDELETED, DOM_SNAME, DOM_BDEFAULTSTORE, DOM_SCONFIGNAME, DOM_XMLDATA " +
            "FROM TDOMAIN WHERE DOM_BISDELETED = 0";

        final List<Map<String, Object>> userStoreRows = queryForList( sql );
        LOG.info( userStoreRows.size() + " user stores retrieved." );

        for ( Map<String, Object> userStoreRow : userStoreRows )
        {
            handler.processDataEntry( userStoreRow );
        }
    }

    @Override
    public void loadUsers( ImportDataCallbackHandler handler )
        throws Exception
    {
        final String sql = "SELECT USR_HKEY, USR_SUID, USR_SFULLNAME, USR_DTETIMESTAMP, USR_UT_LKEY, " +
            "USR_DOM_LKEY, USR_SSYNCVALUE2, USR_SEMAIL, USR_SPASSWORD, USR_GRP_HKEY, USR_PHOTO, USR_BISDELETED FROM TUSER WHERE USR_BISDELETED = 0";

        final List<Map<String, Object>> userRows = queryForList( sql );
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
        throws Exception
    {
        final String sql = "SELECT GRP_HKEY, GRP_SDESCRIPTION, GRP_SNAME, GRP_BRESTRICTED, GRP_SSYNCVALUE, " +
            "GRP_LTYPE, GRP_DOM_LKEY, GRP_BISDELETED FROM TGROUP WHERE GRP_BISDELETED = 0";

        final List<Map<String, Object>> userRows = queryForList( sql );
        LOG.info( userRows.size() + " groups retrieved." );

        for ( Map<String, Object> userRow : userRows )
        {
            handler.processDataEntry( userRow );
        }
    }

    @Override
    public void loadMemberships( ImportDataCallbackHandler handler )
        throws Exception
    {
        final String sql = "SELECT GGM_MBR_GRP_HKEY, GGM_GRP_HKEY FROM TGRPGRPMEMBERSHIP";

        final List<Map<String, Object>> membershipRows = queryForList( sql );
        LOG.info( membershipRows.size() + " memberships retrieved." );

        for ( Map<String, Object> membership : membershipRows )
        {
            handler.processDataEntry( membership );
        }
    }

    private Map<String, Object> fetchUserFields( String userKey )
        throws Exception
    {
        final String sql = "SELECT USF_NAME, USF_VALUE FROM TUSERFIELD WHERE USF_USR_HKEY = ?";
        final List<Map<String, Object>> userFieldsResults = queryForList( sql, userKey );

        final Map<String, Object> userFields = new HashMap<String, Object>();
        for ( Map<String, Object> row : userFieldsResults )
        {
            userFields.put( (String) row.get( "USF_NAME" ), row.get( "USF_VALUE" ) );
        }
        return userFields;
    }

    private List<Map<String, Object>> queryForList( final String sql, final Object... args )
        throws Exception
    {
        try (final Connection conn = this.dataSource.getConnection())
        {
            return queryForList( conn, sql, args );
        }
    }

    private List<Map<String, Object>> queryForList( final Connection conn, final String sql, final Object... args )
        throws Exception
    {
        try (final PreparedStatement stmt = conn.prepareStatement( sql ))
        {
            return queryForList( stmt, args );
        }
    }

    private List<Map<String, Object>> queryForList( final PreparedStatement stmt, final Object... args )
        throws Exception
    {
        for ( int i = 0; i < args.length; i++ )
        {
            stmt.setObject( i + 1, args[i] );
        }

        try (final ResultSet result = stmt.executeQuery())
        {
            return queryForList( result );
        }
    }

    private List<Map<String, Object>> queryForList( final ResultSet result )
        throws Exception
    {
        final List<Map<String, Object>> list = Lists.newArrayList();
        final ResultSetMetaData md = result.getMetaData();

        while ( result.next() )
        {
            final Map<String, Object> entry = Maps.newHashMap();
            for ( int i = 0; i < md.getColumnCount(); i++ )
            {
                entry.put( md.getColumnName( i + 1 ).toUpperCase(), result.getObject( i + 1 ) );
            }

            list.add( entry );
        }

        return list;
    }
}
