package com.enonic.xp.web.impl.dispatch.pipeline;

import java.io.IOException;
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

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.impl.dispatch.mapping.FilterDefinition;

import static org.assertj.core.api.Assertions.assertThat;

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
        return new FilterPipelineImpl( Map.of( DispatchConstants.CONNECTOR_PROPERTY, "xp" ) );
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

        assertThat( this.pipeline.list.snapshot() ).isEmpty();
        this.pipeline.addFilter( filter, Map.of() );

        assertThat( this.pipeline.list.snapshot().size() ).isEqualTo( 1 );
        this.pipeline.removeFilter( filter );
    }

    @Test
    public void addRemove_mapping()
    {
        final FilterMapping mapping = Mockito.mock( FilterMapping.class );
        Mockito.when( mapping.getResource() ).thenReturn( Mockito.mock( Filter.class ) );

        assertThat( this.pipeline.list.snapshot() ).isEmpty();
        this.pipeline.addMapping( mapping );

        assertThat( this.pipeline.list.snapshot().size() ).isEqualTo( 1 );
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
