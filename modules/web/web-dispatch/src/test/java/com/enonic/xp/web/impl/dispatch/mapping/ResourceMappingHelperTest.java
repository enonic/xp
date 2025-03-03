package com.enonic.xp.web.impl.dispatch.mapping;

import java.io.IOException;
import java.util.ArrayList;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebInitParam;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.annotation.Order;
import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.dispatch.MappingBuilder;
import com.enonic.xp.web.dispatch.ResourceMapping;
import com.enonic.xp.web.dispatch.ServletMapping;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class ResourceMappingHelperTest
{
    @Order(10)
    @WebServlet(name = "test", value = "/", urlPatterns = "/*", initParams = @WebInitParam(name = "a", value = "1"))
    static final class MyServlet
        extends HttpServlet
    {
    }

    @Order(10)
    @WebFilter(filterName = "test", value = "/", urlPatterns = "/*", initParams = @WebInitParam(name = "a", value = "1"))
    static final class MyFilter
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

    private Filter mockFilter;

    private Servlet mockServlet;

    @BeforeEach
    public void setup()
    {
        this.mockFilter = Mockito.mock( Filter.class );
        this.mockServlet = Mockito.mock( Servlet.class );
    }

    @Test
    public void testFilter_none()
    {
        final FilterMapping mapping = ResourceMappingHelper.filter( this.mockFilter, new ArrayList<>() );
        assertNull( mapping );
    }

    @Test
    public void testNew()
    {
        new ResourceMappingHelper();
    }

    @Test
    public void testServlet_none()
    {
        final ServletMapping mapping = ResourceMappingHelper.servlet( this.mockServlet, new ArrayList<>() );
        assertNull( mapping );
    }

    @Test
    public void testConfigureOrder_null()
    {
        final MappingBuilder builder = MappingBuilder.newBuilder();

        final FilterMapping mapping1 = builder.filter( this.mockFilter );
        assertEquals( 0, mapping1.getOrder() );

        final MyFilter filter2 = new MyFilter();
        ResourceMappingHelper.configureOrder( builder, filter2 );

        final FilterMapping mapping2 = builder.filter( filter2 );
        assertEquals( 10, mapping2.getOrder() );
    }

    @Test
    public void testConfigure_servlet()
    {
        final MyServlet servlet = new MyServlet();
        final ServletMapping mapping = ResourceMappingHelper.servlet( servlet, new ArrayList<>() );

        assertNotNull( mapping );
        assertSame( servlet, mapping.getResource() );

        assertMapping( mapping );
    }

    @Test
    public void testConfigure_filter()
    {
        final MyFilter filter = new MyFilter();
        final FilterMapping mapping = ResourceMappingHelper.filter( filter, new ArrayList<>() );

        assertNotNull( mapping );
        assertSame( filter, mapping.getResource() );

        assertMapping( mapping );
    }

    private void assertMapping( final ResourceMapping<?> mapping )
    {
        assertEquals( 10, mapping.getOrder() );
        assertEquals( "test", mapping.getName() );
        assertEquals( "{a=1}", mapping.getInitParams().toString() );
        assertEquals( "[/, /*]", mapping.getUrlPatterns().toString() );
    }
}
