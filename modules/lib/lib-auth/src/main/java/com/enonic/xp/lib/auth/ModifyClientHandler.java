package com.enonic.xp.lib.auth;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.enonic.xp.script.bean.BeanContext;
import com.enonic.xp.script.bean.ScriptBean;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.token.UpdateClientParams;

@NullMarked
public final class ModifyClientHandler
    implements ScriptBean
{
    private Supplier<SecurityService> securityService;

    private IdProviderKey idProvider;

    private String id;

    private final Map<String, @Nullable String> links = new HashMap<>();

    private @Nullable Set<String> grantTypes;

    private @Nullable Set<String> redirectUris;

    private @Nullable Set<String> scope;

    private @Nullable Set<String> resource;

    private @Nullable Duration grantTtl;

    private boolean grantTtlSet;

    private @Nullable Boolean enabled;

    public void setIdProvider( final String idProvider )
    {
        this.idProvider = IdProviderKey.from( idProvider );
    }

    public void setId( final String id )
    {
        this.id = id;
    }

    public void setDisplayName( final @Nullable String displayName )
    {
        links.put( "displayName", displayName );
    }

    public void setUri( final @Nullable String uri )
    {
        links.put( "uri", uri );
    }

    public void setLogoUri( final @Nullable String logoUri )
    {
        links.put( "logoUri", logoUri );
    }

    public void setPolicyUri( final @Nullable String policyUri )
    {
        links.put( "policyUri", policyUri );
    }

    public void setTosUri( final @Nullable String tosUri )
    {
        links.put( "tosUri", tosUri );
    }

    public void setGrantTypes( final String @Nullable [] grantTypes )
    {
        this.grantTypes = Set.of( grantTypes );
    }

    public void setRedirectUris( final String @Nullable [] redirectUris )
    {
        this.redirectUris = Set.of( redirectUris );
    }

    public void setScope( final String @Nullable [] scope )
    {
        this.scope = Set.of( scope );
    }

    public void setResource( final String @Nullable [] resource )
    {
        this.resource = Set.of( resource );
    }

    public void setGrantTtl( final @Nullable String grantTtl )
    {
        this.grantTtl = grantTtl == null ? null : Duration.parse( grantTtl );
        this.grantTtlSet = true;
    }

    public void setEnabled( final boolean enabled )
    {
        this.enabled = enabled;
    }

    public @Nullable ClientMapper modifyClient()
    {
        final UpdateClientParams params = UpdateClientParams.create().idProvider( idProvider ).id( id ).editor( edit -> {
            if ( links.containsKey( "displayName" ) )
            {
                edit.displayName = links.get( "displayName" );
            }
            if ( links.containsKey( "uri" ) )
            {
                edit.uri = links.get( "uri" );
            }
            if ( links.containsKey( "logoUri" ) )
            {
                edit.logoUri = links.get( "logoUri" );
            }
            if ( links.containsKey( "policyUri" ) )
            {
                edit.policyUri = links.get( "policyUri" );
            }
            if ( links.containsKey( "tosUri" ) )
            {
                edit.tosUri = links.get( "tosUri" );
            }
            if ( grantTypes != null )
            {
                edit.grantTypes = grantTypes;
            }
            if ( redirectUris != null )
            {
                edit.redirectUris = redirectUris;
            }
            if ( scope != null )
            {
                edit.scope = scope;
            }
            if ( resource != null )
            {
                edit.resource = resource;
            }
            if ( grantTtlSet )
            {
                edit.grantTtl = grantTtl;
            }
            if ( enabled != null )
            {
                edit.enabled = enabled;
            }
        } ).build();
        return securityService.get().updateClient( params ).map( ClientMapper::new ).orElse( null );
    }

    @Override
    public void initialize( final BeanContext context )
    {
        this.securityService = context.getService( SecurityService.class );
    }
}
