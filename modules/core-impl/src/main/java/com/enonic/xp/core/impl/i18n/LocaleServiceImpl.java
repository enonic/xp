package com.enonic.xp.core.impl.i18n;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceNotFoundException;
import com.enonic.xp.resource.ResourceService;

@Component(immediate = true)
public final class LocaleServiceImpl
    implements LocaleService
{
    private final static Logger LOG = LoggerFactory.getLogger( LocaleServiceImpl.class );

    private static final String PHRASE_FOLDER = "site/i18n/phrases";

    private static final String DELIMITER = "_";

    private ResourceService resourceService;

    @Override
    public MessageBundle getBundle( final ApplicationKey applicationKey, final Locale locale )
    {
        if ( applicationKey == null )
        {
            return null;
        }

        return createMessageBundle( applicationKey, locale );
    }

    private MessageBundle createMessageBundle( final ApplicationKey applicationKey, final Locale locale )
    {
        final Properties props = new Properties();

        if ( locale == null )
        {
            props.putAll( loadBundle( applicationKey, "" ) );
            return new MessageBundleImpl( props );
        }

        String lang = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();

        props.putAll( loadBundle( applicationKey, "" ) );

        if ( StringUtils.isNotEmpty( lang ) )
        {
            lang = lang.toLowerCase();
            props.putAll( loadBundle( applicationKey, DELIMITER + lang ) );
        }

        if ( StringUtils.isNotEmpty( country ) )
        {
            props.putAll( loadBundle( applicationKey, DELIMITER + lang + DELIMITER + country ) );
        }

        if ( StringUtils.isNotEmpty( variant ) )
        {
            variant = variant.toLowerCase();
            props.putAll( loadBundle( applicationKey, DELIMITER + lang + DELIMITER + country + DELIMITER + variant ) );
        }

        return new MessageBundleImpl( props );
    }

    private Properties loadBundle( final ApplicationKey applicationKey, final String bundleExtension )
    {
        Properties properties = getOrCreateProperties( applicationKey );

        final ResourceKey resourceKey = ResourceKey.from( applicationKey, PHRASE_FOLDER + bundleExtension + ".properties" );
        try
        {
            final Resource resource = resourceService.getResource( resourceKey );

            if ( resource != null )
            {
                try
                {
                    properties.load( resource.openStream() );
                }
                catch ( final IOException e )
                {
                    throw new LocalizationException( "Not able to load resource for: " + applicationKey.toString(), e );
                }
            }
        }
        catch ( ResourceNotFoundException e )
        {
            LOG.info( "Resource not found: " + resourceKey.toString() );
        }

        return properties;
    }

    private Properties getOrCreateProperties( final ApplicationKey applicationKey )
    {
        Properties properties = null /*getFromCache( application )*/;

        if ( properties == null )
        {
            properties = new Properties();
        }

        return properties;
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
