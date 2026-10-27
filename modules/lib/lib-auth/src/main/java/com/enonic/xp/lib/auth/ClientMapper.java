package com.enonic.xp.lib.auth;

import java.util.Collection;

import org.jspecify.annotations.NullMarked;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.token.Client;

@NullMarked
public final class ClientMapper
    implements MapSerializable
{
    private final Client client;

    public ClientMapper( final Client client )
    {
        this.client = client;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "idProvider", client.getIdProvider().toString() );
        gen.value( "id", client.getId() );
        gen.value( "displayName", client.getDisplayName() );
        gen.value( "uri", client.getUri() );
        gen.value( "logoUri", client.getLogoUri() );
        gen.value( "policyUri", client.getPolicyUri() );
        gen.value( "tosUri", client.getTosUri() );
        strings( gen, "grantTypes", client.getGrantTypes() );
        strings( gen, "redirectUris", client.getRedirectUris() );
        strings( gen, "scope", client.getScope() );
        strings( gen, "resource", client.getResource() );
        gen.value( "grantTtl", client.getGrantTtl() == null ? null : client.getGrantTtl().toString() );
        gen.value( "enabled", client.isEnabled() );
    }

    private static void strings( final MapGenerator gen, final String key, final Collection<String> values )
    {
        gen.array( key );
        values.stream().sorted().forEach( gen::value );
        gen.end();
    }
}
