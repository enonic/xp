package com.enonic.wem.core.content.site;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.site.GetAllSiteTemplates;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplateName;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateName;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateName;
import com.enonic.wem.api.content.site.ContentTypeFilter;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.config.SystemConfig;
import com.enonic.wem.core.exporters.SiteTemplateExporter;

import static com.enonic.wem.api.content.site.ContentTypeFilter.newContentFilter;
import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
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

        Mockito.when( systemConfig.getTemplatesDir() ).thenReturn( templatesDir );

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

        assertNotNull( template.getImageTemplates().getTemplate( new ImageTemplateName( "my-image" ) ) );
        assertNotNull( template.getPartTemplates().getTemplate( new PartTemplateName( "my-part" ) ) );
        assertNotNull( template.getLayoutTemplates().getTemplate( new LayoutTemplateName( "my-layout" ) ) );
        assertNotNull( template.getPageTemplates().getTemplate( new PageTemplateName( "my-page" ) ) );

        template = result.get( 1 );

        assertEquals( "Search-1.0.0", template.getKey().toString() );
        assertEquals( "Google Search", template.getDisplayName() );
        assertEquals( "Ultimate search engine", template.getDescription() );
        assertEquals( "Google", template.getVendor().getName() );
        assertEquals( "https://www.google.com", template.getVendor().getUrl() );

        assertNotNull( template.getImageTemplates().getTemplate( new ImageTemplateName( "my-image" ) ) );
        assertNotNull( template.getPartTemplates().getTemplate( new PartTemplateName( "my-part" ) ) );
        assertNotNull( template.getLayoutTemplates().getTemplate( new LayoutTemplateName( "my-layout" ) ) );
        assertNotNull( template.getPageTemplates().getTemplate( new PageTemplateName( "my-page" ) ) );
    }

    private SiteTemplate createSiteTemplate( String key, String displayName, String description, String vendorName, String vendorUrl )
    {
        final RootDataSet partTemplateConfig = new RootDataSet();
        partTemplateConfig.addProperty( "width", new Value.Long( 200 ) );

        final PartTemplate partTemplate = PartTemplate.newPartTemplate().
            name( new PartTemplateName( "my-part" ) ).
            displayName( "News part template" ).
            config( partTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/news-part.xml" ) ).
            build();

        final RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", new Value.Long( 10000 ) );

        final PageTemplate pageTemplate = PageTemplate.newPageTemplate().
            name( new PageTemplateName( "my-page" ) ).
            displayName( "Main page template" ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "article", "banner" ) ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/landing-page.xml" ) ).
            build();

        final RootDataSet layoutTemplateConfig = new RootDataSet();
        layoutTemplateConfig.addProperty( "columns", new Value.Long( 3 ) );

        final LayoutTemplate layoutTemplate = LayoutTemplate.newLayoutTemplate().
            name( new LayoutTemplateName( "my-layout" ) ).
            displayName( "Layout template" ).
            config( layoutTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/some-layout.xml" ) ).
            build();

        final RootDataSet imageTemplateConfig = new RootDataSet();
        imageTemplateConfig.addProperty( "width", new Value.Long( 3000 ) );

        final ImageTemplate imageTemplate = ImageTemplate.newImageTemplate().
            name( new ImageTemplateName( "my-image" ) ).
            displayName( "Image template" ).
            config( imageTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/some-image.xml" ) ).
            build();

        final ContentTypeFilter contentTypeFilter =
            newContentFilter().defaultDeny().allowContentTypes( ContentTypeNames.from( "com.enonic.intranet", "system.folder" ) ).build();

        return SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( key ) ).
            displayName( displayName ).
            description( description ).
            vendor( newVendor().name( vendorName ).url( vendorUrl ).build() ).
            modules( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                      "com.enonic.resolvers-1.0.0" ) ).
            contentTypeFilter( contentTypeFilter ).
            rootContentType( ContentTypeName.from( "com.enonic.intranet" ) ).
            addTemplate( partTemplate ).
            addTemplate( pageTemplate ).
            addTemplate( layoutTemplate ).
            addTemplate( imageTemplate ).
            build();
    }
}
