package com.enonic.xp.web.impl.dispatch.status;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.net.MediaType;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;

import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinitionFactory;
import com.enonic.xp.web.impl.dispatch.pipeline.FilterPipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class FilterStatusReporterTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    @Test
    public void testReport()
        throws Exception
    {
        final Filter filter1 = new MyFilter();
        final Filter filter2 = new MyFilter();

        final FilterPipeline filterPipeline = Mockito.mock( FilterPipeline.class );
        when( filterPipeline.list() ).thenReturn( List.of( ResourceDefinitionFactory.create( filter1, List.of( "a" ) ),
                                                           ResourceDefinitionFactory.create( filter2,
                                                                                                               List.of( "a", "b" ) ) ) );

        final FilterStatusReporter reporter = new FilterStatusReporter( filterPipeline );

        assertEquals( "http.filter", reporter.getName() );
        assertJson( "filter_status_report.json", reporter );
    }

    private void assertJson( final String fileName, final StatusReporter reporter )
        throws Exception
    {
        assertEquals( MediaType.JSON_UTF_8, reporter.getMediaType() );

        final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        reporter.report( outputStream );

        jsonTestHelper.assertJsonEquals( jsonTestHelper.loadTestJson( fileName ),
                                         jsonTestHelper.bytesToJson( outputStream.toByteArray() ) );
    }

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
}
