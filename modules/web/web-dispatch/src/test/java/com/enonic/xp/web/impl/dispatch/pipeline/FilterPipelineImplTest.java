package com.enonic.xp.web.impl.dispatch.pipeline;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.impl.dispatch.mapping.FilterDefinition;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilterPipelineImplTest
    extends ResourcePipelineImplTest<FilterDefinition, FilterPipelineImpl>
{
    @WebFilter
    private static final class MyFilter
        implements Filter
    {
        @Override
        public void init( final FilterConfig config )
            throws ServletException
        {
            // Do nothing
        }

        @Override
        public void doFilter( final ServletRequest req, final ServletResponse res, final FilterChain chain )
            throws IOException, ServletException
        {
            // Do nothing
        }

        @Override
        public void destroy()
        {
            // Do nothing
        }
    }

    @Override
    FilterPipelineImpl newPipeline()
    {
        return new FilterPipelineImpl();
    }

    @Override
    FilterDefinition newDefinition()
    {
        final FilterDefinition def = Mockito.mock( FilterDefinition.class );
        Mockito.when( def.getResource() ).thenReturn( new MyFilter() );
        return def;
    }

    @Test
    public void addRemove_filter()
    {
        final MyFilter filter = new MyFilter();

        assertEquals( 0, Lists.newArrayList( this.pipeline ).size() );
        this.pipeline.addFilter( filter, Map.of() );

        this.pipeline.activate( new HashMap<>() );

        assertEquals( 1, Lists.newArrayList( this.pipeline ).size() );
        this.pipeline.removeFilter( filter );
    }

    @Test
    public void addRemove_mapping()
    {
        final FilterMapping mapping = Mockito.mock( FilterMapping.class );
        Mockito.when( mapping.getResource() ).thenReturn( Mockito.mock( Filter.class ) );

        assertEquals( 0, Lists.newArrayList( this.pipeline ).size() );
        this.pipeline.addMapping( mapping );

        this.pipeline.activate( new HashMap<>() );

        assertEquals( 1, Lists.newArrayList( this.pipeline ).size() );
        this.pipeline.removeMapping( mapping );
    }

    @Test
    public void testFilter()
        throws Exception
    {
        final ServletPipeline servletPipeline = Mockito.mock( ServletPipeline.class );
        this.pipeline.filter( this.request, this.response, servletPipeline );
    }
}
