package com.enonic.xp.portal.impl.rendering;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.Media;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.ContentFixtures;
import com.enonic.xp.portal.impl.url.AbstractPortalUrlServiceImplTest;
import com.enonic.xp.region.ImageComponent;
import com.enonic.xp.web.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ImageRendererTest
    extends AbstractPortalUrlServiceImplTest
{
    private ImageComponent imageComponent;

    private ImageRenderer renderer;

    private Content createContent()
    {
        final Media media = ContentFixtures.newMedia();
        Mockito.when( this.contentService.getByPath( media.getPath() ) ).thenReturn( media );
        Mockito.when( this.contentService.getById( Mockito.any( ContentId.class ) ) ).thenReturn( media );
        return media;
    }

    @BeforeEach
    public void before()
    {
        portalRequest.setMode( RenderMode.LIVE );
    }

    @Test
    public void imageComponentWithNoImage()
    {
        // setup
        imageComponent = ImageComponent.create().build();
        renderer = new ImageRenderer( this.service, contentService );

        // exercise
        PortalResponse portalResponse = renderer.render( imageComponent, portalRequest );

        // verify
        assertEquals( "", portalResponse.getBody() );
        assertEquals( HttpStatus.NOT_FOUND, portalResponse.getStatus() );
    }

    @Test
    public void imageComponentWithImage()
    {
        String expected =
            "<figure data-portal-component-type=\"image\"><img style=\"width: 100%\" src=\"/site/myproject/draft/_/media:image/myproject:draft/123456:b12b4c973748042e3b3a7e4798344289/width-768/mycontent\" alt=\"logo.png\"/><figcaption>Image Title</figcaption></figure>";

        testImageComponentWithImage( createContent(), expected );
    }

    @Test
    public void imageComponentWithImageAndAltFieldSet()
    {
        final Content content = createContent();
        content.getData().setString( "altText", "alternative" );

        String expected =
            "<figure data-portal-component-type=\"image\"><img style=\"width: 100%\" src=\"/site/myproject/draft/_/media:image/myproject:draft/123456:b12b4c973748042e3b3a7e4798344289/width-768/mycontent\" alt=\"alternative\"/><figcaption>Image Title</figcaption></figure>";

        testImageComponentWithImage( content, expected );
    }

    private void testImageComponentWithImage( final Content content, final String expected )
    {
        this.portalRequest.setContent( content );

        final PropertyTree config = new PropertyTree();
        config.addString( "caption", "Image Title" );
        imageComponent = ImageComponent.create().image( ContentId.from( "123456" ) ).config( config ).build();
        renderer = new ImageRenderer( this.service, contentService );

        // exercise
        PortalResponse portalResponse = renderer.render( imageComponent, portalRequest );

        // verify
        assertEquals( expected, portalResponse.getBody() );
    }


    @Test
    public void imageComponentRenderModeEdit()
    {
        // setup
        portalRequest.setMode( RenderMode.EDIT );
        imageComponent = ImageComponent.create().build();
        renderer = new ImageRenderer( this.service, contentService );

        // exercise
        PortalResponse portalResponse = renderer.render( imageComponent, portalRequest );

        // verify
        String result = "<figure data-portal-component-type=\"image\"></figure>";
        assertEquals( result, portalResponse.getBody() );
        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
    }

    @Test
    public void imageComponentMissingImageModeLive()
    {
        // setup
        this.portalRequest.setContent( createContent() );
        Mockito.when( this.contentService.getById( Mockito.any( ContentId.class ) ) ).thenThrow( ContentNotFoundException.class );

        final PropertyTree config = new PropertyTree();
        config.addString( "caption", "Image Title" );
        imageComponent = ImageComponent.create().image( ContentId.from( "123456" ) ).config( config ).build();
        renderer = new ImageRenderer( this.service, contentService );

        // exercise
        PortalResponse portalResponse = renderer.render( imageComponent, portalRequest );

        // verify
        String result = "<figure data-portal-component-type=\"image\"></figure>";
        assertEquals( result, portalResponse.getBody() );
        assertEquals( HttpStatus.NOT_FOUND, portalResponse.getStatus() );
    }

    @Test
    public void imageComponentMissingImageModeEdit()
    {
        // setup
        portalRequest.setMode( RenderMode.EDIT );
        this.portalRequest.setContent( createContent() );

        Mockito.when( this.contentService.getById( Mockito.any( ContentId.class ) ) ).thenThrow( ContentNotFoundException.class );

        final PropertyTree config = new PropertyTree();
        config.addString( "caption", "Image Title" );

        imageComponent = ImageComponent.create().image( ContentId.from( "123456" ) ).config( config ).build();

        renderer = new ImageRenderer( this.service, contentService );

        // exercise
        PortalResponse portalResponse = renderer.render( imageComponent, portalRequest );

        // verify
        String result =
            "<div data-portal-component-type=\"image\" data-portal-placeholder=\"true\" data-portal-placeholder-error=\"true\"><span class=\"data-portal-placeholder-error\">Image could not be found</span></div>";
        assertEquals( result, portalResponse.getBody() );
        assertEquals( HttpStatus.OK, portalResponse.getStatus() );
    }

    @Test
    public void testRenderImageComponentWithReadPermission()
    {
        // simulate case when a user does not have a permission on read
        Mockito.when( contentService.getById( Mockito.any( ContentId.class ) ) ).thenThrow( ContentNotFoundException.class );

        imageComponent = ImageComponent.create().image( ContentId.from( "id" ) ).build();

        renderer = new ImageRenderer( this.service, contentService );

        PortalResponse response = renderer.render( imageComponent, portalRequest );
        assertEquals( "<figure data-portal-component-type=\"image\"></figure>", response.getBody() );
        assertEquals( HttpStatus.NOT_FOUND, response.getStatus() );
    }
}
