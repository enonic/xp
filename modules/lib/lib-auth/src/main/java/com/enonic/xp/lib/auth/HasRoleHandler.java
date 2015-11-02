package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.context.Context;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.auth.AuthenticationInfo;

public final class HasRoleHandler
    implements ScriptBean
{
    private Supplier<Context> context;

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
        final AuthenticationInfo authInfo = this.context.get().getAuthInfo();
        return authInfo.getPrincipals().contains( this.roleKey );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.context = context.getBinding( Context.class );
    }
}
