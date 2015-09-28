package com.enonic.xp.lib.auth;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class HasRoleHandler
{
    private PrincipalKey roleKey;

    public void setRole( final String roleKey )
    {
        if ( roleKey == null )
        {
            this.roleKey = null;
        }
        else if ( roleKey.startsWith( "role:" ) )
        {
            this.roleKey = PrincipalKey.from( roleKey );
        }
        else
        {
            this.roleKey = PrincipalKey.ofRole( roleKey );
        }
    }

    public boolean hasRole()
    {
        if ( this.roleKey == null )
        {
            return false;
        }
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return authInfo.getPrincipals().contains( this.roleKey );
    }

}
