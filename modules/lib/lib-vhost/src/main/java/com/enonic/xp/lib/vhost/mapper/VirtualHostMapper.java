package com.enonic.xp.lib.vhost.mapper;

import java.util.Set;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.security.IdProviderKey;
import com.enonic.xp.web.vhost.VirtualHost;

public class VirtualHostMapper
    implements MapSerializable
{

    private final VirtualHost virtualHost;

    public VirtualHostMapper( final VirtualHost virtualHost )
    {
        this.virtualHost = virtualHost;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.value( "name", virtualHost.getName() );
        gen.value( "source", virtualHost.getSource() );
        gen.value( "target", virtualHost.getTarget() );
        gen.value( "host", virtualHost.getHost() );

        final Set<IdProviderKey> idProviderKeys = virtualHost.getIdProviders().keySet();

        if ( !idProviderKeys.isEmpty() )
        {
            gen.value( "defaultIdProviderKey", idProviderKeys.iterator().next().toString() );
        }

        gen.array( "idProviderKeys" );
        idProviderKeys.forEach( idProviderKey -> {
            gen.map();
            gen.value( "idProviderKey", idProviderKey.toString() );
            gen.end();
        } );
        gen.end();
    }

}
