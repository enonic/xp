package com.enonic.wem.web.rest2.resource.account;

import javax.ws.rs.core.UriBuilder;

import com.enonic.wem.web.rest.resource.account.AccountImageResource;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.UserEntity;

public class AccountUriHelper
{
    private static String getImageUri( final String name )
    {
        return UriBuilder.fromResource( AccountImageResource.class ).path( "default" ).path( name ).build().toString();
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
            return UriBuilder.fromResource( AccountImageResource.class ).path(
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
}
