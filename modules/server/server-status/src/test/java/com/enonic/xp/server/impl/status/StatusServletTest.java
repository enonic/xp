package com.enonic.xp.server.impl.status;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.vhost.VirtualHost;
import com.enonic.xp.web.vhost.VirtualHostService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class StatusServletTest
{
    private static final Context ADMIN = ContextBuilder.create()
        .authInfo( AuthenticationInfo.create().principals( RoleKeys.ADMIN ).user( User.anonymous() ).build() )
        .build();

    private final HttpServletRequest req = mock( HttpServletRequest.class );

    private final HttpServletResponse res = mock( HttpServletResponse.class );

    private final VirtualHostService virtualHostService = mock( VirtualHostService.class );

    private final ByteArrayOutputStream body = new ByteArrayOutputStream();

    private final StringWriter error = new StringWriter();

    private StatusServlet servlet;

    @BeforeEach
    void setUp()
        throws IOException
    {
        when( res.getOutputStream() ).thenReturn( new ServletOutputStream()
        {
            @Override
            public void write( final int b )
            {
                body.write( b );
            }

            @Override
            public boolean isReady()
            {
                return true;
            }

            @Override
            public void setWriteListener( final WriteListener writeListener )
            {
            }
        } );
        when( res.getWriter() ).thenReturn( new PrintWriter( error ) );

        servlet = new StatusServlet( virtualHostService );
        servlet.addReporter( new SensitiveReporter( "dump.threads" ) );
        servlet.addReporter( new PlainReporter( "jvm.info" ) );
    }

    private void virtualHosts( final String... connectors )
    {
        final List<VirtualHost> virtualHosts = new ArrayList<>();
        for ( final String connector : connectors )
        {
            final VirtualHost virtualHost = mock( VirtualHost.class );
            when( virtualHost.getConnector() ).thenReturn( connector );
            virtualHosts.add( virtualHost );
        }
        when( virtualHostService.isEnabled() ).thenReturn( true );
        when( virtualHostService.getVirtualHosts() ).thenReturn( virtualHosts );
    }

    @Test
    void sensitiveReporterOpenWithoutStatisticsVirtualHost()
        throws Exception
    {
        virtualHosts( DispatchConstants.WEB_CONNECTOR, DispatchConstants.MANAGEMENT_CONNECTOR );
        when( req.getRequestURI() ).thenReturn( "/dump.threads" );

        servlet.doGet( req, res );

        verify( res ).setStatus( 200 );
        assertEquals( "dump.threads", body.toString( StandardCharsets.UTF_8 ) );
    }

    @Test
    void sensitiveReporterRequiresAdminWithStatisticsVirtualHost()
        throws Exception
    {
        virtualHosts( DispatchConstants.WEB_CONNECTOR, DispatchConstants.STATISTICS_CONNECTOR );
        when( req.getRequestURI() ).thenReturn( "/dump.threads" );

        servlet.doGet( req, res );

        verify( res ).setStatus( 403 );
        assertTrue( error.toString().contains( "Reporter [dump.threads] requires the admin role" ) );
        assertEquals( 0, body.size() );
    }

    @Test
    void sensitiveReporterServedToAdminWithStatisticsVirtualHost()
        throws Exception
    {
        virtualHosts( DispatchConstants.STATISTICS_CONNECTOR );
        when( req.getRequestURI() ).thenReturn( "/dump.threads" );

        ADMIN.callWith( () -> {
            servlet.doGet( req, res );
            return null;
        } );

        verify( res ).setStatus( 200 );
        assertEquals( "dump.threads", body.toString( StandardCharsets.UTF_8 ) );
    }

    @Test
    void sensitiveReporterOpenWithVirtualHostsDisabled()
        throws Exception
    {
        when( virtualHostService.isEnabled() ).thenReturn( false );
        when( req.getRequestURI() ).thenReturn( "/dump.threads" );

        servlet.doGet( req, res );

        verify( res ).setStatus( 200 );
        verify( virtualHostService, never() ).getVirtualHosts();
        assertEquals( "dump.threads", body.toString( StandardCharsets.UTF_8 ) );
    }

    @Test
    void plainReporterOpenWithStatisticsVirtualHost()
        throws Exception
    {
        virtualHosts( DispatchConstants.STATISTICS_CONNECTOR );
        when( req.getRequestURI() ).thenReturn( "/jvm.info" );

        servlet.doGet( req, res );

        verify( res ).setStatus( 200 );
        assertEquals( "jvm.info", body.toString( StandardCharsets.UTF_8 ) );
    }

    @Test
    void unknownReporter()
        throws Exception
    {
        when( req.getRequestURI() ).thenReturn( "/nothing" );

        servlet.doGet( req, res );

        verify( res ).setStatus( 404 );
    }

    private static class PlainReporter
        implements StatusReporter
    {
        private final String name;

        PlainReporter( final String name )
        {
            this.name = name;
        }

        @Override
        public String getName()
        {
            return name;
        }

        @Override
        public MediaType getMediaType()
        {
            return MediaType.PLAIN_TEXT_UTF_8;
        }

        @Override
        public void report( final OutputStream stream )
            throws IOException
        {
            stream.write( name.getBytes( StandardCharsets.UTF_8 ) );
        }
    }

    private static final class SensitiveReporter
        extends PlainReporter
    {
        SensitiveReporter( final String name )
        {
            super( name );
        }

        @Override
        public boolean isSensitive()
        {
            return true;
        }
    }
}
