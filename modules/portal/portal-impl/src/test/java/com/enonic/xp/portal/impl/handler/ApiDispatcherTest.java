package com.enonic.xp.portal.impl.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ApiDispatcherTest
{
    private ApiDispatcher handler;

    private SlashApiHandler slashApiHandler;

    private ComponentHandler componentHandler;

    private AssetHandler assetHandler;

    private ServiceHandler serviceHandler;

    private IdentityHandler identityHandler;

    private ImageHandler imageHandler;

    private AttachmentHandler attachmentHandler;

    private ErrorHandler errorHandler;

    private MediaHandler mediaHandler;

    @BeforeEach
    public void setup()
    {
        slashApiHandler = mock( SlashApiHandler.class );
        componentHandler = mock( ComponentHandler.class );
        assetHandler = mock( AssetHandler.class );
        serviceHandler = mock( ServiceHandler.class );
        identityHandler = mock( IdentityHandler.class );
        imageHandler = mock( ImageHandler.class );
        attachmentHandler = mock( AttachmentHandler.class );
        errorHandler = mock( ErrorHandler.class );
        mediaHandler = mock( MediaHandler.class );

        this.handler = new ApiDispatcher( slashApiHandler, componentHandler, assetHandler, serviceHandler, identityHandler, imageHandler,
                                          attachmentHandler, errorHandler, mediaHandler );

        PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        this.handler.activate( portalConfig );
    }

    @Test
    public void testCanHandle()
    {
        WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );

        when( webRequest.getRawPath() ).thenReturn( "/path" );
        assertFalse( this.handler.canHandle( webRequest ) );

        // test media
        when( webRequest.getEndpointPath() ).thenReturn( null );
        when( webRequest.getRawPath() ).thenReturn( "/api/media/image/project:branch/id:fingerprint/name" );
        assertTrue( this.handler.canHandle( webRequest ) );

        // test image
        when( webRequest.getEndpointPath() ).thenReturn( "/_/image/id:version/scale/name" );
        when( webRequest.getRawPath() ).thenReturn( "path-to-content/_/image/id:version/scale/name" );
        assertTrue( this.handler.canHandle( webRequest ) );

        // test attachment
        when( webRequest.getEndpointPath() ).thenReturn( "/_/attachment/mode/id:version/name" );
        when( webRequest.getRawPath() ).thenReturn( "path-to-content/_/attachment/mode/id:version/name" );
        assertTrue( this.handler.canHandle( webRequest ) );

        // test service
        when( webRequest.getEndpointPath() ).thenReturn( "/_/service/application/name" );
        when( webRequest.getRawPath() ).thenReturn( "contextPath/_/service/application/name" );
        assertTrue( this.handler.canHandle( webRequest ) );

        // test asset
        when( webRequest.getEndpointPath() ).thenReturn( "/_/asset/application:ts/pathToAsset" );
        when( webRequest.getRawPath() ).thenReturn( "contextPath/_/asset/application:ts/pathToAsset" );
        assertTrue( this.handler.canHandle( webRequest ) );

        // test error
        when( webRequest.getEndpointPath() ).thenReturn( "/_/error/message" );
        when( webRequest.getRawPath() ).thenReturn( "contextPath/_/error/message" );
        assertTrue( this.handler.canHandle( webRequest ) );

        // test idprovider
        when( webRequest.getEndpointPath() ).thenReturn( "/_/idprovider/name/function" );
        when( webRequest.getRawPath() ).thenReturn( "contextPath/_/idprovider/name/function" );
        assertTrue( this.handler.canHandle( webRequest ) );

        // test component
        when( webRequest.getEndpointPath() ).thenReturn( "/_/component/pathToComponent" );
        when( webRequest.getRawPath() ).thenReturn( "/site/project/branch/content/_/component/pathToComponent" );
        assertTrue( this.handler.canHandle( webRequest ) );

        // test slashApi
        when( webRequest.getEndpointPath() ).thenReturn( null );
        when( webRequest.getRawPath() ).thenReturn( "/api/com.enonic.app.myapp/api-key" );
        assertTrue( this.handler.canHandle( webRequest ) );

        // test handle as endpoint
        when( webRequest.getEndpointPath() ).thenReturn( "/_/com.enonic.app.myapp/api-key" );
        when( webRequest.getRawPath() ).thenReturn( "path-to-content/_/com.enonic.app.myapp/api-key" );
        assertTrue( this.handler.canHandle( webRequest ) );
    }

    @Test
    public void testDoHandleFailed()
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getEndpointPath() ).thenReturn( null );
        when( webRequest.getRawPath() ).thenReturn( "/site/project/branch/path" );

        IllegalStateException ex = assertThrows( IllegalStateException.class, () -> this.handler.doHandle( webRequest, null, null ) );
        assertEquals( "Invalid API path: /site/project/branch/path", ex.getMessage() );
    }

    @Test
    public void testHandleMedia()
        throws Exception
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( null );
        when( webRequest.getRawPath() ).thenReturn( "/api/media/image/project:branch/id:fingerprint/name" );

        final WebResponse webResponse = WebResponse.create().build();
        when( mediaHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( webRequest, webResponse, null ) );
    }

    @Test
    public void testHandleImage()
        throws Exception
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMethod( HttpMethod.GET );
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRawPath( "path-to-content/_/image/id:version/scale/name" );
        portalRequest.setEndpointPath( "/_/image/id:version/scale/name" );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( imageHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( portalRequest, webResponse, null ) );
    }

    @Test
    public void testHandleAttachment()
        throws Exception
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMethod( HttpMethod.GET );
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRawPath( "path-to-content/_/attachment/mode/id:version/name" );
        portalRequest.setEndpointPath( "/_/attachment/mode/id:version/name" );

        // test canHandle
        assertTrue( this.handler.canHandle( portalRequest ) );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( attachmentHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( portalRequest, webResponse, null ) );
    }

    @Test
    public void testHandleService()
        throws Exception
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( "/_/service/application/name" );
        when( webRequest.getRawPath() ).thenReturn( "contextPath/_/service/application/name" );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( serviceHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( webRequest, webResponse, null ) );
    }

    @Test
    public void testHandleAsset()
        throws Exception
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( "/_/asset/application:ts/pathToAsset" );
        when( webRequest.getRawPath() ).thenReturn( "contextPath/_/asset/application:ts/pathToAsset" );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( assetHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( webRequest, webResponse, null ) );
    }

    @Test
    public void testHandleError()
        throws Exception
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( "/_/error/message" );
        when( webRequest.getRawPath() ).thenReturn( "contextPath/_/error/message" );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( errorHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( webRequest, webResponse, null ) );
    }

    @Test
    public void testHandleIdProvider()
        throws Exception
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( "/_/idprovider/name/function" );
        when( webRequest.getRawPath() ).thenReturn( "contextPath/_/idprovider/name/function" );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( identityHandler.handle( any( WebRequest.class ), any( WebResponse.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( webRequest, webResponse, null ) );
    }

    @Test
    public void testHandleComponent()
        throws Exception
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMethod( HttpMethod.GET );
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRawPath( "/site/project/branch/content/_/component/pathToComponent" );
        portalRequest.setEndpointPath( "/_/component/pathToComponent" );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( componentHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( portalRequest, webResponse, null ) );
    }

    @Test
    public void testSlashApi()
        throws Exception
    {
        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );
        when( webRequest.getEndpointPath() ).thenReturn( null );
        when( webRequest.getRawPath() ).thenReturn( "/api/com.enonic.app.myapp/api-key" );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( slashApiHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( webRequest, webResponse, null ) );

        // test handle as endpoint
        when( webRequest.getEndpointPath() ).thenReturn( "/_/com.enonic.app.myapp/api-key" );
        when( webRequest.getRawPath() ).thenReturn( "path-to-content/_/com.enonic.app.myapp/api-key" );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( webRequest, webResponse, null ) );
    }

    @Test
    public void testLegacyHandler()
        throws Exception
    {
        PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( false );
        when( portalConfig.legacy_attachmentService_enabled() ).thenReturn( false );
        when( portalConfig.legacy_httpService_enabled() ).thenReturn( false );

        this.handler.activate( portalConfig );

        final WebRequest webRequest = mock( WebRequest.class );
        when( webRequest.getMethod() ).thenReturn( HttpMethod.GET );

        // test image
        when( webRequest.getEndpointPath() ).thenReturn( "/_/image/id:version/scale/name" );
        when( webRequest.getRawPath() ).thenReturn( "path-to-content/_/image/id:version/scale/name" );
        assertEquals( HttpStatus.NOT_FOUND, this.handler.doHandle( webRequest, WebResponse.create().build(), null ).getStatus() );

        // test attachment
        when( webRequest.getEndpointPath() ).thenReturn( "/_/attachment/mode/id:version/name" );
        when( webRequest.getRawPath() ).thenReturn( "path-to-content/_/attachment/mode/id:version/name" );
        assertEquals( HttpStatus.NOT_FOUND, this.handler.doHandle( webRequest, WebResponse.create().build(), null ).getStatus() );

        // test service
        when( webRequest.getEndpointPath() ).thenReturn( "/_/service/application/name" );
        when( webRequest.getRawPath() ).thenReturn( "contextPath/_/service/application/name" );
        assertEquals( HttpStatus.NOT_FOUND, this.handler.doHandle( webRequest, WebResponse.create().build(), null ).getStatus() );
    }

}
