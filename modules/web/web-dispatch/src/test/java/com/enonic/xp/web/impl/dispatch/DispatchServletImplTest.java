package com.enonic.xp.web.impl.dispatch;

import java.util.Map;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.impl.dispatch.pipeline.FilterPipeline;
import com.enonic.xp.web.impl.dispatch.pipeline.ServletPipeline;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class DispatchServletImplTest
{
    private FilterPipeline filterPipeline;

    private ServletPipeline servletPipeline;

    private DispatchServletImpl servlet;

    @BeforeEach
    public void setup()
    {
        this.filterPipeline = mock( FilterPipeline.class );
        this.servletPipeline = mock( ServletPipeline.class );

        this.servlet = new DispatchServletImpl( Map.of( DispatchConstants.CONNECTOR_PROPERTY, DispatchConstants.XP_CONNECTOR ) );
        this.servlet.addFilterPipeline( this.filterPipeline,
                                        Map.of( DispatchConstants.CONNECTOR_PROPERTY, DispatchConstants.XP_CONNECTOR ) );
        this.servlet.addServletPipeline( this.servletPipeline,
                                         Map.of( DispatchConstants.CONNECTOR_PROPERTY, DispatchConstants.XP_CONNECTOR ) );
    }

    @Test
    public void testInit()
        throws Exception
    {
        final ServletContext context = mock( ServletContext.class );

        final ServletConfig config = mock( ServletConfig.class );
        Mockito.when( config.getServletContext() ).thenReturn( context );

        this.servlet.init( config );
        verify( this.filterPipeline, times( 1 ) ).init( context );
        verify( this.servletPipeline, times( 1 ) ).init( context );
    }

    @Test
    public void testDestroy()
        throws Exception
    {
        this.servlet.destroy();
        verify( this.filterPipeline, times( 1 ) ).destroy();
        verify( this.servletPipeline, times( 1 ) ).destroy();
    }

    @Test
    public void testService()
        throws Exception
    {
        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );

        this.servlet.service( req, res );

        verify( req, times( 1 ) ).setAttribute( DispatchConstants.CONNECTOR_ATTRIBUTE, DispatchConstants.XP_CONNECTOR );
        verify( this.filterPipeline, times( 1 ) ).filter( req, res, this.servletPipeline );
    }

}
