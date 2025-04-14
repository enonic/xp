package com.enonic.xp.impl.shared;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceRanking;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import com.enonic.xp.shared.SharedMapService;

@Component(immediate = true)
@ServiceRanking(1)
public class HazelcastSharedMapService
    implements SharedMapService
{
    private final HazelcastInstance hazelcastInstance;

    @Activate
    public HazelcastSharedMapService( @Reference final HazelcastInstance hazelcastInstance )
    {
        this.hazelcastInstance = hazelcastInstance;
    }

    @Override
    public <K, V> HazelcastSharedMap<K, V> getSharedMap( final String name )
    {
        final IMap<K, V> iMap = hazelcastInstance.getMap( name );
        return new HazelcastSharedMap<>( iMap );
    }
}
