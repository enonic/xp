package com.enonic.wem.admin.rest.resource.content.page;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.admin.rest.resource.AbstractResourceTest;
import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.GetPageTemplatesBySiteTemplate;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.page.PageTemplates;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.data.RootDataSet;
import com.enonic.wem.api.data.Value;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.schema.content.ContentTypeNames;

public class PageTemplateResourceTest
    extends AbstractResourceTest
{
    private Client client;

    @Override
    protected Object getResourceInstance()
    {
        client = Mockito.mock( Client.class );
        final PageTemplateResource resource = new PageTemplateResource();
        resource.setClient( client );

        return resource;
    }

    @Before
    public void setup()
        throws IOException
    {
        mockCurrentContextHttpRequest();
    }

    @Test
    public void list_page_template_success()
        throws Exception
    {
        final PageTemplate pageTemplate = createPageTemplate();
        final PageTemplates pageTemplates = PageTemplates.from( pageTemplate );

        Mockito.when( client.execute( Mockito.isA( GetPageTemplatesBySiteTemplate.class ) ) ).thenReturn( pageTemplates );

        String jsonString = resource().
            path( "content/page/template/list" ).
            queryParam( "key", "mySiteTemplate-1.0.0" ).
            get( String.class );

        assertJson( "list_page_template_success.json", jsonString );
    }

    private PageTemplate createPageTemplate()
    {
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( "mySiteTemplate-1.0.0" );

        final ModuleKey module = ModuleKey.from( "mymodule-1.0.0" );

        final RootDataSet pageTemplateConfig = new RootDataSet();
        pageTemplateConfig.addProperty( "pause", new Value.Long( 10000 ) );

        final PageTemplate pageTemplate = PageTemplate.newPageTemplate().
            key( PageTemplateKey.from( siteTemplateKey, module, new PageTemplateName( "my-page" ) ) ).
            displayName( "Main page template" ).
            config( pageTemplateConfig ).
            canRender( ContentTypeNames.from( "article", "banner" ) ).
            descriptor( ModuleResourceKey.from( "mainmodule-1.0.0:/components/landing-page.xml" ) ).
            build();

        return pageTemplate;
    }
}
