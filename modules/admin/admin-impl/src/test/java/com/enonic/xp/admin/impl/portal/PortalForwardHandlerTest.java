package com.enonic.xp.admin.impl.portal;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.portal.PortalAttributes;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.security.RoleKeys;

public class PortalForwardHandlerTest
{
    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    private PortalForwardHandler servlet;

    @Before
    public void setup()
    {
        this.req = new MockHttpServletRequest();
        this.res = new MockHttpServletResponse();
        this.servlet = new PortalForwardHandler();
    }

    @Test
    public void forward_forbidden()
        throws Exception
    {
        this.req.setRequestURI( "/admin/portal/edit/ws/a/b" );
        this.servlet.service( this.req, this.res );

        Assert.assertEquals( 403, this.res.getStatus() );
        Assert.assertEquals( null, this.res.getForwardedUrl() );
    }

    @Test
    public void forward_illegalMode()
        throws Exception
    {
        this.req.addUserRole( RoleKeys.ADMIN_LOGIN_ID );
        this.req.setRequestURI( "/admin/portal/mode/ws/a/b" );
        this.servlet.service( this.req, this.res );

        Assert.assertEquals( 404, this.res.getStatus() );
        Assert.assertEquals( null, this.res.getForwardedUrl() );
    }

    @Test
    public void forward_edit()
        throws Exception
    {
        this.req.addUserRole( RoleKeys.ADMIN_LOGIN_ID );
        this.req.setRequestURI( "/admin/portal/edit/ws/a/b" );
        this.servlet.service( this.req, this.res );

        Assert.assertEquals( "/portal/ws/a/b", this.res.getForwardedUrl() );

        final PortalAttributes portalAttributes = (PortalAttributes) this.req.getAttribute( PortalAttributes.class.getName() );
        Assert.assertNotNull( portalAttributes );
        Assert.assertEquals( "/admin/portal/edit", portalAttributes.getBaseUri() );
        Assert.assertEquals( RenderMode.EDIT, portalAttributes.getRenderMode() );
    }

    @Test
    public void forward_preview()
        throws Exception
    {
        this.req.addUserRole( RoleKeys.ADMIN_LOGIN_ID );
        this.req.setRequestURI( "/admin/portal/preview/ws/a/b" );
        this.servlet.service( this.req, this.res );

        Assert.assertEquals( "/portal/ws/a/b", this.res.getForwardedUrl() );

        final PortalAttributes portalAttributes = (PortalAttributes) this.req.getAttribute( PortalAttributes.class.getName() );
        Assert.assertNotNull( portalAttributes );
        Assert.assertEquals( "/admin/portal/preview", portalAttributes.getBaseUri() );
        Assert.assertEquals( RenderMode.PREVIEW, portalAttributes.getRenderMode() );
    }
}
