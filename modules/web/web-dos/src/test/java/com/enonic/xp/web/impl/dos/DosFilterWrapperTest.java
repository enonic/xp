package com.enonic.xp.web.impl.dos;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;

public class DosFilterWrapperTest
{
    private DosFilterWrapper filter;

    private DosFilterConfig config;

    private FilterConfig filterConfig;

    private ServletRequest servletRequest;

    private ServletResponse servletResponse;

    private FilterChain filterChain;

    @Before
    public void setup()
    {
        this.filter = new DosFilterWrapper();
        this.config = Mockito.mock( DosFilterConfig.class );
        this.filterConfig = Mockito.mock( FilterConfig.class );
        this.servletRequest = Mockito.mock( ServletRequest.class );
        this.servletResponse = Mockito.mock( ServletResponse.class );
        this.filterChain = Mockito.mock( FilterChain.class );
    }

    @Test
    public void testNotEnabled()
        throws Exception
    {
        Mockito.when( this.config.enabled() ).thenReturn( false );
        this.filter.activate( this.config );

        this.filter.init( this.filterConfig );
        assertNull( this.filter.delegate );

        this.filter.doFilter( this.servletRequest, this.servletResponse, this.filterChain );
        Mockito.verify( this.filterChain, Mockito.times( 1 ) ).doFilter( this.servletRequest, this.servletResponse );

        this.filter.destroy();
        assertNull( this.filter.delegate );
    }

    @Test
    public void testEnabled()
        throws Exception
    {
        Mockito.when( this.config.enabled() ).thenReturn( true );
        this.filter.activate( this.config );

        this.filter.init( this.filterConfig );
        assertNotNull( this.filter.delegate );

        final Filter delegate = Mockito.mock( Filter.class );
        this.filter.delegate = delegate;

        this.filter.doFilter( this.servletRequest, this.servletResponse, this.filterChain );
        Mockito.verify( delegate, Mockito.times( 1 ) ).doFilter( this.servletRequest, this.servletResponse, this.filterChain );

        this.filter.destroy();
        Mockito.verify( delegate, Mockito.times( 1 ) ).destroy();
        assertNull( this.filter.delegate );
    }
}
