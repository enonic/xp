package com.enonic.wem.core.module;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeFilter;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.content.site.SiteTemplateExporter;

import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;
import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static com.enonic.wem.api.schema.content.ContentTypeFilter.newContentFilter;
import static junit.framework.Assert.assertEquals;
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

        checkTemplate( new SiteTemplateExporter().importFromZip( exportedSiteTemplateZip ).build() );
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

        checkTemplate( new SiteTemplateExporter().importFromDirectory( exportedSiteTemplateDir ).build() );
    }

    private void checkTemplate( final SiteTemplate siteTemplate1 )
    {
        assertEquals( "Intranet-1.0.0", siteTemplate1.getKey().toString() );
        assertEquals( "Enonic Intranet", siteTemplate1.getDisplayName() );
        assertEquals( "A social intranet for the Enterprise", siteTemplate1.getDescription() );
        assertEquals( "Enonic", siteTemplate1.getVendor().getName() );
        assertEquals( "https://www.enonic.com", siteTemplate1.getVendor().getUrl() );

        assertNotNull( siteTemplate1.getPageTemplates().getTemplate( new PageTemplateName( "my-page" ) ) );
    }

    private SiteTemplate createSiteTemplate()
    {
        final RootDataSet partTemplateConfig = new RootDataSet();
        partTemplateConfig.addProperty( "width", Value.newLong( 200 ) );

        final RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", Value.newLong( 10000 ) );

        final PageTemplate pageTemplate = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( new PageTemplateName( "my-page" ) ) ).
            displayName( "Main page template" ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "mymodule:article", "mymodule:banner" ) ).
            descriptor( PageDescriptorKey.from( "mainmodule:landing-page" ) ).
            regions( newPageRegions().build() ).
            build();

        final RootDataSet layoutTemplateConfig = new RootDataSet();
        layoutTemplateConfig.addProperty( "columns", Value.newLong( 3 ) );

        final RootDataSet imageTemplateConfig = new RootDataSet();
        imageTemplateConfig.addProperty( "width", Value.newLong( 3000 ) );

        final ContentTypeFilter contentTypeFilter = newContentFilter().defaultDeny().allowContentTypes(
            ContentTypeNames.from( "mymodule:com.enonic.intranet", "mymodule:system.folder" ) ).build();

        return SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "Intranet-1.0.0" ) ).
            displayName( "Enonic Intranet" ).
            description( "A social intranet for the Enterprise" ).
            vendor( newVendor().name( "Enonic" ).url( "https://www.enonic.com" ).build() ).
            modules(
                ModuleKeys.from( "com.enonic.intranet", "com.company.sampleModule", "com.company.theme.someTheme", "com.enonic.resolvers",
                                 "mymodule" ) ).
            contentTypeFilter( contentTypeFilter ).
            addPageTemplate( pageTemplate ).
            build();
    }
}
