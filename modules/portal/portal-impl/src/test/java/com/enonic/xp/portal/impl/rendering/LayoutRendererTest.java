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
import com.enonic.xp.region.LayoutComponent;
import com.enonic.xp.region.LayoutDescriptor;
import com.enonic.xp.region.LayoutDescriptorService;
import com.enonic.xp.region.RegionDescriptor;
import com.enonic.xp.region.RegionDescriptors;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.websocket.WebSocketEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class LayoutRendererTest
{
    private PortalRequest portalRequest;

    private PortalResponse portalResponse;

    private LayoutComponent layoutComponent;

    private LayoutRenderer renderer;

    private String configureEmptyComponent( RenderMode mode )
    {
        // setup
        portalRequest.setMode( mode );
        layoutComponent = LayoutComponent.create().build();
        renderer = new LayoutRenderer();

        // exercise
        portalResponse = renderer.render( layoutComponent, portalRequest );

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
        String result = "<div data-portal-component-type=\"layout\"></div>";
        assertEquals( result, response );
    }

    @Test
    public void emptyComponentPreviewMode()
    {
        // verify
        String response = this.configureEmptyComponent( RenderMode.PREVIEW );
        String result = "<div data-portal-component-type=\"layout\"></div>";
        assertEquals( result, response );
    }

    @Test
    public void emptyComponentInlineMode()
    {
        // verify
        String response = this.configureEmptyComponent( RenderMode.INLINE );
        String result = "<div data-portal-component-type=\"layout\"></div>";
        assertEquals( result, response );
    }

    @Test
    public void emptyComponentLiveMode()
    {
        // verify
        String response = this.configureEmptyComponent( RenderMode.LIVE );
        String result = "<div data-portal-component-type=\"layout\"></div>";
        assertEquals( result, response );
    }

    @Test
    public void emptyComponentNoMode()
    {
        assertThrows(DescriptorNotFoundException.class, () -> this.configureEmptyComponent( RenderMode.ADMIN ));
    }

    @Test
    public void errorComponentPlaceHolderEditMode()
    {
        // setup
        portalRequest.setMode( RenderMode.EDIT );
        layoutComponent = LayoutComponent.create().descriptor( "myapp:myLayoutComponent" ).
            descriptor( "descriptor-x" ).
            config( new PropertyTree() ).
            build();
        renderer = new LayoutRenderer();

        // exercise
        portalResponse = renderer.render( layoutComponent, portalRequest );

        // verify
        String result =
            "<div data-portal-component-type=\"layout\" data-portal-placeholder=\"true\" data-portal-placeholder-error=\"true\"><span class=\"data-portal-placeholder-error\"></span></div>";
        assertEquals( result, portalResponse.getAsString() );
    }

    @Test
    public void htmlResponseComponentEditMode()
    {
        final LayoutDescriptor layoutDescriptor = LayoutDescriptor.create().
            displayName( "My layout component" ).
            config( Form.create().build() ).
            key( DescriptorKey.from( "module:myLayoutComponent" ) ).
            regions( RegionDescriptors.create().
                add( RegionDescriptor.create().name( "left" ).build() ).
                add( RegionDescriptor.create().name( "right" ).build() ).
                build() ).
            build();
        final ControllerScript controllerScript = new ControllerScript()
        {
            @Override
            public PortalResponse execute( final PortalRequest portalRequest )
            {
                return PortalResponse.create().body(
                    "<div class=\"row\"><div data-portal-region=\"left\" class=\"col-left\"></div><div data-portal-region=\"right\" class=\"col-right\"></div></div>" ).contentType(
                    MediaType.HTML_UTF_8 ).status( HttpStatus.OK ).build();
            }

            @Override
            public void onSocketEvent( final WebSocketEvent event )
            {
            }
        };

        final LayoutDescriptorService layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );
        final ControllerScriptFactory controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        renderer = new LayoutRenderer();
        renderer.setLayoutDescriptorService( layoutDescriptorService );
        renderer.setControllerScriptFactory( controllerScriptFactory );

        when( layoutDescriptorService.getByKey( any() ) ).thenReturn( layoutDescriptor );
        when( controllerScriptFactory.fromDir( any() ) ).thenReturn( controllerScript );
        portalRequest.setMode( RenderMode.EDIT );
        layoutComponent = LayoutComponent.create().descriptor( "myapp:myLayoutComponent" ).descriptor( layoutDescriptor.getKey() ).build();

        // exercise
        portalResponse = renderer.render( layoutComponent, portalRequest );

        // verify
        String expected =
            "<div data-portal-component-type=\"layout\" class=\"row\"><div data-portal-region=\"left\" class=\"col-left\"></div><div data-portal-region=\"right\" class=\"col-right\"></div></div>";
        assertEquals( expected, portalResponse.getAsString() );
    }

    @Test
    public void nullResponseComponentEditMode()
    {
        final LayoutDescriptor layoutDescriptor = LayoutDescriptor.create().
            displayName( "My layout component" ).
            config( Form.create().build() ).
            key( DescriptorKey.from( "module:myLayoutComponent" ) ).
            regions( RegionDescriptors.create().
                add( RegionDescriptor.create().name( "left" ).build() ).
                add( RegionDescriptor.create().name( "right" ).build() ).
                build() ).
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

        final LayoutDescriptorService layoutDescriptorService = Mockito.mock( LayoutDescriptorService.class );
        final ControllerScriptFactory controllerScriptFactory = Mockito.mock( ControllerScriptFactory.class );
        renderer = new LayoutRenderer();
        renderer.setLayoutDescriptorService( layoutDescriptorService );
        renderer.setControllerScriptFactory( controllerScriptFactory );

        when( layoutDescriptorService.getByKey( any() ) ).thenReturn( layoutDescriptor );
        when( controllerScriptFactory.fromDir( any() ) ).thenReturn( controllerScript );
        portalRequest.setMode( RenderMode.EDIT );
        layoutComponent = LayoutComponent.create().descriptor( "myapp:myLayoutComponent" ).descriptor( layoutDescriptor.getKey() ).build();

        // exercise
        portalResponse = renderer.render( layoutComponent, portalRequest );

        // verify
        String expected = "<div data-portal-component-type=\"layout\"></div>";
        assertEquals( expected, portalResponse.getAsString() );
    }
}
