package com.enonic.wem.core.rendering;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.GetPageTemplateByKey;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;
import com.enonic.wem.core.content.page.rendering.PageRendererRegistrar;

import static com.enonic.wem.api.content.page.Page.newPage;
import static com.enonic.wem.api.content.page.PageTemplate.newPageTemplate;
import static com.enonic.wem.api.resource.Resource.newResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class CoreRendererTest
{
    private Context context;

    public CoreRendererTest()
    {
        PageRendererRegistrar.register();
    }

    @Test
    public void testRenderComponent()
        throws Exception
    {
        // setup
        Site site = Site.newSite().template( SiteTemplateKey.from( "mySiteTemplate-1.0.0" ) ).build();
        context = Context.newContext().site( site ).build();
        Client client = Mockito.mock( Client.class );

        PageTemplateName pageTemplateName = new PageTemplateName( "my-page-tpl" );
        PageTemplate template = newPageTemplate().
            name( pageTemplateName ).
            descriptor( ModuleResourceKey.from( "module-1.0.0:templates/template.xml" ) ).
            build();
        when( client.execute( isA( GetPageTemplateByKey.class ) ) ).thenReturn( template );

        Resource pageDescriptorResource = newResource().
            name( "page-descriptor" ).
            stringValue( "<page-component/>" ).
            build();
        Resource controllerResource = newResource().
            name( "controller.js" ).
            stringValue( "test();" ).
            build();
        when( client.execute( isA( GetModuleResource.class ) ) ).thenReturn( pageDescriptorResource ).thenReturn( controllerResource );

        // exercise
        CoreRenderer coreRenderer = new CoreRenderer( client, context );
        Page page = newPage().
            pageTemplateName( pageTemplateName ).
            build();
        RenderingResult result = coreRenderer.render( page );

        // verify
        assertNotNull( result );
    }
}
