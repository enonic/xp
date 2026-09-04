package com.enonic.xp.web.impl.dispatch.mapping;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.dispatch.FilterMapping;
import com.enonic.xp.web.dispatch.MappingBuilder;
import com.enonic.xp.web.dispatch.ServletMapping;
import com.enonic.xp.web.filter.BaseWebFilter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResourceDefinitionFactoryTest
{
    @WebFilter("/*")
    private static final class AnnotatedFilter
        implements Filter
    {
        @Override
        public void doFilter( final ServletRequest req, final ServletResponse res, final FilterChain chain )
            throws IOException, ServletException
        {
            chain.doFilter( req, res );
        }
    }

    private abstract static class AnnotatedFilterBase
        implements Filter
    {
        @Override
        public void doFilter( final ServletRequest req, final ServletResponse res, final FilterChain chain )
            throws IOException, ServletException
        {
            chain.doFilter( req, res );
        }
    }

    private static final class UnannotatedFilter
        implements Filter
    {
        @Override
        public void doFilter( final ServletRequest req, final ServletResponse res, final FilterChain chain )
            throws IOException, ServletException
        {
            chain.doFilter( req, res );
        }
    }

    @WebFilter
    private static final class FilterWithoutUrlPatterns
        implements Filter
    {
        @Override
        public void doFilter( final ServletRequest req, final ServletResponse res, final FilterChain chain )
            throws IOException, ServletException
        {
            chain.doFilter( req, res );
        }
    }

    @WebServlet("/*")
    private static class AnnotatedServlet
        extends HttpServlet
    {
    }

    private static final class UnannotatedServlet
        extends HttpServlet
    {
    }

    private static final class LifecycleFilter
        extends AnnotatedFilterBase
    {
        @Override
        public void init( final FilterConfig config )
        {
            // expects a lifecycle XP does not drive
        }
    }

    private static final class DestroyingFilter
        extends AnnotatedFilterBase
    {
        @Override
        public void destroy()
        {
            // expects a lifecycle XP does not drive
        }
    }

    private static final class BaseWebFilterSubclass
        extends BaseWebFilter
    {
        @Override
        protected void doFilter( final HttpServletRequest req, final HttpServletResponse res, final FilterChain chain )
        {
            // Do nothing
        }
    }

    private static final class LifecycleServlet
        extends AnnotatedServlet
    {
        @Override
        public void init()
        {
            // expects a lifecycle XP does not drive
        }
    }

    @Test
    void declaresLifecycle_filter()
    {
        assertFalse( ResourceDefinitionFactory.declaresLifecycle( new AnnotatedFilter() ) );
        assertFalse( ResourceDefinitionFactory.declaresLifecycle( new BaseWebFilterSubclass() ) );

        assertTrue( ResourceDefinitionFactory.declaresLifecycle( new LifecycleFilter() ) );
        assertTrue( ResourceDefinitionFactory.declaresLifecycle( new DestroyingFilter() ) );
    }

    @Test
    void declaresLifecycle_servlet()
    {
        assertFalse( ResourceDefinitionFactory.declaresLifecycle( new AnnotatedServlet() ) );

        assertTrue( ResourceDefinitionFactory.declaresLifecycle( new LifecycleServlet() ) );
    }

    @Test
    void testNew()
    {
        new ResourceDefinitionFactory();
    }

    @Test
    void filter_annotated()
    {
        assertNotNull( ResourceDefinitionFactory.create( new AnnotatedFilter(), List.of( "xp" ) ) );
    }

    @Test
    void filter_withoutAnnotation()
    {
        // a Filter service without @WebFilter cannot be mapped and is rejected, it used to be dropped silently
        assertNull( ResourceDefinitionFactory.create( new UnannotatedFilter(), List.of( "xp" ) ) );
    }

    @Test
    void filter_withoutUrlPatterns()
    {
        // a definition without url patterns matches no request at all, so it is rejected outright
        assertNull( ResourceDefinitionFactory.create( new FilterWithoutUrlPatterns(), List.of( "xp" ) ) );
    }

    @Test
    void filterMapping_withoutUrlPatterns()
    {
        final Filter filter = new AnnotatedFilter();

        assertNull( ResourceDefinitionFactory.create( MappingBuilder.newBuilder().connector( "xp" ).filter( filter ) ) );
        assertNotNull(
            ResourceDefinitionFactory.create( MappingBuilder.newBuilder().connector( "xp" ).urlPatterns( "/*" ).filter( filter ) ) );
    }

    @Test
    void filterMapping_null()
    {
        assertNull( ResourceDefinitionFactory.create( (FilterMapping) null ) );
    }

    @Test
    void servlet_annotated()
    {
        assertNotNull( ResourceDefinitionFactory.create( new AnnotatedServlet(), List.of( "xp" ) ) );
    }

    @Test
    void servlet_withoutAnnotation()
    {
        assertNull( ResourceDefinitionFactory.create( new UnannotatedServlet(), List.of( "xp" ) ) );
    }

    @Test
    void servletMapping_withoutUrlPatterns()
    {
        final Servlet servlet = new AnnotatedServlet();

        assertNull( ResourceDefinitionFactory.create( MappingBuilder.newBuilder().connector( "xp" ).servlet( servlet ) ) );
        assertNotNull(
            ResourceDefinitionFactory.create( MappingBuilder.newBuilder().connector( "xp" ).urlPatterns( "/*" ).servlet( servlet ) ) );
    }

    @Test
    void servletMapping_null()
    {
        assertNull( ResourceDefinitionFactory.create( (ServletMapping) null ) );
    }
}
