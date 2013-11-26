package com.enonic.wem.core.content.site;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.site.DeleteSiteTemplate;
import com.enonic.wem.api.content.site.NoSiteTemplateExistsException;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.core.config.SystemConfig;

import static org.junit.Assert.*;

public class DeleteSiteTemplateHandlerTest
{
    private DeleteSiteTemplateHandler handler;

    private SystemConfig systemConfig;

    private Path tempDir;

    @Before
    public void setUp()
        throws Exception
    {
        handler = new DeleteSiteTemplateHandler();
        systemConfig = Mockito.mock( SystemConfig.class );
        handler.setSystemConfig( systemConfig );
        tempDir = Files.createTempDirectory( "tempo" );
    }

    @Test
    public void delete_site_template()
        throws Exception
    {
        final File templatesDir = new File( tempDir.toFile(), "sites" );
        templatesDir.mkdir();
        final File templateDir = new File( templatesDir, "Intranet-1.0.0" );
        templateDir.mkdir();

        assertTrue( templateDir.exists() );

        Mockito.when( systemConfig.getTemplatesDir() ).thenReturn( templatesDir );

        SiteTemplateKey key = SiteTemplateKey.from( "Intranet-1.0.0" );
        DeleteSiteTemplate command = Commands.site().template().delete( key );
        handler.setCommand( command );
        handler.handle();

        assertEquals( key, command.getResult() );
        assertTrue( templatesDir.exists() );
        assertFalse( templateDir.exists() );
    }

    @Test(expected = NoSiteTemplateExistsException.class)
    public void delete_site_template_not_exist()
        throws Exception
    {
        final File templatesDir = new File( tempDir.toFile(), "sites" );
        templatesDir.mkdir();
        final File templateDir = new File( templatesDir, "Intranet-1.0.0" );
        templateDir.mkdir();

        assertTrue( templateDir.exists() );

        Mockito.when( systemConfig.getTemplatesDir() ).thenReturn( templatesDir );

        DeleteSiteTemplate command = Commands.site().template().delete( SiteTemplateKey.from( "root-1.0.0" ) );
        handler.setCommand( command );
        handler.handle();

        assertTrue( templatesDir.exists() );
        assertTrue( templateDir.exists() );
    }
}