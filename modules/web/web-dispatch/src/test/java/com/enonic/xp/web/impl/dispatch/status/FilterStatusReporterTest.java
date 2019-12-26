package com.enonic.xp.web.impl.dispatch.status;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.status.JsonStatusReporterTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FilterStatusReporterTest
    extends JsonStatusReporterTest
{
    @Test
    public void testReport()
        throws Exception
    {
        final Filter filter1 = new MyFilter();
        final Filter filter2 = new MyFilter();

        final ServiceReference<Filter> serviceReference1 = Mockito.mock( ServiceReference.class );
        final ServiceReference<Filter> serviceReference2 = Mockito.mock( ServiceReference.class );

        Mockito.when( serviceReference1.getProperty( "connector" ) ).thenReturn( "a" );
        Mockito.when( serviceReference2.getProperty( "connector" ) ).thenReturn( new String[]{"a", "b"} );

        final FilterStatusReporter reporter = new FilterStatusReporter();
        reporter.addFilter( filter1, serviceReference1 );
        reporter.addFilter( filter2, serviceReference2 );

        assertEquals( "http.filter", reporter.getName() );
        assertEquals( parseJson( readFromFile( "filter_status_report.json" ) ), reporter.getReport() );
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
