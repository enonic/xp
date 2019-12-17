package com.enonic.xp.core.impl.i18n;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationInvalidator;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceService;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang.StringUtils.substringBefore;
import static org.apache.commons.lang.StringUtils.substringBetween;

@Component(immediate = true)
public final class LocaleServiceImpl
    implements LocaleService, ApplicationInvalidator
{
    private static final String DELIMITER = "_";

    private static final String KEY_SEPARATOR = "|";

    private ResourceService resourceService;

    private final ConcurrentMap<String, MessageBundle> bundleCache;

    private final ConcurrentMap<String, Set<Locale>> appLocalesCache;

    public LocaleServiceImpl()
    {
        this.bundleCache = new ConcurrentHashMap<>();
        this.appLocalesCache = new ConcurrentHashMap<>();
    }

    @Override
    public MessageBundle getBundle( final ApplicationKey applicationKey, final Locale locale )
    {
        return getBundle( applicationKey, locale, "site/i18n/phrases", "i18n/phrases" );
    }

    @Override
    public MessageBundle getBundle( final ApplicationKey applicationKey, final Locale locale, final String... bundleNames )
    {
        if ( applicationKey == null )
        {
            return null;
        }

        return getMessageBundle( applicationKey, locale, bundleNames );
    }

    @Override
    public Set<Locale> getLocales( final ApplicationKey applicationKey, final String... bundleNames )
    {
        if ( applicationKey == null )
        {
            return Collections.emptySet();
        }

        final String key = appBundlesCacheKey( applicationKey, bundleNames );
        return this.appLocalesCache.computeIfAbsent( key, ( k ) -> getAppLocales( applicationKey, bundleNames ) );
    }

    @Override
    public Locale getSupportedLocale( final List<Locale> preferredLocales, final ApplicationKey applicationKey, String... bundleNames )
    {
        if ( preferredLocales == null || preferredLocales.isEmpty() )
        {
            return null;
        }
        if ( bundleNames == null || bundleNames.length == 0 )
        {
            bundleNames = new String[]{"site/i18n/phrases", "i18n/phrases"};
        }

        final Set<String> supportedLocales = this.getLocales( applicationKey, bundleNames ).stream().
            map( ( l ) -> l.toLanguageTag().toLowerCase() ).
            collect( toSet() );

        for ( Locale locale : preferredLocales )
        {
            final String localeTag = locale.toLanguageTag().toLowerCase();
            if ( supportedLocales.contains( localeTag ) )
            {
                return locale;
            }
            if ( localeTag.contains( "-" ) )
            {
                final String language = substringBefore( localeTag, "-" );
                if ( supportedLocales.contains( language ) )
                {
                    return new Locale( language ); // language locale supported, e.g. locale=="en-us" && supportedLocales.contains("en")
                }
            }
        }
        return null;
    }

    private Set<Locale> getAppLocales( final ApplicationKey applicationKey, final String... bundleNames )
    {
        final Set<Locale> locales = new LinkedHashSet<>();
        for ( final String bundleName : bundleNames )
        {
            final String bundlePattern = Pattern.quote( bundleName ) + ".*\\.properties";
            final ResourceKeys resourceKeys = resourceService.findFiles( applicationKey, bundlePattern );
            for ( ResourceKey resourceKey : resourceKeys )
            {
                if ( resourceService.getResource( resourceKey ).exists() )
                {
                    locales.add( localeFromResource( resourceKey.getName() ) );
                }
            }
        }
        return new LinkedHashSet<>( locales );
    }

    private Locale localeFromResource( final String resourceName )
    {
        if ( !resourceName.contains( "_" ) )
        {
            return Locale.ENGLISH;
        }
        final String localeStr = substringBetween( resourceName, "_", ".properties" );
        final String[] localeParts = localeStr.split( "_" );
        final int partCount = localeParts.length;
        switch ( partCount )
        {
            case 1:
                return new Locale( localeParts[0] );
            case 2:
                return new Locale( localeParts[0], localeParts[1] );
            case 3:
                return new Locale( localeParts[0], localeParts[1], localeParts[2] );
            default:
                return new Locale( localeParts[partCount - 3], localeParts[partCount - 2], localeParts[partCount - 1] );
        }
    }

    private String bundleCacheKey( final ApplicationKey applicationKey, final Locale locale, final String... bundleNames )
    {
        String lang = locale != null ? locale.getLanguage() : "";
        String country = locale != null ? locale.getCountry() : "";
        String variant = locale != null ? locale.getVariant() : "";
        StringJoiner key = new StringJoiner( KEY_SEPARATOR ).
            add( applicationKey.toString() ).
            add( lang ).
            add( country ).
            add( variant );
        if ( bundleNames != null )
        {
            for ( String bundleName : bundleNames )
            {
                key.add( bundleName );
            }
        }
        return key.toString();
    }

    private String appBundlesCacheKey( final ApplicationKey applicationKey, final String... bundleNames )
    {
        StringJoiner key = new StringJoiner( KEY_SEPARATOR ).
            add( applicationKey.toString() );
        if ( bundleNames != null )
        {
            for ( String bundleName : bundleNames )
            {
                key.add( bundleName );
            }
        }
        return key.toString();
    }

    private MessageBundle getMessageBundle( final ApplicationKey applicationKey, final Locale locale, final String... bundleNames )
    {
        final String key = bundleCacheKey( applicationKey, locale, bundleNames );
        return this.bundleCache.computeIfAbsent( key, ( k ) -> createMessageBundle( applicationKey, locale, bundleNames ) );
    }

    private MessageBundle createMessageBundle( final ApplicationKey applicationKey, final Locale locale, final String... bundleNames )
    {
        final Properties props = new Properties();
        for ( final String bundleName : bundleNames )
        {
            props.putAll( loadBundles( applicationKey, locale, bundleName ) );
        }

        return new MessageBundleImpl( props );
    }

    private Properties loadBundles( final ApplicationKey applicationKey, final Locale locale, final String bundleName )
    {
        final Properties props = new Properties();

        if ( locale == null )
        {
            props.putAll( loadBundle( applicationKey, bundleName, "" ) );
            return props;
        }

        String lang = locale.getLanguage();
        String country = locale.getCountry();
        String variant = locale.getVariant();

        props.putAll( loadBundle( applicationKey, bundleName, "" ) );

        if ( !isNullOrEmpty( lang ) )
        {
            lang = lang.toLowerCase();
            props.putAll( loadBundle( applicationKey, bundleName, DELIMITER + lang ) );
        }

        if ( !isNullOrEmpty( country ) )
        {
            props.putAll( loadBundle( applicationKey, bundleName, DELIMITER + lang + DELIMITER + country ) );
        }

        if ( !isNullOrEmpty( variant ) )
        {
            variant = variant.toLowerCase();
            props.putAll( loadBundle( applicationKey, bundleName, DELIMITER + lang + DELIMITER + country + DELIMITER + variant ) );
        }

        return props;
    }

    private Properties loadBundle( final ApplicationKey applicationKey, final String bundleName, final String bundleExtension )
    {
        final Properties properties = new Properties();
        properties.putAll( loadSingleBundle( applicationKey, bundleName, bundleExtension ) );
        return properties;
    }

    private Properties loadSingleBundle( final ApplicationKey applicationKey, final String bundleName, final String bundleExtension )
    {
        final Properties properties = new Properties();
        final ResourceKey resourceKey = ResourceKey.from( applicationKey, bundleName + bundleExtension + ".properties" );
        final Resource resource = resourceService.getResource( resourceKey );

        if ( resource.exists() )
        {
            try (Reader in = resource.openReader())
            {
                properties.load( in );
            }
            catch ( final IOException e )
            {
                throw new LocalizationException( "Not able to load resource for: " + applicationKey.toString(), e );
            }
        }

        return properties;
    }

    @Override
    public void invalidate( final ApplicationKey appKey )
    {
        final String cacheKeyPrefix = appKey.toString() + KEY_SEPARATOR;
        bundleCache.keySet().removeIf( ( k ) -> k.startsWith( cacheKeyPrefix ) );
        appLocalesCache.keySet().removeIf( ( k ) -> k.startsWith( cacheKeyPrefix ) );
    }

    @Reference
    public void setResourceService( final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }
}
