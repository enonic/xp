package com.enonic.xp.web.session.impl;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import org.apache.ignite.Ignite;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.User;
import com.enonic.xp.security.auth.AuthenticationInfo;

public class WebSessionFilterTest
{
    private WebSessionConfig config;

    private MockHttpServletRequest req;

    private MockHttpServletResponse res;

    private FilterChain chain;

    private org.apache.ignite.cache.websession.WebSessionFilter igniteFilter;

    private FilterConfig filterConfig;

    private ServletContext servletContext;

    @Before
    public void setup()
    {
        this.req = Mockito.spy( new MockHttpServletRequest() );
        this.res = new MockHttpServletResponse();
        this.chain = Mockito.mock( FilterChain.class );

        this.servletContext = Mockito.mock( ServletContext.class );
        this.filterConfig = Mockito.mock( FilterConfig.class );
        Mockito.when( this.filterConfig.getServletContext() ).
            thenReturn( this.servletContext );

        this.config = Mockito.mock( WebSessionConfig.class );
        //Mockito.when( this.config. ).thenReturn( this.mappings );

        this.igniteFilter = Mockito.mock( org.apache.ignite.cache.websession.WebSessionFilter.class );
    }


    @Test
    public void name()
        throws Exception
    {
        final IgniteConfiguration igniteConfig = new IgniteConfiguration();
        igniteConfig.setIgniteInstanceName( "fisk" );

        final Ignite ignite = Ignition.start( igniteConfig );

        Mockito.when( this.config.write_sync_mode() ).thenReturn( "full" );
        Mockito.when( this.config.cache_mode() ).thenReturn( "replicated" );

        final WebSessionFilter filter = new WebSessionFilter();
        filter.setIgnite( ignite );
        filter.activate( this.config );
        filter.init( this.filterConfig );

        this.req.setAttribute( "someAttribute", AuthenticationInfo.create().
            principals( PrincipalKey.ofAnonymous() ).
            user( User.ANONYMOUS ).
            build() );

        filter.doHandle( this.req, this.res, this.chain );


    }
}