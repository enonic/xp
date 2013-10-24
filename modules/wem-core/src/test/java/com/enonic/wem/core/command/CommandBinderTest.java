package com.enonic.wem.core.command;

import java.util.Map;

import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.util.Types;

import static org.junit.Assert.*;

public class CommandBinderTest
{
    @Test
    public void bindTask()
    {
        final AbstractModule module = new AbstractModule()
        {
            @Override
            protected void configure()
            {
                CommandBinder.from( binder() ).add( TestCommandHandler.class );
            }
        };

        final Injector injector = Guice.createInjector( module );
        final Object result = injector.getInstance( Key.get( Types.mapOf( Class.class, CommandHandler.class ) ) );

        assertNotNull( result );

        final Map<Class, CommandHandler> map = castToMap( result );
        assertEquals( 1, map.size() );

        assertNotNull( map.get( TestCommand.class ) );
        assertSame( TestCommandHandler.class, map.get( TestCommand.class ).getClass() );
    }

    @SuppressWarnings("unchecked")
    private Map<Class, CommandHandler> castToMap( final Object o )
    {
        return (Map<Class, CommandHandler>) o;
    }
}
