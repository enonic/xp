package com.enonic.xp.core.impl.i18n;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import com.google.common.collect.Maps;

import com.enonic.xp.i18n.MessageBundle;

@NullMarked
final class MessageBundleImpl
    implements MessageBundle
{
    private final Properties properties;

    private final Locale locale;

    MessageBundleImpl( final Properties properties, final Locale locale )
    {
        this.properties = properties;
        this.locale = locale;
    }

    @Override
    public Set<String> getKeys()
    {
        return this.properties.stringPropertyNames();
    }

    @Override
    public @Nullable String localize( final String key, final @Nullable Object... args )
    {
        final String message = this.properties.getProperty( key, "" );
        return message.isEmpty() ? null : format( message, args );
    }

    @Override
    public @Nullable String getMessage( final String key )
    {
        return this.properties.getProperty( key );
    }

    private String format( final String message, final @Nullable Object[] args )
    {
        if ( args.length == 0 )
        {
            return message;
        }
        final MessageFormat mf = new MessageFormat( message, locale );
        final Object[] formats = mf.getFormats();
        for ( final Object format : formats )
        {
            if ( format instanceof DateFormat )
            {
                ( (DateFormat) format ).setTimeZone( TimeZone.getTimeZone( ZoneOffset.UTC ) );
            }
        }
        return mf.format( args );
    }

    @Override
    public Map<String, String> asMap()
    {
        return Maps.fromProperties( this.properties );
    }
}
