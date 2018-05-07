package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalNotFoundException;
import com.enonic.xp.security.SecurityService;

public final class DeletePrincipalHandler
    implements ScriptBean
{
    private PrincipalKey principalKey;

    private Supplier<SecurityService> securityService;

    public void setPrincipalKey( final String principalKey )
    {
        this.principalKey = PrincipalKey.from( principalKey );
    }

    public boolean deletePrincipal()
    {
        if ( PrincipalKey.ofAnonymous().equals( principalKey ) || PrincipalKey.ofSuperUser().equals( principalKey ) )
        {
            throw new IllegalArgumentException( "Not allowed to delete principal [" + principalKey + "]" );
        }

        try
        {
            this.securityService.get().deletePrincipal( this.principalKey );
            return true;
        }
        catch ( PrincipalNotFoundException e )
        {
        }
        return false;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
