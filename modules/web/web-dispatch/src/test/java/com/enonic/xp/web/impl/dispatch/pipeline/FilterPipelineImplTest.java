package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.impl.dispatch.mapping.FilterDefinition;

import static org.assertj.core.api.Assertions.assertThat;

class FilterPipelineImplTest
    extends ResourcePipelineImplTest<FilterDefinition, FilterPipelineImpl>
{
    @WebFilter("/*")
    private static final class MyFilter
        implements Filter
    {
        @Override
        public void doFilter( final ServletRequest req, final ServletResponse res, final FilterChain chain )
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
    void addRemove_filter()
    {
        final MyFilter filter = new MyFilter();

        assertThat( this.pipeline.list() ).isEmpty();
        this.pipeline.addFilter( filter, Map.of() );
        assertThat( this.pipeline.list() ).hasSize( 1 );

        this.pipeline.removeFilter( filter );
        assertThat( this.pipeline.list() ).isEmpty();
    }

    @Test
    void addRemove_mapping()
    {
        final FilterMapping mapping = Mockito.mock( FilterMapping.class );
        Mockito.when( mapping.getResource() ).thenReturn( Mockito.mock( Filter.class ) );
        Mockito.when( mapping.getUrlPatterns() ).thenReturn( Set.of( "/*" ) );

        assertThat( this.pipeline.list() ).isEmpty();
        this.pipeline.addMapping( mapping );
        assertThat( this.pipeline.list() ).hasSize( 1 );

        this.pipeline.removeMapping( mapping );
        assertThat( this.pipeline.list() ).isEmpty();
    }

    @Test
    void testFilter()
        throws Exception
    {
        final ServletPipeline servletPipeline = Mockito.mock( ServletPipeline.class );
        this.pipeline.filter( this.request, this.response, servletPipeline );
    }
}
