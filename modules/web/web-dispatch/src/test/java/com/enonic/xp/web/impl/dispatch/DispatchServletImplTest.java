package com.enonic.xp.web.impl.dispatch;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.impl.dispatch.pipeline.FilterPipeline;
import com.enonic.xp.web.impl.dispatch.pipeline.ServletPipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private HttpServletRequest req;

    private HttpServletResponse res;

    @BeforeEach
    void setup()
    {
        this.filterPipeline = mock( FilterPipeline.class );
        this.servletPipeline = mock( ServletPipeline.class );

        this.req = mock( HttpServletRequest.class );
        this.res = mock( HttpServletResponse.class );

        this.servlet = new DispatchServletImpl( WEB_CONNECTOR_PROPERTIES );
    }

    private void addPipelines()
    {
        this.servlet.addFilterPipeline( this.filterPipeline, WEB_CONNECTOR_PROPERTIES );
        this.servlet.addServletPipeline( this.servletPipeline, WEB_CONNECTOR_PROPERTIES );
    }

    @Test
    void testService()
        throws Exception
    {
        addPipelines();

        this.servlet.service( this.req, this.res );

        verify( this.req, times( 1 ) ).setAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE, DispatchConstants.WEB_CONNECTOR );
        verify( this.filterPipeline, times( 1 ) ).filter( this.req, this.res, this.servletPipeline );
    }

    @Test
    void service_beforePipelinesAreBound()
        throws Exception
    {
        this.servlet.service( this.req, this.res );

        verify( this.res, times( 1 ) ).sendError( HttpServletResponse.SC_SERVICE_UNAVAILABLE );
        verifyNoInteractions( this.filterPipeline, this.servletPipeline );
    }

    @Test
    void service_afterPipelinesAreUnbound()
        throws Exception
    {
        addPipelines();
        this.servlet.removeFilterPipeline( this.filterPipeline );
        this.servlet.removeServletPipeline( this.servletPipeline );

        this.servlet.service( this.req, this.res );

        verify( this.res, times( 1 ) ).sendError( HttpServletResponse.SC_SERVICE_UNAVAILABLE );
        verify( this.filterPipeline, never() ).filter( this.req, this.res, this.servletPipeline );
    }

    @Test
    void pipelinesOfOtherConnectorsAreIgnored()
        throws Exception
    {
        final Map<String, ?> otherConnector = Map.of( DispatchConstants.CONNECTOR_PROPERTY, DispatchConstants.MANAGEMENT_CONNECTOR );

        this.servlet.addFilterPipeline( this.filterPipeline, otherConnector );
        this.servlet.addServletPipeline( this.servletPipeline, otherConnector );

        this.servlet.service( this.req, this.res );

        verify( this.res, times( 1 ) ).sendError( HttpServletResponse.SC_SERVICE_UNAVAILABLE );
        verifyNoInteractions( this.filterPipeline, this.servletPipeline );
    }

    @Test
    void unbindingAnotherPipelineKeepsTheCurrentOne()
        throws Exception
    {
        addPipelines();

        this.servlet.removeFilterPipeline( mock( FilterPipeline.class ) );
        this.servlet.removeServletPipeline( mock( ServletPipeline.class ) );

        this.servlet.service( this.req, this.res );

        verify( this.filterPipeline, times( 1 ) ).filter( this.req, this.res, this.servletPipeline );
    }

    @Test
    void getConnector()
    {
        assertEquals( DispatchConstants.WEB_CONNECTOR, this.servlet.getConnector() );
    }
}
