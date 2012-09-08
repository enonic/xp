package com.enonic.wem.web.rest2.resource.account;

import javax.ws.rs.core.UriBuilder;

import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.web.data.binary.AccountImageController;
import com.enonic.wem.web.rest2.resource.account.group.GroupResource;
import com.enonic.wem.web.rest2.resource.account.role.RoleResource;
import com.enonic.wem.web.rest2.resource.account.user.UserResource;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.UserEntity;

public class AccountUriHelper
{
    private static String getImageUri( final String name )
    {
        return UriBuilder.fromResource( AccountImageController.class ).path( "default" ).path( name ).build().toString();
    }

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

    public static String getAccountImageUri( final UserEntity userAccount )
    {
        if ( userAccount.isAnonymous() )
        {
            return getImageUri( "anonymous" );
        }
        else if ( userAccount.isEnterpriseAdmin() )
        {
            return getImageUri( "admin" );
        }
        if ( userAccount.hasPhoto() )
        {
            return UriBuilder.fromResource( AccountImageController.class ).path(
                NewAccountKeyHelper.composeNewKey( userAccount ) ).build().toString();
        }
        else
        {
            return getImageUri( "user" );
        }
    }

    public static String getAccountImageUri( final GroupEntity groupAccount )
    {
        boolean isRole = groupAccount.isBuiltIn();
        if ( isRole )
        {
            return getImageUri( "role" );
        }
        else
        {
            return getImageUri( "group" );
        }
    }

    public static String getAccountGraphUri( final String accountKey )
    {
        return UriBuilder.fromPath( "account/graph" ).path( accountKey ).build().toString();
    }
}
