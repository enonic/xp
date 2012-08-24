package com.enonic.wem.web.rest2.resource.account;

import javax.ws.rs.core.UriBuilder;

import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.web.rest2.resource.account.group.GroupResource;
import com.enonic.wem.web.rest2.resource.account.role.RoleResource;
import com.enonic.wem.web.rest2.resource.account.user.UserResource;

public class RestUriBuilder
{
    // TODO: refactor to use com.enonic.wem.api.account.AccountKey for parameters, instead of String

    public static String getAccountInfoUri( final AccountType type, final String accountKey )
    {
        if ( type == AccountType.USER )
        {
            return UriBuilder.fromResource( UserResource.class ).path( accountKey ).build().toString();
        }
        else if ( type == AccountType.GROUP )
        {
            return UriBuilder.fromResource( GroupResource.class ).path( accountKey ).build().toString();
        }
        else if ( type == AccountType.ROLE )
        {
            return UriBuilder.fromResource( RoleResource.class ).path( accountKey ).build().toString();
        }
        else
        {
            return null;
        }
    }

    public static String getImageUri( final AccountType type, final String accountKey )
    {
        if ( type == AccountType.USER )
        {
            return UriBuilder.fromResource( UserResource.class ).path( accountKey ).path( "photo" ).build().toString();
        }
        else
        {
            return null;
        }
    }

}
