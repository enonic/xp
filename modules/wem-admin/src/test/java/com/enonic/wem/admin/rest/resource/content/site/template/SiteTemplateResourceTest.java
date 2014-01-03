package com.enonic.wem.admin.rest.resource.content.site.template;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.ws.rs.core.MediaType;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataBodyPart;
import com.sun.jersey.multipart.FormDataMultiPart;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.site.CreateSiteTemplate;
import com.enonic.wem.api.command.content.site.DeleteSiteTemplate;
import com.enonic.wem.api.command.content.site.GetAllSiteTemplates;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.page.ComponentDescriptorName;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.image.ImageDescriptorKey;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;
import com.enonic.wem.api.content.page.layout.LayoutDescriptorKey;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;
import com.enonic.wem.api.content.page.part.PartDescriptorKey;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateKey;
import com.enonic.wem.api.content.site.ContentTypeFilter;
import com.enonic.wem.api.content.site.NoSiteTemplateExistsException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.api.content.site.Vendor;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.support.export.InvalidZipFileException;
import com.enonic.wem.core.content.site.SiteTemplateExporter;

import static com.enonic.wem.api.content.page.PageTemplate.newPageTemplate;
import static com.enonic.wem.api.content.page.image.ImageTemplate.newImageTemplate;
import static com.enonic.wem.api.content.page.layout.LayoutTemplate.newLayoutTemplate;
import static com.enonic.wem.api.content.page.part.PartTemplate.newPartTemplate;
import static com.enonic.wem.api.content.site.Vendor.newVendor;
import static org.junit.Assert.*;

public class SiteTemplateResourceTest
    extends AbstractResourceTest
{

    private Client client;

    private Path tempDir;

    @Override
    protected Object getResourceInstance()
    {

        client = Mockito.mock( Client.class );
        SiteTemplateResource resource = new SiteTemplateResource();

        resource.setClient( client );

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

        Mockito.when( client.execute( Mockito.isA( GetAllSiteTemplates.class ) ) ).thenReturn( siteTemplates );

        String resultJson = resource().path( "content/site/template/list" ).get( String.class );

        assertJson( "list_site_template_success.json", resultJson );
    }

    @Test
    public void testDeleteSiteTemplate()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( DeleteSiteTemplate.class ) ) ).thenReturn(
            SiteTemplateKey.from( "sitetemplate-1.0.0" ) );
        String response = resource().path( "content/site/template/delete" ).entity( readFromFile( "delete_site_template_params.json" ),
                                                                                    MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "delete_site_template_success.json", response );
    }

    @Test(expected = NoSiteTemplateExistsException.class)
    public void testDeleteNonExistingSiteTemplate()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( DeleteSiteTemplate.class ) ) ).thenThrow(
            new NoSiteTemplateExistsException( SiteTemplateKey.from( "sitetemplate-1.0.0" ) ) );
        String response = resource().path( "content/site/template/delete" ).entity( readFromFile( "delete_site_template_params.json" ),
                                                                                    MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "delete_site_template_failure.json", response );
    }

    @Test
    public void testGetSiteTemplate()
        throws Exception
    {
        final PageTemplate pageTemplate = newPageTemplate().
            key( PageTemplateKey.from( "sitetemplate-1.0.0|mod-1.0.0|mainpage" ) ).
            displayName( "Main Page" ).
            descriptor( PageDescriptorKey.from( ModuleKey.from( "mod-1.0.0" ), new ComponentDescriptorName( "page-descr" ) ) ).
            build();
        final PartTemplate partTemplate = newPartTemplate().
            key( PartTemplateKey.from( "sitetemplate-1.0.0|mod-1.0.0|mainpart" ) ).
            displayName( "Main Part" ).
            descriptor( PartDescriptorKey.from( ModuleKey.from( "mod-1.0.0" ), new ComponentDescriptorName( "part-descr" ) ) ).
            build();
        final ImageTemplate imageTemplate = newImageTemplate().
            key( ImageTemplateKey.from( "sitetemplate-1.0.0|mod-1.0.0|mainimage" ) ).
            displayName( "Main Image" ).
            descriptor( ImageDescriptorKey.from( ModuleKey.from( "mod-1.0.0" ), new ComponentDescriptorName( "image-descr" ) ) ).
            build();
        final LayoutTemplate layoutTemplate = newLayoutTemplate().
            key( LayoutTemplateKey.from( "sitetemplate-1.0.0|mod-1.0.0|mainlayout" ) ).
            displayName( "Main Layout" ).
            descriptor( LayoutDescriptorKey.from( ModuleKey.from( "mod-1.0.0" ), new ComponentDescriptorName( "layout-descr" ) ) ).
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
            addTemplate( pageTemplate ).
            addTemplate( partTemplate ).
            addTemplate( imageTemplate ).
            addTemplate( layoutTemplate ).
            build();

        Mockito.when( client.execute( Mockito.isA( GetSiteTemplateByKey.class ) ) ).thenReturn( siteTemplate );
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
        Mockito.when( client.execute( Mockito.isA( GetSiteTemplateByKey.class ) ) ).thenThrow(
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
    public void import_site_template_success()
        throws Exception
    {
        final SiteTemplate siteTemplate = createSiteTemplate();
        Mockito.when( client.execute( Mockito.isA( CreateSiteTemplate.class ) ) ).thenReturn( siteTemplate );

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
        Mockito.when( client.execute( Mockito.isA( GetSiteTemplateByKey.class ) ) ).thenReturn( siteTemplate );

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
        return SiteTemplate.newSiteTemplate().key( SiteTemplateKey.from( "name-1.0.0" ) ).displayName( "displayName" ).description(
            "info" ).url( "url" ).vendor( Vendor.newVendor().name( "vendorName" ).url( "vendorUrl" ).build() ).modules(
            ModuleKeys.from( "module1-1.0.0" ) ).contentTypeFilter(
            ContentTypeFilter.newContentFilter().allowContentType( ContentTypeName.imageMedia() ).denyContentType(
                ContentTypeName.shortcut() ).build() ).rootContentType( ContentTypeName.folder() ).build();
    }
}
