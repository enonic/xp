package com.enonic.xp.elasticsearch.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.ClusterService;
import org.elasticsearch.transport.TransportRequestHandler;
import org.elasticsearch.transport.TransportService;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.google.common.collect.Maps;

public class ElasticsearchActivatorTest
{
    private BundleContext context;

    private ElasticsearchActivator activator;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private ServiceRegistration<Client> clientReg;

    private ServiceRegistration<ClusterService> clusterServiceReg;

    private ServiceRegistration<TransportService> transportServiceReg;

    private TransportRequestHandler transportRequestHandler;

    private Map<String, String> transportRequestHandlerProperties;

    @Before
    public void setup()
        throws Exception
    {
        this.context = Mockito.mock( BundleContext.class );
        this.activator = new ElasticsearchActivator();

        final File homeDir = this.temporaryFolder.newFolder( "home" );
        System.setProperty( "xp.home", homeDir.getAbsolutePath() );

        this.clientReg = mockRegisterService( Client.class );
        this.clusterServiceReg = mockRegisterService( ClusterService.class );
        this.transportServiceReg = mockRegisterService( TransportService.class );
        this.transportRequestHandler = Mockito.mock( TransportRequestHandler.class );
        this.transportRequestHandlerProperties = new HashMap<>();
        this.transportRequestHandlerProperties.put( "action", "cms/cluster/send" );
    }

    @Test
    public void testLifeCycle()
        throws Exception
    {
        final Map<String, String> map = Maps.newHashMap();

        this.activator.activate( this.context, map );
        verifyRegisterService( Client.class );
        verifyRegisterService( ClusterService.class );
        verifyRegisterService( TransportService.class );

        this.activator.addTransportRequestHandler( this.transportRequestHandler, this.transportRequestHandlerProperties );
        this.activator.removeTransportRequestHandler( this.transportRequestHandler, this.transportRequestHandlerProperties );

        this.activator.deactivate();
        verifyUnregisterService( this.clientReg );
        verifyUnregisterService( this.clusterServiceReg );
        verifyUnregisterService( this.transportServiceReg );
    }

    private <T> void verifyRegisterService( final Class<T> type )
    {
        Mockito.verify( this.context, Mockito.times( 1 ) ).registerService( Mockito.eq( type ), Mockito.any( type ), Mockito.any() );
    }

    private <T> void verifyUnregisterService( final ServiceRegistration<T> reg )
    {
        Mockito.verify( reg, Mockito.times( 1 ) ).unregister();
    }

    @SuppressWarnings("unchecked")
    private <T> ServiceRegistration<T> mockRegisterService( final Class<T> type )
    {
        final ServiceRegistration<T> reg = Mockito.mock( ServiceRegistration.class );
        Mockito.when( this.context.registerService( Mockito.eq( type ), Mockito.any( type ), Mockito.any() ) ).thenReturn( reg );
        return reg;
    }


}
