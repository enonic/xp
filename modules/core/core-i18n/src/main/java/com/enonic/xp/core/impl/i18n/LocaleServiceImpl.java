package com.enonic.xp.core.impl.i18n;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.StringJoiner;
import java.util.regex.Pattern;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.resource.MultiResourceProcessor;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceService;

import static java.util.Objects.requireNonNullElse;

@Component(immediate = true)
@NullMarked
public final class LocaleServiceImpl
    implements LocaleService
{
    private static final Logger LOG = LoggerFactory.getLogger( LocaleServiceImpl.class );

    private static final String KEY_SEPARATOR = "|";

    private static final String[] DEFAULT_BASE_NAMES = {"/i18n/phrases"};

    private static final Locale LOCALE_NO = new Locale( "no" );

    private static final Locale LOCALE_NB = new Locale( "nb" );

    private static final Locale LOCALE_NN = new Locale( "nn" );

    private final ResourceService resourceService;

    @Activate
    public LocaleServiceImpl( @Reference final ResourceService resourceService )
    {
        this.resourceService = resourceService;
    }

    @Override
    public MessageBundle getBundle( final ApplicationKey applicationKey, final @Nullable Locale locale, final String... bundleNames )
    {
        final String[] baseNames = bundleNames.length == 0 ? DEFAULT_BASE_NAMES : bundleNames;
        final Locale nonNullLocale = requireNonNullElse( locale, Locale.ROOT );

        final MultiResourceProcessor<String, MessageBundle> processor =
            new MultiResourceProcessor.Builder<String, MessageBundle>().key( bundleCacheKey( applicationKey, nonNullLocale, baseNames ) )
                .segment( "i18n" )
                .keysTranslator( k -> candidateKeys( applicationKey, nonNullLocale, baseNames ) )
                .processor( resources -> createMessageBundle( resources, nonNullLocale ) )
                .build();

        final MessageBundle bundle = resourceService.processResources( processor );
        return bundle != null ? bundle : new MessageBundleImpl( new Properties(), nonNullLocale );
    }

    @Override
    public Set<Locale> getLocales( final ApplicationKey applicationKey, final String... bundleNames )
    {
        final String[] baseNames = bundleNames.length == 0 ? DEFAULT_BASE_NAMES : bundleNames;
        return getAppLocales( applicationKey, baseNames );
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

    private List<ResourceKey> candidateKeys( final ApplicationKey applicationKey, final Locale locale, final String... bundleNames )
    {
        final ResourceBundle.Control control = ResourceBundle.Control.getControl( ResourceBundle.Control.FORMAT_PROPERTIES );

        final List<ResourceKey> keys = new ArrayList<>();
        for ( final String baseName : bundleNames )
        {
            final List<Locale> candidateLocales = control.getCandidateLocales( baseName, locale );
            Collections.reverse( candidateLocales );

            for ( final Locale candidateLocale : candidateLocales )
            {
                keys.add( ResourceKey.from( applicationKey, control.toBundleName( baseName, candidateLocale ) + ".properties" ) );
            }
        }
        return keys;
    }

    private MessageBundle createMessageBundle( final List<Resource> resources, final Locale locale )
    {
        LOG.debug( "Create message bundle for {}", locale );
        final Properties props = new Properties();
        for ( final Resource resource : resources )
        {
            if ( resource.exists() )
            {
                props.putAll( loadProperties( resource ) );
            }
        }
        return new MessageBundleImpl( props, locale );
    }

    private Properties loadProperties( final Resource resource )
    {
        final Properties properties = new Properties();
        try (Reader in = resource.openReader())
        {
            properties.load( in );
        }
        catch ( final IOException e )
        {
            throw new LocalizationException( "Not able to load resource for: " + resource.getKey(), e );
        }
        return properties;
    }
}
