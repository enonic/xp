package com.enonic.xp.lib.vhost.mapper;

import java.util.List;

import com.enonic.xp.script.serializer.MapGenerator;
import com.enonic.xp.script.serializer.MapSerializable;
import com.enonic.xp.web.vhost.VirtualHost;

public class VirtualHostsMapper
    implements MapSerializable
{

    private final List<VirtualHost> virtualHosts;

    public VirtualHostsMapper( final List<VirtualHost> virtualHosts )
    {
        this.virtualHosts = virtualHosts;
    }

    @Override
    public void serialize( final MapGenerator gen )
    {
        gen.array( "vhosts" );

        if ( virtualHosts != null )
        {
            virtualHosts.forEach( virtualHost -> {
                gen.map();
                new VirtualHostMapper( virtualHost ).serialize( gen );
                gen.end();
            } );
        }

        gen.end();
    }

}
