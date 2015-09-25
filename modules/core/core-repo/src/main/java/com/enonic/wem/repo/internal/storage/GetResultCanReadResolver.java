package com.enonic.wem.repo.internal.storage;

import com.enonic.wem.repo.internal.ReturnValue;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

public class GetResultCanReadResolver
{
    public static boolean canRead( final PrincipalKeys principalsKeys, final ReturnValue returnValue )
    {
        if ( principalsKeys.contains( RoleKeys.ADMIN ) )
        {
            return true;
        }

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
