package com.enonic.xp.impl.shared;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HazelcastSharedMapServiceTest
{
    @Mock
    HazelcastInstance hazelcastInstance;

    @Mock
    IMap<Object, Object> map;

    @Test
    void getSharedMap()
    {
        when( hazelcastInstance.getMap( "map" ) ).thenReturn( map );
        final HazelcastSharedMapService hazelcastSharedMapService = new HazelcastSharedMapService( hazelcastInstance );

        hazelcastSharedMapService.getSharedMap( "map" );
        verify( hazelcastInstance ).getMap( "map" );
    }
}
