package com.enonic.xp.portal.impl.mapper;

import java.util.Collection;
import java.util.Set;
import java.util.TreeMap;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;

/**
 * {@code request.idProviderSettings}: the id provider's settings on the request's virtual host,
 * that is its flows, the host's audience and the scope catalogue. Collections are sorted so the
 * output is stable.
 */
final class IdProviderSettingsMapper
    implements MapSerializable
{
    private final VirtualHostIdProvider settings;

    private final Set<String> audience;

    IdProviderSettingsMapper( final VirtualHostIdProvider settings, final Set<String> audience )
    {
        this.settings = settings;
        this.audience = audience;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        strings( gen, "flows", settings.getFlows() );
        strings( gen, "audience", audience );
        gen.map( "scopes" );
        for ( var scope : new TreeMap<>( settings.getScopes() ).entrySet() )
        {
            gen.map( scope.getKey() );
            strings( gen, "roles", scope.getValue().roles().stream().map( PrincipalKey::toString ).toList() );
            gen.value( "required", scope.getValue().required() );
            gen.end();
        }
        gen.end();
    }

    private static void strings( final MapGenerator gen, final String key, final Collection<String> values )
    {
        gen.array( key );
        values.stream().sorted().forEach( gen::value );
        gen.end();
    }
}
