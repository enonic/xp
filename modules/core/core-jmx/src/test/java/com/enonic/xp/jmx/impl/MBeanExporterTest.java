package com.enonic.xp.jmx.impl;

import java.lang.management.ManagementFactory;
import java.util.Dictionary;

import javax.management.MBeanServer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class MBeanExporterTest
{
    private MBeanExporter exporter;

    private BundleContext bundleContext;

    @Before
    public void setup()
    {
        this.exporter = new MBeanExporter();
        this.bundleContext = Mockito.mock( BundleContext.class );
    }

    @Test
    public void testActivate()
    {
        final ServiceRegistration reg = mockInvocation();
        this.exporter.activate( this.bundleContext );
        final ArgumentCaptor<MBeanServer> capture = verifyInvocation();

        Assert.assertNotNull( capture.getValue() );
        Assert.assertSame( ManagementFactory.getPlatformMBeanServer(), capture.getValue() );

        this.exporter.deactivate();
        Mockito.verify( reg, Mockito.times( 1 ) ).unregister();
    }

    @SuppressWarnings("unchecked")
    private ServiceRegistration mockInvocation()
    {
        final ServiceRegistration reg = Mockito.mock( ServiceRegistration.class );
        Mockito.when( this.bundleContext.registerService( Mockito.eq( MBeanServer.class ), Mockito.any( MBeanServer.class ),
                                                          Mockito.any( Dictionary.class ) ) ).thenReturn( reg );
        return reg;
    }

    @SuppressWarnings("unchecked")
    private ArgumentCaptor<MBeanServer> verifyInvocation()
    {
        final ArgumentCaptor<MBeanServer> arg = ArgumentCaptor.forClass( MBeanServer.class );
        Mockito.verify( this.bundleContext, Mockito.times( 1 ) ).registerService( Mockito.eq( MBeanServer.class ), arg.capture(),
                                                                                  Mockito.any( Dictionary.class ) );
        return arg;
    }
}
