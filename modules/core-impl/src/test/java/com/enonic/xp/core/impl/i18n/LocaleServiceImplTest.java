package com.enonic.xp.core.impl.i18n;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.Locale;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.resource.ResourceService;

import static org.junit.Assert.*;

public class LocaleServiceImplTest
{

    private LocaleServiceImpl localeService;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();


    private void writeFile( final File dir, final String path, final String value )
        throws Exception
    {
        final File file = new File( dir, path );
        file.getParentFile().mkdirs();
        ByteSource.wrap( value.getBytes( Charsets.UTF_8 ) ).copyTo( new FileOutputStream( file ) );
    }

    @Before
    public void before()
        throws Exception
    {
        localeService = new LocaleServiceImpl();

        final File applicationsDir = this.temporaryFolder.newFolder( "applications" );

        writeFile( applicationsDir, "myapplication/site/i18n/phrases_en_US_1.properties", "d = phrases_en_US_1.properties" );
        writeFile( applicationsDir, "myapplication/site/i18n/phrases_en_US.properties", "b = phrases_en_US.properties" );
        writeFile( applicationsDir, "myapplication/site/i18n/phrases_en.properties", "a = phrases_en.properties" );
        writeFile( applicationsDir, "myapplication/site/i18n/phrases.properties", "c = phrases.properties" );

        final ResourceService resourceService = Mockito.mock( ResourceService.class );
        Mockito.when( resourceService.getResource( Mockito.any() ) ).thenAnswer( invocation -> {
            final ResourceKey resourceKey = (ResourceKey) invocation.getArguments()[0];
            final String path = resourceKey.getApplicationKey().toString() + resourceKey.getPath().toString();
            final URL resourceUrl = new File( applicationsDir, path ).toURI().toURL();
            return resourceUrl == null ? null : new Resource( resourceKey, resourceUrl );
        } );
        localeService.setResourceService( resourceService );
    }


    @Test
    public void get_bundle()
        throws Exception
    {
        MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "myapplication" ), Locale.ENGLISH );
        Object[] result = {"a", "c"};

        assertNotNull( bundle );
        assertArrayEquals( bundle.getKeys().toArray(), result );
    }

    @Test
    public void get_bundle_with_country()
        throws Exception
    {
        MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "myapplication" ), Locale.US );
        Object[] result = {"a", "b", "c"};

        assertNotNull( bundle );
        assertArrayEquals( bundle.getKeys().toArray(), result );
    }

    @Test
    public void get_bundle_with_variant()
        throws Exception
    {
        Locale locale = new Locale( "en", "US", "1" );
        MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "myapplication" ), locale );
        Object[] result = {"a", "b", "c", "d"};

        assertNotNull( bundle );
        assertArrayEquals( bundle.getKeys().toArray(), result );
    }
}
