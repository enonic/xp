package com.enonic.xp.lib.portal.url;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;

import com.enonic.xp.portal.url.ApiUrlParams;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.portal.url.ServiceUrlParams;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.testing.ScriptTestSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

class UrlServiceScriptTest
    extends ScriptTestSupport
{
    PortalUrlService portalUrlService;

    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();
        portalUrlService = Mockito.mock( PortalUrlService.class, this::urlAnswer );
        addService( PortalUrlService.class, portalUrlService );
    }

    private Object urlAnswer( final InvocationOnMock invocation )
    {
        return invocation.getArgument( 0 ).toString();
    }

    private boolean execute( final String method )
    {
        final ScriptExports exports = runScript( "/test/url-test.js" );
        final ScriptValue value = exports.executeMethod( method );
        return value != null ? value.getValue( Boolean.class ) : false;
    }

    @Test
    void assertUrlTest()
    {
        assertTrue( execute( "assetUrlTest" ) );
    }

    @Test
    void assertUrlTest_unknownProperty()
    {
        assertTrue( execute( "assetUrlTest_unknownProperty" ) );
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
    }

    @Test
    void attachmentUrlTest_unknownProperty()
    {
        assertTrue( execute( "attachmentUrlTest_unknownProperty" ) );
    }

    @Test
    void componentUrlTest()
    {
        assertTrue( execute( "componentUrlTest" ) );
    }

    @Test
    void componentUrlTest_unknownProperty()
    {
        assertTrue( execute( "componentUrlTest_unknownProperty" ) );
    }

    @Test
    void imageUrlTest()
    {
        assertTrue( execute( "imageUrlTest" ) );
    }

    @Test
    void imageUrlTest_unknownProperty()
    {
        assertTrue( execute( "imageUrlTest_unknownProperty" ) );
    }

    @Test
    void pageUrlTest()
    {
        assertTrue( execute( "pageUrlTest" ) );
    }

    @Test
    void pageUrlTest_unknownProperty()
    {
        assertTrue( execute( "pageUrlTest_unknownProperty" ) );
    }

    @Test
    void serviceUrlTest()
    {
        assertTrue( execute( "serviceUrlTest" ) );
    }

    @Test
    void serviceUrlWebSocketTest()
    {
        assertTrue( execute( "serviceUrlWebSocketTest" ) );
    }

    @Test
    void serviceUrlTest_unknownProperty()
    {
        assertTrue( execute( "serviceUrlTest_unknownProperty" ) );
    }

    @Test
    void processHtmlTest()
    {
        assertTrue( execute( "processHtmlTest" ) );
    }

    @Test
    void processHtmlTest_ignoreUnknownProperty()
    {
        assertTrue( execute( "processHtmlTest_ignoreUnknownProperty" ) );
    }

    @Test
    void processHtmlTest_imageUrlProcessing()
    {
        assertTrue( execute( "processHtmlImageUrlProcessingTest" ) );
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
        assertThat( params.getDescriptorKey() ).asString().isEqualTo( "com.enonic.app.myapp:myapi" );
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
