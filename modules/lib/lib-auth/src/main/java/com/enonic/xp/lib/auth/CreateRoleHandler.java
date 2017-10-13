package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.lib.common.PrincipalMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.CreateRoleParams;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.Role;
import com.enonic.xp.security.SecurityService;

public final class CreateRoleHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private PrincipalKey key;

    private String displayName;

    private String description;

    public void setKey( final String key )
    {
        this.key = PrincipalKey.from( key );
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public PrincipalMapper createRole()
    {
        final Role role = this.securityService.get().createRole( CreateRoleParams.create().
            roleKey( this.key ).
            displayName( this.displayName ).
            description( this.description ).
            build() );
        return role != null ? new PrincipalMapper( role ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
