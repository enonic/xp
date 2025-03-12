package com.enonic.xp.web.impl.dispatch.status;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import jakarta.servlet.Servlet;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.status.StatusReporter;
import com.enonic.xp.support.JsonTestHelper;
import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinitionFactory;
import com.enonic.xp.web.impl.dispatch.pipeline.ServletPipeline;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServletStatusReporterTest
{
    JsonTestHelper jsonTestHelper = new JsonTestHelper( this );

    @Test
    public void testReport()
        throws Exception
    {
        final Servlet servlet1 = new MyServlet1();
        final Servlet servlet2 = new MyServlet2();

        final ServletPipeline servletPipeline = mock( ServletPipeline.class );
        when( servletPipeline.list() ).thenReturn( List.of( ResourceDefinitionFactory.create( servlet1, List.of( "a" ) ),
                                                            ResourceDefinitionFactory.create( servlet2,
                                                                                                                List.of( "a", "b" ) ) ) );
        final ServletStatusReporter reporter = new ServletStatusReporter( servletPipeline );

        assertEquals( "http.servlet", reporter.getName() );
        assertJson( "servlet_status_report.json", reporter );
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
