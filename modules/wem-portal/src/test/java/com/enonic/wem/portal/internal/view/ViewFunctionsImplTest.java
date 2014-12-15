package com.enonic.wem.portal.internal.view;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import junit.framework.Assert;

import com.enonic.wem.api.content.Content;
import com.enonic.wem.api.content.ContentId;
import com.enonic.wem.api.module.ModuleKey;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.portal.PortalContextAccessor;
import com.enonic.wem.portal.PortalRequest;
import com.enonic.wem.portal.RenderMode;
import com.enonic.wem.portal.internal.controller.PortalContextImpl;
import com.enonic.wem.portal.view.ViewFunctions;
import com.enonic.wem.portal.view.ViewHelper;

public class ViewFunctionsImplTest
{
    private ViewFunctions functions;

    @Before
    public void setup()
    {
        final PortalRequest request = Mockito.mock( PortalRequest.class );
        Mockito.when( request.getBaseUri() ).thenReturn( "/root" );
        Mockito.when( request.getMode() ).thenReturn( RenderMode.LIVE );
        Mockito.when( request.getWorkspace() ).thenReturn( Workspace.from( "stage" ) );

        final PortalContextImpl context = new PortalContextImpl();
        context.setRequest( request );
        context.setModule( ModuleKey.from( "mymodule" ) );

        final Content content = Content.newContent().id( ContentId.from( "123" ) ).path( "some/path" ).build();
        context.setContent( content );

        PortalContextAccessor.set( context );
        this.functions = new ViewFunctionsImpl();
    }

    @Test
    public void testUrl()
    {
        final String str = this.functions.url( ViewHelper.toParamMap( "_path=a/b", "b=2" ) );
        Assert.assertEquals( "/root/portal/a/b?b=2", str );
    }

    @Test
    public void testAssetUrl()
    {
        final String str = this.functions.assetUrl( ViewHelper.toParamMap( "_path=a/b", "b=2" ) );
        Assert.assertEquals( "/root/portal/live/stage/some/path/_/public/mymodule/a/b?b=2", str );
    }

    @Test
    public void testImageUrl()
    {
        final String str = this.functions.imageUrl( ViewHelper.toParamMap( "_id=123", "b=2" ) );
        Assert.assertEquals( "/root/portal/live/stage/some/path/_/image/id/123?b=2", str );
    }

    @Test
    public void testAttachmentUrl()
    {
        final String str = this.functions.attachmentUrl( ViewHelper.toParamMap( "a=1", "b=2" ) );
        Assert.assertEquals( "/root/portal/live/stage/some/path?a=1&b=2", str );
    }

    @Test
    public void testPageUrl()
    {
        final String str = this.functions.pageUrl( ViewHelper.toParamMap( "a=1", "b=2" ) );
        Assert.assertEquals( "/root/portal/live/stage/some/path?a=1&b=2", str );
    }

    @Test
    public void testComponentUrl()
    {
        final String str = this.functions.componentUrl( ViewHelper.toParamMap( "_component=a", "b=2" ) );
        Assert.assertEquals( "/root/portal/live/stage/some/path/_/component/a?b=2", str );
    }

    @Test
    public void testServiceUrl()
    {
        final String str = this.functions.serviceUrl( ViewHelper.toParamMap( "_service=a", "b=2" ) );
        Assert.assertEquals( "/root/portal/live/stage/some/path/_/service/mymodule/a?b=2", str );
    }
}
