package com.enonic.xp.elasticsearch.impl;

import java.util.Hashtable;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;

import com.enonic.xp.cluster.ClusterConfig;

@ExtendWith(MockitoExtension.class)
class ElasticsearchActivatorNodbGateTest
{
    @Mock
    private BundleContext context;

    @Mock(stubOnly = true, answer = Answers.RETURNS_DEEP_STUBS)
    private ClusterConfig clusterConfig;

    @Mock(stubOnly = true)
    private ConfigurationAdmin configurationAdmin;

    @Test
    void nodbBackendInTheActivationMapStartsNoNodeAndRegistersNothing()
    {
        final ElasticsearchActivator activator = new ElasticsearchActivator( clusterConfig, configurationAdmin );

        activator.activate( this.context, Map.of( "backend", "nodb" ) );

        Mockito.verifyNoInteractions( this.context );

        activator.deactivate();
    }

    @Test
    void nodbBackendVisibleOnlyViaConfigurationAdminStartsNoNodeEither()
        throws Exception
    {
        final Configuration configuration = Mockito.mock( Configuration.class, Mockito.withSettings().stubOnly() );
        final Hashtable<String, Object> properties = new Hashtable<>();
        properties.put( "backend", "nodb" );
        Mockito.lenient().when( configuration.getProperties() ).thenReturn( properties );
        Mockito.lenient()
            .when( configurationAdmin.listConfigurations( "(service.pid=com.enonic.xp.storage.nodb)" ) )
            .thenReturn( new Configuration[]{configuration} );

        final ElasticsearchActivator activator = new ElasticsearchActivator( clusterConfig, configurationAdmin );

        activator.activate( this.context, Map.of() );

        Mockito.verifyNoInteractions( this.context );

        activator.deactivate();
    }
}
