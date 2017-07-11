package com.enonic.xp.app.contentstudio;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.i18n.MessageBundle;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.postprocess.HtmlTag;
import com.enonic.xp.web.servlet.ServletRequestHolder;

import static org.junit.Assert.*;

public class LiveEditInjectionTest
{
    private PortalRequest portalRequest;

    private PortalResponse portalResponse;

    private LocaleService localeService;

    private LiveEditInjection injection;

    @Before
    public void setup()
    {
        this.portalRequest = new PortalRequest();
        this.portalResponse = PortalResponse.create().build();
        this.localeService = Mockito.mock( LocaleService.class );
        mockCurrentContextHttpRequest();

        this.injection = new LiveEditInjection();
        this.injection.setLocaleService( localeService );
    }

    @Test
    public void testNoInjection()
    {
        this.portalRequest.setMode( RenderMode.EDIT );

        final List<String> result1 = this.injection.inject( this.portalRequest, this.portalResponse, HtmlTag.HEAD_BEGIN );
        assertNull( result1 );

        final List<String> result2 = this.injection.inject( this.portalRequest, this.portalResponse, HtmlTag.BODY_BEGIN );
        assertNull( result2 );

        this.portalRequest.setMode( RenderMode.LIVE );

        final List<String> result3 = this.injection.inject( this.portalRequest, this.portalResponse, HtmlTag.BODY_END );
        assertNull( result3 );
    }

    @Test
    public void testInjectHeadEnd()
        throws Exception
    {
        this.portalRequest.setMode( RenderMode.EDIT );

        final List<String> list = this.injection.inject( this.portalRequest, this.portalResponse, HtmlTag.HEAD_END );
        assertNotNull( list );

        final String result = list.get( 0 );
        assertNotNull( result );
        assertEquals( readResource( "liveEditInjectionHeadEnd.html" ).trim() + "\n", result );
    }

    @Test
    public void testInjectBodyEnd()
        throws Exception
    {
        this.portalRequest.setMode( RenderMode.EDIT );
        this.portalRequest.setRawRequest( ServletRequestHolder.getRequest() );
        Mockito.when( localeService.getBundle( Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any() ) ).thenReturn(
            generateMessageBundle() );

        final List<String> list = this.injection.inject( this.portalRequest, this.portalResponse, HtmlTag.BODY_END );
        assertNotNull( list );

        final String result = list.get( 0 );
        assertNotNull( result );
        assertEquals( readResource( "liveEditInjectionBodyEnd.html" ).trim() + "\n", result );
    }

    private void mockCurrentContextHttpRequest()
    {
        final HttpServletRequest req = Mockito.mock( HttpServletRequest.class );
        Mockito.when( req.getScheme() ).thenReturn( "http" );
        Mockito.when( req.getServerName() ).thenReturn( "localhost" );
        Mockito.when( req.getLocalPort() ).thenReturn( 80 );
        Mockito.when( req.getLocale() ).thenReturn( Locale.forLanguageTag( "no" ) );
        ServletRequestHolder.setRequest( req );
    }

    private MessageBundle generateMessageBundle()
    {
        return new MessageBundle()
        {
            @Override
            public Set<String> getKeys()
            {
                return null;
            }

            @Override
            public String localize( final String key, final Object... args )
            {
                return null;
            }

            @Override
            public Map<String, String> asMap()
            {
                return Maps.newHashMap();
            }
        };
    }

    private String readResource( final String resourceName )
        throws Exception
    {
        return Resources.toString( getClass().getResource( resourceName ), Charsets.UTF_8 );
    }
}
