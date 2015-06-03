package com.enonic.xp.core.impl.i18n;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.i18n.MessageBundle;

@Component
public final class MessageBundleImpl
    implements MessageBundle
{
    private static final String UTF_8_ENCODING = "UTF-8";

    private static final String LATIN_1_ENCODING = "ISO-8859-1";

    public static final String MISSING_VALUE_MESSAGE = "NOT_TRANSLATED";

    private final static Logger LOG = LoggerFactory.getLogger( MessageBundleImpl.class );

    private final Properties properties;

    public MessageBundleImpl( Properties properties )
    {
        this.properties = properties;
    }

    @Override
    public Set<String> getKeys()
    {
        HashSet set = new HashSet<String>();
        this.properties.keySet().forEach( set::add );

        return set;
    }

    @Override
    public String localize( final String key, final Object... args )
    {
        String message = (String) handleGetObject( key );

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
        catch ( UnsupportedEncodingException e )
        {
            LOG.error( "Parsing localized phrase: " + localizedPhrase + " failed", e );
            return null;
        }
    }
}
