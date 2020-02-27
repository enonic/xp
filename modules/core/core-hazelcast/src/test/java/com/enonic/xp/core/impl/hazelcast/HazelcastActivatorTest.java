package com.enonic.xp.core.impl.hazelcast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hazelcast.config.Config;
import com.hazelcast.osgi.HazelcastOSGiInstance;
import com.hazelcast.osgi.HazelcastOSGiService;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HazelcastActivatorTest
{
    @Mock
    private HazelcastOSGiService hazelcastOSGiService;

    @Mock
    private HazelcastConfigService hazelcastConfigService;

    @Test
    void lifecycle_enabled()
    {
        when( hazelcastConfigService.isHazelcastEnabled() ).thenReturn( true );

        final Config config = mock( Config.class );
        when( hazelcastConfigService.configure() ).thenReturn( config );

        final HazelcastOSGiInstance hazelcastOSGiInstance = mock( HazelcastOSGiInstance.class );
        when( hazelcastOSGiService.newHazelcastInstance( same( config ) ) ).thenReturn( hazelcastOSGiInstance );

        final HazelcastActivator hazelcastActivator = new HazelcastActivator( hazelcastOSGiService, hazelcastConfigService );

        hazelcastActivator.activate();
        hazelcastActivator.deactivate();

        verify( hazelcastOSGiService ).shutdownHazelcastInstance( same( hazelcastOSGiInstance ) );
    }

    @Test
    void lifecycle_disabled()
    {
        when( hazelcastConfigService.isHazelcastEnabled() ).thenReturn( false );

        final HazelcastActivator hazelcastActivator = new HazelcastActivator( hazelcastOSGiService, hazelcastConfigService );

        hazelcastActivator.activate();
        hazelcastActivator.deactivate();

        verifyZeroInteractions( hazelcastOSGiService );
    }
}