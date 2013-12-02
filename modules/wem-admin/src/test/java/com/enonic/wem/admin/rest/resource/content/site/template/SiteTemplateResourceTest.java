package com.enonic.wem.admin.rest.resource.content.site.template;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.site.DeleteSiteTemplate;
import com.enonic.wem.api.command.content.site.GetAllSiteTemplates;
import com.enonic.wem.api.command.content.site.GetSiteTemplateByKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplateName;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateName;
import com.enonic.wem.api.content.page.part.PartTemplate;
import com.enonic.wem.api.content.page.part.PartTemplateName;
import com.enonic.wem.api.content.site.ContentTypeFilter;
import com.enonic.wem.api.content.site.NoSiteTemplateExistsException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateNotFoundException;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.api.content.site.Vendor;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;
import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;

import static com.enonic.wem.api.content.page.PageTemplate.newPageTemplate;
import static com.enonic.wem.api.content.page.image.ImageTemplate.newImageTemplate;
import static com.enonic.wem.api.content.page.layout.LayoutTemplate.newLayoutTemplate;
import static com.enonic.wem.api.content.page.part.PartTemplate.newPartTemplate;
import static com.enonic.wem.api.content.site.Vendor.newVendor;

public class SiteTemplateResourceTest
    extends AbstractResourceTest
{

    private Client client;

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
    {
        mockCurrentContextHttpRequest();
    }

    @Test
    public void list_site_template_success()
        throws Exception
    {
        SiteTemplate siteTemplate = createSiteTemplate();

        Mockito.when( client.execute( Mockito.isA( GetAllSiteTemplates.class ) ) ).thenReturn(
            SiteTemplates.from( ImmutableList.of( siteTemplate ) ) );

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

    @Test
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
            name( new PageTemplateName( "mainpage" ) ).
            displayName( "Main Page" ).
            descriptor( new ModuleResourceKey( ModuleKey.from( "mod-1.0.0" ), ResourcePath.from( "components/page-descr.xml" ) ) ).
            build();
        final PartTemplate partTemplate = newPartTemplate().
            name( new PartTemplateName( "mainpart" ) ).
            displayName( "Main Part" ).
            descriptor( new ModuleResourceKey( ModuleKey.from( "mod-1.0.0" ), ResourcePath.from( "components/part-descr.xml" ) ) ).
            build();
        final ImageTemplate imageTemplate = newImageTemplate().
            name( new ImageTemplateName( "mainimage" ) ).
            displayName( "Main Image" ).
            descriptor( new ModuleResourceKey( ModuleKey.from( "mod-1.0.0" ), ResourcePath.from( "components/image-descr.xml" ) ) ).
            build();
        final LayoutTemplate layoutTemplate = newLayoutTemplate().
            name( new LayoutTemplateName( "mainlayout" ) ).
            displayName( "Main Layout" ).
            descriptor( new ModuleResourceKey( ModuleKey.from( "mod-1.0.0" ), ResourcePath.from( "components/layout-descr.xml" ) ) ).
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
            queryParam( "key", siteTemplate.getKey().toString() ).
            get( String.class );
        assertJson( "get_site_template_by_key_success.json", response );
    }

    @Test
    public void testGetSiteTemplateMissing()
        throws Exception
    {
        final SiteTemplateKey siteTemplate = SiteTemplateKey.from( "blueman-1.0.0" );
        Mockito.when( client.execute( Mockito.isA( GetSiteTemplateByKey.class ) ) ).thenThrow(
            new SiteTemplateNotFoundException( siteTemplate ) );
        String response = resource().
            path( "content/site/template" ).
            queryParam( "key", siteTemplate.toString() ).
            get( String.class );
        assertJson( "get_site_template_by_key_failure.json", response );
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
