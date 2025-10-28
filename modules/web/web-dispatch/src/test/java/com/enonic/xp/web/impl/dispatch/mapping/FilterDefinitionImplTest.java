package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.dispatch.MappingBuilder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FilterDefinitionImplTest
    extends ResourceDefinitionImplTest<Filter, FilterDefinition>
{
    @Override
    Filter newResource()
    {
        return mock( Filter.class );
    }

    @Override
    FilterDefinition newDefinition()
    {
        final MappingBuilder builder = MappingBuilder.newBuilder();
        configure( builder );

        return ResourceDefinitionFactory.create( builder.filter( this.resource ) );
    }

    @Test
    void create_noAnnotations()
    {
        assertNull( ResourceDefinitionFactory.create( this.resource, new ArrayList<>() ) );
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
    void doFilter()
        throws Exception
    {
        final FilterDefinition def = newDefinition();
        def.init( this.context );

        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        when( req.getRequestURI() ).thenReturn( "/b" );
        assertFalse( def.doFilter( req, res, chain ) );
        Mockito.verify( this.resource, Mockito.times( 0 ) ).doFilter( req, res, chain );

        when( req.getRequestURI() ).thenReturn( "/a/b/c" );
        assertTrue( def.doFilter( req, res, chain ) );
        Mockito.verify( this.resource, Mockito.times( 1 ) ).doFilter( req, res, chain );
    }
}
