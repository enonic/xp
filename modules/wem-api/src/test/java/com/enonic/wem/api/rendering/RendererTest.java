package com.enonic.wem.api.rendering;

import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.io.ByteStreams;

import com.enonic.wem.api.Client;
import com.enonic.wem.api.command.module.GetModuleResource;
import com.enonic.wem.api.command.template.GetTemplate;
import com.enonic.wem.api.content.page.Page;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.content.page.PageTemplateId;
import com.enonic.wem.api.resource.Resource;

import static com.enonic.wem.api.content.page.Page.newPage;
import static com.enonic.wem.api.content.page.PageTemplate.newPageTemplate;
import static com.enonic.wem.api.resource.Resource.newResource;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.when;

public class RendererTest
{
    @Test
    public void testRenderComponent()
        throws Exception
    {
        // setup
        final Client client = Mockito.mock( Client.class );

        final PageTemplateId pageTemplateId = new PageTemplateId( "my-page-tpl" );
        final PageTemplate template = newPageTemplate().
            id( pageTemplateId ).
            build();
        when( client.execute( isA( GetTemplate.class ) ) ).thenReturn( template );

        final Resource pageDescriptorResource = newResource().name( "page-descriptor" ).build();
        final Resource controllerResource = newResource().name( "controller.js" ) .byteSource( ByteStreams.asByteSource( "test();".getBytes() ) ).build();
        when( client.execute( isA( GetModuleResource.class ) ) ).thenReturn( pageDescriptorResource ).thenReturn( controllerResource );

        // exercise
        final Renderer renderer = new Renderer( client );
        final Page page = newPage().
            pageTemplateId( pageTemplateId ).
            build();
        final RenderingResult result = renderer.renderComponent( page );

        // verify
        assertNotNull( result );
    }
}
