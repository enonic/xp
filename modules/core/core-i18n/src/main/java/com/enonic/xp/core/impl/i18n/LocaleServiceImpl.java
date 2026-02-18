package com.enonic.xp.core.impl.i18n;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.regex.Pattern;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationListener;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceService;

@Component(immediate = true)
@NullMarked
public final class LocaleServiceImpl
    implements LocaleService, ApplicationListener
{
    private static final Logger LOG = LoggerFactory.getLogger( LocaleServiceImpl.class );

    private static final String KEY_SEPARATOR = "|";

    private static final String[] DEFAULT_BASE_NAMES = {"/site/i18n/phrases", "/i18n/phrases"};

    private static final Locale LOCALE_NO = new Locale( "no" );

    private static final Locale LOCALE_NB = new Locale( "nb" );

    private static final Locale LOCALE_NN = new Locale( "nn" );

    private final ResourceService resourceService;

    private final ConcurrentMap<String, MessageBundle> bundleCache = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Set<Locale>> appLocalesCache = new ConcurrentHashMap<>();

    @Activate
    public LocaleServiceImpl( @Reference final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Override
    public MessageBundle getBundle( final ApplicationKey applicationKey, final @Nullable Locale locale )
    {
        return getBundle( applicationKey, locale, DEFAULT_BASE_NAMES );
    }

    @Override
    public MessageBundle getBundle( final ApplicationKey applicationKey, final @Nullable Locale locale, final String... bundleNames )
    {
        final String[] baseNames = bundleNames.length == 0 ? DEFAULT_BASE_NAMES : bundleNames;
        final Locale nonNullLocale = Objects.requireNonNullElse( locale, Locale.ROOT );

        final String key = bundleCacheKey( applicationKey, nonNullLocale, baseNames );
        return this.bundleCache.computeIfAbsent( key, k -> createMessageBundle( applicationKey, nonNullLocale, baseNames ) );
    }

    @Override
    public Set<Locale> getLocales( final ApplicationKey applicationKey, final String... bundleNames )
    {
        final String[] baseNames = bundleNames.length == 0 ? DEFAULT_BASE_NAMES : bundleNames;

        final String key = appBundlesCacheKey( applicationKey, baseNames );
        return this.appLocalesCache.computeIfAbsent( key, k -> getAppLocales( applicationKey, baseNames ) );
    }

    @Override
    public @Nullable Locale getSupportedLocale( final List<Locale> preferredLocales, final ApplicationKey applicationKey,
                                                final String... bundleNames )
    {
        if ( preferredLocales.isEmpty() )
        {
            return null;
        }
        final Set<Locale> supportedLocales = this.getLocales( applicationKey, bundleNames );
        if ( supportedLocales.isEmpty() )
        {
            return null;
        }
        final List<Locale.LanguageRange> priorityList =
            preferredLocales.stream().map( Locale::toLanguageTag ).map( Locale.LanguageRange::new ).toList();
        return Locale.lookup( priorityList, supportedLocales );
    }

    private Set<Locale> getAppLocales( final ApplicationKey applicationKey, final String... bundleNames )
    {
        LOG.debug( "Create app locales for {}", applicationKey );
        final Set<Locale> locales = new LinkedHashSet<>();
        for ( final String bundleName : bundleNames )
        {
            final String bundlePattern =
                "^" + Pattern.quote( bundleName.startsWith( "/" ) ? bundleName : "/" + bundleName ) + ".*\\.properties$";
            final ResourceKeys resourceKeys = resourceService.findFiles( applicationKey, bundlePattern );
            for ( ResourceKey resourceKey : resourceKeys )
            {
                final Locale locale = localeFromResource( resourceKey.getName() );
                locales.add( locale );
                if ( locale.equals( LOCALE_NO ) )
                {
                    locales.add( LOCALE_NB );
                    locales.add( LOCALE_NN );
                }
                if ( locale.getLanguage().equals( LOCALE_NB.getLanguage() ) || locale.getLanguage().equals( LOCALE_NN.getLanguage() ) )
                {
                    locales.add( LOCALE_NO );
                }
            }
        }
        return locales;
    }

    private Locale localeFromResource( final String resourceName )
    {
        if ( !resourceName.contains( "_" ) )
        {
            return Locale.ENGLISH;
        }
        final String localeStr = Files.getNameWithoutExtension( resourceName ).substring( resourceName.indexOf( '_' ) + 1 );
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
        StringJoiner key = new StringJoiner( KEY_SEPARATOR ).add( applicationKey.toString() );
        key.add( locale.getLanguage() ).add( locale.getCountry() ).add( locale.getVariant() );
        for ( String bundleName : bundleNames )
        {
            key.add( bundleName );
        }
        return key.toString();
    }

    private String appBundlesCacheKey( final ApplicationKey applicationKey, final String... baseNames )
    {
        StringJoiner key = new StringJoiner( KEY_SEPARATOR ).add( applicationKey.toString() );
        for ( String bundleName : baseNames )
        {
            key.add( bundleName );
        }
        return key.toString();
    }

    private MessageBundle createMessageBundle( final ApplicationKey applicationKey, final Locale locale, final String... bundleNames )
    {
        LOG.debug( "Create message bundle for {} {}", applicationKey, locale );
        final Properties props = new Properties();
        for ( final String baseName : bundleNames )
        {
            props.putAll( loadBundles( applicationKey, locale, baseName ) );
        }

        return new MessageBundleImpl( props, locale );
    }

    private Properties loadBundles( final ApplicationKey applicationKey, final Locale locale, final String baseName )
    {
        final Properties props = new Properties();

        final ResourceBundle.Control control = ResourceBundle.Control.getControl( ResourceBundle.Control.FORMAT_PROPERTIES );
        final List<Locale> candidateLocales = control.getCandidateLocales( baseName, locale );
        Collections.reverse( candidateLocales );

        for ( Locale candidateLocale : candidateLocales )
        {
            props.putAll( loadBundle( applicationKey, control.toBundleName( baseName, candidateLocale ) ) );
        }

        return props;
    }

    private Properties loadBundle( final ApplicationKey applicationKey, final String bundleName )
    {
        final ResourceKey resourceKey = ResourceKey.from( applicationKey, bundleName + ".properties" );
        final Resource resource = resourceService.getResource( resourceKey );

        final Properties properties = new Properties();
        if ( resource.exists() )
        {
            try (Reader in = resource.openReader())
            {
                properties.load( in );
            }
            catch ( final IOException e )
            {
                throw new LocalizationException( "Not able to load resource for: " + applicationKey, e );
            }
        }

        return properties;
    }

    @Override
    public void activated( final Application app )
    {
        // Locale and message bundle cache can get populated even when application was uninstalled.
        // We clear it as soon as application is started, so it can be repopulated.
        clearCache( app.getKey() );
    }

    @Override
    public void deactivated( final Application app )
    {
        clearCache( app.getKey() );
    }

    private void clearCache( ApplicationKey appKey )
    {
        LOG.debug( "Cleanup i18n caches for {}", appKey );
        final String cacheKeyPrefix = appKey + KEY_SEPARATOR;
        bundleCache.keySet().removeIf( ( k ) -> k.startsWith( cacheKeyPrefix ) );
        appLocalesCache.keySet().removeIf( ( k ) -> k.startsWith( cacheKeyPrefix ) );
    }
}
