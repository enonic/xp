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
import com.enonic.xp.security.token.CreateScopeParams;

@NullMarked
public final class CreateScopeHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private String idProvider;

    private String id;

    private String title;

    private String description;

    private String[] roles;

    private boolean required;

    public void setIdProvider( final String idProvider )
    {
        this.idProvider = idProvider;
    }

    public void setId( final String id )
    {
        this.id = id;
    }

    public void setTitle( final String title )
    {
        this.title = title;
    }

    public void setDescription( final String description )
    {
        this.description = description;
    }

    public void setRoles( final String[] roles )
    {
        this.roles = roles;
    }

    public void setRequired( final boolean required )
    {
        this.required = required;
    }

    public ScopeMapper createScope()
    {
        final PrincipalKeys roles = this.roles == null
            ? PrincipalKeys.empty()
            : List.of( this.roles ).stream().map( PrincipalKey::from ).collect( PrincipalKeys.collector() );
        return new ScopeMapper( securityService.get().createScope( CreateScopeParams.create()
                                                               .idProvider( IdProviderKey.from( idProvider ) )
                                                               .id( id )
                                                               .title( title )
                                                               .description( description )
                                                               .roles( roles )
                                                               .required( required )
                                                               .build() ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
