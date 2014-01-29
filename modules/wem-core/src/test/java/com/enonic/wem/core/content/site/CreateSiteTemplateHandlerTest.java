package com.enonic.wem.core.content.site;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.command.Commands;
import com.enonic.wem.api.command.content.site.CreateSiteTemplate;
import com.enonic.wem.api.content.page.Template;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateKey;
import com.enonic.wem.api.content.page.part.PartTemplateName;
import com.enonic.wem.api.content.site.ContentTypeFilter;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.Vendor;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.core.config.SystemConfig;

import static com.enonic.wem.api.content.site.ContentTypeFilter.newContentFilter;
import static org.junit.Assert.*;

public class CreateSiteTemplateHandlerTest
{

    private static final ModuleKey MODULE_KEY = ModuleKey.from( "foomodule-1.0.0" );

    private static final SiteTemplateKey SITE_TEMPLATE = SiteTemplateKey.from( "Intranet-1.0.0" );

    private CreateSiteTemplateHandler handler;

    private SystemConfig systemConfig;

    private Path tempDir;

    @Before
    public void setUp()
        throws Exception
    {
        handler = new CreateSiteTemplateHandler();

        systemConfig = Mockito.mock( SystemConfig.class );
        handler.setSystemConfig( systemConfig );

        handler.setSiteTemplateExporter( new SiteTemplateExporter() );

        tempDir = Files.createTempDirectory( "wemce" );
    }

    @Test
    public void create_site_template()
        throws Exception
    {
        Vendor.Builder vendorBuilder = new Vendor.Builder();
        vendorBuilder.name( "Enonic" );
        vendorBuilder.url( "http://enonic.net" );
        final Vendor vendor = vendorBuilder.build();

        final ModuleKeys moduleKeys = ModuleKeys.from( MODULE_KEY );

        CreateSiteTemplate command = Commands.site().template().create().
            siteTemplateKey( SITE_TEMPLATE ).
            displayName( "name" ).
            vendor( vendor ).
            modules( moduleKeys ).
            description( "description" ).
            rootContentType( ContentTypeName.from( "document" ) );

        PartTemplate.Builder templateBuilder = PartTemplate.newPartTemplate();
        templateBuilder.key( PartTemplateKey.from( MODULE_KEY.getName(), new PartTemplateName( "template-name" ) ) );
        templateBuilder.displayName( "display-name" );
        templateBuilder.descriptor( PartDescriptorKey.from( "resource-1.0.0:part-descr" ) );
        PartTemplate template = templateBuilder.build();

        command.addTemplate( template );

        final ContentTypeFilter contentTypeFilter = newContentFilter().
            allowContentType( "article" ).
            build();
        command.contentTypeFilter( contentTypeFilter );

        final File templatesDir = new File( tempDir.toFile(), "sites" );
        templatesDir.mkdir();
        final File templateDir = new File( templatesDir, "Intranet-1.0.0" );
        templateDir.mkdir();

        assertTrue( templateDir.exists() );

        Mockito.when( systemConfig.getTemplatesDir() ).thenReturn( templatesDir.toPath() );

        handler.setCommand( command );
        handler.handle();

        SiteTemplate siteTemplate = command.getResult();
        assertEquals( "name", siteTemplate.getDisplayName() );
        assertEquals( "Enonic", siteTemplate.getVendor().getName() );
        assertEquals( "http://enonic.net", siteTemplate.getVendor().getUrl() );
        assertEquals( ContentTypeName.from( "document" ), siteTemplate.getRootContentType() );
        assertEquals( ContentTypeName.from( "article" ), siteTemplate.getContentTypeFilter().iterator().next() );

        final Template resTempl = siteTemplate.iterator().next();
        assertEquals( new PartTemplateName( "template-name" ), resTempl.getName() );
    }
}
