package com.enonic.xp.portal.impl.rendering;

import org.junit.Test;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.rendering.Renderer;

import static org.junit.Assert.*;

public class RendererFactoryImplTest
{
    @Test
    public void given_Renderable_matching_only_on_superType_when_getRenderer_then_Renderer_for_superType_is_returned()
    {
        RendererFactoryImpl factory = new RendererFactoryImpl();
        factory.addRenderer( createRenderer( Content.class ) );

        // exercise
        Renderer renderer = factory.getRenderer( createPageTemplate() );

        // verify
        assertNotNull( renderer );
        assertEquals( Content.class, renderer.getType() );
    }

    @Test
    public void given_Renderable_having_not_matching_Renderer_when_getRenderer_then_Renderer_for_that_type_is_returned()
    {
        RendererFactoryImpl factory = new RendererFactoryImpl();
        factory.addRenderer( createRenderer( Content.class ) );

        // exercise
        Renderer renderer = factory.getRenderer( createContent() );

        // verify
        assertNotNull( renderer );
        assertEquals( Content.class, renderer.getType() );
    }

    @Test(expected = RendererNotFoundException.class)
    public void given_Renderable_matching_no_given_type_when_getRenderer_then_Renderer_for_that_type_is_returned()
    {
        RendererFactoryImpl factory = new RendererFactoryImpl();
        factory.addRenderer( createRenderer( RendererFactoryImplTest.class ) );

        // exercise
        factory.getRenderer( createContent() );
    }

    private PageTemplate createPageTemplate()
    {
        return PageTemplate.newPageTemplate().name( "my-template" ).parentPath( ContentPath.ROOT ).build();
    }

    private Content createContent()
    {
        return Content.newContent().name( "my-content" ).parentPath( ContentPath.ROOT ).build();
    }

    private Renderer createRenderer( final Class type )
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
                return null;
            }
        };
    }
}