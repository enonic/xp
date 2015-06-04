package com.enonic.xp.core.impl.i18n;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceNotFoundException;

@Component
public final class LocaleServiceImpl
    implements LocaleService
{
    private final static Logger LOG = LoggerFactory.getLogger( LocaleServiceImpl.class );

    private static final String PHRASE_FOLDER = "cms/i18n/phrases";

    private static final String DELIMITER = "_";

    @Override
    public MessageBundle getBundle( final ModuleKey module, final Locale locale )
    {
        if ( module == null )
        {
            return null;
        }

        return createMessageBundle( module, locale );
    }

    private MessageBundle createMessageBundle( final ModuleKey module, final Locale locale )
    {
        final Properties props = new Properties();

        if ( locale == null )
        {
            props.putAll( loadBundle( module, "" ) );
            return new MessageBundleImpl( props );
        }

        String lang = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();

        props.putAll( loadBundle( module, "" ) );

        if ( StringUtils.isNotEmpty( lang ) )
        {
            lang = lang.toLowerCase();
            props.putAll( loadBundle( module, DELIMITER + lang ) );
        }

        if ( StringUtils.isNotEmpty( country ) )
        {
            props.putAll( loadBundle( module, DELIMITER + lang + DELIMITER + country ) );
        }

        if ( StringUtils.isNotEmpty( variant ) )
        {
            variant = variant.toLowerCase();
            props.putAll( loadBundle( module, DELIMITER + lang + DELIMITER + country + DELIMITER + variant ) );
        }

        return new MessageBundleImpl( props );
    }

    private Properties loadBundle( final ModuleKey module, final String bundleExtension )
    {
        Properties properties = getOrCreateProperties( module );

        final ResourceKey resourceKey = ResourceKey.from( module, PHRASE_FOLDER + bundleExtension + ".properties" );
        try
        {
            final Resource resource = Resource.from( resourceKey );
            if ( resource != null )
            {
                try
                {
                    properties.load( resource.openStream() );
                }
                catch ( final IOException e )
                {
                    throw new LocalizationException( "Not able to load resource for: " + module.toString(), e );
                }
            }
        }
        catch ( ResourceNotFoundException e )
        {
            LOG.info( "Resource not found: " + resourceKey.toString() );
        }

        return properties;
    }

    private Properties getOrCreateProperties( final ModuleKey module )
    {

        Properties properties = null /*getFromCache( module )*/;

        if ( properties == null )
        {
            properties = new Properties();
        }

        return properties;
    }


}
