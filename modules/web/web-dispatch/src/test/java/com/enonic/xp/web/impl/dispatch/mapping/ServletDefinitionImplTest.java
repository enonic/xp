package com.enonic.xp.web.impl.dispatch.mapping;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

import com.google.common.collect.Lists;

import com.enonic.xp.web.dispatch.MappingBuilder;

import static org.junit.Assert.*;

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
    public void service()
        throws Exception
    {
        final ServletDefinition def = newDefinition();
        def.init( this.context );

        final MockHttpServletRequest req = new MockHttpServletRequest();
        final HttpServletResponse res = Mockito.mock( HttpServletResponse.class );

        req.setRequestURI( "/b" );
        assertFalse( def.service( req, res ) );
        Mockito.verify( this.resource, Mockito.times( 0 ) ).service( req, res );

        req.setRequestURI( "/a/b/c" );
        assertTrue( def.service( req, res ) );
        Mockito.verify( this.resource, Mockito.times( 1 ) ).service( req, res );
    }
}
