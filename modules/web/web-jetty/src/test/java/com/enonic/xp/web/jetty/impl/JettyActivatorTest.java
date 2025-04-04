package com.enonic.xp.web.jetty.impl;

import java.util.Collections;

import org.eclipse.jetty.server.Server;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.dispatch.DispatchServlet;
import com.enonic.xp.web.jetty.impl.session.JettySessionStoreConfigurator;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @BeforeEach
    void setup()
    {
        when( bundleContext.registerService( eq( Server.class ), any( Server.class ), any() ) ).
            thenReturn( serverServiceRegistration );

        this.config = mock( JettyConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( this.config.http_xp_port() ).thenReturn( 0 );
        when( this.config.http_monitor_port() ).thenReturn( 0 );
        when( this.config.http_management_port() ).thenReturn( 0 );
    }


    @Test
    void testLifecycle()
        throws Exception
    {
        final JettySessionStoreConfigurator jettySessionStoreConfigurator = Mockito.mock( JettySessionStoreConfigurator.class );
        final DispatchServlet xpDispatcherServlet = mock( DispatchServlet.class );
        when( xpDispatcherServlet.getConnector() ).thenReturn( DispatchConstants.XP_CONNECTOR );
        JettyActivator activator =
            new JettyActivator( config, bundleContext, jettySessionStoreConfigurator, Collections.singletonList( xpDispatcherServlet ) );

        activator.activate();

        activator.deactivate();
    }
}
