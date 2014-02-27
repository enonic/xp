package com.enonic.wem.core.content.site;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeFilter;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.core.config.SystemConfig;

import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;
import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static com.enonic.wem.api.schema.content.ContentTypeFilter.newContentFilter;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.*;

public class GetSiteTemplateByKeyHandlerTest
{
    private static final String KEY = "Intranet-1.0.0";

    private static final SiteTemplateKey SITE_TEMPLATE_KEY = SiteTemplateKey.from( KEY );

    private static final ModuleKey MODULE_KEY = ModuleKey.from( "mymodule-1.0.0" );

    private GetSiteTemplateByKeyHandler handler;

    private SystemConfig systemConfig;

    private SiteTemplateExporter importer;

    private Path tempDir;

    @Before
    public void setUp()
        throws Exception
    {
        handler = new GetSiteTemplateByKeyHandler();

        systemConfig = Mockito.mock( SystemConfig.class );
        handler.setSystemConfig( systemConfig );

        importer = new SiteTemplateExporter();
        handler.setSiteTemplateExporter( importer );

        tempDir = Files.createTempDirectory( "wemce" );
    }

    @Test
    public void get_site_template()
        throws Exception
    {
        final File templatesDir = new File( tempDir.toFile(), "sites" );
        templatesDir.mkdir();

        assertTrue( templatesDir.exists() );

        Mockito.when( systemConfig.getTemplatesDir() ).thenReturn( templatesDir.toPath() );

        final SiteTemplate siteTemplate = createSiteTemplate();

        importer.exportToDirectory( siteTemplate, templatesDir.toPath() );

        final GetSiteTemplateByKey command = Commands.site().template().get().byKey( SITE_TEMPLATE_KEY );

        handler.setCommand( command );
        handler.handle();

        final SiteTemplate result = command.getResult();

        Assert.assertEquals( "Intranet-1.0.0", result.getKey().toString() );
        Assert.assertEquals( "Enonic Intranet", result.getDisplayName() );
        Assert.assertEquals( "A social intranet for the Enterprise", result.getDescription() );
        Assert.assertEquals( "Enonic", result.getVendor().getName() );
        Assert.assertEquals( "https://www.enonic.com", result.getVendor().getUrl() );

        assertNotNull( result.getPageTemplates().getTemplate( new PageTemplateName( "my-page" ) ) );

        assertTrue( templatesDir.exists() );
        assertTrue( new File( templatesDir, KEY ).exists() );
    }

    @Test(expected = SiteTemplateNotFoundException.class)
    public void get_site_template_dir_not_found()
        throws Exception
    {
        final File templatesDir = new File( tempDir.toFile(), "sites" );
        templatesDir.mkdir();
        final File templateDir = new File( templatesDir, KEY );
        templateDir.mkdir();

        assertTrue( templateDir.exists() );

        Mockito.when( systemConfig.getTemplatesDir() ).thenReturn( templatesDir.toPath() );

        final GetSiteTemplateByKey command = Commands.site().template().get().byKey( SiteTemplateKey.from( "root-1.0.0" ) );

        handler.setCommand( command );
        handler.handle();
    }

    private SiteTemplate createSiteTemplate()
    {
        final RootDataSet partTemplateConfig = new RootDataSet();
        partTemplateConfig.addProperty( "width", new Value.Long( 200 ) );

        final RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", new Value.Long( 10000 ) );

        final PageTemplate pageTemplate = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( MODULE_KEY.getName(), new PageTemplateName( "my-page" ) ) ).
            displayName( "Main page template" ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "article", "banner" ) ).
            descriptor( PageDescriptorKey.from( "mainmodule-1.0.0:landing-page" ) ).
            regions( newPageRegions().build() ).
            build();

        final RootDataSet layoutTemplateConfig = new RootDataSet();
        layoutTemplateConfig.addProperty( "columns", new Value.Long( 3 ) );

        final RootDataSet imageTemplateConfig = new RootDataSet();
        imageTemplateConfig.addProperty( "width", new Value.Long( 3000 ) );

        final ContentTypeFilter contentTypeFilter =
            newContentFilter().defaultDeny().allowContentTypes( ContentTypeNames.from( "com.enonic.intranet", "system.folder" ) ).build();

        return SiteTemplate.newSiteTemplate().
            key( SITE_TEMPLATE_KEY ).
            displayName( "Enonic Intranet" ).
            description( "A social intranet for the Enterprise" ).
            vendor( newVendor().name( "Enonic" ).url( "https://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( "com.enonic.intranet-1.0.0", "com.company.sampleModule-1.1.0", "com.company.theme.someTheme-1.4.1",
                                      "com.enonic.resolvers-1.0.0", "mymodule-1.0.0" ) ).
            contentTypeFilter( contentTypeFilter ).
            rootContentType( ContentTypeName.from( "com.enonic.intranet" ) ).
            addPageTemplate( pageTemplate ).
            build();
    }
}
