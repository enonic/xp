package com.enonic.xp.web.impl.dispatch.status;

import javax.servlet.Servlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.osgi.framework.ServiceReference;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.status.JsonStatusReporterTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ServletStatusReporterTest
    extends JsonStatusReporterTest
{
    @Test
    public void testReport()
        throws Exception
    {
        final Servlet servlet1 = new MyServlet1();
        final Servlet servlet2 = new MyServlet2();

        final ServiceReference<Servlet> serviceReference1 = Mockito.mock( ServiceReference.class );
        final ServiceReference<Servlet> serviceReference2 = Mockito.mock( ServiceReference.class );

        Mockito.when( serviceReference1.getProperty( "connector" ) ).thenReturn( "a" );
        Mockito.when( serviceReference2.getProperty( "connector" ) ).thenReturn( new String[]{"a", "b"} );

        final ServletStatusReporter reporter = new ServletStatusReporter();
        reporter.addServlet( servlet1, serviceReference1 );
        reporter.addServlet( servlet2, serviceReference2 );

        assertEquals( "http.servlet", reporter.getName() );
        assertEquals( parseJson( readFromFile( "servlet_status_report.json" ) ), reporter.getReport() );
    }

    @Order(10)
    @WebServlet(name = "test1", value = "/321", urlPatterns = "/*", initParams = @WebInitParam(name = "a", value = "1"))
    static final class MyServlet1
        extends HttpServlet
    {
    }

    @Order(20)
    @WebServlet(name = "test2", value = "/123", urlPatterns = "/*/", initParams = @WebInitParam(name = "b", value = "2"))
    static final class MyServlet2
        extends HttpServlet
    {
    }
}
