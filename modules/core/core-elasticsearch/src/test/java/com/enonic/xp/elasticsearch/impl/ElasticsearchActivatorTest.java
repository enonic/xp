package com.enonic.xp.elasticsearch.impl;

import java.io.File;
import java.util.Map;

import org.elasticsearch.client.Client;
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

    @Before
    public void setup()
        throws Exception
    {
        this.context = Mockito.mock( BundleContext.class );
        this.activator = new ElasticsearchActivator();

        final File homeDir = this.temporaryFolder.newFolder( "home" );
        System.setProperty( "xp.home", homeDir.getAbsolutePath() );

        this.clientReg = mockRegisterService( Client.class );
    }

    @Test
    public void testActivate()
        throws Exception
    {
        final Map<String, String> map = Maps.newHashMap();

        this.activator.activate( this.context, map );
        verifyRegisterService( Client.class );

        this.activator.deactivate();
        verifyUnregisterService( this.clientReg );
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
