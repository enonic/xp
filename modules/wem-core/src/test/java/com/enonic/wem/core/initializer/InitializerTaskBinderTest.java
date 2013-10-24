package com.enonic.wem.core.initializer;

import java.util.Set;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.util.Types;

import static org.junit.Assert.*;

public class InitializerTaskBinderTest
{
    @Test
    public void bindTask()
    {
        final AbstractModule module = new AbstractModule()
        {
            @Override
            protected void configure()
            {
                InitializerTaskBinder.from( binder() ).add( TestInitializerTask.class );
            }
        };

        final Injector injector = Guice.createInjector( module );
        final Object result = injector.getInstance( Key.get( Types.setOf( InitializerTask.class ) ) );

        assertNotNull( result );

        final Set<InitializerTask> set = castToSet( result );
        assertEquals( 1, set.size() );
        assertSame( TestInitializerTask.class, set.iterator().next().getClass() );
    }

    @SuppressWarnings("unchecked")
    private Set<InitializerTask> castToSet( final Object o )
    {
        return (Set<InitializerTask>) o;
    }
}
