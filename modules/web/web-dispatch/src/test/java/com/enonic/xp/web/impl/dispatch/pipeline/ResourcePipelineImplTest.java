package com.enonic.xp.web.impl.dispatch.pipeline;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

import static org.junit.Assert.*;

public abstract class ResourcePipelineImplTest<D extends ResourceDefinition<?>, P extends ResourcePipelineImpl<D>>
{
    P pipeline;

    private ServletContext context;

    HttpServletRequest request;

    HttpServletResponse response;

    @Before
    public void setup()
    {
        this.pipeline = newPipeline();
        this.context = Mockito.mock( ServletContext.class );
        this.request = Mockito.mock( HttpServletRequest.class );
        this.response = Mockito.mock( HttpServletResponse.class );
    }

    abstract P newPipeline();

    abstract D newDefinition();

    @Test
    public void testAddNull()
    {
        this.pipeline.add( null );
        assertEquals( 0, Lists.newArrayList( this.pipeline ).size() );
    }

    @Test
    public void testInit()
        throws Exception
    {
        final D def1 = newDefinition();
        this.pipeline.add( def1 );
        Mockito.verify( def1, Mockito.times( 0 ) ).init( Mockito.any() );

        this.pipeline.activate( Maps.newHashMap() );

        this.pipeline.init( this.context );
        Mockito.verify( def1, Mockito.times( 1 ) ).init( this.context );

        final D def2 = newDefinition();
        this.pipeline.add( def2 );
        Mockito.verify( def2, Mockito.times( 1 ) ).init( this.context );
    }

    @Test
    public void testDestroy()
    {
        final D def = newDefinition();
        this.pipeline.add( def );

        this.pipeline.activate( Maps.newHashMap() );

        this.pipeline.destroy();
        Mockito.verify( def, Mockito.times( 1 ) ).destroy();
    }

    protected final class MyServiceReference<T>
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
