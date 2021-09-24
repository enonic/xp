package com.enonic.xp.core.impl.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.common.collect.Maps;

import com.enonic.xp.i18n.MessageBundle;

import static com.google.common.base.Strings.isNullOrEmpty;

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
    public String localize( final String key, final Object... args )
    {
        final String message = this.properties.getProperty( key, "" );
        return message.isEmpty() ? null : format( message, args );
    }

    @Override
    public String getMessage( final String key )
    {
        return this.properties.getProperty( key );
    }

    private String format( final String message, final Object[] args )
    {
        return locale != null ? new MessageFormat( message, locale ).format( args ) : MessageFormat.format( message, args );
    }

    @Override
    public Map<String, String> asMap()
    {
        return Maps.fromProperties( this.properties );
    }
}
