package com.enonic.xp.core.impl.i18n;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceKeys;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class LocaleServiceImplTest
{
    private LocaleServiceImpl localeService;

    private ResourceService resourceService;

    @BeforeEach
    void before()
    {
        this.resourceService = Mockito.mock( ResourceService.class );
        when( resourceService.getResource( any() ) ).thenAnswer( this::loadResource );
        this.localeService = new LocaleServiceImpl( resourceService );
    }

    private Resource loadResource( final InvocationOnMock invocation )
    {
        final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
        final String path = resourceKey.getName();
        final URL url = getClass().getResource( path.substring( path.lastIndexOf( '/' ) + 1 ) );
        return new UrlResource( resourceKey, url );
    }

    @Test
    void get_bundle_norwegian_no()
    {
        final MessageBundle bundle =
            localeService.getBundle( ApplicationKey.from( "myapplication" ), Locale.forLanguageTag( "no" ), "norwegian" );

        assertThat( bundle.asMap() ).containsExactlyInAnyOrderEntriesOf( Map.of( "a", "default", "b", "nb", "d", "no" ) );
    }

    @Test
    void get_bundle_norwegian_nb()
    {
        final MessageBundle bundle =
            localeService.getBundle( ApplicationKey.from( "myapplication" ), Locale.forLanguageTag( "nb" ), "norwegian" );

        assertThat( bundle.asMap() ).containsExactlyInAnyOrderEntriesOf( Map.of( "a", "default", "b", "nb", "d", "no" ) );

    }

    @Test
    void get_bundle_norwegian_nn()
    {
        final MessageBundle bundle =
            localeService.getBundle( ApplicationKey.from( "myapplication" ), Locale.forLanguageTag( "nn" ), "norwegian" );

        assertThat( bundle.asMap() ).containsExactlyInAnyOrderEntriesOf( Map.of( "a", "default", "c", "nn", "d", "no" ) );
    }

    @Test
    void get_bundle_no_application()
    {
        final MessageBundle bundle = localeService.getBundle( null, Locale.ENGLISH );
        assertNull( bundle );
    }

    @Test
    void get_bundle_no_locale()
    {
        final MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "myapplication" ), null );

        assertNotNull( bundle );
        assertEquals( 7, bundle.getKeys().size() );
        assertEquals( "default", bundle.localize( "msg" ) );
    }

    @Test
    void get_bundle_with_country()
    {
        final MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "myapplication" ), Locale.US );

        assertNotNull( bundle );
        assertEquals( 9, bundle.getKeys().size() );
        assertEquals( "en_US", bundle.localize( "msg" ) );
    }

    @Test
    void get_bundle_with_variant()
    {
        final Locale locale = new Locale( "en", "US", "1" );
        final MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "myapplication" ), locale );

        assertNotNull( bundle );
        assertEquals( 10, bundle.getKeys().size() );
        assertEquals( "en_US_1", bundle.localize( "msg" ) );
    }

    @Test
    void get_UTF8_chars()
    {
        final MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "myapplication" ), Locale.ENGLISH );

        assertNotNull( bundle );
        assertEquals( "æøå", bundle.localize( "norwegian" ) );
        assertEquals( "ÄäÜüß", bundle.localize( "german" ) );
        assertEquals( "ŁĄŻĘĆŃŚŹ", bundle.localize( "polish" ) );
        assertEquals( "ЯБГДЖЙ", bundle.localize( "russian" ) );
        assertEquals( "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃ", bundle.localize( "japanese" ) );
    }

    @Test
    void get_bundle_multi()
    {
        final MessageBundle bundle =
            localeService.getBundle( ApplicationKey.from( "myapplication" ), Locale.ENGLISH, "/phrases", "/override" );

        assertNotNull( bundle );
        assertEquals( 9, bundle.getKeys().size() );
        assertEquals( "override", bundle.localize( "msg" ) );
    }

    @Test
    void getLocales()
    {
        final ResourceKeys resourceKeys =
            ResourceKeys.from( "myapplication:/i18n/myphrases.properties", "myapplication:/i18n/myphrases_en.properties",
                               "myapplication:/i18n/myphrases_en_US.properties", "myapplication:/i18n/myphrases_en_US_1.properties",
                               "myapplication:/i18n/myphrases_fr.properties", "myapplication:/i18n/myphrases_ca.properties" );

        when( resourceService.findFiles( any(), Mockito.eq( "^\\Q/i18n/myphrases\\E.*\\.properties$" ) ) )
            .thenReturn( resourceKeys );

        final Set<Locale> locales = localeService.getLocales( ApplicationKey.from( "myapplication" ), "i18n/myphrases" );

        assertNotNull( locales );
        assertEquals( 5, locales.size() );
        assertTrue( locales.contains( new Locale( "en" ) ) );
        assertTrue( locales.contains( new Locale( "en", "US" ) ) );
        assertTrue( locales.contains( new Locale( "en", "US", "1" ) ) );
        assertTrue( locales.contains( new Locale( "fr" ) ) );
        assertTrue( locales.contains( new Locale( "ca" ) ) );
    }

    @Test
    void bundleInvalidateCaching()
    {
        final ResourceKeys resourceKeys = ResourceKeys.empty();
        when( resourceService.findFiles( any(), anyString() ) ).thenReturn( resourceKeys );

        final ApplicationKey myApp = ApplicationKey.from( "myapplication" );
        final ApplicationKey otherApp = ApplicationKey.from( "otherapp" );

        MessageBundle bundleCached = localeService.getBundle( myApp, Locale.ENGLISH, "/phrases", "/override" );
        MessageBundle bundle = localeService.getBundle( myApp, Locale.ENGLISH, "/phrases", "/override" );

        MessageBundle otherBundleCached = localeService.getBundle( otherApp, Locale.ENGLISH, "/texts" );
        MessageBundle otherBundle = localeService.getBundle( otherApp, Locale.ENGLISH, "/texts" );

        assertSame( bundle, bundleCached );
        assertSame( otherBundle, otherBundleCached );

        Application application = Mockito.mock( Application.class );
        when( application.getKey() ).thenReturn( myApp );
        localeService.activated( application );

        bundle = localeService.getBundle( myApp, Locale.ENGLISH, "/phrases", "/override" );
        otherBundle = localeService.getBundle( otherApp, Locale.ENGLISH, "/texts" );

        assertNotSame( bundle, bundleCached );
        assertSame( otherBundle, otherBundleCached );
    }

    @Test
    void getSupportedLocale_onePreferredFound()
    {
        final ResourceKeys resourceKeys =
            ResourceKeys.from( "myapplication:/site/i18n/myphrases.properties", "myapplication:/site/i18n/myphrases_en.properties",
                               "myapplication:/site/i18n/myphrases_en_US.properties",
                               "myapplication:/site/i18n/myphrases_en_US_1.properties", "myapplication:/site/i18n/myphrases_fr.properties",
                               "myapplication:/site/i18n/myphrases_ca.properties" );

        when( resourceService.findFiles( any(), anyString() ) ).thenReturn( resourceKeys );

        List<Locale> preferredLocales = localeList( "en-US" );
        Locale supportedLocale = localeService.getSupportedLocale( preferredLocales, ApplicationKey.from( "myapplication" ), "/myphrases" );

        assertNotNull( supportedLocale );
        assertEquals( "en-US", supportedLocale.toLanguageTag() );
    }

    @Test
    void getSupportedLocale_LanguagePreferredFound()
    {
        final ResourceKeys resourceKeys =
            ResourceKeys.from( "myapplication:/site/i18n/myphrases.properties", "myapplication:/site/i18n/myphrases_en.properties",
                               "myapplication:/site/i18n/myphrases_en_US.properties",
                               "myapplication:/site/i18n/myphrases_en_US_1.properties", "myapplication:/site/i18n/myphrases_fr.properties",
                               "myapplication:/site/i18n/myphrases_ca.properties" );

        when( resourceService.findFiles( any(), anyString() ) ).thenReturn( resourceKeys );

        List<Locale> preferredLocales = localeList( "en-UK" );
        Locale supportedLocale = localeService.getSupportedLocale( preferredLocales, ApplicationKey.from( "myapplication" ), "/myphrases" );

        assertNotNull( supportedLocale );
        assertEquals( "en", supportedLocale.toLanguageTag() );
    }

    @Test
    void getSupportedLocale_moreThanOneFound()
    {
        final ResourceKeys resourceKeys =
            ResourceKeys.from( "myapplication:/site/i18n/myphrases.properties", "myapplication:/site/i18n/myphrases_en.properties",
                               "myapplication:/site/i18n/myphrases_en_US.properties",
                               "myapplication:/site/i18n/myphrases_en_US_1.properties", "myapplication:/site/i18n/myphrases_fr.properties",
                               "myapplication:/site/i18n/myphrases_ca.properties" );

        when( resourceService.findFiles( any(), anyString() ) ).thenReturn( resourceKeys );

        List<Locale> preferredLocales = localeList( "no", "ca-ES", "en" );
        Locale supportedLocale = localeService.getSupportedLocale( preferredLocales, ApplicationKey.from( "myapplication" ), "/myphrases" );

        assertNotNull( supportedLocale );
        assertEquals( "ca", supportedLocale.toLanguageTag() );
    }


    @Test
    void getSupportedLocale_noPreferredFound()
    {
        final ResourceKeys resourceKeys =
            ResourceKeys.from( "myapplication:/site/i18n/myphrases.properties", "myapplication:/site/i18n/myphrases_en.properties",
                               "myapplication:/site/i18n/myphrases_en_US.properties",
                               "myapplication:/site/i18n/myphrases_en_US_1.properties", "myapplication:/site/i18n/myphrases_fr.properties",
                               "myapplication:/site/i18n/myphrases_ca.properties" );

        when( resourceService.findFiles( any(), anyString() ) ).thenReturn( resourceKeys );

        List<Locale> preferredLocales = localeList( "no" );
        Locale supportedLocale = localeService.getSupportedLocale( preferredLocales, ApplicationKey.from( "myapplication" ), "/myphrases" );

        assertNull( supportedLocale );
    }

    @Test
    void getSupportedLocale_no_file_supports_nb_locale()
    {
        final ResourceKeys resourceKeys =
            ResourceKeys.from( "myapplication:/site/i18n/myphrases.properties", "myapplication:/site/i18n/myphrases_no.properties" );

        when( resourceService.findFiles( any(), anyString() ) ).thenReturn( resourceKeys );

        List<Locale> preferredLocales = localeList( "nb" );
        Locale supportedLocale = localeService.getSupportedLocale( preferredLocales, ApplicationKey.from( "myapplication" ), "/myphrases" );

        assertNotNull( supportedLocale );
        assertEquals( "nb", supportedLocale.toLanguageTag() );
    }

    @Test
    void getSupportedLocale_no_file_supports_nn_locale()
    {
        final ResourceKeys resourceKeys =
            ResourceKeys.from( "myapplication:/site/i18n/myphrases.properties", "myapplication:/site/i18n/myphrases_no.properties" );

        when( resourceService.findFiles( any(), anyString() ) ).thenReturn( resourceKeys );

        List<Locale> preferredLocales = localeList( "nn" );
        Locale supportedLocale = localeService.getSupportedLocale( preferredLocales, ApplicationKey.from( "myapplication" ), "/myphrases" );

        assertNotNull( supportedLocale );
        assertEquals( "nn", supportedLocale.toLanguageTag() );
    }

    @Test
    void getSupportedLocale_nb_file_supports_no_locale()
    {
        final ResourceKeys resourceKeys =
            ResourceKeys.from( "myapplication:/site/i18n/myphrases.properties", "myapplication:/site/i18n/myphrases_nb.properties" );

        when( resourceService.findFiles( any(), anyString() ) ).thenReturn( resourceKeys );

        List<Locale> preferredLocales = localeList( "no" );
        Locale supportedLocale = localeService.getSupportedLocale( preferredLocales, ApplicationKey.from( "myapplication" ), "/myphrases" );

        assertNotNull( supportedLocale );
        assertEquals( "no", supportedLocale.toLanguageTag() );
    }

    @Test
    void getSupportedLocale_nn_file_supports_no_locale()
    {
        final ResourceKeys resourceKeys =
            ResourceKeys.from( "myapplication:/site/i18n/myphrases.properties", "myapplication:/site/i18n/myphrases_nn.properties" );

        when( resourceService.findFiles( any(), anyString() ) ).thenReturn( resourceKeys );

        List<Locale> preferredLocales = localeList( "no" );
        Locale supportedLocale = localeService.getSupportedLocale( preferredLocales, ApplicationKey.from( "myapplication" ), "/myphrases" );

        assertNotNull( supportedLocale );
        assertEquals( "no", supportedLocale.toLanguageTag() );
    }

    @Test
    void getSupportedLocale_nb_file_does_not_support_nn_locale()
    {
        final ResourceKeys resourceKeys =
            ResourceKeys.from( "myapplication:/site/i18n/myphrases.properties", "myapplication:/site/i18n/myphrases_nn.properties" );

        when( resourceService.findFiles( any(), anyString() ) ).thenReturn( resourceKeys );

        List<Locale> preferredLocales = localeList( "nb" );
        Locale supportedLocale = localeService.getSupportedLocale( preferredLocales, ApplicationKey.from( "myapplication" ), "/myphrases" );

        assertNull( supportedLocale );
    }

    private List<Locale> localeList( final String... localeTags )
    {
        return Arrays.stream( localeTags ).map( Locale::forLanguageTag ).collect( Collectors.toList() );
    }
}
