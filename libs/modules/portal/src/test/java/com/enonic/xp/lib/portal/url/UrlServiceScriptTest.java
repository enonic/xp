package com.enonic.xp.lib.portal.url;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.script.ScriptExports;
import com.enonic.xp.portal.script.ScriptValue;
import com.enonic.xp.portal.url.PortalUrlService;
import com.enonic.xp.testing.script.ScriptTestSupport;

public class UrlServiceScriptTest
    extends ScriptTestSupport
{
    @Before
    public void setUp()
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setBranch( Branch.from( "draft" ) );
        portalRequest.setModule( ModuleKey.from( "mymodule" ) );
        portalRequest.setBaseUri( "/portal" );

        final Content content = Content.newContent().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        portalRequest.setContent( content );
        PortalRequestAccessor.set( portalRequest );

        final UrlServiceWrapper service = new UrlServiceWrapper();
        service.setUrlService( Mockito.mock( PortalUrlService.class, (Answer) this::urlAnswer ) );

        addBean( "com.enonic.xp.lib.portal.url.UrlServiceWrapper", service );
        addBean( "com.enonic.xp.lib.portal.current.PortalServiceWrapper", new Object() );
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
