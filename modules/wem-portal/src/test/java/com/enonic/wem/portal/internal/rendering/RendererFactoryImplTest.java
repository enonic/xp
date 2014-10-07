package com.enonic.wem.portal.internal.rendering;

import java.util.ArrayList;

import org.junit.Test;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.content.page.PageTemplate;
import com.enonic.wem.api.rendering.Renderable;
import com.enonic.wem.portal.PortalContext;

import static org.junit.Assert.*;

public class RendererFactoryImplTest
{
    @Test
    public void given_Renderable_matching_only_on_superType_when_getRenderer_then_Renderer_for_superType_is_returned()
    {
        ArrayList<Renderer> rendererList = new ArrayList<>();
        rendererList.add( createRenderer( Content.class ) );

        RendererFactoryImpl factory = new RendererFactoryImpl( rendererList );

        // exercise
        Renderer renderer = factory.getRenderer( createPageTemplate() );

        // verify
        assertNotNull( renderer );
        assertEquals( Content.class, renderer.getType() );
    }

    @Test
    public void given_Renderable_having_not_matching_Renderer_when_getRenderer_then_Renderer_for_that_type_is_returned()
    {
        ArrayList<Renderer> rendererList = new ArrayList<>();
        rendererList.add( createRenderer( Content.class ) );

        RendererFactoryImpl factory = new RendererFactoryImpl( rendererList );

        // exercise
        Renderer renderer = factory.getRenderer( createContent() );

        // verify
        assertNotNull( renderer );
        assertEquals( Content.class, renderer.getType() );
    }

    @Test(expected = RendererNotFoundException.class)
    public void given_Renderable_matching_no_given_type_when_getRenderer_then_Renderer_for_that_type_is_returned()
    {
        ArrayList<Renderer> rendererList = new ArrayList<>();
        rendererList.add( createRenderer( RendererFactoryImplTest.class ) );

        RendererFactoryImpl factory = new RendererFactoryImpl( rendererList );

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
            public RenderResult render( final Renderable component, final PortalContext context )
            {
                return null;
            }
        };
    }
}