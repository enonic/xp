package com.enonic.wem.web.rest.resource.account;

import com.enonic.wem.api.account.Account;
import com.enonic.wem.api.account.RoleAccount;
import com.enonic.wem.api.account.UserAccount;
import com.enonic.wem.web.servlet.ServletRequestUrlHelper;

public final class AccountImageUriResolver
{
    public static String resolve( final Account account )
    {
        if ( account instanceof UserAccount )
        {
            return getImageUrl( (UserAccount) account );
        }

        if ( account instanceof RoleAccount )
        {
            return buildImageUrl( "default/role" );
        }

        return buildImageUrl( "default/group" );
    }

    private static String getImageUrl( final UserAccount account )
    {
        if ( account.getKey().isAnonymous() )
        {
            return buildImageUrl( "default/anonymous" );
        }

        if ( account.getKey().isSuperUser() )
        {
            return buildImageUrl( "default/admin" );
        }

        if ( account.getImage() != null )
        {
            return buildImageUrl( account.getKey().toString() );
        }
        else
        {
            return buildImageUrl( "default/user" );
        }
    }

    private static String buildImageUrl( final String path )
    {
        return ServletRequestUrlHelper.createUrl( "/admin/rest/account/image/" + path );
    }
}
