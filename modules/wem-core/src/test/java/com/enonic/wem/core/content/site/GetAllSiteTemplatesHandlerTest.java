package com.enonic.wem.core.content.site;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.site.GetAllSiteTemplates;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.config.SystemConfig;

import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.*;

public class GetAllSiteTemplatesHandlerTest
{
    private GetAllSiteTemplatesHandler handler;

    private SystemConfig systemConfig;

    private SiteTemplateExporter importer;

    private Path tempDir;

    @Before
    public void setUp()
        throws Exception
    {
        handler = new GetAllSiteTemplatesHandler();

        systemConfig = Mockito.mock( SystemConfig.class );
        handler.setSystemConfig( systemConfig );

        importer = new SiteTemplateExporter();
        handler.setSiteTemplateExporter( importer );

        tempDir = Files.createTempDirectory( "wemce" );
    }

    @Test
    public void get_all_site_templates()
        throws Exception
    {
        final File templatesDir = new File( tempDir.toFile(), "sites" );
        templatesDir.mkdir();

        assertTrue( templatesDir.exists() );

        Mockito.when( systemConfig.getTemplatesDir() ).thenReturn( templatesDir.toPath() );

        final SiteTemplate siteTemplate1 =
            createSiteTemplate( "Intranet-1.0.0", "Enonic Intranet", "A social intranet for the Enterprise", "Enonic",
                                "https://www.enonic.com" );
        final SiteTemplate siteTemplate2 =
            createSiteTemplate( "Search-1.0.0", "Google Search", "Ultimate search engine", "Google", "https://www.google.com" );
        importer.exportToDirectory( siteTemplate1, templatesDir.toPath() );
        importer.exportToDirectory( siteTemplate2, templatesDir.toPath() );

        final GetAllSiteTemplates command = Commands.site().template().get().all();

        handler.setCommand( command );
        handler.handle();

        final SiteTemplates result = command.getResult();
        assertEquals( 2, result.getSize() );

        SiteTemplate template = result.get( 0 );

        assertEquals( "Intranet-1.0.0", template.getKey().toString() );
        assertEquals( "Enonic Intranet", template.getDisplayName() );
        assertEquals( "A social intranet for the Enterprise", template.getDescription() );
        assertEquals( "Enonic", template.getVendor().getName() );
        assertEquals( "https://www.enonic.com", template.getVendor().getUrl() );

        template = result.get( 1 );

        assertEquals( "Search-1.0.0", template.getKey().toString() );
        assertEquals( "Google Search", template.getDisplayName() );
        assertEquals( "Ultimate search engine", template.getDescription() );
        assertEquals( "Google", template.getVendor().getName() );
        assertEquals( "https://www.google.com", template.getVendor().getUrl() );

    }

    private SiteTemplate createSiteTemplate( String key, String displayName, String description, String vendorName, String vendorUrl )
    {
        return SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( key ) ).
            displayName( displayName ).
            description( description ).
            vendor( newVendor().name( vendorName ).url( vendorUrl ).build() ).
            modules( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                      "com.enonic.resolvers-1.0.0" ) ).
            rootContentType( ContentTypeName.from( "com.enonic.intranet" ) ).
            build();
    }
}
