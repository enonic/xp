package com.enonic.xp.lib.vhost.mapper;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
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

        if ( virtualHost.getDefaultIdProviderKey() != null )
        {
            gen.value( "defaultIdProviderKey", virtualHost.getDefaultIdProviderKey().toString() );
        }

        if ( virtualHost.getIdProviderKeys() != null )
        {
            gen.array( "idProviderKeys" );
            virtualHost.getIdProviderKeys().forEach( idProviderKey -> {
                gen.map();
                gen.value( "idProviderKey", idProviderKey.toString() );
                gen.end();
            } );
            gen.end();
        }
    }

}
