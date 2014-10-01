package com.enonic.wem.portal.url;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.content.ContentPath;
import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.portal.PortalContext;
import com.enonic.wem.portal.PortalRequest;

import static org.junit.Assert.*;

public class PortalUrlBuildersTest
{
    private PortalContext context;

    private PortalRequest request;

    @Before
    public void setup()
    {
        this.context = Mockito.mock( PortalContext.class );
        this.request = Mockito.mock( PortalRequest.class );

        Mockito.when( this.context.getRequest() ).thenReturn( this.request );

        setBaseUri( "" );
        setMode( RenderingMode.LIVE );
        setWorkspace( Workspace.from( "stage" ) );
        setResolvedModule( "mymodule" );
        setContent( ContentPath.from( "path/to/content" ) );
    }

    private void setBaseUri( final String baseUri )
    {
        Mockito.when( this.request.getBaseUri() ).thenReturn( baseUri );
    }

    private void setMode( final RenderingMode mode )
    {
        Mockito.when( this.request.getMode() ).thenReturn( mode );
    }

    private void setWorkspace( final Workspace workspace )
    {
        Mockito.when( this.request.getWorkspace() ).thenReturn( workspace );
    }

    private void setResolvedModule( final String resolvedModule )
    {
        Mockito.when( this.context.getResolvedModule() ).thenReturn( resolvedModule );
    }

    private void setContent( final ContentPath path )
    {
        final Content content = Content.newContent().path( path ).build();
        Mockito.when( this.context.getContent() ).thenReturn( content );
    }

    @Test
    public void getBaseUrl()
    {
        setBaseUri( "/root" );

        final PortalUrlBuilders bean = new PortalUrlBuilders( this.context );
        assertEquals( "/root", bean.getBaseUrl() );
    }

    @Test
    public void createUrl()
    {
        setMode( RenderingMode.EDIT );
        setWorkspace( Workspace.from( "test" ) );

        final PortalUrlBuilders bean = new PortalUrlBuilders( this.context );
        final GeneralUrlBuilder urlBuilder = bean.createUrl( "some/path" );

        assertEquals( "/portal/edit/test/some/path", urlBuilder.toString() );
    }

    @Test
    public void createResourceUrl()
    {
        final PortalUrlBuilders bean = new PortalUrlBuilders( this.context );
        final PublicUrlBuilder urlBuilder = bean.createResourceUrl( "some/path" );

        assertEquals( "/portal/live/stage/path/to/content/_/public/mymodule/some/path", urlBuilder.toString() );
    }

    @Test
    public void createImageUrl()
    {
        final PortalUrlBuilders bean = new PortalUrlBuilders( this.context );
        final ImageUrlBuilder urlBuilder = bean.createImageUrl( "myimage" );

        assertEquals( "/portal/live/stage/path/to/content/_/image/myimage", urlBuilder.toString() );
    }

    @Test
    public void createImageByIdUrl()
    {
        final PortalUrlBuilders bean = new PortalUrlBuilders( this.context );
        final ImageUrlBuilder urlBuilder = bean.createImageByIdUrl( ContentId.from( "123" ) );

        assertEquals( "/portal/live/stage/path/to/content/_/image/id/123", urlBuilder.toString() );
    }

    @Test
    public void createServiceUrl()
    {
        final PortalUrlBuilders bean = new PortalUrlBuilders( this.context );
        final ServiceUrlBuilder urlBuilder = bean.createServiceUrl( "myservice" );

        assertEquals( "/portal/live/stage/path/to/content/_/service/mymodule/myservice", urlBuilder.toString() );
    }
}
