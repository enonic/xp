package com.enonic.xp.web.impl.dispatch;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.web.impl.dispatch.pipeline.FilterPipeline;
import com.enonic.xp.web.impl.dispatch.pipeline.ServletPipeline;

public class DispatchServletImplTest
{
    private FilterPipeline filterPipeline;

    private ServletPipeline servletPipeline;

    private DispatchServletImpl servlet;

    @Before
    public void setup()
    {
        this.filterPipeline = Mockito.mock( FilterPipeline.class );
        this.servletPipeline = Mockito.mock( ServletPipeline.class );

        this.servlet = new DispatchServletImpl();
        this.servlet.setFilterPipeline( this.filterPipeline );
        this.servlet.setServletPipeline( this.servletPipeline );
    }

    @Test
    public void testInit()
        throws Exception
    {
        final ServletContext context = Mockito.mock( ServletContext.class );

        final ServletConfig config = Mockito.mock( ServletConfig.class );
        Mockito.when( config.getServletContext() ).thenReturn( context );

        this.servlet.init( config );
        Mockito.verify( this.filterPipeline, Mockito.times( 1 ) ).init( context );
        Mockito.verify( this.servletPipeline, Mockito.times( 1 ) ).init( context );
    }

    @Test
    public void testDestroy()
        throws Exception
    {
        this.servlet.destroy();
        Mockito.verify( this.filterPipeline, Mockito.times( 1 ) ).destroy();
        Mockito.verify( this.servletPipeline, Mockito.times( 1 ) ).destroy();
    }

    @Test
    public void testService()
        throws Exception
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        final HttpServletResponse res = Mockito.mock( HttpServletResponse.class );

        this.servlet.service( req, res );
        Mockito.verify( this.filterPipeline, Mockito.times( 1 ) ).filter( req, res, this.servletPipeline );
    }
}
