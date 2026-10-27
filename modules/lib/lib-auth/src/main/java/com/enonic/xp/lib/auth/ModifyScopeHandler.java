package com.enonic.xp.lib.auth;

import java.util.List;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.token.UpdateScopeParams;

@NullMarked
public final class ModifyScopeHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private IdProviderKey idProvider;

    private String id;

    private String title;

    private boolean titleSet;

    private String description;

    private boolean descriptionSet;

    private PrincipalKeys roles;

    private Boolean required;

    public void setIdProvider( final String idProvider )
    {
        this.idProvider = IdProviderKey.from( idProvider );
    }

    public void setId( final String id )
    {
        this.id = id;
    }

    public void setTitle( final String title )
    {
        this.title = title;
        this.titleSet = true;
    }

    public void setDescription( final String description )
    {
        this.description = description;
        this.descriptionSet = true;
    }

    public void setRoles( final String[] roles )
    {
        this.roles = List.of( roles ).stream().map( PrincipalKey::from ).collect( PrincipalKeys.collector() );
    }

    public void setRequired( final boolean required )
    {
        this.required = required;
    }

    public ScopeMapper modifyScope()
    {
        final UpdateScopeParams params = UpdateScopeParams.create().idProvider( idProvider ).id( id ).editor( edit -> {
            if ( titleSet )
            {
                edit.title = title;
            }
            if ( descriptionSet )
            {
                edit.description = description;
            }
            if ( roles != null )
            {
                edit.roles = roles;
            }
            if ( required != null )
            {
                edit.required = required;
            }
        } ).build();
        return securityService.get().updateScope( params ).map( ScopeMapper::new ).orElse( null );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
