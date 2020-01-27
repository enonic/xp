package com.enonic.xp.portal.impl.rendering;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

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

    @BeforeEach
    public void before()
    {
        portalResponse = PortalResponse.create().build();
        portalRequest.setMode( RenderMode.LIVE );
    }

    @Test
    public void imageComponentWithNoImage()
    {
        // setup
        imageComponent = ImageComponent.create().build();
        renderer = new ImageRenderer();

        // exercise
        portalResponse = renderer.render( imageComponent, portalRequest );

        // verify
        assertEquals( "", portalResponse.getAsString() );
    }

    @Test
    public void imageComponentWithImage()
    {
        String expected =
            "<figure data-portal-component-type=\"image\"><img style=\"width: 100%\" src=\"/site/default/draft/a/b/mycontent/_/image/abcdef1234567890:8cf45815bba82c9711c673c9bb7304039a790026/width-768/mycontent\" alt=\"logo.png\"/><figcaption>Image Title</figcaption></figure>";

        testImageComponentWithImage( createContent(), expected );
    }

    @Test
    public void imageComponentWithImageAndAltFieldSet()
    {
        final Content content = createContent();
        content.getData().setString( "altText", "alternative" );

        String expected =
            "<figure data-portal-component-type=\"image\"><img style=\"width: 100%\" src=\"/site/default/draft/a/b/mycontent/_/image/abcdef1234567890:8cf45815bba82c9711c673c9bb7304039a790026/width-768/mycontent\" alt=\"alternative\"/><figcaption>Image Title</figcaption></figure>";

        testImageComponentWithImage( content, expected );
    }

    private void testImageComponentWithImage( final Content content, final String expected )
    {
        this.portalRequest.setContent( content );
        Mockito.when( this.contentService.contentExists( Mockito.any( ContentId.class ) ) ).thenReturn( true );

        final PropertyTree config = new PropertyTree();
        config.addString( "caption", "Image Title" );
        imageComponent = ImageComponent.create().
            image( ContentId.from( "abcdef1234567890" ) ).
            config( config ).
            build();
        renderer = new ImageRenderer();
        renderer.setUrlService( this.service );
        renderer.setContentService( contentService );

        // exercise
        portalResponse = renderer.render( imageComponent, portalRequest );

        // verify
        assertEquals( expected, portalResponse.getAsString() );
    }


    @Test
    public void imageComponentRenderModeEdit()
    {
        // setup
        portalRequest.setMode( RenderMode.EDIT );
        imageComponent = ImageComponent.create().build();
        renderer = new ImageRenderer();

        // exercise
        portalResponse = renderer.render( imageComponent, portalRequest );

        // verify
        String result = "<figure data-portal-component-type=\"image\"></figure>";
        assertEquals( result, portalResponse.getAsString() );
    }

    @Test
    public void imageComponentMissingImageModeLive()
    {
        // setup
        this.portalRequest.setContent( createContent() );
        Mockito.when( this.contentService.contentExists( Mockito.any( ContentId.class ) ) ).thenReturn( false );

        final PropertyTree config = new PropertyTree();
        config.addString( "caption", "Image Title" );
        imageComponent = ImageComponent.create().
            image( ContentId.from( "abcdef1234567890" ) ).
            config( config ).
            build();
        renderer = new ImageRenderer();
        renderer.setUrlService( this.service );
        renderer.setContentService( contentService );

        // exercise
        portalResponse = renderer.render( imageComponent, portalRequest );

        // verify
        String result = "<figure data-portal-component-type=\"image\"></figure>";
        assertEquals( result, portalResponse.getAsString() );
    }

    @Test
    public void imageComponentMissingImageModeEdit()
    {
        // setup
        portalRequest.setMode( RenderMode.EDIT );
        this.portalRequest.setContent( createContent() );
        Mockito.when( this.contentService.contentExists( Mockito.any( ContentId.class ) ) ).thenReturn( false );

        final PropertyTree config = new PropertyTree();
        config.addString( "caption", "Image Title" );
        imageComponent = ImageComponent.create().
            image( ContentId.from( "abcdef1234567890" ) ).
            config( config ).
            build();
        renderer = new ImageRenderer();
        renderer.setUrlService( this.service );
        renderer.setContentService( contentService );

        // exercise
        portalResponse = renderer.render( imageComponent, portalRequest );

        // verify
        String result =
            "<div data-portal-component-type=\"image\" data-portal-placeholder=\"true\" data-portal-placeholder-error=\"true\"><span class=\"data-portal-placeholder-error\">Image could not be found</span></div>";
        assertEquals( result, portalResponse.getAsString() );
    }
}
