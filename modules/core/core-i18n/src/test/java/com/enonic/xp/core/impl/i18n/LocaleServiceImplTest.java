package com.enonic.xp.core.impl.i18n;

import java.net.URL;
import java.util.Locale;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.resource.UrlResource;

import static org.junit.Assert.*;

public class LocaleServiceImplTest
{
    private LocaleServiceImpl localeService;

    @Before
    public void before()
        throws Exception
    {
        this.localeService = new LocaleServiceImpl();
        final ResourceService resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( this::loadResource );
        this.localeService.setResourceService( resourceService );
    }

    private Resource loadResource( final InvocationOnMock invocation )
    {
        final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
        final String path = resourceKey.getName();
        final URL url = getClass().getResource( path.substring( path.lastIndexOf( '/' ) + 1 ) );
        return new UrlResource( resourceKey, url );
    }

    @Test
    public void get_bundle()
        throws Exception
    {
        final MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "myapplication" ), Locale.ENGLISH );

        assertNotNull( bundle );
        assertEquals( 8, bundle.getKeys().size() );
        assertEquals( "en", bundle.localize( "msg" ) );
    }

    @Test
    public void get_bundle_no_application()
        throws Exception
    {
        final MessageBundle bundle = localeService.getBundle( null, Locale.ENGLISH );
        assertNull( bundle );
    }

    @Test
    public void get_bundle_no_locale()
        throws Exception
    {
        final MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "myapplication" ), null );

        assertNotNull( bundle );
        assertEquals( 7, bundle.getKeys().size() );
        assertEquals( "default", bundle.localize( "msg" ) );
    }

    @Test
    public void get_bundle_with_country()
        throws Exception
    {
        final MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "myapplication" ), Locale.US );

        assertNotNull( bundle );
        assertEquals( 9, bundle.getKeys().size() );
        assertEquals( "en_US", bundle.localize( "msg" ) );
    }

    @Test
    public void get_bundle_with_variant()
        throws Exception
    {
        final Locale locale = new Locale( "en", "US", "1" );
        final MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "myapplication" ), locale );

        assertNotNull( bundle );
        assertEquals( 10, bundle.getKeys().size() );
        assertEquals( "en_US_1", bundle.localize( "msg" ) );
    }

    @Test
    public void get_UTF8_chars()
    {
        final MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "myapplication" ), Locale.ENGLISH );

        assertNotNull( bundle );
        assertEquals( "æøå", bundle.localize( "norwegian" ) );
        assertEquals( "ÄäÜüß", bundle.localize( "german" ) );
        assertEquals( "ŁĄŻĘĆŃŚŹ", bundle.localize( "polish" ) );
        assertEquals( "ЯБГДЖЙ", bundle.localize( "russian" ) );
        assertEquals( "ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃ", bundle.localize( "japanese" ) );

        assertEquals( "{msg=en, a=en, german=ÄäÜüß, japanese=ｱｲｳｴｵｶｷｸｹｺｻｼｽｾｿﾀﾁﾂﾃ, c=none, russian=ЯБГДЖЙ, polish=ŁĄŻĘĆŃŚŹ, norwegian=æøå}", bundle.asMap().toString() );
    }

    @Test
    public void get_bundle_multi()
        throws Exception
    {
        final MessageBundle bundle =
            localeService.getBundle( ApplicationKey.from( "myapplication" ), Locale.ENGLISH, "/phrases", "/override" );

        assertNotNull( bundle );
        assertEquals( 9, bundle.getKeys().size() );
        assertEquals( "override", bundle.localize( "msg" ) );
    }
}
