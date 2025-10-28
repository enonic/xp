package com.enonic.xp.portal.impl.rendering;

import java.time.Instant;

import org.junit.jupiter.api.Test;

import com.google.common.net.MediaType;

import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.content.ContentNotFoundException;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.content.ContentService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.Page;
import com.enonic.xp.page.PageTemplate;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.region.FragmentComponent;
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.Region;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.region.TextComponent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RendererDelegateImplTest
{
    @Test
    void given_Renderable_matching_only_on_superType_when_getRenderer_then_Renderer_for_superType_is_returned()
    {
        RendererDelegateImpl factory = new RendererDelegateImpl( mock( ContentService.class ), mock( LayoutDescriptorService.class ) );
        final PortalResponse response = PortalResponse.create().build();
        factory.addRenderer( createRenderer( Content.class, response ) );

        // exercise
        final PortalResponse renderResponse = factory.render( createPageTemplate(), null );

        // verify
        assertSame( response, renderResponse );
    }

    @Test
    void given_Renderable_having_not_matching_Renderer_when_getRenderer_then_Renderer_for_that_type_is_returned()
    {
        RendererDelegateImpl factory = new RendererDelegateImpl( mock( ContentService.class ), mock( LayoutDescriptorService.class ) );
        final PortalResponse response = PortalResponse.create().build();
        factory.addRenderer( createRenderer( Content.class, response ) );

        // exercise
        final PortalResponse renderResponse = factory.render( createContent(), null );

        // verify
        assertSame( response, renderResponse );
    }

    @Test
    void given_Renderable_matching_no_given_type_when_getRenderer_then_Renderer_for_that_type_is_returned()
    {
        RendererDelegateImpl factory = new RendererDelegateImpl( mock( ContentService.class ), mock( LayoutDescriptorService.class ) );
        factory.addRenderer( createRenderer( RendererDelegateImplTest.class, null ) );

        // exercise
        assertThrows( RendererNotFoundException.class, () -> factory.render( createContent(), null ) );
    }

    @Test
    void renderEmptyFragment()
    {
        RendererDelegateImpl factory = new RendererDelegateImpl( mock( ContentService.class ), mock( LayoutDescriptorService.class ) );
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.EDIT );

        // exercise
        final PortalResponse renderResponse = factory.render( createEmptyFragmentComponent(), portalRequest );

        // verify
        assertEquals( renderResponse.getBody(), "<div data-portal-component-type=\"fragment\"></div>" );
    }

    @Test
    void fragmentNotFoundInEditMode()
    {
        final ContentService contentService = mock( ContentService.class );
        final ContentId contentId = ContentId.from( "contentId" );
        when( contentService.getById( contentId ) ).thenThrow( ContentNotFoundException.class );
        RendererDelegateImpl factory = new RendererDelegateImpl( contentService, mock( LayoutDescriptorService.class ) );
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.EDIT );

        // exercise
        final PortalResponse renderResponse = factory.render( createFragmentComponent( contentId ), portalRequest );

        // verify
        assertTrue( ((String) renderResponse.getBody()).contains( "Fragment content could not be found" ) );
    }

    @Test
    void fragmentContentHasNoPage()
    {
        final ContentService contentService = mock( ContentService.class );
        final ContentId contentId = ContentId.from( "contentId" );
        when( contentService.getById( contentId ) ).thenReturn( createContent() );
        RendererDelegateImpl factory = new RendererDelegateImpl( contentService, mock( LayoutDescriptorService.class ) );
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.EDIT );

        final PortalResponse renderResponse = factory.render( createFragmentComponent( contentId ), portalRequest );

        assertTrue( ((String) renderResponse.getBody()).contains( "Fragment content could not be found" ) );
    }

    @Test
    void fragmentNotFoundInNonEditMode()
    {
        final ContentService contentService = mock( ContentService.class );
        final ContentId contentId = ContentId.from( "contentId" );
        when( contentService.getById( contentId ) ).thenThrow( ContentNotFoundException.class );
        RendererDelegateImpl factory = new RendererDelegateImpl( contentService, mock( LayoutDescriptorService.class ) );
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.PREVIEW );

        final PortalResponse renderResponse = factory.render( createFragmentComponent( contentId ), portalRequest );

        assertTrue( ((String) renderResponse.getBody()).isEmpty() );
    }

    @Test
    void fragmentRenderLayoutNoDescriptor()
    {
        final ContentService contentService = mock( ContentService.class );
        final ContentId contentId = ContentId.from( "contentId" );
        when( contentService.getById( contentId ) ).thenReturn( createFragmentContentWithLayoutComponent( DescriptorKey.from( "des" ) ) );
        RendererDelegateImpl factory = new RendererDelegateImpl( contentService, mock( LayoutDescriptorService.class ) );
        factory.addRenderer( createRenderer( LayoutComponent.class, PortalResponse.create().body( "LayoutRendered" ).build() ) );
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setContent( createContentWithPage() );
        portalRequest.setMode( RenderMode.EDIT );

        final PortalResponse response = factory.render( createFragmentComponent( contentId ), portalRequest );

        assertEquals( "LayoutRendered", response.getBody() );
    }

    @Test
    void fragmentRenderLayoutWithoDescriptor()
    {
        final ContentService contentService = mock( ContentService.class );
        final LayoutDescriptorService layoutDescriptorService = mock( LayoutDescriptorService.class );
        final ContentId contentId = ContentId.from( "contentId" );
        final DescriptorKey descriptorKey = DescriptorKey.from( "descriptorKey" );
        when( contentService.getById( contentId ) ).thenReturn( createFragmentContentWithLayoutComponent( descriptorKey ) );
        when( layoutDescriptorService.getByKey( descriptorKey ) ).thenReturn( createLayoutDescriptor(descriptorKey) );
        RendererDelegateImpl factory = new RendererDelegateImpl( contentService, layoutDescriptorService );
        factory.addRenderer( createRenderer( LayoutComponent.class, PortalResponse.create().body( "LayoutRendered" ).build() ) );
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setContent( createContentWithPage() );
        portalRequest.setMode( RenderMode.EDIT );

        final PortalResponse response = factory.render( createFragmentComponent( contentId ), portalRequest );

        assertEquals( "LayoutRendered", response.getBody() );
    }

    private LayoutDescriptor createLayoutDescriptor( final DescriptorKey descriptorKey )
    {
        return LayoutDescriptor.create()
            .key( descriptorKey )
            .regions( RegionDescriptors.create().add( RegionDescriptor.create().name( "r1" ).build() ).build() )
            .config( Form.empty() )
            .modifiedTime( Instant.now() )
            .build();
    }

    @Test
    void fragmentRenderComponentNonUTF8()
    {
        final String textComponentValue = "textrendered";
        final PortalResponse fragmentResponse = PortalResponse.create().body( textComponentValue ).build();
        final PortalResponse renderResponse = renderFragmentComponent( fragmentResponse, RenderMode.EDIT );

        assertEquals( textComponentValue, renderResponse.getBody() );
    }

    @Test
    void fragmentRenderComponentUTF8()
    {
        final String textComponentValue = "textrendered";
        final PortalResponse fragmentResponse =
            PortalResponse.create().contentType( MediaType.HTML_UTF_8 ).body( textComponentValue ).build();
        final PortalResponse renderResponse = renderFragmentComponent( fragmentResponse, RenderMode.EDIT );

        assertEquals( "<div data-portal-component-type=\"fragment\">" + textComponentValue + "</div>", renderResponse.getBody() );
    }

    @Test
    void fragmentRenderNonEditMode()
    {
        final String textComponentValue = "textrendered";
        final PortalResponse fragmentResponse =
            PortalResponse.create().contentType( MediaType.HTML_UTF_8 ).body( textComponentValue ).build();
        final PortalResponse renderResponse = renderFragmentComponent( fragmentResponse, RenderMode.PREVIEW );

        assertEquals( textComponentValue, renderResponse.getBody() );
    }

    @Test
    void fragmentRenderWithNoSuchMethodError()
    {
        final String errorText = "No method provided to handle request";
        final PortalResponse fragmentResponse = PortalResponse.create().contentType( MediaType.HTML_UTF_8 ).body( errorText ).build();
        final PortalResponse renderResponse = renderFragmentComponent( fragmentResponse, RenderMode.EDIT );

        assertTrue( ((String) renderResponse.getBody()).contains( "<span class=\"data-portal-placeholder-error\">" + errorText + "</span>" ) );
    }

    private PortalResponse renderFragmentComponent( final PortalResponse fragmentRenderResult, final RenderMode renderMode )
    {
        final ContentService contentService = mock( ContentService.class );
        final ContentId contentId = ContentId.from( "contentId" );
        when( contentService.getById( contentId ) ).thenReturn( createFragmentContentWithTextComponent() );
        RendererDelegateImpl factory = new RendererDelegateImpl( contentService, mock( LayoutDescriptorService.class ) );
        factory.addRenderer( createRenderer( TextComponent.class, fragmentRenderResult ) );
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setContent( createContentWithPage() );
        portalRequest.setMode( renderMode );

        return factory.render( createFragmentComponent( contentId ), portalRequest );
    }

    private FragmentComponent createEmptyFragmentComponent()
    {
        return FragmentComponent.create().build();
    }

    private FragmentComponent createFragmentComponent( final ContentId contentId )
    {
        final FragmentComponent fragmentComponent = FragmentComponent.create().fragment( contentId ).build();
        Region.create().add( fragmentComponent ).name( "main" ).build();
        return fragmentComponent;
    }

    private Content createFragmentContentWithTextComponent()
    {
        final TextComponent textComponent = TextComponent.create().text( "my-text" ).build();

        return Content.create()
            .name( "my-content" )
            .parentPath( ContentPath.ROOT )
            .page( Page.create().fragment( textComponent ).build() )
            .build();
    }

    private Content createFragmentContentWithLayoutComponent( final DescriptorKey descriptorKey )
    {
        final LayoutComponent layoutComponent = LayoutComponent.create().descriptor( descriptorKey ).build();

        return Content.create()
            .name( "my-content" )
            .parentPath( ContentPath.ROOT )
            .page( Page.create().fragment( layoutComponent ).build() )
            .build();
    }

    private PageTemplate createPageTemplate()
    {
        return PageTemplate.newPageTemplate().name( "my-template" ).parentPath( ContentPath.ROOT ).build();
    }

    private Content createContent()
    {
        return Content.create().name( "my-content" ).parentPath( ContentPath.ROOT ).build();
    }

    private Content createContentWithPage()
    {
        return Content.create().name( "my-content" ).page( Page.create().build() ).parentPath( ContentPath.ROOT ).build();
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
