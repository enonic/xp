package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.ArrayList;

import jakarta.servlet.Servlet;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.dispatch.MappingBuilder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServletDefinitionImplTest
    extends ResourceDefinitionImplTest<Servlet, ServletDefinition>
{
    @Override
    Servlet newResource()
    {
        return mock( Servlet.class );
    }

    @Override
    ServletDefinition newDefinition()
    {
        final MappingBuilder builder = MappingBuilder.newBuilder();
        configure( builder );

        return ResourceDefinitionFactory.create( builder.servlet( this.resource ) );
    }

    @Test
    public void create_noAnnotations()
    {
        assertNull( ResourceDefinitionFactory.create( this.resource, new ArrayList<>() ) );
    }

    @Override
    void verifyInit( final int times )
        throws Exception
    {
        verify( this.resource, times( times ) ).init( any() );
    }

    @Override
    void verifyDestroy( final int times )
    {
        verify( this.resource, times( times ) ).destroy();
    }

    @Override
    void setupInitException()
        throws Exception
    {
        doThrow( new ServletException( "test" ) ).when( this.resource ).init( any() );
    }

    @Test
    public void service()
        throws Exception
    {
        final ServletDefinition def = newDefinition();
        def.init( this.context );

        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );

        when( req.getRequestURI() ).thenReturn( "/b" );
        assertFalse( def.service( req, res ) );
        verify( this.resource, times( 0 ) ).service( req, res );

        when( req.getRequestURI() ).thenReturn( "/a/b/c" );
        assertTrue( def.service( req, res ) );
        verify( this.resource, times( 1 ) ).service( req, res );
    }
}
