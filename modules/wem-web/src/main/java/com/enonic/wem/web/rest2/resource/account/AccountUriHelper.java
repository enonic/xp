package com.enonic.wem.web.rest2.resource.account;

import javax.ws.rs.core.UriBuilder;

import com.enonic.wem.api.account.AccountType;
import com.enonic.wem.web.rest2.resource.account.graph.GraphResource;
import com.enonic.wem.web.rest2.resource.account.group.GroupResource;
import com.enonic.wem.web.rest2.resource.account.role.RoleResource;
import com.enonic.wem.web.rest2.resource.account.user.UserResource;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.UserEntity;

public class AccountUriHelper
{
    private static final String USER_DEFAULT_IMAGE_URI = "misc/image/user";

    private static final String ADMIN_USER_IMAGE_URI = "misc/image/admin";

    private static final String ANONYMOUS_USER_IMAGE_URI = "misc/image/anonymous";

    private static final String ROLE_DEFAULT_IMAGE_URI = "misc/image/role";

    private static final String GROUP_DEFAULT_IMAGE_URI = "misc/image/group";

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

    public static String getAccountImageUri( final UserEntity userAccount )
    {
        if ( userAccount.isAnonymous() )
        {
            return ANONYMOUS_USER_IMAGE_URI;
        }
        else if ( userAccount.isEnterpriseAdmin() )
        {
            return ADMIN_USER_IMAGE_URI;
        }
        if ( userAccount.hasPhoto() )
        {
            return UriBuilder.fromResource( UserResource.class ).path( userAccount.getKey().toString() ).path( "photo" ).build().toString();
        }
        else
        {
            return USER_DEFAULT_IMAGE_URI;
        }
    }

    public static String getAccountImageUri( final GroupEntity groupAccount )
    {
        boolean isRole = groupAccount.isBuiltIn();
        if ( isRole )
        {
            return ROLE_DEFAULT_IMAGE_URI;
        }
        else
        {
            return GROUP_DEFAULT_IMAGE_URI;
        }
    }

    public static String getAccountGraphUri( final String accountKey )
    {
        return UriBuilder.fromResource( GraphResource.class ).path( accountKey ).build().toString();
    }
}
