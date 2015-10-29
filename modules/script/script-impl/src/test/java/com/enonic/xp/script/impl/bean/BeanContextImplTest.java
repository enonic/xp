package com.enonic.xp.script.impl.bean;

import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.script.impl.executor.ScriptExecutor;
import com.enonic.xp.script.impl.service.ServiceRef;
import com.enonic.xp.script.impl.service.ServiceRegistry;
import com.enonic.xp.script.runtime.ScriptSettings;

import static org.junit.Assert.*;

public class BeanContextImplTest
{
    private BeanContextImpl context;

    private ResourceKey resourceKey;

    private ServiceRegistry serviceRegistry;

    private ScriptExecutor executor;

    @Before
    public void setup()
    {
        this.resourceKey = ResourceKey.from( "myapp:/a/b" );
        this.serviceRegistry = Mockito.mock( ServiceRegistry.class );

        this.executor = Mockito.mock( ScriptExecutor.class );
        Mockito.when( this.executor.getServiceRegistry() ).thenReturn( this.serviceRegistry );

        this.context = new BeanContextImpl();
        this.context.setResourceKey( this.resourceKey );
        this.context.setExecutor( this.executor );
    }

    @Test
    public void getResourceKey()
    {
        assertEquals( this.resourceKey, this.context.getResourceKey() );
    }

    @Test
    public void getApplicationKey()
    {
        assertEquals( this.resourceKey.getApplicationKey(), this.context.getApplicationKey() );
    }

    @Test
    public void getService()
    {
        final ServiceRef ref = mockServiceRef( this.serviceRegistry, String.class );
        assertSame( ref, this.context.getService( String.class ) );
    }

    @Test
    public void getBinding()
    {
        final Supplier<String> binding = () -> "hello";

        final ScriptSettings settings = ScriptSettings.create().
            binding( String.class, binding ).
            build();

        Mockito.when( this.executor.getScriptSettings() ).thenReturn( settings );

        assertNotNull( this.context.getBinding( String.class ) );
        assertSame( "hello", this.context.getBinding( String.class ).get() );

        assertNotNull( this.context.getBinding( Integer.class ) );
        assertNull( this.context.getBinding( Integer.class ).get() );
    }

    private ServiceRef mockServiceRef( final ServiceRegistry registry, final Class type )
    {
        final ServiceRef ref = Mockito.mock( ServiceRef.class );
        Mockito.when( registry.getService( type ) ).thenReturn( ref );
        return ref;
    }
}
