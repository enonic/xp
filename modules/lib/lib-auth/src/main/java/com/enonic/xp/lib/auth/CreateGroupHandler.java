package com.enonic.xp.lib.auth;

import java.util.function.Supplier;

import com.enonic.xp.lib.common.PrincipalMapper;
import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.CreateGroupParams;
import com.enonic.xp.security.Group;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.SecurityService;

public final class CreateGroupHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private IdProviderKey userStore;

    private String name;

    private String displayName;

    private String description;

    public void setUserStore( final String idProvider )
    {
        this.userStore = IdProviderKey.from( idProvider );
    }

    public void setName( final String name )
    {
        this.name = name;
    }

    public void setDisplayName( final String displayName )
    {
        this.displayName = displayName;
    }

    public void setDescription( String description )
    {
        this.description = description;
    }

    public PrincipalMapper createGroup()
    {
        final Group group = this.securityService.get().createGroup( CreateGroupParams.create().
            displayName( this.displayName != null ? this.displayName : this.name ).
            groupKey( PrincipalKey.ofGroup( this.userStore, this.name ) ).
            description( this.description ).
            build() );
        return group != null ? new PrincipalMapper( group ) : null;
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
