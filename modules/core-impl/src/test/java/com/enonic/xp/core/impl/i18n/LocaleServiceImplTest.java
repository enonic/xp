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
import org.osgi.framework.Bundle;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

import com.enonic.xp.app.Application;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.app.ApplicationService;
import com.enonic.xp.core.impl.resource.ResourceServiceImpl;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.resource.ResourceUrlRegistry;
import com.enonic.xp.resource.ResourceUrlTestHelper;

import static org.junit.Assert.*;

public class LocaleServiceImplTest
{

    private LocaleServiceImpl localeService;

    protected ApplicationService applicationService;

    protected ResourceServiceImpl resourceService;

    protected Bundle bundle;

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

        final File modulesDir = this.temporaryFolder.newFolder( "modules" );

        writeFile( modulesDir, "mymodule/site/i18n/phrases_en_US_1.properties", "d = phrases_en_US_1.properties" );
        writeFile( modulesDir, "mymodule/site/i18n/phrases_en_US.properties", "b = phrases_en_US.properties" );
        writeFile( modulesDir, "mymodule/site/i18n/phrases_en.properties", "a = phrases_en.properties" );
        writeFile( modulesDir, "mymodule/site/i18n/phrases.properties", "c = phrases.properties" );

        final ResourceUrlRegistry registry = ResourceUrlTestHelper.mockModuleScheme();
        registry.modulesDir( modulesDir );

        final ApplicationKey applicationKey = ApplicationKey.from( "mymodule" );
        this.bundle = Mockito.mock( Bundle.class );
        Mockito.when( bundle.getResource( "/site/i18n/phrases_en_US_1.properties" ) ).thenReturn(
            new URL( "module:mymodule:/site/i18n/phrases_en_US_1.properties" ) );
        Mockito.when( bundle.getResource( "/site/i18n/phrases_en_US.properties" ) ).thenReturn(
            new URL( "module:mymodule:/site/i18n/phrases_en_US.properties" ) );
        Mockito.when( bundle.getResource( "/site/i18n/phrases_en.properties" ) ).thenReturn(
            new URL( "module:mymodule:/site/i18n/phrases_en.properties" ) );
        Mockito.when( bundle.getResource( "/site/i18n/phrases.properties" ) ).thenReturn(
            new URL( "module:mymodule:/site/i18n/phrases.properties" ) );
        Mockito.when( bundle.getState() ).thenReturn( Bundle.ACTIVE );

        Application module = Mockito.mock( Application.class );
        Mockito.when( module.getKey() ).thenReturn( applicationKey );
        Mockito.when( module.getBundle() ).thenReturn( bundle );

        this.applicationService = Mockito.mock( ApplicationService.class );
        Mockito.when( applicationService.getModule( applicationKey ) ).thenReturn( module );

        this.resourceService = new ResourceServiceImpl();
        this.resourceService.setApplicationService( applicationService );

        this.localeService.setResourceService( this.resourceService );
    }


    @Test
    public void get_bundle()
        throws Exception
    {
        MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "mymodule" ), Locale.ENGLISH );
        Object[] result = {"a", "c"};

        assertNotNull( bundle );
        assertArrayEquals( bundle.getKeys().toArray(), result );
    }

    @Test
    public void get_bundle_with_country()
        throws Exception
    {
        MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "mymodule" ), Locale.US );
        Object[] result = {"a", "b", "c"};

        assertNotNull( bundle );
        assertArrayEquals( bundle.getKeys().toArray(), result );
    }

    @Test
    public void get_bundle_with_variant()
        throws Exception
    {
        Locale locale = new Locale( "en", "US", "1" );
        MessageBundle bundle = localeService.getBundle( ApplicationKey.from( "mymodule" ), locale );
        Object[] result = {"a", "b", "c", "d"};

        assertNotNull( bundle );
        assertArrayEquals( bundle.getKeys().toArray(), result );
    }
}
