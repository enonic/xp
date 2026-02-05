package com.enonic.xp.portal.impl.handler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.impl.PortalConfig;
import com.enonic.xp.web.HttpMethod;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ApiDispatcherTest
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

    @BeforeEach
    void setup()
    {
        slashApiHandler = mock( SlashApiHandler.class );
        componentHandler = mock( ComponentHandler.class );
        assetHandler = mock( AssetHandler.class );
        serviceHandler = mock( ServiceHandler.class );
        identityHandler = mock( IdentityHandler.class );
        imageHandler = mock( ImageHandler.class );
        attachmentHandler = mock( AttachmentHandler.class );
        errorHandler = mock( ErrorHandler.class );

        this.handler = new ApiDispatcher( slashApiHandler, componentHandler, assetHandler, serviceHandler, identityHandler, imageHandler,
                                          attachmentHandler, errorHandler );

        PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        this.handler.activate( portalConfig );
    }

    @Test
    void testCanHandle()
    {
        final WebRequest webRequest1 = new WebRequest();
        webRequest1.setMethod( HttpMethod.GET );
        webRequest1.setRawPath( "/path" );
        assertFalse( this.handler.canHandle( webRequest1 ) );

        // test media
        final WebRequest webRequest2 = new WebRequest();
        webRequest2.setMethod( HttpMethod.GET );
        webRequest2.setRawPath( "/api/media:image/project:branch/id:fingerprint/name" );
        assertTrue( this.handler.canHandle( webRequest2 ) );

        // test image
        final WebRequest webRequest3 = new WebRequest();
        webRequest3.setMethod( HttpMethod.GET );
        webRequest3.setRawPath( "/path-to-content/_/image/id:version/scale/name" );
        assertTrue( this.handler.canHandle( webRequest3 ) );

        // test attachment
        final WebRequest webRequest4 = new WebRequest();
        webRequest4.setMethod( HttpMethod.GET );
        webRequest4.setRawPath( "/path-to-content/_/attachment/mode/id:version/name" );
        assertTrue( this.handler.canHandle( webRequest4 ) );

        // test service
        final WebRequest webRequest5 = new WebRequest();
        webRequest5.setMethod( HttpMethod.GET );
        webRequest5.setRawPath( "/contextPath/_/service/application/name" );
        assertTrue( this.handler.canHandle( webRequest5 ) );

        // test asset
        final WebRequest webRequest6 = new WebRequest();
        webRequest6.setMethod( HttpMethod.GET );
        webRequest6.setRawPath( "/contextPath/_/asset/application:ts/pathToAsset" );
        assertTrue( this.handler.canHandle( webRequest6 ) );

        // test error
        final WebRequest webRequest7 = new WebRequest();
        webRequest7.setMethod( HttpMethod.GET );
        webRequest7.setRawPath( "/contextPath/_/error/message" );
        assertTrue( this.handler.canHandle( webRequest7 ) );

        // test idprovider
        final WebRequest webRequest8 = new WebRequest();
        webRequest8.setMethod( HttpMethod.GET );
        webRequest8.setRawPath( "/contextPath/_/idprovider/name/function" );
        assertTrue( this.handler.canHandle( webRequest8 ) );

        // test component
        final WebRequest webRequest9 = new WebRequest();
        webRequest9.setMethod( HttpMethod.GET );
        webRequest9.setRawPath( "/site/project/branch/content/_/component/pathToComponent" );
        assertTrue( this.handler.canHandle( webRequest9 ) );

        // test slashApi
        final WebRequest webRequest10 = new WebRequest();
        webRequest10.setMethod( HttpMethod.GET );
        webRequest10.setRawPath( "/api/com.enonic.app.myapp:api-key" );
        assertTrue( this.handler.canHandle( webRequest10 ) );

        // test handle as endpoint
        final WebRequest webRequest11 = new WebRequest();
        webRequest11.setMethod( HttpMethod.GET );
        webRequest11.setRawPath( "/path-to-content/_/com.enonic.app.myapp:api-key" );
        assertTrue( this.handler.canHandle( webRequest11 ) );
    }

    @Test
    void testHandleImage()
        throws Exception
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setMethod( HttpMethod.GET );
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRawPath( "/path-to-content/_/image/id:version/scale/name" );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( imageHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( portalRequest, webResponse, null ) );
    }

    @Test
    void testHandleAttachment()
        throws Exception
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setMethod( HttpMethod.GET );
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRawPath( "/path-to-content/_/attachment/mode/id:version/name" );

        // test canHandle
        assertTrue( this.handler.canHandle( portalRequest ) );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( attachmentHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( portalRequest, webResponse, null ) );
    }

    @Test
    void testHandleService()
        throws Exception
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/contextPath/_/service/application/name" );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( serviceHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( webRequest, webResponse, null ) );
    }

    @Test
    void testHandleAsset()
        throws Exception
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/contextPath/_/asset/application:ts/pathToAsset" );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( assetHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( webRequest, webResponse, null ) );
    }

    @Test
    void testHandleError()
        throws Exception
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/contextPath/_/error/message" );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( errorHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( webRequest, webResponse, null ) );
    }

    @Test
    void testHandleIdProvider()
        throws Exception
    {
        final WebRequest webRequest = new WebRequest();
        webRequest.setMethod( HttpMethod.GET );
        webRequest.setRawPath( "/contextPath/_/idprovider/name/function" );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( identityHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( webRequest, webResponse, null ) );
    }

    @Test
    void testHandleComponent()
        throws Exception
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setMethod( HttpMethod.GET );
        portalRequest.setBaseUri( "/site" );
        portalRequest.setRawPath( "/site/project/branch/content/_/component/pathToComponent" );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( componentHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( portalRequest, webResponse, null ) );
    }

    @Test
    void testSlashApi()
        throws Exception
    {
        final WebRequest webRequest1 = new WebRequest();
        webRequest1.setMethod( HttpMethod.GET );
        webRequest1.setRawPath( "/api/com.enonic.app.myapp:api-key" );

        final PortalResponse webResponse = PortalResponse.create().build();
        when( slashApiHandler.handle( any( WebRequest.class ) ) ).thenReturn( webResponse );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( webRequest1, webResponse, null ) );

        // test handle as endpoint
        final WebRequest webRequest2 = new WebRequest();
        webRequest2.setMethod( HttpMethod.GET );
        webRequest2.setRawPath( "/path-to-content/_/com.enonic.app.myapp:api-key" );

        // test handle
        assertEquals( webResponse, this.handler.doHandle( webRequest2, webResponse, null ) );
    }

    @Test
    void testLegacyHandler()
        throws Exception
    {
        PortalConfig portalConfig = mock( PortalConfig.class, invocation -> invocation.getMethod().getDefaultValue() );
        when( portalConfig.legacy_imageService_enabled() ).thenReturn( false );
        when( portalConfig.legacy_attachmentService_enabled() ).thenReturn( false );
        when( portalConfig.legacy_httpService_enabled() ).thenReturn( false );

        this.handler.activate( portalConfig );

        // test image
        final WebRequest webRequest1 = new WebRequest();
        webRequest1.setMethod( HttpMethod.GET );
        webRequest1.setRawPath( "/path-to-content/_/image/id:version/scale/name" );
        assertEquals( HttpStatus.NOT_FOUND, this.handler.doHandle( webRequest1, WebResponse.create().build(), null ).getStatus() );

        // test attachment
        final WebRequest webRequest2 = new WebRequest();
        webRequest2.setMethod( HttpMethod.GET );
        webRequest2.setRawPath( "/path-to-content/_/attachment/mode/id:version/name" );
        assertEquals( HttpStatus.NOT_FOUND, this.handler.doHandle( webRequest2, WebResponse.create().build(), null ).getStatus() );

        // test service
        final WebRequest webRequest3 = new WebRequest();
        webRequest3.setMethod( HttpMethod.GET );
        webRequest3.setRawPath( "/contextPath/_/service/application/name" );
        assertEquals( HttpStatus.NOT_FOUND, this.handler.doHandle( webRequest3, WebResponse.create().build(), null ).getStatus() );
    }

}
