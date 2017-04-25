package com.enonic.xp.core.impl.i18n;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;

import com.enonic.xp.i18n.MessageBundle;

final class MessageBundleImpl
    implements MessageBundle
{
    private static final String UTF_8_ENCODING = "UTF-8";

    private static final String LATIN_1_ENCODING = "ISO-8859-1";

    static final String MISSING_VALUE_MESSAGE = "NOT_TRANSLATED";

    private final static Logger LOG = LoggerFactory.getLogger( MessageBundleImpl.class );

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
        final String message = (String) handleGetObject( key );
        return StringUtils.isNotEmpty( message ) ? format( message, args ) : MISSING_VALUE_MESSAGE;
    }

    private String format( final String message, final Object[] args )
    {
        return MessageFormat.format( message, args );
    }

    private Object handleGetObject( String key )
    {
        return createUTF8EncodedPhrase( (String) this.properties.get( key ) );
    }

    private String createUTF8EncodedPhrase( String localizedPhrase )
    {
        if ( StringUtils.isBlank( localizedPhrase ) )
        {
            return null;
        }

        try
        {
            return new String( localizedPhrase.getBytes( LATIN_1_ENCODING ), UTF_8_ENCODING );
        }
        catch ( final UnsupportedEncodingException e )
        {
            LOG.error( "Parsing localized phrase: " + localizedPhrase + " failed", e );
            return null;
        }
    }

    @Override
    public Map<String, String> asMap()
    {
        final Map<String, String> map = Maps.newHashMap();
        for ( final Object key : this.properties.keySet() )
        {
            map.put( key.toString(), this.properties.getProperty( key.toString() ) );
        }

        return map;
    }
}
