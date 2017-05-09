package com.enonic.xp.web.impl.dispatch.status;

import java.util.List;

import javax.servlet.Servlet;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.xp.web.dispatch.MappingBuilder;
import com.enonic.xp.web.dispatch.ServletMapping;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinitionFactory;
import com.enonic.xp.web.impl.dispatch.mapping.ServletDefinition;
import com.enonic.xp.web.impl.dispatch.pipeline.ServletPipeline;

import static org.junit.Assert.*;

public class ServletStatusReporterTest
{
    private ServletDefinition newDefinition()
    {
        final Servlet servlet = Mockito.mock( Servlet.class );

        final ServletMapping mapping = MappingBuilder.newBuilder().
            order( 10 ).
            name( "test" ).
            initParam( "a", "1" ).
            urlPatterns( "/*" ).
            servlet( servlet );

        return ResourceDefinitionFactory.create( mapping );
    }

    @Test
    public void testReport()
    {
        final List<ServletDefinition> list = Lists.newArrayList( newDefinition() );

        final ServletPipeline pipeline = Mockito.mock( ServletPipeline.class );
        Mockito.when( pipeline.iterator() ).thenReturn( list.iterator() );

        final ServletStatusReporter reporter = new ServletStatusReporter();
        reporter.setPipeline( pipeline );

        assertEquals( "http.servlet", reporter.getName() );
        assertNotNull( reporter.getReport() );
    }
}
