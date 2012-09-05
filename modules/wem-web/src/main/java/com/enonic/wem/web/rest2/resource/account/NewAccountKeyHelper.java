package com.enonic.wem.web.rest2.resource.account;

import com.enonic.cms.core.security.group.GroupEntity;
import com.enonic.cms.core.security.user.UserEntity;
import com.enonic.cms.core.security.userstore.UserStoreEntity;

public final class NewAccountKeyHelper
{
    public static String composeNewKey( final UserEntity entity )
    {
        return composeNewKey( "user", entity.getUserStore(), entity.getName() );
    }

    public static String composeNewKey( final GroupEntity entity )
    {
        final String type = entity.isBuiltIn() ? "role" : "group";
        return composeNewKey( type, entity.getUserStore(), entity.getName() );
    }

    private static String composeNewKey( final String type, final UserStoreEntity userStore, final String localName )
    {
        final StringBuilder str = new StringBuilder( type );
        str.append( ":" );

        if ( userStore != null )
        {
            str.append( userStore.getName() );
        }
        else
        {
            str.append( "system" );
        }

        str.append( ":" ).append( localName );
        return str.toString();
    }
}
