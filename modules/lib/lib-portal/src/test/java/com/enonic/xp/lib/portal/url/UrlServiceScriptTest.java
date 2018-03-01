package com.enonic.xp.lib.portal.url;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.testing.ScriptTestSupport;

public class UrlServiceScriptTest
    extends ScriptTestSupport
{
    @Override
    protected void initialize()
        throws Exception
    {
        super.initialize();
        addService( PortalUrlService.class, Mockito.mock( PortalUrlService.class, (Answer) this::urlAnswer ) );
    }

    private Object urlAnswer( final InvocationOnMock invocation )
        throws Exception
    {
        return invocation.getArguments()[0].toString();
    }

    private boolean execute( final String method )
    {
        final ScriptExports exports = runScript( "/site/test/url-test.js" );
        final ScriptValue value = exports.executeMethod( method );
        return value != null ? value.getValue( Boolean.class ) : false;
    }

    @Test
    public void assertUrlTest()
    {
        Assert.assertTrue( execute( "assetUrlTest" ) );
    }

    @Test
    public void assertUrlTest_unknownProperty()
    {
        Assert.assertTrue( execute( "assetUrlTest_unknownProperty" ) );
    }

    @Test
    public void assertUrlTest_invalidProperty()
    {
        Assert.assertTrue( execute( "assetUrlTest_invalidProperty" ) );
    }

    @Test
    public void attachmentUrlTest()
    {
        Assert.assertTrue( execute( "attachmentUrlTest" ) );
    }

    @Test
    public void attachmentUrlTest_unknownProperty()
    {
        Assert.assertTrue( execute( "attachmentUrlTest_unknownProperty" ) );
    }

    @Test
    public void componentUrlTest()
    {
        Assert.assertTrue( execute( "componentUrlTest" ) );
    }

    @Test
    public void componentUrlTest_unknownProperty()
    {
        Assert.assertTrue( execute( "componentUrlTest_unknownProperty" ) );
    }

    @Test
    public void imageUrlTest()
    {
        Assert.assertTrue( execute( "imageUrlTest" ) );
    }

    @Test
    public void imageUrlTest_unknownProperty()
    {
        Assert.assertTrue( execute( "imageUrlTest_unknownProperty" ) );
    }

    @Test
    public void pageUrlTest()
    {
        Assert.assertTrue( execute( "pageUrlTest" ) );
    }

    @Test
    public void pageUrlTest_unknownProperty()
    {
        Assert.assertTrue( execute( "pageUrlTest_unknownProperty" ) );
    }

    @Test
    public void serviceUrlTest()
    {
        Assert.assertTrue( execute( "serviceUrlTest" ) );
    }

    @Test
    public void serviceUrlTest_unknownProperty()
    {
        Assert.assertTrue( execute( "serviceUrlTest_unknownProperty" ) );
    }

    @Test
    public void processHtmlTest()
    {
        Assert.assertTrue( execute( "processHtmlTest" ) );
    }

    @Test
    public void processHtmlTest_unknownProperty()
    {
        Assert.assertTrue( execute( "processHtmlTest_unknownProperty" ) );
    }

    @Test
    public void imagePlaceholderTest()
    {
        Assert.assertTrue( execute( "imagePlaceholderTest" ) );
    }

    @Test
    public void testExample_assetUrl()
    {
        runScript( "/site/lib/xp/examples/portal/assetUrl.js" );
    }

    @Test
    public void testExample_imageUrl()
    {
        runScript( "/site/lib/xp/examples/portal/imageUrl.js" );
    }

    @Test
    public void testExample_componentUrl()
    {
        runScript( "/site/lib/xp/examples/portal/componentUrl.js" );
    }

    @Test
    public void testExample_attachmentUrl()
    {
        runScript( "/site/lib/xp/examples/portal/attachmentUrl.js" );
    }

    @Test
    public void testExample_pageUrl()
    {
        runScript( "/site/lib/xp/examples/portal/pageUrl.js" );
    }

    @Test
    public void testExample_serviceUrl()
    {
        runScript( "/site/lib/xp/examples/portal/serviceUrl.js" );
    }

    @Test
    public void testExample_generateUrl()
    {
        runScript( "/site/lib/xp/examples/portal/url.js" );
    }

    @Test
    public void testExample_processHtml()
    {
        runScript( "/site/lib/xp/examples/portal/processHtml.js" );
    }

    @Test
    public void testExample_imagePlaceholder()
    {
        runScript( "/site/lib/xp/examples/portal/imagePlaceholder.js" );
    }
}
