package com.enonic.wem.migrate.jcr;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class MockDatabaseAccountsLoader
    implements DatabaseAccountsLoader
{

    @Override
    public void loadUserStores( final ImportDataCallbackHandler handler )
    {
        try
        {
            final List<String> lines = readDataFile( "userStores.csv" );
            final List<Map<String, Object>> dataRows =
                parseLines( lines, "DOM_LKEY", "DOM_BISDELETED", "DOM_SNAME", "DOM_BDEFAULTSTORE", "DOM_SCONFIGNAME", "DOM_XMLDATA" );
            for ( Map<String, Object> dataRow : dataRows )
            {
                handler.processDataEntry( dataRow );
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void loadUsers( final ImportDataCallbackHandler handler )
    {
        final Map<String, Map<String, Object>> userFields = loadUserFields();
        try
        {
            final List<String> lines = readDataFile( "users.csv" );
            final List<Map<String, Object>> dataRows =
                parseLines( lines, "USR_HKEY", "USR_SUID", "USR_SFULLNAME", "USR_DTETIMESTAMP", "USR_UT_LKEY", "USR_DOM_LKEY",
                            "USR_SSYNCVALUE2", "USR_SEMAIL", "USR_SPASSWORD", "USR_GRP_HKEY", "USR_PHOTO", "USR_BISDELETED" );
            for ( Map<String, Object> dataRow : dataRows )
            {
                Map<String, Object> fields = userFields.get( dataRow.get( "USR_HKEY" ) );
                if ( fields == null )
                {
                    fields = Maps.newHashMap();
                }
                dataRow.put( DatabaseAccountsLoaderImpl.USER_INFO_FIELDS_MAP, fields );
                handler.processDataEntry( dataRow );
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void loadGroups( final ImportDataCallbackHandler handler )
    {
        try
        {
            final List<String> lines = readDataFile( "groups.csv" );
            final List<Map<String, Object>> dataRows =
                parseLines( lines, "GRP_HKEY", "GRP_SDESCRIPTION", "GRP_SNAME", "GRP_BRESTRICTED", "GRP_SSYNCVALUE", "GRP_LTYPE",
                            "GRP_DOM_LKEY", "GRP_BISDELETED" );
            for ( Map<String, Object> dataRow : dataRows )
            {
                handler.processDataEntry( dataRow );
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void loadMemberships( final ImportDataCallbackHandler handler )
    {
        try
        {
            final List<String> lines = readDataFile( "memberships.csv" );
            final List<Map<String, Object>> dataRows = parseLines( lines, "GGM_MBR_GRP_HKEY", "GGM_GRP_HKEY" );
            for ( Map<String, Object> dataRow : dataRows )
            {
                handler.processDataEntry( dataRow );
            }
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    private Map<String, Map<String, Object>> loadUserFields()
    {
        try
        {
            final List<String> lines = readDataFile( "userFields.csv" );
            final List<Map<String, Object>> dataRows = parseLines( lines, "USF_USR_HKEY", "USF_NAME", "USF_VALUE" );
            final Map<String, Map<String, Object>> userFields = Maps.newHashMap();
            for ( Map<String, Object> dataRow : dataRows )
            {
                final String key = (String) dataRow.get( "USF_USR_HKEY" );
                Map<String, Object> values = userFields.get( key );
                if ( values == null )
                {
                    values = Maps.newHashMap();
                    userFields.put( key, values );
                }
                values.put( (String) dataRow.get( "USF_NAME" ), dataRow.get( "USF_VALUE" ) );
            }
            return userFields;
        }
        catch ( IOException e )
        {
            throw new RuntimeException( e );
        }
    }

    private List<Map<String, Object>> parseLines( final List<String> lines, final String... columnNames )
    {
        final List<Map<String, Object>> rows = Lists.newArrayList();
        for ( String line : lines )
        {
            rows.add( parseLine( line, columnNames ) );
        }
        return rows;
    }

    private Map<String, Object> parseLine( final String line, final String... columnNames )
    {
        final Map<String, Object> values = Maps.newHashMap();
        final String[] parts = line.split( "\\|\\|" );
        for ( int i = 0; i < parts.length; i++ )
        {
            values.put( columnNames[i], parseValue( parts[i] ) );
        }
        return values;
    }

    private Object parseValue( final String value )
    {
        if ( value == null || value.isEmpty() )
        {
            return null;
        }
        final String type = StringUtils.substringBefore( value, "=" );
        final String valueStr = StringUtils.substringAfter( value, "=" );
        if ( type.equals( "String" ) )
        {
            return valueStr;
        }
        else if ( type.equals( "Integer" ) )
        {
            return Integer.valueOf( valueStr );
        }
        else if ( type.equals( "BYTE[]" ) )
        {
            final String[] valueBytes = valueStr.split( "," );
            final byte[] bytes = new byte[valueBytes.length];
            for ( int i = 0; i < valueBytes.length; i++ )
            {
                bytes[i] = Byte.valueOf( valueBytes[i] );
            }
            return bytes;
        }
        else if ( type.equals( "DateTime" ) )
        {
            return DateTime.parse( valueStr ).toDate();
        }
        else
        {
            throw new IllegalArgumentException( "Unexpected type in data file: " + type );
        }
    }

    private List<String> readDataFile( String fileName )
        throws IOException
    {
        return IOUtils.readLines( new InputStreamReader( getClass().getResourceAsStream( fileName ) ) );
    }
}
