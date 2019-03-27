package com.enonic.xp.web.impl.dispatch.mapping;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.common.collect.Lists;

import com.enonic.xp.web.dispatch.MappingBuilder;

import static org.junit.Assert.*;

public class FilterDefinitionImplTest
    extends ResourceDefinitionImplTest<Filter, FilterDefinition>
{
    @Override
    Filter newResource()
    {
        return Mockito.mock( Filter.class );
    }

    @Override
    FilterDefinition newDefinition()
    {
        final MappingBuilder builder = MappingBuilder.newBuilder();
        configure( builder );

        return ResourceDefinitionFactory.create( builder.filter( this.resource ) );
    }

    @Test
    public void create_noAnnotations()
    {
        assertNull( ResourceDefinitionFactory.create( this.resource, Lists.newArrayList() ) );
    }

    @Override
    void verifyInit( final int times )
        throws Exception
    {
        Mockito.verify( this.resource, Mockito.times( times ) ).init( Mockito.any() );
    }

    @Override
    void verifyDestroy( final int times )
    {
        Mockito.verify( this.resource, Mockito.times( times ) ).destroy();
    }

    @Override
    void setupInitException()
        throws Exception
    {
        Mockito.doThrow( new ServletException( "test" ) ).when( this.resource ).init( Mockito.any() );
    }

    @Test
    public void doFilter()
        throws Exception
    {
        final FilterDefinition def = newDefinition();
        def.init( this.context );

        final MockHttpServletRequest req = new MockHttpServletRequest();
        final HttpServletResponse res = Mockito.mock( HttpServletResponse.class );
        final FilterChain chain = Mockito.mock( FilterChain.class );

        req.setRequestURI( "/b" );
        assertFalse( def.doFilter( req, res, chain ) );
        Mockito.verify( this.resource, Mockito.times( 0 ) ).doFilter( req, res, chain );

        req.setRequestURI( "/a/b/c" );
        assertTrue( def.doFilter( req, res, chain ) );
        Mockito.verify( this.resource, Mockito.times( 1 ) ).doFilter( req, res, chain );
    }
}
