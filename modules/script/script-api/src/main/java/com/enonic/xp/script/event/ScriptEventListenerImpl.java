package com.enonic.xp.script.event;

import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.event.Event;
import com.enonic.xp.script.serializer.MapSerializable;

final class ScriptEventListenerImpl
    implements ScriptEventListener
{
    private static final Logger LOG = LoggerFactory.getLogger( ScriptEventListenerImpl.class );

    private final ApplicationKey application;

    private final Pattern typePattern;

    private final Consumer<Object> listener;

    private final boolean localOnly;

    ScriptEventListenerImpl( final ApplicationKey application, final Pattern typePattern, final Consumer<Object> listener,
                             final boolean localOnly )
    {
        this.application = application;
        this.typePattern = typePattern;
        this.listener = listener;
        this.localOnly = localOnly;
    }

    @Override
    public ApplicationKey getApplication()
    {
        return this.application;
    }

    @Override
    public void onEvent( final Event event )
    {
        if ( this.localOnly && !event.isLocalOrigin() )
        {
            return;
        }

        if ( !this.typePattern.matcher( event.getType() ).matches() )
        {
            return;
        }

        try
        {
            this.listener.accept( toJson( event ) );
        }
        catch ( final Exception e )
        {
            LOG.error( "Error handling event", e );
        }
    }

    private static MapSerializable toJson( final Event event )
    {
        return gen -> {
            gen.value( "type", event.getType() );
            gen.value( "timestamp", event.getTimestamp() );
            gen.value( "localOrigin", event.isLocalOrigin() );
            gen.value( "distributed", event.isDistributed() );

            gen.map( "data" );
            for ( final Map.Entry<String, Object> entry : event.getData().entrySet() )
            {
                gen.value( entry.getKey(), entry.getValue() );
            }
            gen.end();
        };
    }
}
