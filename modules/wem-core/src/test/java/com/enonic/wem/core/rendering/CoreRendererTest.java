package com.enonic.wem.core.rendering;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.content.template.GetPageTemplate;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateName;
import com.enonic.wem.api.module.ModuleResourceKey;
import com.enonic.wem.api.resource.Resource;

import static com.enonic.wem.api.content.page.Page.newPage;
import static com.enonic.wem.api.content.page.PageTemplate.newPageTemplate;
import static com.enonic.wem.api.resource.Resource.newResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class CoreRendererTest
{
    @Test
    public void testRenderComponent()
        throws Exception
    {
        // setup
        final Client client = Mockito.mock( Client.class );

        final PageTemplateName pageTemplateName = new PageTemplateName( "my-page-tpl" );
        final PageTemplate template = newPageTemplate().
            name( pageTemplateName ).
            descriptor( ModuleResourceKey.from( "module-1.0.0:templates/template.xml" ) ).
            build();
        when( client.execute( isA( GetPageTemplate.class ) ) ).thenReturn( template );

        final Resource pageDescriptorResource = newResource().
            name( "page-descriptor" ).
            stringValue( "<page-component/>" ).
            build();
        final Resource controllerResource = newResource().
            name( "controller.js" ).
            stringValue( "test();" ).
            build();
        when( client.execute( isA( GetModuleResource.class ) ) ).thenReturn( pageDescriptorResource ).thenReturn( controllerResource );

        // exercise
        final CoreRenderer coreRenderer = new CoreRenderer( client );
        final Page page = newPage().
            pageTemplateName( pageTemplateName ).
            build();
        final RenderingResult result = coreRenderer.render( page );

        // verify
        assertNotNull( result );
    }
}
