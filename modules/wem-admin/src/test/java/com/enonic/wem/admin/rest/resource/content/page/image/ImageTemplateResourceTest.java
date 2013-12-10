package com.enonic.wem.admin.rest.resource.content.page.image;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.image.GetImageTemplatesBySiteTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplate;
import com.enonic.wem.api.content.page.image.ImageTemplateKey;
import com.enonic.wem.api.content.page.image.ImageTemplates;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleResourceKey;

public class ImageTemplateResourceTest
    extends AbstractResourceTest
{
    private Client client;

    @Test
    public void list_image_templates_by_site_template_success()
        throws Exception
    {
        final ImageTemplates imageTemplates = createImageTemplates();
        Mockito.when( client.execute( Mockito.isA( GetImageTemplatesBySiteTemplate.class ) ) ).thenReturn( imageTemplates );

        String resultJson =
            resource().path( "content/page/image/template/list" ).queryParam( "key", "sitetemplate-1.0.0" ).get( String.class );

        assertJson( "list_image_template_success.json", resultJson );
    }

    private ImageTemplates createImageTemplates()
    {
        RootDataSet imageTemplateConfig = new RootDataSet();
        imageTemplateConfig.addProperty( "width", new Value.Long( 200 ) );

        ImageTemplate imageTemplate1 = ImageTemplate.newImageTemplate().
            key( ImageTemplateKey.from( "sitetemplate-1.0.0|mainmodule-1.0.0|image" ) ).
            displayName( "Image template" ).
            config( imageTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/image-temp.xml" ) ).
            build();

        ImageTemplate imageTemplate2 = ImageTemplate.newImageTemplate().
            key( ImageTemplateKey.from( "sitetemplate-1.0.0|module2-1.0.0|imageresponsive" ) ).
            displayName( "Responsive Image template" ).
            config( imageTemplateConfig ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/imageresp-temp.xml" ) ).
            build();

        return ImageTemplates.from( imageTemplate1 , imageTemplate2);
    }

    @Override
    protected Object getResourceInstance()
    {
        client = Mockito.mock( Client.class );
        final ImageTemplateResource resource = new ImageTemplateResource();
        resource.setClient( client );

        return resource;
    }
}
