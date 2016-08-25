package com.enonic.xp.portal.impl.rendering;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.ContentPath;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.region.PartComponent;

import static org.junit.Assert.*;

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
        partComponent = PartComponent.create().name( "myPartComponent" ).build();
        renderer = new PartRenderer();

        // exercise
        portalResponse = renderer.render( partComponent, portalRequest );

        return portalResponse.getAsString();
    }

    @Before
    public void before()
    {
        this.portalRequest = new PortalRequest();
        this.portalRequest.setBranch( Branch.from( "draft" ) );
        this.portalRequest.setApplicationKey( ApplicationKey.from( "myapplication" ) );
        this.portalRequest.setBaseUri( "/portal" );
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
    public void emptyComponentLiveMode()
    {
        // verify
        String response = this.configureEmptyComponent( RenderMode.LIVE );
        String result = "<div data-portal-component-type=\"part\"></div>";
        assertEquals( result, response );
    }

    @Test(expected = DescriptorNotFoundException.class)
    public void emptyComponentNoMode()
    {
        this.configureEmptyComponent( RenderMode.ADMIN );
    }

    @Test
    public void errorComponentPlaceHolderEditMode()
    {
        // setup
        portalRequest.setMode( RenderMode.EDIT );
        partComponent = PartComponent.create().name( "myPartComponent" ).
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
}