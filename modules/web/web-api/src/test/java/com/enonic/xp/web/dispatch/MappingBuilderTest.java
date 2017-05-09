package com.enonic.xp.web.dispatch;

import javax.servlet.Filter;
import javax.servlet.Servlet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class MappingBuilderTest
{
    private Filter filter;

    private Servlet servlet;

    @Before
    public void setup()
    {
        this.filter = Mockito.mock( Filter.class );
        this.servlet = Mockito.mock( Servlet.class );
    }

    @Test
    public void testFilter_default()
    {
        final FilterMapping mapping = MappingBuilder.newBuilder().filter( this.filter );
        assertSame( this.filter, mapping.getResource() );
        assertDefault( mapping );
    }

    @Test
    public void testServlet_default()
    {
        final ServletMapping mapping = MappingBuilder.newBuilder().servlet( this.servlet );
        assertSame( this.servlet, mapping.getResource() );
        assertDefault( mapping );
    }

    @Test
    public void testFilter_values()
    {
        final MappingBuilder builder = MappingBuilder.newBuilder();
        buildValues( builder );

        final FilterMapping mapping = builder.filter( this.filter );
        assertSame( this.filter, mapping.getResource() );
        assertValues( mapping );
    }

    @Test
    public void testServlet_values()
    {
        final MappingBuilder builder = MappingBuilder.newBuilder();
        buildValues( builder );

        final ServletMapping mapping = builder.servlet( this.servlet );
        assertSame( this.servlet, mapping.getResource() );
        assertValues( mapping );
    }

    private void assertDefault( final ResourceMapping mapping )
    {
        assertEquals( 0, mapping.getOrder() );
        assertEquals( mapping.getResource().getClass().getSimpleName(), mapping.getName() );
        assertEquals( "{}", mapping.getInitParams().toString() );
        assertEquals( "[]", mapping.getUrlPatterns().toString() );
    }

    private void assertValues( final ResourceMapping mapping )
    {
        assertEquals( 10, mapping.getOrder() );
        assertEquals( "test", mapping.getName() );
        assertEquals( "{a=1, b=2}", mapping.getInitParams().toString() );
        assertEquals( "[/, /*]", mapping.getUrlPatterns().toString() );
    }

    private void buildValues( final MappingBuilder builder )
    {
        builder.order( 10 );
        builder.name( "test" );
        builder.initParam( "a", "1" );
        builder.initParam( "b", "2" );
        builder.urlPatterns( "/", "/*" );
    }

    @Test(expected = NullPointerException.class)
    public void testFilter_null()
    {
        MappingBuilder.newBuilder().filter( null );
    }

    @Test(expected = NullPointerException.class)
    public void testServlet_null()
    {
        MappingBuilder.newBuilder().servlet( null );
    }
}
