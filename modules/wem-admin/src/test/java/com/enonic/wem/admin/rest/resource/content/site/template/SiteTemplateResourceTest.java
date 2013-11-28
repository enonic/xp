package com.enonic.wem.admin.rest.resource.content.site.template;

import javax.ws.rs.core.MediaType;

import org.elasticsearch.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.site.DeleteSiteTemplate;
import com.enonic.wem.api.command.content.site.GetAllSiteTemplates;
import com.enonic.wem.api.content.site.ContentTypeFilter;
import com.enonic.wem.api.content.site.NoSiteTemplateExistsException;
import com.enonic.wem.api.content.site.SiteTemplate;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.content.site.SiteTemplateName;
import com.enonic.wem.api.content.site.SiteTemplateVersion;
import com.enonic.wem.api.content.site.SiteTemplates;
import com.enonic.wem.api.content.site.Vendor;
import com.enonic.wem.api.module.ModuleKeys;
import com.enonic.wem.api.schema.content.ContentTypeName;

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
        String response =
            resource().path( "content/site/template/delete" ).entity( readFromFile( "delete_site_template_params.json" ),
                                                                      MediaType.APPLICATION_JSON_TYPE ).post( String.class );
        assertJson( "delete_site_template_success.json", response );
    }

    @Test
    public void testDeleteNonExistingSiteTemplate()
        throws Exception
    {
        Mockito.when( client.execute( Mockito.isA( DeleteSiteTemplate.class ) ) ).thenThrow(
            new NoSiteTemplateExistsException( SiteTemplateKey.from( "sitetemplate-1.0.0" ) ) );
        String response =
            resource().path( "content/site/template/delete" ).entity( readFromFile( "delete_site_template_params.json" ), MediaType.APPLICATION_JSON_TYPE ).post(
                String.class );
        assertJson( "delete_site_template_failure.json", response );
    }

    private SiteTemplate createSiteTemplate()
    {
        return SiteTemplate.newSiteTemplate()
            .key( SiteTemplateKey.from( "name-1.0.0" ) )
            .displayName( "displayName" )
            .info( "info" )
            .url( "url" )
            .vendor( Vendor.newVendor().name( "vendorName" ).url( "vendorUrl" ).build() )
            .modules( ModuleKeys.from( "module1-1.0.0" ) )
            .contentTypeFilter(
                ContentTypeFilter.newContentFilter().allowContentType( ContentTypeName.imageMedia() ).denyContentType( ContentTypeName.shortcut() ).build() )
            .rootContentType( ContentTypeName.folder() )
            .build();
    }

}
