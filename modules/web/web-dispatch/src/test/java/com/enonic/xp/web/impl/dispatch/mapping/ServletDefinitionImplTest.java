package com.enonic.xp.web.impl.dispatch.mapping;

import org.junit.jupiter.api.Test;

import jakarta.servlet.Servlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.dispatch.MappingBuilder;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class ServletDefinitionImplTest
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
    void service()
        throws Exception
    {
        final ServletDefinition def = newDefinition();

        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );

        def.service( req, res );

        verify( this.resource, times( 1 ) ).service( req, res );
    }
}
