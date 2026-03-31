package com.enonic.xp.lib.portal.url;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ServiceUrlParams;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.testing.ScriptTestSupport;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Multimap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

class UrlServiceScriptTest
    extends ScriptTestSupport
{
    PortalUrlService portalUrlService;

    private String buildQueryString( final Multimap<String, String> params )
    {
        if ( params == null || params.isEmpty() )
        {
            return "";
        }

        final String queryString = params.entries()
            .stream()
            .map( entry -> urlEncode( entry.getKey() ) + "=" + urlEncode( entry.getValue() ) )
            .collect( Collectors.joining( "&" ) );

        return "?" + queryString;
    }

    private String urlEncode( final String value )
    {
        try
        {
            return java.net.URLEncoder.encode( value, java.nio.charset.StandardCharsets.UTF_8 );
        }
        catch ( Exception e )
        {
            return value;
        }
    }

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();
        portalUrlService = Mockito.mock( PortalUrlService.class, this::urlAnswer );
        addService( PortalUrlService.class, portalUrlService );
    }

        // Configure mock to return proper URL format
        when( portalUrlService.assetUrl( any( AssetUrlParams.class ) ) ).thenAnswer(
            invocation -> {
                final AssetUrlParams params = invocation.getArgument( 0, AssetUrlParams.class );
                return "/site/mocksite/_/asset/" + params.getPath() + buildQueryString( params.getParams() );
            } );

        when( portalUrlService.attachmentUrl( any( AttachmentUrlParams.class ) ) ).thenAnswer(
            invocation -> {
                final AttachmentUrlParams params = invocation.getArgument( 0, AttachmentUrlParams.class );
                return "/site/mocksite/_/attachment/inline/mockid/" + params.getName() + buildQueryString( params.getParams() );
            } );

        when( portalUrlService.componentUrl( any( ComponentUrlParams.class ) ) ).thenAnswer(
            invocation -> {
                final ComponentUrlParams params = invocation.getArgument( 0, ComponentUrlParams.class );
                return "/site/mocksite/_/component/" + params.getComponent() + buildQueryString( params.getParams() );
            } );

        when( portalUrlService.imageUrl( any( ImageUrlParams.class ) ) ).thenAnswer(
            invocation -> {
                final ImageUrlParams params = invocation.getArgument( 0, ImageUrlParams.class );
                return "/site/mocksite/_/image/" + params.getId() + buildQueryString( params.getParams() );
            } );

        when( portalUrlService.pageUrl( any( PageUrlParams.class ) ) ).thenAnswer(
            invocation -> {
                final PageUrlParams params = invocation.getArgument( 0, PageUrlParams.class );
                final String path = params.getPath();
                final String normalizedPath = path != null && path.startsWith( "/" ) ? path.substring( 1 ) : path;
                return "/site/mocksite/" + normalizedPath + buildQueryString( params.getParams() );
            } );

        when( portalUrlService.serviceUrl( any( ServiceUrlParams.class ) ) ).thenAnswer(
            invocation -> {
                final ServiceUrlParams params = invocation.getArgument( 0, ServiceUrlParams.class );
                final String type = "websocket".equals( params.getType() ) ? "ws://" : "/site/mocksite/_/service/";
                return type + params.getService() + buildQueryString( params.getParams() );
            } );

        when( portalUrlService.processHtml( any( ProcessHtmlParams.class ) ) ).thenAnswer(
            invocation -> invocation.getArgument( 0, ProcessHtmlParams.class ).getValue() );

        when( portalUrlService.apiUrl( any( ApiUrlParams.class ) ) ).thenAnswer(
            invocation -> {
                final ApiUrlParams params = invocation.getArgument( 0, ApiUrlParams.class );
                final Multimap<String, String> queryParams = LinkedListMultimap.create();
                params.getQueryParams().forEach( queryParams::putAll );
                return "/site/mocksite/_/api/" + params.getDescriptorKey() + buildQueryString( queryParams );
            } );

        when( portalUrlService.baseUrl( any( BaseUrlParams.class ) ) ).thenAnswer(
            invocation -> "/site/mocksite" );

        when( portalUrlService.generateUrl( any( GenerateUrlParams.class ) ) ).thenAnswer(
            invocation -> {
                final GenerateUrlParams params = invocation.getArgument( 0, GenerateUrlParams.class );
                String pathStr;
                if ( params.getPath() != null )
                {
                    pathStr = params.getPath();
                }
                else if ( params.getPathSegments() != null && !params.getPathSegments().isEmpty() )
                {
                    pathStr = "/" + String.join( "/", params.getPathSegments() );
                }
                else
                {
                    pathStr = "";
                }
                final String normalizedPath = pathStr.startsWith( "/" ) ? pathStr.substring( 1 ) : pathStr;
                return "/site/mocksite/_/generated/" + normalizedPath + buildQueryString( params.getParams() );
            } );

        addService( PortalUrlService.class, this.portalUrlService );
    }

    private boolean execute( final String method )
    {
        final ScriptExports exports = runScript( "/test/url-test.js" );
        final ScriptValue value = exports.executeMethod( method );
        return value != null ? value.getValue( Boolean.class ) : false;
    }

    private void verifyAssetUrl( final String expectedPath )
    {
        final ArgumentCaptor<AssetUrlParams> captor = ArgumentCaptor.forClass( AssetUrlParams.class );
        verify( portalUrlService ).assetUrl( captor.capture() );
        assertEquals( expectedPath, captor.getValue().getPath() );
        assertNotNull( captor.getValue().getParams() );
    }

    private void verifyAttachmentUrl( final String expectedName )
    {
        final ArgumentCaptor<AttachmentUrlParams> captor = ArgumentCaptor.forClass( AttachmentUrlParams.class );
        verify( portalUrlService ).attachmentUrl( captor.capture() );
        assertEquals( expectedName, captor.getValue().getName() );
        assertNotNull( captor.getValue().getParams() );
    }

    private void verifyComponentUrl( final String expectedComponent )
    {
        final ArgumentCaptor<ComponentUrlParams> captor = ArgumentCaptor.forClass( ComponentUrlParams.class );
        verify( portalUrlService ).componentUrl( captor.capture() );
        assertEquals( expectedComponent, captor.getValue().getComponent() );
        assertNotNull( captor.getValue().getParams() );
    }

    private void verifyImageUrl( final String expectedId )
    {
        final ArgumentCaptor<ImageUrlParams> captor = ArgumentCaptor.forClass( ImageUrlParams.class );
        verify( portalUrlService ).imageUrl( captor.capture() );
        assertEquals( expectedId, captor.getValue().getId() );
    }

    private void verifyPageUrl( final String expectedPath )
    {
        final ArgumentCaptor<PageUrlParams> captor = ArgumentCaptor.forClass( PageUrlParams.class );
        verify( portalUrlService ).pageUrl( captor.capture() );
        assertEquals( expectedPath, captor.getValue().getPath() );
        assertNotNull( captor.getValue().getParams() );
    }

    private void verifyServiceUrl( final String expectedService )
    {
        final ArgumentCaptor<ServiceUrlParams> captor = ArgumentCaptor.forClass( ServiceUrlParams.class );
        verify( portalUrlService ).serviceUrl( captor.capture() );
        assertEquals( expectedService, captor.getValue().getService() );
    }

    private void verifyProcessHtml()
    {
        final ArgumentCaptor<ProcessHtmlParams> captor = ArgumentCaptor.forClass( ProcessHtmlParams.class );
        verify( portalUrlService ).processHtml( captor.capture() );
        assertNotNull( captor.getValue().getValue() );
    }

    @Test
    void assertUrlTest()
    {
        assertTrue( execute( "assetUrlTest" ) );
        verifyAssetUrl( "styles/my.css" );
    }

    @Test
    void assertUrlTest_unknownProperty()
    {
        assertTrue( execute( "assetUrlTest_unknownProperty" ) );
        verifyAssetUrl( "styles/my.css" );
    }

    @Test
    void assertUrlTest_invalidProperty()
    {
        assertTrue( execute( "assetUrlTest_invalidProperty" ) );
    }

    @Test
    void attachmentUrlTest()
    {
        assertTrue( execute( "attachmentUrlTest" ) );
        verifyAttachmentUrl( "myattachment.pdf" );
    }

    @Test
    void attachmentUrlTest_unknownProperty()
    {
        assertTrue( execute( "attachmentUrlTest_unknownProperty" ) );
        verifyAttachmentUrl( "myattachment.pdf" );
    }

    @Test
    void componentUrlTest()
    {
        assertTrue( execute( "componentUrlTest" ) );
        verifyComponentUrl( "mycomp" );
    }

    @Test
    void componentUrlTest_unknownProperty()
    {
        assertTrue( execute( "componentUrlTest_unknownProperty" ) );
        verifyComponentUrl( "mycomp" );
    }

    @Test
    void imageUrlTest()
    {
        assertTrue( execute( "imageUrlTest" ) );
        verifyImageUrl( "123" );
    }

    @Test
    void imageUrlTest_unknownProperty()
    {
        assertTrue( execute( "imageUrlTest_unknownProperty" ) );
        verifyImageUrl( "123" );
    }

    @Test
    void pageUrlTest()
    {
        assertTrue( execute( "pageUrlTest" ) );
        verifyPageUrl( "a/b" );
    }

    @Test
    void pageUrlTest_unknownProperty()
    {
        assertTrue( execute( "pageUrlTest_unknownProperty" ) );
        verifyPageUrl( "a/b" );
    }

    @Test
    void serviceUrlTest()
    {
        assertTrue( execute( "serviceUrlTest" ) );
        verifyServiceUrl( "myservice" );
    }

    @Test
    void serviceUrlWebSocketTest()
    {
        assertTrue( execute( "serviceUrlWebSocketTest" ) );
        verifyServiceUrl( "myservice" );
    }

    @Test
    void serviceUrlTest_unknownProperty()
    {
        assertTrue( execute( "serviceUrlTest_unknownProperty" ) );
        verifyServiceUrl( "myservice" );
    }

    @Test
    void processHtmlTest()
    {
        assertTrue( execute( "processHtmlTest" ) );
        verifyProcessHtml();
    }

    @Test
    void processHtmlTest_ignoreUnknownProperty()
    {
        assertTrue( execute( "processHtmlTest_ignoreUnknownProperty" ) );
        verifyProcessHtml();
    }

    @Test
    void processHtmlTest_imageUrlProcessing()
    {
        assertTrue( execute( "processHtmlImageUrlProcessingTest" ) );
        verifyProcessHtml();
    }

    @Test
    void imagePlaceholderTest()
    {
        assertTrue( execute( "imagePlaceholderTest" ) );
    }

    @Test
    void testExample_assetUrl()
    {
        runScript( "/lib/xp/examples/portal/assetUrl.js" );
    }

    @Test
    void testExample_imageUrl()
    {
        runScript( "/lib/xp/examples/portal/imageUrl.js" );
    }

    @Test
    void testExample_componentUrl()
    {
        runScript( "/lib/xp/examples/portal/componentUrl.js" );
    }

    @Test
    void testExample_attachmentUrl()
    {
        runScript( "/lib/xp/examples/portal/attachmentUrl.js" );
    }

    @Test
    void testExample_pageUrl()
    {
        runScript( "/lib/xp/examples/portal/pageUrl.js" );
    }

    @Test
    void testExample_serviceUrl()
    {
        runScript( "/lib/xp/examples/portal/serviceUrl.js" );
        ArgumentCaptor<ServiceUrlParams> captor = ArgumentCaptor.forClass( ServiceUrlParams.class );
        verify( portalUrlService ).serviceUrl( captor.capture() );
        final ServiceUrlParams params = captor.getValue();
        assertThat( params.getService() ).isEqualTo( "myservice" );
        assertThat( params.getParams().asMap() ).containsExactly( entry( "å", List.of( "a" ) ), entry( "ø", List.of( "o" ) ),
                                                                  entry( "æ", List.of( "a", "e" ) ), entry( "empty", List.of( "" ) ) );
    }

    @Test
    void testExample_generateUrl()
    {
        runScript( "/lib/xp/examples/portal/url.js" );
    }

    @Test
    void testExample_processHtml()
    {
        runScript( "/lib/xp/examples/portal/processHtml.js" );
    }

    @Test
    void testExample_imagePlaceholder()
    {
        runScript( "/lib/xp/examples/portal/imagePlaceholder.js" );
    }

    @Test
    void testExample_apiUrl()
    {
        runScript( "/lib/xp/examples/portal/apiUrl.js" );
        ArgumentCaptor<ApiUrlParams> captor = ArgumentCaptor.forClass( ApiUrlParams.class );
        verify( portalUrlService ).apiUrl( captor.capture() );
        final ApiUrlParams params = captor.getValue();
        assertEquals( "com.enonic.app.myapp:myapi", params.getApi().toString() );
        assertThat( params.getPathSegments() ).containsExactly( "segment1", "segment2" );
        assertThat( params.getBaseUrl() ).isEqualTo( "https://example.com" );
        assertThat( params.getQueryParams() ).containsExactly( entry( "å", List.of( "a" ) ), entry( "ø", List.of( "o" ) ),
                                                               entry( "æ", List.of( "a", "e" ) ), entry( "empty", List.of( "" ) ),
                                                               entry( "no-value", List.of() ) );

    }

    @Test
    void testExample_baseUrl()
    {
        runScript( "/lib/xp/examples/portal/baseUrl.js" );
    }
}
