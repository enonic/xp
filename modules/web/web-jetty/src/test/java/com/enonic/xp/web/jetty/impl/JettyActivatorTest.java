package com.enonic.xp.web.jetty.impl;

import java.util.Collections;

import javax.servlet.ServletContext;

import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.core.internal.Dictionaries;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.dispatch.DispatchServlet;
import com.enonic.xp.web.jetty.impl.session.JettySessionStorageConfigurator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JettyActivatorTest
{
    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceRegistration<Server> serverServiceRegistration;

    private JettyConfig config;

    @Mock
    private ServiceRegistration<ServletContext> xpServletContextReg;

    @BeforeEach
    void setup()
        throws Exception
    {
        System.getProperties().remove( "jetty.version" );

        final Bundle bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getHeaders() ).thenReturn( Dictionaries.of( "X-Jetty-Version", "9.x" ) );

        when( bundleContext.registerService( eq( Server.class ), any( Server.class ), any() ) ).
            thenReturn( serverServiceRegistration );

        when( bundleContext.registerService( eq( ServletContext.class ), any( ServletContext.class ), notNull() ) ).
            thenReturn( xpServletContextReg );

        when( this.bundleContext.getBundle() ).thenReturn( bundle );

        this.config = new JettyConfigMockFactory().newConfig();
        when( this.config.http_xp_port() ).thenReturn( 0 );
    }


    @Test
    void testLifecycle()
        throws Exception
    {
        final JettySessionStorageConfigurator jettySessionStorageConfigurator = Mockito.mock( JettySessionStorageConfigurator.class );
        final DispatchServlet xpDispatcherServlet = mock( DispatchServlet.class );
        when( xpDispatcherServlet.getConnector() ).thenReturn( DispatchConstants.XP_CONNECTOR );
        JettyActivator activator =
            new JettyActivator( config, bundleContext, jettySessionStorageConfigurator, Collections.singletonList( xpDispatcherServlet ) );

        activator.activate();

        assertEquals( "9.x", System.getProperty( "jetty.version" ) );

        activator.deactivate();
    }
}
