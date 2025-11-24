package com.enonic.xp.script.impl.bean;

import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.impl.service.ServiceRef;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.runtime.ScriptSettings;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BeanContextImplTest
{
    private ResourceKey resourceKey;

    private ServiceRegistry serviceRegistry;

    @BeforeEach
    public void setup()
    {
        this.resourceKey = ResourceKey.from( "myapp:/a/b" );
        this.serviceRegistry = mock( ServiceRegistry.class );
    }

    @Test
    public void getResourceKey()
    {
        final BeanContextImpl context = new BeanContextImpl(this.resourceKey, this.serviceRegistry, mock(), mock() );
        assertEquals( this.resourceKey, context.getResourceKey() );
    }

    @Test
    public void getApplicationKey()
    {
        final BeanContextImpl context = new BeanContextImpl(this.resourceKey, this.serviceRegistry, mock(), mock() );
        assertEquals( this.resourceKey.getApplicationKey(), context.getApplicationKey() );
    }

    @Test
    public void getService()
    {
        final ServiceRef ref = mockServiceRef( this.serviceRegistry, String.class );
        final BeanContextImpl context = new BeanContextImpl(this.resourceKey, this.serviceRegistry, mock(), mock() );

        assertSame( ref, context.getService( String.class ) );
    }

    @Test
    public void getBinding()
    {

        final Supplier<String> binding = () -> "hello";

        final ScriptSettings settings = ScriptSettings.create().
            binding( String.class, binding ).
            build();
        final BeanContextImpl context = new BeanContextImpl(this.resourceKey, this.serviceRegistry, settings, mock() );


        assertNotNull( context.getBinding( String.class ) );
        assertSame( "hello", context.getBinding( String.class ).get() );

        assertNotNull( context.getBinding( Integer.class ) );
        assertNull( context.getBinding( Integer.class ).get() );
    }

    private ServiceRef mockServiceRef( final ServiceRegistry registry, final Class type )
    {
        final ServiceRef ref = mock( ServiceRef.class );
        when( registry.getService( type ) ).thenReturn( ref );
        return ref;
    }
}
