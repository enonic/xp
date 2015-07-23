package com.enonic.xp.lib.xslt;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.content.Content;
import com.enonic.xp.content.ContentId;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;

public class UrlFunctionsTest
    extends AbstractFunctionTest
{
    @Before
    public void setUp()
        throws Exception
    {
        mockResource( "mymodule:/test/url/UrlFunctionsTest.xsl" );
    }

    @Test
    public void testAll()
        throws Exception
    {
        final PortalRequest portalRequest = new PortalRequest();
        portalRequest.setMode( RenderMode.LIVE );
        portalRequest.setBranch( Branch.from( "draft" ) );
        portalRequest.setApplicationKey( ApplicationKey.from( "mymodule" ) );
        portalRequest.setBaseUri( "/portal" );

        final Content content = Content.create().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        portalRequest.setContent( content );
        PortalRequestAccessor.set( portalRequest );

        processTemplate( "UrlFunctionsTest" );
    }
}
