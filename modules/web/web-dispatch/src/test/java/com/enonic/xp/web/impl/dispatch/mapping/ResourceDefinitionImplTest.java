package com.enonic.xp.web.impl.dispatch.mapping;

import jakarta.servlet.ServletContext;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.web.dispatch.MappingBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public abstract class ResourceDefinitionImplTest<T, D extends ResourceDefinition<T>>
{
    T resource;

    ServletContext context;

    @BeforeEach
    public final void setup()
    {
        this.resource = newResource();
        this.context = Mockito.mock( ServletContext.class );
    }

    abstract T newResource();

    abstract D newDefinition();

    final void configure( final MappingBuilder builder )
    {
        builder.order( 10 );
        builder.name( "test" );
        builder.initParam( "a", "1" );
        builder.urlPatterns( "/a/*" );
    }

    @Test
    public void testCreate()
    {
        final D def = newDefinition();
        assertEquals( 10, def.getOrder() );
        assertEquals( "test", def.getName() );
        assertEquals( "{a=1}", def.getInitParams().toString() );
        assertEquals( "[/a/*]", def.getUrlPatterns().toString() );
        assertEquals( this.resource, def.getResource() );
    }

    @Test
    public void testMatches()
    {
        final ResourceDefinitionImpl def = (ResourceDefinitionImpl) newDefinition();
        assertFalse( def.matches( "/a/b/c" ) );

        def.init( this.context );
        assertFalse( def.matches( "/b" ) );
        assertTrue( def.matches( "/a/b/c" ) );
    }

    abstract void verifyInit( int times )
        throws Exception;

    abstract void verifyDestroy( int times );

    abstract void setupInitException()
        throws Exception;

    @Test
    public void testInitDestroy()
        throws Exception
    {
        final D def = newDefinition();

        def.destroy();
        verifyDestroy( 0 );
        verifyInit( 0 );

        def.init( this.context );
        verifyInit( 1 );

        def.init( this.context );
        verifyInit( 1 );

        def.destroy();
        verifyDestroy( 1 );

        def.destroy();
        verifyDestroy( 1 );
    }

    @Test
    public void testInitException()
        throws Exception
    {
        final D def = newDefinition();
        setupInitException();
        def.init( this.context );
    }
}
