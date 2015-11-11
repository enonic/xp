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
import com.enonic.xp.testing.script.OldScriptTestSupport;

public class UrlServiceScriptTest
    extends OldScriptTestSupport
{
    @Before
    public void setUp()
    {
        setupRequest();
        addService( PortalUrlService.class, Mockito.mock( PortalUrlService.class, (Answer) this::urlAnswer ) );
    }

    private Object urlAnswer( final InvocationOnMock invocation )
        throws Exception
    {
        return invocation.getArguments()[0].toString();
    }

    private boolean execute( final String method )
        throws Exception
    {
        final ScriptExports exports = runTestScript( "test/url-test.js" );
        final ScriptValue value = exports.executeMethod( method );
        return value != null ? value.getValue( Boolean.class ) : false;
    }

    @Test
    public void assertUrlTest()
        throws Exception
    {
        Assert.assertTrue( execute( "assetUrlTest" ) );
    }

    @Test
    public void attachmentUrlTest()
        throws Exception
    {
        Assert.assertTrue( execute( "attachmentUrlTest" ) );
    }

    @Test
    public void componentUrlTest()
        throws Exception
    {
        Assert.assertTrue( execute( "componentUrlTest" ) );
    }

    @Test
    public void imageUrlTest()
        throws Exception
    {
        Assert.assertTrue( execute( "imageUrlTest" ) );
    }

    @Test
    public void pageUrlTest()
        throws Exception
    {
        Assert.assertTrue( execute( "pageUrlTest" ) );
    }

    @Test
    public void serviceUrlTest()
        throws Exception
    {
        Assert.assertTrue( execute( "serviceUrlTest" ) );
    }

    @Test
    public void processHtmlTest()
        throws Exception
    {
        Assert.assertTrue( execute( "processHtmlTest" ) );
    }
}
