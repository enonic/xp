package com.enonic.wem.admin.rest.resource.content.site.template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mockito;

import com.google.common.collect.Lists;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Icon;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.site.CreateSiteTemplateParams;
import com.enonic.wem.api.content.site.NoSiteTemplateExistsException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplateService;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.api.content.site.UpdateSiteTemplateParams;
import com.enonic.wem.api.content.site.Vendor;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.SchemaName;
import com.enonic.wem.api.schema.content.ContentTypeFilter;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.content.ContentTypeService;
import com.enonic.wem.api.support.export.InvalidZipFileException;
import com.enonic.wem.core.content.site.SiteTemplateExporter;

import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;
import static com.enonic.wem.api.content.page.PageTemplate.newPageTemplate;
import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static org.junit.Assert.*;

public class SiteTemplateResourceTest
    extends AbstractResourceTest
{
    private static final Instant SOME_DATE = LocalDateTime.of( 2013, 1, 1, 12, 0, 0 ).toInstant( ZoneOffset.UTC );

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    private SiteTemplateService siteTemplateService;

    private ContentTypeService contentTypeService;

    private Path tempDir;

    @Override
    protected Object getResourceInstance()
    {
        siteTemplateService = Mockito.mock( SiteTemplateService.class );
        SiteTemplateResource resource = new SiteTemplateResource();
        resource.setSiteTemplateService( siteTemplateService );

        contentTypeService = Mockito.mock( ContentTypeService.class );
        resource.setContentTypeService( contentTypeService );

        return resource;
    }

    @Before
    public void setup()
        throws IOException
    {
        this.tempDir = Files.createTempDirectory( "wemtest" );
    }

    @Test
    public void list_site_template_success()
        throws Exception
    {
        final SiteTemplate siteTemplate = createSiteTemplate();
        final SiteTemplates siteTemplates = SiteTemplates.from( siteTemplate );

        Mockito.when( this.siteTemplateService.getSiteTemplates() ).thenReturn( siteTemplates );

        String resultJson = request().path( "content/site/template/list" ).get().getAsString();

        assertJson( "list_site_template_success.json", resultJson );
    }

    @Test
    public void testTemplateTreeSite()
        throws Exception
    {
        final SiteTemplate siteTemplate = createSiteTemplate();
        final SiteTemplates siteTemplates = SiteTemplates.from( siteTemplate );

        Mockito.when( this.siteTemplateService.getSiteTemplates() ).thenReturn( siteTemplates );

        String resultJson = request().path( "content/site/template/tree" ).get().getAsString();

        assertJson( "tree_site_template_success.json", resultJson );
    }

    @Test
    public void testTemplateTreePage()
        throws Exception
    {
        final SiteTemplate siteTemplate = createTemplateWithPageTemplates();
        final SiteTemplateKey siteTemplateKey = siteTemplate.getKey();
        Mockito.when( siteTemplateService.getSiteTemplate( Mockito.eq( siteTemplateKey ) ) ).thenReturn( siteTemplate );

        String resultJson = request().path( "content/site/template/tree" ).
            queryParam( "parentId", "name" ).
            get().getAsString();

        assertJson( "tree_page_templates_success.json", resultJson );
    }

    @Test
    public void testDeleteSiteTemplate()
        throws Exception
    {
        String response = request().path( "content/site/template/delete" ).entity( readFromFile( "delete_site_template_params.json" ),
                                                                                   MediaType.APPLICATION_JSON_TYPE ).post().getAsString();
        assertJson( "delete_site_template_success.json", response );
    }

    @Test(expected = NoSiteTemplateExistsException.class)
    public void testDeleteNonExistingSiteTemplate()
        throws Exception
    {
        Mockito.doThrow( new NoSiteTemplateExistsException( SiteTemplateKey.from( "sitetemplate" ) ) ).when(
            this.siteTemplateService ).deleteSiteTemplate( Mockito.isA( SiteTemplateKey.class ) );
        String response = request().path( "content/site/template/delete" ).entity( readFromFile( "delete_site_template_params.json" ),
                                                                                   MediaType.APPLICATION_JSON_TYPE ).post().getAsString();
        assertJson( "delete_site_template_failure.json", response );
    }

    @Test
    public void testGetSiteTemplate()
        throws Exception
    {
        final PageTemplate pageTemplate = newPageTemplate().
            key( PageTemplateKey.from( "mod|mainpage" ) ).
            displayName( "Main Page" ).
            descriptor( PageDescriptorKey.from( ModuleKey.from( "mod" ), new ComponentDescriptorName( "page-descr" ) ) ).
            config( new RootDataSet() ).
            build();

        final ContentTypeFilter filter = ContentTypeFilter.newContentFilter().
            defaultAllow().
            denyContentType( ContentTypeName.from( "mymodule:com.enonic.tweet" ) ).
            denyContentType( "mymodule:system.folder" ).
            denyContentTypes( ContentTypeNames.from( "mymodule:com.enonic.article", "mymodule:com.enonic.employee" ) ).
            build();

        final SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "blueman" ) ).
            displayName( "Blueman Site Template" ).
            icon( Icon.from( new byte[]{123}, "image/gif", SOME_DATE ) ).
            vendor( newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( "module1", "module2" ) ).
            description( "Demo site template" ).
            url( "http://enonic.net" ).
            contentTypeFilter( filter ).
            addPageTemplate( pageTemplate ).
            build();

        Mockito.when( siteTemplateService.getSiteTemplate( Mockito.isA( SiteTemplateKey.class ) ) ).thenReturn( siteTemplate );
        String response = request().
            path( "content/site/template" ).
            queryParam( "siteTemplateKey", siteTemplate.getKey().toString() ).
            get().getAsString();
        assertJson( "get_site_template_by_key_success.json", response );
    }

    @Test(expected = SiteTemplateNotFoundException.class)
    public void testGetSiteTemplateMissing()
        throws Exception
    {
        final SiteTemplateKey siteTemplate = SiteTemplateKey.from( "blueman" );
        Mockito.when( siteTemplateService.getSiteTemplate( Mockito.isA( SiteTemplateKey.class ) ) ).thenThrow(
            new SiteTemplateNotFoundException( siteTemplate ) );
        request().
            path( "content/site/template" ).
            queryParam( "siteTemplateKey", siteTemplate.toString() ).
            get().getAsString();
    }

    @Test(expected = InvalidZipFileException.class)
    public void import_site_template_exception()
        throws Exception
    {
        final String jsonString = request().
            path( "content/site/template/import" ).
            multipart( "file", "template.zip", "INVALID_ZIP_CONTENT".getBytes(), MediaType.TEXT_PLAIN_TYPE ).
            post().getAsString();

        assertJson( "import_site_template_exception.json", jsonString );
    }

    @Test
    public void create_site_template_success()
        throws Exception
    {
        final PageTemplate pageTemplate = newPageTemplate().
            key( PageTemplateKey.from( "mod|mainpage" ) ).
            displayName( "Main Page" ).
            descriptor( PageDescriptorKey.from( ModuleKey.from( "mod" ), new ComponentDescriptorName( "page-descr" ) ) ).
            config( new RootDataSet() ).
            build();

        final ContentTypeFilter filter = ContentTypeFilter.newContentFilter().
            defaultAllow().
            denyContentType( ContentTypeName.from( "mymodule:com.enonic.tweet" ) ).
            denyContentType( "mymodule:system.folder" ).
            denyContentTypes( ContentTypeNames.from( "mymodule:com.enonic.article", "mymodule:com.enonic.employee" ) ).
            build();

        final SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "blueman" ) ).
            displayName( "Blueman Site Template" ).
            icon( Icon.from( new byte[]{123}, "image/gif", SOME_DATE ) ).
            vendor( newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( "module1", "module2" ) ).
            description( "Demo site template" ).
            url( "http://enonic.net" ).
            contentTypeFilter( filter ).
            addPageTemplate( pageTemplate ).
            build();

        Mockito.when( this.siteTemplateService.createSiteTemplate( Mockito.isA( CreateSiteTemplateParams.class ) ) ).thenAnswer(
            invocation -> {
                final CreateSiteTemplateParams command = (CreateSiteTemplateParams) invocation.getArguments()[0];
                assertEquals( "Demo site template", command.getDescription() );
                assertEquals( "Blueman Site Template", command.getDisplayName() );
                assertEquals( "www.enonic.com", command.getUrl() );

                // Vendor
                Vendor vendor = Vendor.newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build();
                assertEquals( vendor, command.getVendor() );

                // Module keys
                assertUnorderedListEquals( new String[]{"module1", "module2"}, command.getModules().getList() );

                // ContentTypeFilter
                final ContentTypeFilter filter1 = command.getContentTypeFilter();
                assertListEquals( new String[]{"mymodule:image", "mymodule:com.enonic.tweet", "mymodule:system.folder", "mymodule:com.enonic.article", "mymodule:com.enonic.employee"},
                                  parseContentTypeNames( filter1.iterator() ) );

                return siteTemplate;
            } );

        String jsonString = request().path( "content/site/template/create" ).
            entity( readFromFile( "create_site_template_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "create_site_template_success.json", jsonString );
    }

    @Test
    public void update_site_template_success()
        throws Exception
    {
        final PageTemplate pageTemplate = newPageTemplate().
            key( PageTemplateKey.from( "mod|mainpage" ) ).
            displayName( "Main Page" ).
            descriptor( PageDescriptorKey.from( ModuleKey.from( "mod" ), new ComponentDescriptorName( "page-descr" ) ) ).
            config( new RootDataSet() ).
            build();

        final ContentTypeFilter filter = ContentTypeFilter.newContentFilter().
            defaultAllow().
            denyContentType( ContentTypeName.from( "mymodule:com.enonic.tweet" ) ).
            denyContentType( "mymodule:system.folder" ).
            denyContentTypes( ContentTypeNames.from( "mymodule:com.enonic.article", "mymodule:com.enonic.employee" ) ).
            build();

        final SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "blueman" ) ).
            displayName( "Blueman Site Template" ).
            icon( Icon.from( new byte[]{123}, "image/gif", SOME_DATE ) ).
            vendor( newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( "module1", "module2" ) ).
            description( "Demo site template" ).
            url( "http://enonic.net" ).
            contentTypeFilter( filter ).
            addPageTemplate( pageTemplate ).
            build();

        Mockito.when( this.siteTemplateService.updateSiteTemplate( Mockito.isA( UpdateSiteTemplateParams.class ) ) ).thenAnswer(
            invocation -> {
                final UpdateSiteTemplateParams command = (UpdateSiteTemplateParams) invocation.getArguments()[0];
                assertEquals( SiteTemplateKey.from( "blueman" ), command.getKey() );

                return siteTemplate;
            } );

        String jsonString = request().path( "content/site/template/update" ).
            entity( readFromFile( "update_site_template_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post().getAsString();

        assertJson( "update_site_template_success.json", jsonString );
    }

    private List<String> parseContentTypeNames( Iterator<ContentTypeName> iterator )
    {
        return Lists.transform( Lists.newArrayList( iterator ), SchemaName::toString );
    }

    @Test
    @Ignore
    public void import_site_template_success()
        throws Exception
    {
        final SiteTemplate siteTemplate = createSiteTemplate();
        Mockito.when( siteTemplateService.createSiteTemplate( Mockito.isA( CreateSiteTemplateParams.class ) ) ).thenReturn( siteTemplate );

        final SiteTemplateExporter siteTemplateExporter = new SiteTemplateExporter();
        final Path exportedSiteTemplateFile = siteTemplateExporter.exportToZip( siteTemplate, tempDir );

        final byte[] fileData = Files.readAllBytes( exportedSiteTemplateFile );
        final String jsonString = request().
            path( "content/site/template/import" ).
            multipart( "file", "name.zip", fileData, MediaType.APPLICATION_OCTET_STREAM_TYPE ).
            post().getAsString();

        assertJson( "import_site_template_success.json", jsonString );
    }

    @Test
    @Ignore
    public void export_site_template_success()
        throws Exception
    {
        final SiteTemplate siteTemplate = createSiteTemplate();
        Mockito.when( siteTemplateService.getSiteTemplate( Mockito.isA( SiteTemplateKey.class ) ) ).thenReturn( siteTemplate );

        final byte[] response = request().
            path( "content/site/template/export" ).
            queryParam( "siteTemplateKey", "name" ).get().getData();

        final SiteTemplateExporter exporter = new SiteTemplateExporter();
        final Path zipFilePath = Files.write( tempDir.resolve( "name.zip" ), response );
        final SiteTemplate exportedTemplate = exporter.importFromZip( zipFilePath ).build();

        assertEquals( "displayName", exportedTemplate.getDisplayName() );
        assertEquals( "name", exportedTemplate.getKey().toString() );
    }

    private SiteTemplate createSiteTemplate()
    {
        return SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "name" ) ).
            displayName( "displayName" ).
            icon( Icon.from( new byte[]{123}, "image/gif", SOME_DATE ) ).
            description( "info" ).
            url( "url" ).
            vendor( Vendor.newVendor().name( "vendorName" ).url( "vendorUrl" ).build() ).
            modules( ModuleKeys.from( "module1" ) ).
            contentTypeFilter( ContentTypeFilter.newContentFilter().
                allowContentType( ContentTypeName.imageMedia() ).
                denyContentType( ContentTypeName.shortcut() ).
                build() ).
            build();
    }

    private SiteTemplate createTemplateWithPageTemplates()
    {
        final PageTemplate pageTemplate1 = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "module|my-page" ) ).
            displayName( "Main page template" ).
            canRender( ContentTypeNames.from( "mymodule:article", "mymodule:banner" ) ).
            descriptor( PageDescriptorKey.from( "mainmodule:landing-page" ) ).
            regions( newPageRegions().build() ).
            build();
        final PageTemplate pageTemplate2 = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "module|my-other-page" ) ).
            displayName( "Another page template" ).
            canRender( ContentTypeNames.from( "mymodule:article" ) ).
            descriptor( PageDescriptorKey.from( "mainmodule:other-page" ) ).
            regions( newPageRegions().build() ).
            build();

        return SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "name" ) ).
            displayName( "displayName" ).
            description( "info" ).
            url( "url" ).
            vendor( Vendor.newVendor().name( "vendorName" ).url( "vendorUrl" ).build() ).
            modules( ModuleKeys.from( "module1" ) ).
            contentTypeFilter( ContentTypeFilter.newContentFilter().
                allowContentType( ContentTypeName.imageMedia() ).
                denyContentType( ContentTypeName.shortcut() ).
                build() ).
            addPageTemplate( pageTemplate1 ).
            addPageTemplate( pageTemplate2 ).
            build();
    }
}
