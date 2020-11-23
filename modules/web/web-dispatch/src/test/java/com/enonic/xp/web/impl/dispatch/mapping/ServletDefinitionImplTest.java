package com.enonic.xp.web.impl.dispatch.mapping;

import java.util.ArrayList;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.web.dispatch.MappingBuilder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServletDefinitionImplTest
    extends ResourceDefinitionImplTest<Servlet, ServletDefinition>
{
    @Override
    Servlet newResource()
    {
        return Mockito.mock( Servlet.class );
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
    public void service()
        throws Exception
    {
        final ServletDefinition def = newDefinition();
        def.init( this.context );

        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = Mockito.mock( HttpServletResponse.class );

        when( req.getRequestURI() ).thenReturn( "/b" );
        assertFalse( def.service( req, res ) );
        Mockito.verify( this.resource, Mockito.times( 0 ) ).service( req, res );

        when( req.getRequestURI() ).thenReturn( "/a/b/c" );
        assertTrue( def.service( req, res ) );
        Mockito.verify( this.resource, Mockito.times( 1 ) ).service( req, res );
    }
}
