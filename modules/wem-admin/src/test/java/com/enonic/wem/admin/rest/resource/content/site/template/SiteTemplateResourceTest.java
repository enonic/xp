package com.enonic.wem.admin.rest.resource.content.site.template;

import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.site.DeleteSiteTemplate;
import com.enonic.wem.api.content.site.NoSiteTemplateExistsException;
import com.enonic.wem.api.content.site.SiteTemplateKey;

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

}
