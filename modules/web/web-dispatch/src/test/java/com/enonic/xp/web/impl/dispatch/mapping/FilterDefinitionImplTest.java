package com.enonic.xp.web.impl.dispatch.mapping;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.dispatch.MappingBuilder;

import static org.mockito.Mockito.mock;

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
    void doFilter()
        throws Exception
    {
        final FilterDefinition def = newDefinition();

        final HttpServletRequest req = mock( HttpServletRequest.class );
        final HttpServletResponse res = mock( HttpServletResponse.class );
        final FilterChain chain = mock( FilterChain.class );

        def.doFilter( req, res, chain );

        Mockito.verify( this.resource, Mockito.times( 1 ) ).doFilter( req, res, chain );
    }
}
