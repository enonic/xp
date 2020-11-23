package com.enonic.xp.core.impl.hazelcast;

import java.util.Dictionary;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;

import com.hazelcast.config.Config;
import com.hazelcast.osgi.HazelcastOSGiInstance;
import com.hazelcast.osgi.HazelcastOSGiService;

import com.enonic.xp.core.internal.Condition;

import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HazelcastActivatorTest
{
    @Mock
    HazelcastOSGiService hazelcastOSGiService;

    @Mock
    HazelcastConfigService hazelcastConfigService;

    @Mock
    ComponentContext componentContext;

    @Captor
    ArgumentCaptor<Dictionary<String, ?>> captor;

    @Mock
    ServiceRegistration<Condition> serviceRegistration;

    @Test
    void lifecycle_enabled()
    {
        when( hazelcastConfigService.isHazelcastEnabled() ).thenReturn( true );

        final Config config = mock( Config.class );
        when( hazelcastConfigService.configure() ).thenReturn( config );

        final HazelcastOSGiInstance hazelcastOSGiInstance = mock( HazelcastOSGiInstance.class );
        when( hazelcastOSGiService.newHazelcastInstance( same( config ) ) ).thenReturn( hazelcastOSGiInstance );

        final HazelcastActivator hazelcastActivator = new HazelcastActivator( hazelcastOSGiService, hazelcastConfigService );

        hazelcastActivator.activate( componentContext );

        verify( componentContext ).enableComponent( HazelcastActivatorActivatedCondition.class.getName() );

        hazelcastActivator.deactivate();

        verifyNoInteractions( serviceRegistration );
        verify( hazelcastOSGiService ).shutdownHazelcastInstance( same( hazelcastOSGiInstance ) );
    }

    @Test
    void lifecycle_disabled()
    {
        when( hazelcastConfigService.isHazelcastEnabled() ).thenReturn( false );

        final HazelcastActivator hazelcastActivator = new HazelcastActivator( hazelcastOSGiService, hazelcastConfigService );

        hazelcastActivator.activate( componentContext );

        verify( componentContext ).enableComponent( HazelcastActivatorActivatedCondition.class.getName() );

        hazelcastActivator.deactivate();

        verifyNoInteractions( serviceRegistration );
        verifyNoInteractions( hazelcastOSGiService );
    }
}
