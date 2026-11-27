package com.enonic.xp.lib.auth;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.token.CreateClientParams;

@NullMarked
public final class CreateClientHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private final CreateClientParams.Builder params = CreateClientParams.create();

    public void setIdProvider( final String idProvider )
    {
        params.idProvider( IdProviderKey.from( idProvider ) );
    }

    public void setId( final String id )
    {
        params.id( id );
    }

    public void setDisplayName( final @Nullable String displayName )
    {
        params.displayName( displayName );
    }

    public void setUri( final @Nullable String uri )
    {
        params.uri( uri );
    }

    public void setLogoUri( final @Nullable String logoUri )
    {
        params.logoUri( logoUri );
    }

    public void setPolicyUri( final @Nullable String policyUri )
    {
        params.policyUri( policyUri );
    }

    public void setTosUri( final @Nullable String tosUri )
    {
        params.tosUri( tosUri );
    }

    public void setGrantTypes( final String[] grantTypes )
    {
        if ( grantTypes != null )
        {
            params.grantTypes( List.of( grantTypes ) );
        }
    }

    public void setRedirectUris( final String[] redirectUris )
    {
        if ( redirectUris != null )
        {
            params.redirectUris( List.of( redirectUris ) );
        }
    }

    public void setScope( final String[] scope )
    {
        if ( scope != null )
        {
            params.scope( List.of( scope ) );
        }
    }

    public void setResource( final String[] resource )
    {
        if ( resource != null )
        {
            params.resource( List.of( resource ) );
        }
    }

    public void setGrantTtl( final @Nullable String grantTtl )
    {
        params.grantTtl( grantTtl == null ? null : Duration.parse( grantTtl ) );
    }

    public void setEnabled( final boolean enabled )
    {
        params.enabled( enabled );
    }

    public ClientMapper createClient()
    {
        return new ClientMapper( securityService.get().createClient( params.build() ) );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
