package com.enonic.xp.core.impl.i18n;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.enonic.xp.i18n.MessageBundle;

final class MessageBundleImpl
    implements MessageBundle
{
    private final Properties properties;

    MessageBundleImpl( final Properties properties )
    {
        this.properties = properties;
    }

    @Override
    public Set<String> getKeys()
    {
        return this.properties.keySet().stream().map( Object::toString ).collect( Collectors.toSet() );
    }

    @Override
    public String localize( final String key, final Object... args )
    {
        final String message = doGetMessage( key );
        return StringUtils.isNotEmpty( message ) ? format( message, args ) : null;
    }

    @Override
    public String getMessage( final String key )
    {
        return doGetMessage( key );
    }

    private String format( final String message, final Object[] args )
    {
        return MessageFormat.format( message, args );
    }

    private String doGetMessage( final String key )
    {
        return this.properties.getProperty( key, "" );
    }

    @Override
    public Map<String, String> asMap()
    {
        final Map<String, String> map = new HashMap<>();
        for ( final Object key : this.properties.keySet() )
        {
            map.put( key.toString(), doGetMessage( key.toString() ) );
        }

        return map;
    }
}
