package com.enonic.wem.admin.rest.resource.content.site.template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
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
import com.enonic.wem.api.schema.content.ContentTypeFilter;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.support.export.InvalidZipFileException;
import com.enonic.wem.core.content.site.SiteTemplateExporter;

import static com.enonic.wem.api.content.page.PageRegions.newPageRegions;
import static com.enonic.wem.api.content.page.PageTemplate.newPageTemplate;
import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static org.junit.Assert.*;

public class SiteTemplateResourceTest
    extends AbstractResourceTest
{
    private SiteTemplateService siteTemplateService;

    private Path tempDir;

    @Override
    protected Object getResourceInstance()
    {
        siteTemplateService = Mockito.mock( SiteTemplateService.class );
        SiteTemplateResource resource = new SiteTemplateResource();
        resource.setSiteTemplateService( siteTemplateService );

        return resource;
    }

    @Before
    public void setup()
        throws IOException
    {
        mockCurrentContextHttpRequest();
        tempDir = Files.createTempDirectory( "wemtest" );
    }

    @After
    public void after()
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
    public void list_site_template_success()
        throws Exception
    {
        final SiteTemplate siteTemplate = createSiteTemplate();
        final SiteTemplates siteTemplates = SiteTemplates.from( siteTemplate );

        Mockito.when( this.siteTemplateService.getSiteTemplates() ).thenReturn( siteTemplates );

        String resultJson = resource().path( "content/site/template/list" ).get( String.class );

        assertJson( "list_site_template_success.json", resultJson );
    }

    @Test
    public void testTemplateTreeSite()
        throws Exception
    {
        final SiteTemplate siteTemplate = createSiteTemplate();
        final SiteTemplates siteTemplates = SiteTemplates.from( siteTemplate );

        Mockito.when( this.siteTemplateService.getSiteTemplates() ).thenReturn( siteTemplates );

        String resultJson = resource().path( "content/site/template/tree" ).get( String.class );

        assertJson( "tree_site_template_success.json", resultJson );
    }

    @Test
    public void testTemplateTreePage()
        throws Exception
    {
        final SiteTemplate siteTemplate = createTemplateWithPageTemplates();
        final SiteTemplateKey siteTemplateKey = siteTemplate.getKey();
        Mockito.when( siteTemplateService.getSiteTemplate( Mockito.eq( siteTemplateKey ) ) ).thenReturn( siteTemplate );

        String resultJson = resource().path( "content/site/template/tree" ).
            queryParam( "parentId", "name-1.0.0" ).
            get( String.class );

        assertJson( "tree_page_templates_success.json", resultJson );
    }

    @Test
    public void testDeleteSiteTemplate()
        throws Exception
    {
        String response = resource().path( "content/site/template/delete" ).entity( readFromFile( "delete_site_template_params.json" ),
                                                                                    MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "delete_site_template_success.json", response );
    }

    @Test(expected = NoSiteTemplateExistsException.class)
    public void testDeleteNonExistingSiteTemplate()
        throws Exception
    {
        Mockito.doThrow( new NoSiteTemplateExistsException( SiteTemplateKey.from( "sitetemplate-1.0.0" ) ) ).when(
            this.siteTemplateService ).deleteSiteTemplate( Mockito.isA( SiteTemplateKey.class ) );
        String response = resource().path( "content/site/template/delete" ).entity( readFromFile( "delete_site_template_params.json" ),
                                                                                    MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "delete_site_template_failure.json", response );
    }

    @Test
    public void testGetSiteTemplate()
        throws Exception
    {
        final PageTemplate pageTemplate = newPageTemplate().
            key( PageTemplateKey.from( "mod|mainpage" ) ).
            displayName( "Main Page" ).
            descriptor( PageDescriptorKey.from( ModuleKey.from( "mod-1.0.0" ), new ComponentDescriptorName( "page-descr" ) ) ).
            config( new RootDataSet() ).
            build();

        final ContentTypeFilter filter = ContentTypeFilter.newContentFilter().
            defaultAllow().
            denyContentType( ContentTypeName.from( "com.enonic.tweet" ) ).
            denyContentType( "system.folder" ).
            denyContentTypes( ContentTypeNames.from( "com.enonic.article", "com.enonic.employee" ) ).
            build();

        final SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "blueman-1.0.0" ) ).
            displayName( "Blueman Site Template" ).
            vendor( newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( "module1-1.0.0", "module2-1.0.0" ) ).
            description( "Demo site template" ).
            url( "http://enonic.net" ).
            contentTypeFilter( filter ).
            rootContentType( ContentTypeName.page() ).
            addPageTemplate( pageTemplate ).
            build();

        Mockito.when( siteTemplateService.getSiteTemplate( Mockito.isA( SiteTemplateKey.class ) ) ).thenReturn( siteTemplate );
        String response = resource().
            path( "content/site/template" ).
            queryParam( "siteTemplateKey", siteTemplate.getKey().toString() ).
            get( String.class );
        assertJson( "get_site_template_by_key_success.json", response );
    }

    @Test(expected = SiteTemplateNotFoundException.class)
    public void testGetSiteTemplateMissing()
        throws Exception
    {
        final SiteTemplateKey siteTemplate = SiteTemplateKey.from( "blueman-1.0.0" );
        Mockito.when( siteTemplateService.getSiteTemplate( Mockito.isA( SiteTemplateKey.class ) ) ).thenThrow(
            new SiteTemplateNotFoundException( siteTemplate ) );
        resource().
            path( "content/site/template" ).
            queryParam( "siteTemplateKey", siteTemplate.toString() ).
            get( String.class );
    }

    @Test(expected = InvalidZipFileException.class)
    public void import_site_template_exception()
        throws Exception
    {
        final WebResource webResource = resource().path( "content/site/template/import" );
        final FormDataMultiPart mp = new FormDataMultiPart();
        final FormDataContentDisposition file = FormDataContentDisposition.name( "file" ).fileName( "template-1.0.0.zip" ).build();
        mp.bodyPart( new FormDataBodyPart( file, "INVALID_ZIP_CONTENT" ) );

        final String jsonString = webResource.type( MediaType.MULTIPART_FORM_DATA_TYPE ).post( String.class, mp );

        assertJson( "import_site_template_exception.json", jsonString );
    }

    @Test
    public void create_site_template_success()
        throws Exception
    {
        final PageTemplate pageTemplate = newPageTemplate().
            key( PageTemplateKey.from( "mod|mainpage" ) ).
            displayName( "Main Page" ).
            descriptor( PageDescriptorKey.from( ModuleKey.from( "mod-1.0.0" ), new ComponentDescriptorName( "page-descr" ) ) ).
            config( new RootDataSet() ).
            build();

        final ContentTypeFilter filter = ContentTypeFilter.newContentFilter().
            defaultAllow().
            denyContentType( ContentTypeName.from( "com.enonic.tweet" ) ).
            denyContentType( "system.folder" ).
            denyContentTypes( ContentTypeNames.from( "com.enonic.article", "com.enonic.employee" ) ).
            build();

        final SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "blueman-1.0.0" ) ).
            displayName( "Blueman Site Template" ).
            vendor( newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( "module1-1.0.0", "module2-1.0.0" ) ).
            description( "Demo site template" ).
            url( "http://enonic.net" ).
            contentTypeFilter( filter ).
            rootContentType( ContentTypeName.page() ).
            addPageTemplate( pageTemplate ).
            build();

        Mockito.when( this.siteTemplateService.createSiteTemplate( Mockito.isA( CreateSiteTemplateParams.class ) ) ).thenAnswer( new Answer()
        {
            @Override
            public Object answer( final InvocationOnMock invocation )
                throws Throwable
            {
                final CreateSiteTemplateParams command = (CreateSiteTemplateParams) invocation.getArguments()[0];
                assertEquals( "Demo site template", command.getDescription() );
                assertEquals( "Blueman Site Template", command.getDisplayName() );
                assertEquals( "www.enonic.com", command.getUrl() );

                // Vendor
                Vendor vendor = Vendor.newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build();
                assertEquals( vendor, command.getVendor() );

                // Module keys
                assertUnorderedListEquals( new String[]{"module1-1.0.0", "module2-1.0.0"}, command.getModules().getList() );

                // ContentTypeFilter
                final ContentTypeFilter filter = command.getContentTypeFilter();
                assertListEquals( new String[]{"image", "com.enonic.tweet", "system.folder", "com.enonic.article", "com.enonic.employee"},
                                  parseContentTypeNames( filter.iterator() ) );

                // RootContentType
                assertEquals( ContentTypeName.from( "page" ), command.getRootContentType() );

                return siteTemplate;
            }
        } );

        String jsonString = resource().path( "content/site/template/create" ).
            entity( readFromFile( "create_site_template_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "create_site_template_success.json", jsonString );
    }

    @Test
    public void update_site_template_success()
        throws Exception
    {
        final PageTemplate pageTemplate = newPageTemplate().
            key( PageTemplateKey.from( "mod|mainpage" ) ).
            displayName( "Main Page" ).
            descriptor( PageDescriptorKey.from( ModuleKey.from( "mod-1.0.0" ), new ComponentDescriptorName( "page-descr" ) ) ).
            config( new RootDataSet() ).
            build();

        final ContentTypeFilter filter = ContentTypeFilter.newContentFilter().
            defaultAllow().
            denyContentType( ContentTypeName.from( "com.enonic.tweet" ) ).
            denyContentType( "system.folder" ).
            denyContentTypes( ContentTypeNames.from( "com.enonic.article", "com.enonic.employee" ) ).
            build();

        final SiteTemplate siteTemplate = SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "blueman-1.0.0" ) ).
            displayName( "Blueman Site Template" ).
            vendor( newVendor().name( "Enonic AS" ).url( "http://www.enonic.com" ).build() ).
            modules( ModuleKeys.from( "module1-1.0.0", "module2-1.0.0" ) ).
            description( "Demo site template" ).
            url( "http://enonic.net" ).
            contentTypeFilter( filter ).
            rootContentType( ContentTypeName.page() ).
            addPageTemplate( pageTemplate ).
            build();

        Mockito.when( this.siteTemplateService.updateSiteTemplate( Mockito.isA( UpdateSiteTemplateParams.class ) ) ).thenAnswer( new Answer()
        {
            @Override
            public Object answer( final InvocationOnMock invocation )
                throws Throwable
            {
                final UpdateSiteTemplateParams command = (UpdateSiteTemplateParams) invocation.getArguments()[0];
                assertEquals( SiteTemplateKey.from( "blueman-1.0.0" ), command.getKey() );

                return siteTemplate;
            }
        } );

        String jsonString = resource().path( "content/site/template/update" ).
            entity( readFromFile( "update_site_template_params.json" ), MediaType.APPLICATION_JSON_TYPE ).
            post( String.class );

        assertJson( "update_site_template_success.json", jsonString );
    }

    private List<String> parseContentTypeNames( Iterator<ContentTypeName> iterator )
    {
        return Lists.transform( Lists.newArrayList( iterator ), new Function<ContentTypeName, String>()
        {
            @Override
            public String apply( final ContentTypeName name )
            {
                return name.toString();
            }
        } );
    }

    @Test
    public void import_site_template_success()
        throws Exception
    {
        final SiteTemplate siteTemplate = createSiteTemplate();
        Mockito.when( siteTemplateService.createSiteTemplate( Mockito.isA( CreateSiteTemplateParams.class ) ) ).thenReturn( siteTemplate );

        final SiteTemplateExporter siteTemplateExporter = new SiteTemplateExporter();
        final Path exportedSiteTemplateFile = siteTemplateExporter.exportToZip( siteTemplate, tempDir );

        final WebResource webResource = resource().path( "content/site/template/import" );
        final FormDataMultiPart mp = new FormDataMultiPart();
        final FormDataContentDisposition file = FormDataContentDisposition.name( "file" ).fileName( "name-1.0.0.zip" ).build();
        final byte[] fileData = Files.readAllBytes( exportedSiteTemplateFile );
        final FormDataBodyPart p = new FormDataBodyPart( file, fileData, MediaType.APPLICATION_OCTET_STREAM_TYPE );
        mp.bodyPart( p );

        final String jsonString = webResource.type( MediaType.MULTIPART_FORM_DATA_TYPE ).post( String.class, mp );

        assertJson( "import_site_template_success.json", jsonString );
    }

    @Test
    public void export_site_template_success()
        throws Exception
    {
        final SiteTemplate siteTemplate = createSiteTemplate();
        Mockito.when( siteTemplateService.getSiteTemplate( Mockito.isA( SiteTemplateKey.class ) ) ).thenReturn( siteTemplate );

        final WebResource webResource = resource().
            path( "content/site/template/export" ).
            queryParam( "siteTemplateKey", "name-1.0.0" );
        final byte[] response = webResource.get( byte[].class );

        final SiteTemplateExporter exporter = new SiteTemplateExporter();
        final Path zipFilePath = Files.write( tempDir.resolve( "name-1.0.0.zip" ), response );
        final SiteTemplate exportedTemplate = exporter.importFromZip( zipFilePath ).build();

        assertEquals( "displayName", exportedTemplate.getDisplayName() );
        assertEquals( "name-1.0.0", exportedTemplate.getKey().toString() );
    }

    private SiteTemplate createSiteTemplate()
    {
        return SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "name-1.0.0" ) ).
            displayName( "displayName" ).
            description( "info" ).
            url( "url" ).
            vendor( Vendor.newVendor().name( "vendorName" ).url( "vendorUrl" ).build() ).
            modules( ModuleKeys.from( "module1-1.0.0" ) ).
            contentTypeFilter( ContentTypeFilter.newContentFilter().
                allowContentType( ContentTypeName.imageMedia() ).
                denyContentType( ContentTypeName.shortcut() ).
                build() ).
            rootContentType( ContentTypeName.folder() ).
            build();
    }

    private SiteTemplate createTemplateWithPageTemplates()
    {
        final PageTemplate pageTemplate1 = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "module|my-page" ) ).
            displayName( "Main page template" ).
            canRender( ContentTypeNames.from( "article", "banner" ) ).
            descriptor( PageDescriptorKey.from( "mainmodule-1.0.0:landing-page" ) ).
            regions( newPageRegions().build() ).
            build();
        final PageTemplate pageTemplate2 = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( "module|my-other-page" ) ).
            displayName( "Another page template" ).
            canRender( ContentTypeNames.from( "article" ) ).
            descriptor( PageDescriptorKey.from( "mainmodule-1.0.0:other-page" ) ).
            regions( newPageRegions().build() ).
            build();

        return SiteTemplate.newSiteTemplate().
            key( SiteTemplateKey.from( "name-1.0.0" ) ).
            displayName( "displayName" ).
            description( "info" ).
            url( "url" ).
            vendor( Vendor.newVendor().name( "vendorName" ).url( "vendorUrl" ).build() ).
            modules( ModuleKeys.from( "module1-1.0.0" ) ).
            contentTypeFilter( ContentTypeFilter.newContentFilter().
                allowContentType( ContentTypeName.imageMedia() ).
                denyContentType( ContentTypeName.shortcut() ).
                build() ).
            rootContentType( ContentTypeName.folder() ).
            addPageTemplate( pageTemplate1 ).
            addPageTemplate( pageTemplate2 ).
            build();
    }
}
