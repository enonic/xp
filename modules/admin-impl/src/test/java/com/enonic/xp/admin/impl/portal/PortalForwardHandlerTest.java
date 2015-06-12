package com.enonic.xp.admin.impl.portal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalRequestAccessor;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.web.handler.WebHandlerChain;

public class PortalForwardHandlerTest
{
    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    private WebHandlerChain chain;

    private PortalForwardHandler handler;

    @Before
    public void setup()
    {
        this.req = new MockHttpServletRequest();
        this.res = new MockHttpServletResponse();
        this.handler = new PortalForwardHandler();
        this.chain = Mockito.mock( WebHandlerChain.class );
    }

    @Test
    public void notFound()
        throws Exception
    {
        this.req.setRequestURI( "/portal/edit/ws/a/b" );
        this.handler.handle( this.req, this.res, this.chain );

        Mockito.verify( this.chain, Mockito.times( 1 ) ).handle( this.req, this.res );
    }

    @Test
    public void forward_forbidden()
        throws Exception
    {
        this.req.setRequestURI( "/admin/portal/edit/ws/a/b" );
        this.handler.handle( this.req, this.res, this.chain );

        Assert.assertEquals( 403, this.res.getStatus() );
        Mockito.verify( this.chain, Mockito.times( 0 ) ).handle( this.req, this.res );
    }

    @Test
    public void forward_notFound()
        throws Exception
    {
        this.req.addUserRole( RoleKeys.ADMIN_LOGIN_ID );
        this.req.setRequestURI( "/admin/portal/mode/ws/a/b" );
        this.handler.handle( this.req, this.res, this.chain );

        Assert.assertEquals( 404, this.res.getStatus() );
        Mockito.verify( this.chain, Mockito.times( 0 ) ).handle( this.req, this.res );
    }

    @Test
    public void forward_edit()
        throws Exception
    {
        this.req.addUserRole( RoleKeys.ADMIN_LOGIN_ID );
        this.req.setRequestURI( "/admin/portal/edit/ws/a/b" );
        this.handler.handle( this.req, this.res, this.chain );

        Assert.assertEquals( "/portal/ws/a/b", this.res.getForwardedUrl() );
        Mockito.verify( this.chain, Mockito.times( 0 ) ).handle( this.req, this.res );

        final PortalRequest portalRequest = PortalRequestAccessor.get( this.req );
        Assert.assertNotNull( portalRequest );
        Assert.assertEquals( "/admin/portal/edit", portalRequest.getBaseUri() );
        Assert.assertEquals( RenderMode.EDIT, portalRequest.getMode() );
    }

    @Test
    public void forward_preview()
        throws Exception
    {
        this.req.addUserRole( RoleKeys.ADMIN_LOGIN_ID );
        this.req.setRequestURI( "/admin/portal/preview/ws/a/b" );
        this.handler.handle( this.req, this.res, this.chain );

        Assert.assertEquals( "/portal/ws/a/b", this.res.getForwardedUrl() );
        Mockito.verify( this.chain, Mockito.times( 0 ) ).handle( this.req, this.res );

        final PortalRequest portalRequest = PortalRequestAccessor.get( this.req );
        Assert.assertNotNull( portalRequest );
        Assert.assertEquals( "/admin/portal/preview", portalRequest.getBaseUri() );
        Assert.assertEquals( RenderMode.PREVIEW, portalRequest.getMode() );
    }
}
