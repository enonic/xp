package com.enonic.wem.admin.rest.resource.content.page.layout;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.layout.GetLayoutTemplatesBySiteTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplate;
import com.enonic.wem.api.content.page.layout.LayoutTemplateKey;
import com.enonic.wem.api.content.page.layout.LayoutTemplates;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.module.ResourcePath;

public class LayoutTemplateResourceTest
    extends AbstractResourceTest
{
    private Client client;

    @Test
    public void list_layout_template_success()
        throws Exception
    {
        final LayoutTemplates layoutTemplates = createLayoutTemplates();
        Mockito.when( client.execute( Mockito.isA( GetLayoutTemplatesBySiteTemplate.class ) ) ).thenReturn( layoutTemplates );

        String resultJson = resource().path( "content/page/layout/template/list" ).queryParam( "key", "sitetemplate-1.0.0" ).get( String.class );

        assertJson( "list_layout_template_success.json", resultJson );
    }

    private LayoutTemplates createLayoutTemplates()
    {
        LayoutTemplate layoutTemplate1 = LayoutTemplate.newLayoutTemplate()
            .key( LayoutTemplateKey.from( "sitetemplate-1.0.0|module1-1.0.0|layout1" ) )
            .displayName( "Layout1 template" )
            .config( new RootDataSet() )
            .descriptor( ModuleResourceKey.from( "module1-1.0.0:/components/layout1-template.xml" ) )
            .build();

        LayoutTemplate layoutTemplate2 = LayoutTemplate.newLayoutTemplate()
            .key( LayoutTemplateKey.from( "sitetemplate-1.0.0|module2-1.0.0|layout2" ) )
            .displayName( "Layout2 template" )
            .config( new RootDataSet() )
            .descriptor( ModuleResourceKey.from( "module2-1.0.0:/components/layout2-template.xml" ) )
            .build();

        return LayoutTemplates.from( layoutTemplate1, layoutTemplate2 );
    }

    @Override
    protected Object getResourceInstance()
    {
        client = Mockito.mock( Client.class );
        LayoutTemplateResource resource = new LayoutTemplateResource();
        resource.setClient( client );

        return resource;
    }
}
