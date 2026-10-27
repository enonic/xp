package com.enonic.xp.portal.impl.mapper;

import java.util.Collection;
import java.util.Set;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.web.vhost.VirtualHostIdProvider;

/**
 * {@code request.idProviderSettings}: the id provider's settings on the request's virtual host,
 * that is its flows and the host's audience. Collections are sorted so the output is stable.
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
    }

    private static void strings( final MapGenerator gen, final String key, final Collection<String> values )
    {
        gen.array( key );
        values.stream().sorted().forEach( gen::value );
        gen.end();
    }
}
