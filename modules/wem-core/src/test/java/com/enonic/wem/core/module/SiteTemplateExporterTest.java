package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.page.ImageTemplate;
import com.enonic.wem.api.content.page.ImageTemplateName;
import com.enonic.wem.api.content.page.LayoutTemplate;
import com.enonic.wem.api.content.page.LayoutTemplateName;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.page.PartTemplate;
import com.enonic.wem.api.content.page.PartTemplateName;
import com.enonic.wem.api.content.site.ContentTypeFilter;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.exporters.SiteTemplateExporter;

import static com.enonic.wem.api.content.site.ContentTypeFilter.newContentFilter;
import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public class SiteTemplateExporterTest
{
    private Path tempDir;

    @Before
    public void createTempDir()
        throws IOException
    {
        tempDir = Files.createTempDirectory( "wemtest" );
    }

    @After
    public void deleteTempDir()
    {
        try
        {
            FileUtils.deleteDirectory( tempDir.toFile() );
        }
        catch ( IOException e )
        {
            e.printStackTrace();
        }
    }

    @Test
    public void testExportSiteTemplateToZip()
        throws Exception
    {
        final SiteTemplate siteTemplate = createSiteTemplate();

        final Path exportedSiteTemplateZip = new SiteTemplateExporter().exportToZip( siteTemplate, tempDir );
        System.out.println( "SiteTemplate exported to " + exportedSiteTemplateZip );

        assertNotNull( exportedSiteTemplateZip );
        assertTrue( Files.exists( exportedSiteTemplateZip ) && Files.isRegularFile( exportedSiteTemplateZip ) );
    }

    @Test
    public void testExportSiteTemplateToDirectory()
        throws Exception
    {
        final SiteTemplate siteTemplate = createSiteTemplate();

        final Path exportedSiteTemplateDir = new SiteTemplateExporter().exportToDirectory( siteTemplate, tempDir );
        System.out.println( "SiteTemplate exported to " + exportedSiteTemplateDir );

        assertNotNull( exportedSiteTemplateDir );
        assertTrue( Files.exists( exportedSiteTemplateDir ) && Files.isDirectory( exportedSiteTemplateDir ) );
    }

    private SiteTemplate createSiteTemplate()
    {
        final RootDataSet partTemplateConfig = new RootDataSet();
        partTemplateConfig.addProperty( "width", new Value.Long( 200 ) );

        final PartTemplate partTemplate = PartTemplate.newPartTemplate().
            name( new PartTemplateName( "news-part" ) ).
            displayName( "News part template" ).
            config( partTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/news-part.xml" ) ).
            build();

        final RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", new Value.Long( 10000 ) );

        final PageTemplate pageTemplate = PageTemplate.newPageTemplate().
            name( new PageTemplateName( "main-page" ) ).
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
            key( SiteTemplateKey.from( "Intranet-1.0.0" ) ).
            displayName( "Enonic Intranet" ).
            info( "A social intranet for the Enterprise" ).
            vendor( newVendor().name( "Enonic" ).url( "https://www.enonic.com" ).build() ).
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
