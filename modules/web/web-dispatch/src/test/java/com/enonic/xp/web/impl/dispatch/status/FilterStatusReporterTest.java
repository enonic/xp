package com.enonic.xp.web.impl.dispatch.status;

import java.util.List;

import javax.servlet.Filter;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.dispatch.MappingBuilder;
import com.enonic.xp.web.impl.dispatch.mapping.FilterDefinition;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinitionFactory;
import com.enonic.xp.web.impl.dispatch.pipeline.FilterPipeline;

import static org.junit.Assert.*;

public class FilterStatusReporterTest
{
    private FilterDefinition newDefinition()
    {
        final Filter filter = Mockito.mock( Filter.class );

        final FilterMapping mapping = MappingBuilder.newBuilder().
            order( 10 ).
            name( "test" ).
            initParam( "a", "1" ).
            urlPatterns( "/*" ).
            filter( filter );

        return ResourceDefinitionFactory.create( mapping );
    }

    @Test
    public void testReport()
    {
        final List<FilterDefinition> list = Lists.newArrayList( newDefinition() );

        final FilterPipeline pipeline = Mockito.mock( FilterPipeline.class );
        Mockito.when( pipeline.iterator() ).thenReturn( list.iterator() );

        final FilterStatusReporter reporter = new FilterStatusReporter();
        reporter.setPipeline( pipeline );

        assertEquals( "http.filter", reporter.getName() );
        assertNotNull( reporter.getReport() );
    }
}
