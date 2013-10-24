package com.enonic.wem.core.servlet;

import java.util.Set;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.util.Types;

import static org.junit.Assert.*;

public class WebInitializerBinderTest
{
    @Test
    public void bindTask()
    {
        final AbstractModule module = new AbstractModule()
        {
            @Override
            protected void configure()
            {
                WebInitializerBinder.from( binder() ).add( ServletWebInitializer.class );
            }
        };

        final Injector injector = Guice.createInjector( module );
        final Object result = injector.getInstance( Key.get( Types.setOf( WebInitializer.class ) ) );

        assertNotNull( result );

        final Set<WebInitializer> set = castToSet( result );
        assertEquals( 1, set.size() );
        assertSame( ServletWebInitializer.class, set.iterator().next().getClass() );
    }

    @SuppressWarnings("unchecked")
    private Set<WebInitializer> castToSet( final Object o )
    {
        return (Set<WebInitializer>) o;
    }
}
