package com.enonic.wem.repo.internal.elasticsearch;

import com.enonic.wem.repo.internal.index.result.ReturnValue;
import com.enonic.wem.repo.internal.index.result.ReturnValues;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

class GetResultCanReadResolver
{
    public static boolean canRead( final PrincipalKeys principalsKeys, final ReturnValues returnValues )
    {
        if ( principalsKeys.contains( RoleKeys.ADMIN ) )
        {
            return true;
        }

        final ReturnValue returnValue = returnValues.get( NodeIndexPath.PERMISSIONS_READ.getPath() );

        if ( returnValue == null )
        {
            return false;
        }

        final PrincipalKeys keys = principalsKeys.isEmpty() ? PrincipalKeys.from( PrincipalKey.ofAnonymous() ) : principalsKeys;

        for ( final Object readPermission : returnValue.getValues() )
        {
            if ( keys.contains( PrincipalKey.from( readPermission.toString() ) ) )
            {
                return true;
            }
        }

        return false;
    }

}
