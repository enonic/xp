package com.enonic.xp.portal.impl.rendering;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.url.AbstractPortalUrlServiceImplTest;
import com.enonic.xp.region.ImageComponent;

import static org.junit.Assert.*;

public class ImageRendererTest
    extends AbstractPortalUrlServiceImplTest
{
    private PortalResponse portalResponse;

    private ImageComponent imageComponent;

    private ImageRenderer renderer;

    private Content createContent()
    {
        final Media media = ContentFixtures.newMedia();
        Mockito.when( this.contentService.getByPath( media.getPath() ) ).thenReturn( media );
        Mockito.when( this.contentService.getById( Mockito.any( ContentId.class ) ) ).thenReturn( media );
        Mockito.when( this.contentService.getBinaryKey( media.getId(), media.getMediaAttachment().getBinaryReference() ) ).thenReturn(
            "binaryHash" );
        return media;
    }

    @Before
    public void before()
    {
        portalResponse = PortalResponse.create().build();
        portalRequest.setMode( RenderMode.LIVE );
    }

    @Test
    public void imageComponentWithNoImage()
    {
        // setup
        imageComponent = ImageComponent.create().name( "myImageComponent" ).build();
        renderer = new ImageRenderer();

        // exercise
        portalResponse = renderer.render( imageComponent, portalRequest );

        // verify
        assertEquals( "", portalResponse.getAsString() );
    }

    @Test
    public void imageComponentWithImage()
    {
        // setup
        this.portalRequest.setContent( createContent() );

        final PropertyTree config = new PropertyTree();
        config.addString( "caption", "Image Title" );
        imageComponent = ImageComponent.create().
            name( "myImageComponent" ).
            image( ContentId.from( "abcdef1234567890" ) ).
            config( config ).
            build();
        renderer = new ImageRenderer();
        renderer.setUrlService( this.service );

        // exercise
        portalResponse = renderer.render( imageComponent, portalRequest );

        // verify
        String result =
            "<figure data-portal-component-type=\"image\"><img style=\"width: 100%\" src=\"/portal/draft/a/b/mycontent/_/image/abcdef1234567890:992a0004e50e58383fb909fea2b588dc714a7115/width-768/mycontent\"/><figcaption>Image Title</figcaption></figure>";
        assertEquals( result, portalResponse.getAsString() );
    }


    @Test
    public void imageComponentRenderModeEdit()
    {
        // setup
        portalRequest.setMode( RenderMode.EDIT );
        imageComponent = ImageComponent.create().name( "myImageComponent" ).build();
        renderer = new ImageRenderer();

        // exercise
        portalResponse = renderer.render( imageComponent, portalRequest );

        // verify
        String result = "<figure data-portal-component-type=\"image\"></figure>";
        assertEquals( result, portalResponse.getAsString() );
    }
}