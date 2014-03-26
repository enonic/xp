package com.enonic.wem.portal.postprocess.injection;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.sun.jersey.api.core.HttpRequestContext;

import com.enonic.wem.api.rendering.RenderingMode;
import com.enonic.wem.portal.controller.JsContext;
import com.enonic.wem.portal.controller.JsHttpRequest;
import com.enonic.wem.portal.script.lib.PortalUrlScriptBean;
import com.enonic.wem.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class LiveEditInjectionTest
{
    private JsContext context;

    private LiveEditInjection injection;

    @Before
    public void setup()
    {
        final HttpRequestContext rawReq = Mockito.mock( HttpRequestContext.class );
        final JsHttpRequest req = new JsHttpRequest( rawReq );

        this.context = new JsContext();
        this.context.setRequest( req );

        mockCurrentContextHttpRequest();

        final PortalUrlScriptBean urlBean = new PortalUrlScriptBean();
        this.context.setPortalUrlScriptBean( urlBean );

        this.injection = new LiveEditInjection();
    }

    @Test
    public void testNoInjection()
    {
        this.context.getRequest().setMode( RenderingMode.EDIT );

        final String result1 = this.injection.inject( this.context, PostProcessInjection.Tag.HEAD_BEGIN );
        assertNull( result1 );

        final String result2 = this.injection.inject( this.context, PostProcessInjection.Tag.BODY_BEGIN );
        assertNull( result2 );

        this.context.getRequest().setMode( RenderingMode.LIVE );

        final String result3 = this.injection.inject( this.context, PostProcessInjection.Tag.BODY_END );
        assertNull( result3 );
    }

    @Test
    public void testInjectHeadEnd()
        throws Exception
    {
        this.context.getRequest().setMode( RenderingMode.EDIT );

        final String result = this.injection.inject( this.context, PostProcessInjection.Tag.HEAD_END );
        assertNotNull( result );
        assertEquals( readResource( "liveEditInjectionHeadEnd.html" ).trim() + "\n", result );
    }

    @Test
    public void testInjectBodyEnd()
        throws Exception
    {
        this.context.getRequest().setMode( RenderingMode.EDIT);

        final String result = this.injection.inject( this.context, PostProcessInjection.Tag.BODY_END );
        assertNotNull( result );
        assertEquals( readResource( "liveEditInjectionBodyEnd.html" ).trim() + "\n", result );
    }

    private void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        ServletRequestHolder.setRequest( req );
    }

    private String readResource( final String resourceName )
        throws Exception
    {
        return Resources.toString( getClass().getResource( resourceName ), Charsets.UTF_8 );
    }
}
