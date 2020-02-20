package com.enonic.xp.portal.impl.rendering;

import org.junit.jupiter.api.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

public class RendererDelegateImplTest
{
    @Test
    public void given_Renderable_matching_only_on_superType_when_getRenderer_then_Renderer_for_superType_is_returned()
    {
        RendererDelegateImpl factory = new RendererDelegateImpl( mock( ContentService.class ) );
        final PortalResponse response = PortalResponse.create().build();
        factory.addRenderer( createRenderer( Content.class, response ) );

        // exercise
        final PortalResponse renderResponse = factory.render( createPageTemplate(), null );

        // verify
        assertSame( response, renderResponse );
    }

    @Test
    public void given_Renderable_having_not_matching_Renderer_when_getRenderer_then_Renderer_for_that_type_is_returned()
    {
        RendererDelegateImpl factory = new RendererDelegateImpl( mock( ContentService.class ) );
        final PortalResponse response = PortalResponse.create().build();
        factory.addRenderer( createRenderer( Content.class, response ) );

        // exercise
        final PortalResponse renderResponse = factory.render( createContent(), null );

        // verify
        assertSame( response, renderResponse );
    }

    @Test
    public void given_Renderable_matching_no_given_type_when_getRenderer_then_Renderer_for_that_type_is_returned()
    {
        RendererDelegateImpl factory = new RendererDelegateImpl( mock( ContentService.class ) );
        factory.addRenderer( createRenderer( RendererDelegateImplTest.class, null ) );

        // exercise
        assertThrows( RendererNotFoundException.class, () -> factory.render( createContent(), null ) );
    }

    private PageTemplate createPageTemplate()
    {
        return PageTemplate.newPageTemplate().name( "my-template" ).parentPath( ContentPath.ROOT ).build();
    }

    private Content createContent()
    {
        return Content.create().name( "my-content" ).parentPath( ContentPath.ROOT ).build();
    }

    private Renderer createRenderer( final Class type, final PortalResponse response )
    {
        return new Renderer()
        {
            @Override
            public Class getType()
            {
                return type;
            }

            @Override
            public PortalResponse render( final Object component, final PortalRequest portalRequest )
            {
                return response;
            }
        };
    }
}
