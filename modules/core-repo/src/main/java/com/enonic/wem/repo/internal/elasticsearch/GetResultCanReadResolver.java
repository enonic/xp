package com.enonic.wem.repo.internal.elasticsearch;

import java.util.Collection;

import com.enonic.wem.repo.internal.index.result.ResultFieldValues;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

class GetResultCanReadResolver
{
    public static boolean canRead( final PrincipalKeys principalsKeys, final ResultFieldValues resultFieldValues )
    {
        if ( principalsKeys.contains( RoleKeys.ADMIN ) )
        {
            return true;
        }

        final Collection<Object> readPermissions = resultFieldValues.get( NodeIndexPath.PERMISSIONS_READ.getPath() );

        if ( readPermissions == null || readPermissions.isEmpty() )
        {
            return false;
        }

        final PrincipalKeys keys = principalsKeys.isEmpty() ? PrincipalKeys.from( PrincipalKey.ofAnonymous() ) : principalsKeys;

        for ( final Object readPermission : readPermissions )
        {
            if ( keys.contains( PrincipalKey.from( readPermission.toString() ) ) )
            {
                return true;
            }
        }

        return false;
    }

}
