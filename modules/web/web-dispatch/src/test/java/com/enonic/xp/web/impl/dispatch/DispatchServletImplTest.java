package com.enonic.xp.web.impl.dispatch;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

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
        this.servlet.addFilterPipeline( this.filterPipeline, new MyServiceReference<FilterPipeline>() );
        this.servlet.addServletPipeline( this.servletPipeline, new MyServiceReference<ServletPipeline>() );
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

    private final class MyServiceReference<T>
        implements ServiceReference<T>
    {
        @Override
        public Object getProperty( final String key )
        {
            return null;
        }

        @Override
        public String[] getPropertyKeys()
        {
            return new String[0];
        }

        @Override
        public Bundle getBundle()
        {
            return null;
        }

        @Override
        public Bundle[] getUsingBundles()
        {
            return new Bundle[0];
        }

        @Override
        public boolean isAssignableTo( final Bundle bundle, final String className )
        {
            return false;
        }

        @Override
        public int compareTo( final Object reference )
        {
            return 0;
        }
    }
}
