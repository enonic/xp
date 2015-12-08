package com.enonic.xp.lib.portal.url;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.script.ScriptExports;
import com.enonic.xp.script.ScriptValue;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class UrlServiceScriptTest
    extends ScriptTestSupport
{
    @Before
    public void setUp()
    {
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
    public void attachmentUrlTest()
    {
        Assert.assertTrue( execute( "attachmentUrlTest" ) );
    }

    @Test
    public void componentUrlTest()
    {
        Assert.assertTrue( execute( "componentUrlTest" ) );
    }

    @Test
    public void imageUrlTest()
    {
        Assert.assertTrue( execute( "imageUrlTest" ) );
    }

    @Test
    public void pageUrlTest()
    {
        Assert.assertTrue( execute( "pageUrlTest" ) );
    }

    @Test
    public void serviceUrlTest()
    {
        Assert.assertTrue( execute( "serviceUrlTest" ) );
    }

    @Test
    public void processHtmlTest()
    {
        Assert.assertTrue( execute( "processHtmlTest" ) );
    }

    @Test
    public void testExample_assetUrl()
    {
        runScript( "/site/lib/xp/examples/assetUrl.js" );
    }

    @Test
    public void testExample_imageUrl()
    {
        runScript( "/site/lib/xp/examples/imageUrl.js" );
    }

    @Test
    public void testExample_componentUrl()
    {
        runScript( "/site/lib/xp/examples/componentUrl.js" );
    }

    @Test
    public void testExample_attachmentUrl()
    {
        runScript( "/site/lib/xp/examples/attachmentUrl.js" );
    }

    @Test
    public void testExample_pageUrl()
    {
        runScript( "/site/lib/xp/examples/pageUrl.js" );
    }

    @Test
    public void testExample_serviceUrl()
    {
        runScript( "/site/lib/xp/examples/serviceUrl.js" );
    }

    @Test
    public void testExample_processHtml()
    {
        runScript( "/site/lib/xp/examples/processHtml.js" );
    }
}
