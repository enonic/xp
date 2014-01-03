package com.enonic.wem.core.rendering;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.page.GetPageTemplateByKey;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageDescriptorKey;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateKey;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.content.site.Site;
import com.enonic.wem.api.content.site.SiteTemplateKey;
import com.enonic.wem.api.module.ModuleKey;
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
    @Ignore // TODO: CMS-2557
    public void testRenderComponent()
        throws Exception
    {
        // setup
        final SiteTemplateKey siteTemplateKey = SiteTemplateKey.from( "mySiteTemplate-1.0.0" );
        final ModuleKey module = ModuleKey.from( "mymodule-1.0.0" );
        Site site = Site.newSite().template( siteTemplateKey ).build();
        context = Context.newContext().site( site ).build();
        Client client = Mockito.mock( Client.class );

        PageTemplateName pageTemplateName = new PageTemplateName( "my-page-tpl" );
        PageTemplate template = newPageTemplate().
            key( PageTemplateKey.from( siteTemplateKey, module, pageTemplateName ) ).
            descriptor( PageDescriptorKey.from( "module-1.0.0:template" ) ).
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
            template( template.getKey() ).
            build();
        RenderingResult result = coreRenderer.render( page );

        // verify
        assertNotNull( result );
    }
}
