package com.enonic.xp.portal.impl.postprocess.injection;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import com.enonic.xp.portal.PortalContext;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class LiveEditInjectionTest
{
    private PortalContext context;

    private LiveEditInjection injection;

    @Before
    public void setup()
    {
        this.context = new PortalContext();
        mockCurrentContextHttpRequest();

        this.injection = new LiveEditInjection();
    }

    @Test
    public void testNoInjection()
    {
        this.context.setMode( RenderMode.EDIT );

        final List<String> result1 = this.injection.inject( this.context, HtmlTag.HEAD_BEGIN );
        assertNull( result1 );

        final List<String> result2 = this.injection.inject( this.context, HtmlTag.BODY_BEGIN );
        assertNull( result2 );

        this.context.setMode( RenderMode.LIVE );

        final List<String> result3 = this.injection.inject( this.context, HtmlTag.BODY_END );
        assertNull( result3 );
    }

    @Test
    public void testInjectHeadEnd()
        throws Exception
    {
        this.context.setMode( RenderMode.EDIT );

        final String result = this.injection.inject( this.context, HtmlTag.HEAD_END ).get( 0 );
        assertNotNull( result );
        assertEquals( readResource( "liveEditInjectionHeadEnd.html" ).trim() + "\n", result );
    }

    @Test
    public void testInjectBodyEnd()
        throws Exception
    {
        this.context.setMode( RenderMode.EDIT );

        final String result = this.injection.inject( this.context, HtmlTag.BODY_END ).get( 0 );
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
