package com.enonic.xp.portal.impl.rendering;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.net.MediaType;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.form.Form;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.controller.PortalResponseSerializer;
import com.enonic.xp.region.PartComponent;
import com.enonic.xp.region.PartDescriptor;
import com.enonic.xp.region.PartDescriptorService;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.websocket.WebSocketEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class PartRendererTest
{
    private PortalRequest portalRequest;

    private PortalResponse portalResponse;

    private PartComponent partComponent;

    private PartRenderer renderer;

    private String configureEmptyComponent( RenderMode mode )
    {
        // setup
        portalRequest.setMode( mode );
        partComponent = PartComponent.create().build();
        renderer = new PartRenderer();

        // exercise
        portalResponse = renderer.render( partComponent, portalRequest );

        return portalResponse.getAsString();
    }

    @BeforeEach
    public void before()
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        this.portalRequest.setBaseUri( "/site" );
        this.portalRequest.setContentPath( ContentPath.from( "context/path" ) );
        this.portalResponse = PortalResponse.create().build();
        this.portalRequest.setMode( RenderMode.EDIT );
    }

    @Test
    public void emptyComponentEditMode()
    {
        // verify
        String response = this.configureEmptyComponent( RenderMode.EDIT );
        String result = "<div data-portal-component-type=\"part\"></div>";
        assertEquals( result, response );
    }

    @Test
    public void emptyComponentPreviewMode()
    {
        // verify
        String response = this.configureEmptyComponent( RenderMode.PREVIEW );
        String result = "<div data-portal-component-type=\"part\"></div>";
        assertEquals( result, response );
    }

    @Test
    public void emptyComponentInlineMode()
    {
        // verify
        String response = this.configureEmptyComponent( RenderMode.INLINE );
        String result = "<div data-portal-component-type=\"part\"></div>";
        assertEquals( result, response );
    }

    @Test
    public void emptyComponentLiveMode()
    {
        // verify
        String response = this.configureEmptyComponent( RenderMode.LIVE );
        String result = "<div data-portal-component-type=\"part\"></div>";
        assertEquals( result, response );
    }

    @Test
    public void emptyComponentNoMode()
    {
        assertThrows( DescriptorNotFoundException.class, () -> this.configureEmptyComponent( RenderMode.ADMIN ) );
    }

    @Test
    public void errorComponentPlaceHolderEditMode()
    {
        // setup
        portalRequest.setMode( RenderMode.EDIT );
        partComponent = PartComponent.create().descriptor( "myapp:myPartComponent" ).
            descriptor( "descriptor-x" ).
            config( new PropertyTree() ).
            build();
        renderer = new PartRenderer();

        // exercise
        portalResponse = renderer.render( partComponent, portalRequest );

        // verify
        String result =
            "<div data-portal-component-type=\"part\" data-portal-placeholder=\"true\" data-portal-placeholder-error=\"true\"><span class=\"data-portal-placeholder-error\"></span></div>";
        assertEquals( result, portalResponse.getAsString() );
    }

    @Test
    public void htmlResponseComponentEditMode()
    {
        final PartDescriptor partDescriptor = PartDescriptor.create().
            displayName( "My part component" ).
            config( Form.create().build() ).
            key( DescriptorKey.from( "module:myPartComponent" ) ).
            build();
        final ControllerScript controllerScript = new ControllerScript()
        {
            @Override
            public PortalResponse execute( final PortalRequest portalRequest )
            {
                return PortalResponse.create().body( "<h1 class=\"important\">My component</h1>" ).contentType(
                    MediaType.HTML_UTF_8 ).status( HttpStatus.OK ).build();
            }

            @Override
            public void onSocketEvent( final WebSocketEvent event )
            {
            }
        };

        final PartDescriptorService partDescriptorService = Mockito.mock( PartDescriptorService.class );
        final ControllerScriptFactory controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        renderer = new PartRenderer();
        renderer.setPartDescriptorService( partDescriptorService );
        renderer.setControllerScriptFactory( controllerScriptFactory );

        when( partDescriptorService.getByKey( any() ) ).thenReturn( partDescriptor );
        when( controllerScriptFactory.fromDir( any() ) ).thenReturn( controllerScript );
        portalRequest.setMode( RenderMode.EDIT );
        partComponent = PartComponent.create().descriptor( "myapp:myPartComponent" ).descriptor( partDescriptor.getKey() ).build();

        // exercise
        portalResponse = renderer.render( partComponent, portalRequest );

        // verify
        String expected = "<h1 data-portal-component-type=\"part\" class=\"important\">My component</h1>";
        assertEquals( expected, portalResponse.getAsString() );
    }

    @Test
    public void htmlResponseComponentEditModeNoMethodToHandleReq()
    {
        final PartDescriptor partDescriptor = PartDescriptor.create().
            displayName( "My part component" ).
            config( Form.create().build() ).
            key( DescriptorKey.from( "module:myPartComponent" ) ).
            build();
        final ControllerScript controllerScript = new ControllerScript()
        {
            @Override
            public PortalResponse execute( final PortalRequest portalRequest )
            {
                return new PortalResponseSerializer( null, HttpStatus.METHOD_NOT_ALLOWED ).serialize();
            }

            @Override
            public void onSocketEvent( final WebSocketEvent event )
            {
            }
        };

        final PartDescriptorService partDescriptorService = Mockito.mock( PartDescriptorService.class );
        final ControllerScriptFactory controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        renderer = new PartRenderer();
        renderer.setPartDescriptorService( partDescriptorService );
        renderer.setControllerScriptFactory( controllerScriptFactory );

        when( partDescriptorService.getByKey( any() ) ).thenReturn( partDescriptor );
        when( controllerScriptFactory.fromDir( any() ) ).thenReturn( controllerScript );
        portalRequest.setMode( RenderMode.EDIT );
        partComponent = PartComponent.create().descriptor( "myapp:myPartComponent" ).descriptor( partDescriptor.getKey() ).build();

        // exercise
        portalResponse = renderer.render( partComponent, portalRequest );

        // verify
        String expected =
            "<div data-portal-component-type=\"part\" data-portal-placeholder=\"true\" data-portal-placeholder-error=\"true\"><span class=\"data-portal-placeholder-error\">No method provided to handle request</span></div>";
        assertEquals( expected, portalResponse.getAsString() );
    }

    @Test
    public void nullResponseComponentEditMode()
    {
        final PartDescriptor partDescriptor = PartDescriptor.create().
            displayName( "My part component" ).
            config( Form.create().build() ).
            key( DescriptorKey.from( "module:myPartComponent" ) ).
            build();
        final ControllerScript controllerScript = new ControllerScript()
        {
            @Override
            public PortalResponse execute( final PortalRequest portalRequest )
            {
                return new PortalResponseSerializer( null ).serialize();
            }

            @Override
            public void onSocketEvent( final WebSocketEvent event )
            {
            }
        };

        final PartDescriptorService partDescriptorService = Mockito.mock( PartDescriptorService.class );
        final ControllerScriptFactory controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        renderer = new PartRenderer();
        renderer.setPartDescriptorService( partDescriptorService );
        renderer.setControllerScriptFactory( controllerScriptFactory );

        when( partDescriptorService.getByKey( any() ) ).thenReturn( partDescriptor );
        when( controllerScriptFactory.fromDir( any() ) ).thenReturn( controllerScript );
        portalRequest.setMode( RenderMode.EDIT );
        partComponent = PartComponent.create().descriptor( "myapp:myPartComponent" ).descriptor( partDescriptor.getKey() ).build();

        // exercise
        portalResponse = renderer.render( partComponent, portalRequest );

        // verify
        String expected = "<div data-portal-component-type=\"part\"></div>";
        assertEquals( expected, portalResponse.getAsString() );
    }
}
