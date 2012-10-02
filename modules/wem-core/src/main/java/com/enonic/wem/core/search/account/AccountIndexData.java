package com.enonic.wem.core.search.account;

import java.io.IOException;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.AccountKey;
import com.enonic.wem.api.account.AccountKeys;
import com.enonic.wem.api.account.NonUserAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.api.account.profile.UserProfile;

final class AccountIndexData
{
    private final AccountKey key;

    private final XContentBuilder data;

    public AccountIndexData( final Account account )
    {
        key = account.getKey();
        data = build( account );
    }

    private XContentBuilder build( final Account account )
    {
        try
        {
            switch ( account.getKey().getType() )
            {
                case USER:
                    return buildUser( (UserAccount) account );

                case ROLE:
                case GROUP:
                    return buildGroupOrRole( (NonUserAccount) account );

                default:
                    throw new UnsupportedOperationException( "Unable to build index for account of type " + account.getKey().getType() );
            }
        }
        catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
    }

    private XContentBuilder buildUser( final UserAccount user )
        throws Exception
    {
        final XContentBuilder result = buildAccountStart( user );
        addField( result, AccountIndexField.EMAIL_FIELD.id(), user.getEmail() );
        final UserProfile profile = user.getProfile();
        if ( ( profile != null ) && ( profile.getOrganization() != null ) )
        {
            addField( result, AccountIndexField.ORGANIZATION_FIELD.id(), profile.getOrganization() );
        }
        buildAccountEnd( result );
        return result;
    }

    private XContentBuilder buildGroupOrRole( final NonUserAccount noneUserAccount )
        throws Exception
    {
        final XContentBuilder result = buildAccountStart( noneUserAccount );
        final AccountKeys members = noneUserAccount.getMembers() != null ? noneUserAccount.getMembers() : AccountKeys.empty();
        final String[] memberKeys = new String[members.getSize()];
        int i = 0;
        for ( AccountKey member : members )
        {
            memberKeys[i++] = member.toString();
        }
        addField( result, AccountIndexField.MEMBERS_FIELD.id(), memberKeys );

        buildAccountEnd( result );
        return result;
    }

    private XContentBuilder buildAccountStart( final Account account )
        throws Exception
    {
        final XContentBuilder result = XContentFactory.jsonBuilder();
        result.startObject();
        addField( result, AccountIndexField.KEY_FIELD.id(), account.getKey().toString() );

        addField( result, AccountIndexField.TYPE_FIELD.id(), account.getKey().getType().name() );
        addField( result, AccountIndexField.NAME_FIELD.id(), account.getKey().getLocalName() );
        addField( result, AccountIndexField.DISPLAY_NAME_FIELD.id(), account.getDisplayName() );
        addField( result, AccountIndexField.USERSTORE_FIELD.id(), account.getKey().getUserStore() );
        addField( result, AccountIndexField.LAST_MODIFIED_FIELD.id(), account.getModifiedTime() );

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
