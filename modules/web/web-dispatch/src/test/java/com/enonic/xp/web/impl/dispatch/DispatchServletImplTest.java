package com.enonic.xp.web.impl.dispatch;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.impl.dispatch.pipeline.FilterPipeline;
import com.enonic.xp.web.impl.dispatch.pipeline.ServletPipeline;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class DispatchServletImplTest
{
    private static final Map<String, ?> WEB_CONNECTOR_PROPERTIES =
        Map.of( DispatchConstants.CONNECTOR_PROPERTY, DispatchConstants.WEB_CONNECTOR );

    private FilterPipeline filterPipeline;

    private ServletPipeline servletPipeline;

    private DispatchServletImpl servlet;

    private ServletContext context;

    private ServletConfig config;

    @BeforeEach
    void setup()
    {
        this.filterPipeline = mock( FilterPipeline.class );
        this.servletPipeline = mock( ServletPipeline.class );

        this.context = mock( ServletContext.class );
        this.config = mock( ServletConfig.class );
        Mockito.when( this.config.getServletContext() ).thenReturn( this.context );

        this.servlet = new DispatchServletImpl( WEB_CONNECTOR_PROPERTIES );
    }

    private void addPipelines()
    {
        this.servlet.addFilterPipeline( this.filterPipeline, WEB_CONNECTOR_PROPERTIES );
        this.servlet.addServletPipeline( this.servletPipeline, WEB_CONNECTOR_PROPERTIES );
    }

    @Test
    void testInit()
        throws Exception
    {
        addPipelines();

        this.servlet.init( this.config );

        verify( this.filterPipeline, times( 1 ) ).init( this.context );
        verify( this.servletPipeline, times( 1 ) ).init( this.context );
    }

    @Test
    void init_beforePipelinesAreBound()
        throws Exception
    {
        this.servlet.init( this.config );

        verifyNoInteractions( this.filterPipeline, this.servletPipeline );

        addPipelines();

        verify( this.filterPipeline, times( 1 ) ).init( this.context );
        verify( this.servletPipeline, times( 1 ) ).init( this.context );
    }

    @Test
    void init_otherConnectorPipelinesAreIgnored()
        throws Exception
    {
        final Map<String, ?> otherConnector = Map.of( DispatchConstants.CONNECTOR_PROPERTY, DispatchConstants.MANAGEMENT_CONNECTOR );

        this.servlet.init( this.config );
        this.servlet.addFilterPipeline( this.filterPipeline, otherConnector );
        this.servlet.addServletPipeline( this.servletPipeline, otherConnector );

        verifyNoInteractions( this.filterPipeline, this.servletPipeline );
    }

    @Test
    void testDestroy()
    {
        addPipelines();

        this.servlet.destroy();

        verify( this.filterPipeline, times( 1 ) ).destroy();
        verify( this.servletPipeline, times( 1 ) ).destroy();
    }

    @Test
    void destroy_beforePipelinesAreBound()
    {
        this.servlet.destroy();

        verifyNoInteractions( this.filterPipeline, this.servletPipeline );
    }

    @Test
    void destroy_pipelineBoundAfterwardsIsNotInitialized()
        throws Exception
    {
        this.servlet.init( this.config );
        this.servlet.destroy();

        addPipelines();

        verify( this.filterPipeline, never() ).init( this.context );
        verify( this.servletPipeline, never() ).init( this.context );
    }

    @Test
    void testService()
        throws Exception
    {
        addPipelines();

        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );

        this.servlet.service( req, res );

        verify( req, times( 1 ) ).setAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE, DispatchConstants.WEB_CONNECTOR );
        verify( this.filterPipeline, times( 1 ) ).filter( req, res, this.servletPipeline );
    }

    @Test
    void service_beforePipelinesAreBound()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );

        this.servlet.service( req, res );

        verify( res, times( 1 ) ).sendError( HttpServletResponse.SC_SERVICE_UNAVAILABLE );
        verifyNoInteractions( this.filterPipeline, this.servletPipeline );
    }

    @Test
    void service_afterPipelinesAreUnbound()
        throws Exception
    {
        addPipelines();
        this.servlet.removeFilterPipeline( this.filterPipeline );
        this.servlet.removeServletPipeline( this.servletPipeline );

        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );

        this.servlet.service( req, res );

        verify( res, times( 1 ) ).sendError( HttpServletResponse.SC_SERVICE_UNAVAILABLE );
        verify( this.filterPipeline, never() ).filter( req, res, this.servletPipeline );
    }
}
