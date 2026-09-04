package com.enonic.xp.web.impl.dispatch.mapping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.web.dispatch.MappingBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class ResourceDefinitionImplTest<T, D extends ResourceDefinition<T>>
{
    T resource;

    @BeforeEach
    public final void setup()
    {
        this.resource = newResource();
    }

    abstract T newResource();

    abstract D newDefinition();

    final void configure( final MappingBuilder builder )
    {
        builder.order( 10 );
        builder.name( "test" );
        builder.urlPatterns( "/a/*" );
    }

    @Test
    void testCreate()
    {
        final D def = newDefinition();
        assertEquals( 10, def.getOrder() );
        assertEquals( "test", def.getName() );
        assertEquals( "[/a/*]", def.getUrlPatterns().toString() );
        assertEquals( this.resource, def.getResource() );
    }

    @Test
    void testMatches()
    {
        // a definition is ready to serve as soon as it exists, there is nothing to initialize
        final ResourceDefinitionImpl<?> def = (ResourceDefinitionImpl<?>) newDefinition();

        assertFalse( def.matches( null ) );
        assertFalse( def.matches( "/b" ) );
        assertTrue( def.matches( "/a/b/c" ) );
    }
}
