package com.enonic.wem.core.search.account;

import java.io.IOException;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class AccountIndexDataEntity
    implements AccountIndexData
{
    private final AccountKey key;

    private final XContentBuilder data;

    public AccountIndexDataEntity( final Account account )
    {
        final String userStoreName = account.getUserStoreName() != null ? account.getUserStoreName() : "system";
        key = new AccountKey( account.getType().name().toLowerCase() + ":" + userStoreName + ":" + account.getName() );
        data = build( account );
    }

    private XContentBuilder build( final Account account )
    {
        try
        {
            switch ( account.getType() )
            {
                case USER:
                    return buildUser( (User) account );

                case ROLE:
                case GROUP:
                    return buildGroup( (Group) account );

                default:
                    throw new UnsupportedOperationException( "Unable to build index for account of type " + account.getType() );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private XContentBuilder buildUser( final User user )
        throws Exception
    {
        final XContentBuilder result = buildAccountStart(user);
        addField( result, AccountIndexField.EMAIL_FIELD.id(), user.getEmail() );
        addField( result, AccountIndexField.ORGANIZATION_FIELD.id(), user.getUserInfo().getOrganization() );

        buildAccountEnd( result );
        return result;
    }

    private XContentBuilder buildGroup( final Group group )
        throws Exception
    {
        final XContentBuilder result = buildAccountStart(group);

        buildAccountEnd( result );
        return result;
    }

    private XContentBuilder buildAccountStart( final Account account )
        throws Exception
    {
        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();
        addField( result, AccountIndexField.KEY_FIELD.id(), key.toString() );

        addField( result, AccountIndexField.TYPE_FIELD.id(), account.getType().name() );
        addField( result, AccountIndexField.NAME_FIELD.id(), account.getName() );
        addField( result, AccountIndexField.DISPLAY_NAME_FIELD.id(), account.getDisplayName() );
        addField( result, AccountIndexField.USERSTORE_FIELD.id(), account.getUserStoreName() );
        addField( result, AccountIndexField.LAST_MODIFIED_FIELD.id(), account.getLastModified() );

        return result;
    }

    private void buildAccountEnd( final XContentBuilder contentBuilder )
        throws Exception
    {
        contentBuilder.endObject();
    }

    private void addField( XContentBuilder result, String name, Object value )
        throws Exception
    {
        if ( value == null )
        {
            return;
        }
        if ( value instanceof String )
        {
            value = ( (String) value ).trim();
        }

        result.field( name, value );
    }

    public AccountKey getKey()
    {
        return key;
    }

    public XContentBuilder getData()
    {
        return data;
    }

    public String toString()
    {
        try
        {
            return data.string();
        }
        catch ( IOException e )
        {
            return "";
        }
    }
}
