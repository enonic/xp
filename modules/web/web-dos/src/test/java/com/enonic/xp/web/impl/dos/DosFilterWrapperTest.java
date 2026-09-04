package com.enonic.xp.web.impl.dos;

import org.eclipse.jetty.ee11.servlets.DoSFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

class DosFilterWrapperTest
{
    private DosFilterConfig config;

    private Filter delegate;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private FilterChain filterChain;

    @BeforeEach
    void setup()
    {
        this.config = Mockito.mock( DosFilterConfig.class );
        this.delegate = Mockito.mock( Filter.class );
        this.request = Mockito.mock( HttpServletRequest.class );
        this.response = Mockito.mock( HttpServletResponse.class );
        this.filterChain = Mockito.mock( FilterChain.class );
    }

    @Test
    void delegate_notEnabled()
    {
        Mockito.when( this.config.enabled() ).thenReturn( false );

        assertNull( new DosFilterWrapper( this.config ).getDelegate() );
    }

    @Test
    void delegate_enabled()
    {
        Mockito.when( this.config.enabled() ).thenReturn( true );

        assertInstanceOf( DoSFilter.class, new DosFilterWrapper( this.config ).getDelegate() );
    }

    @Test
    void doFilter_notEnabled()
        throws Exception
    {
        final DosFilterWrapper filter = new DosFilterWrapper( this.config, null );

        filter.doFilter( this.request, this.response, this.filterChain );

        Mockito.verify( this.filterChain, Mockito.times( 1 ) ).doFilter( this.request, this.response );
    }

    @Test
    void doFilter_initializesDelegateOnce()
        throws Exception
    {
        Mockito.when( this.request.getServletContext() ).thenReturn( Mockito.mock( ServletContext.class ) );

        final DosFilterWrapper filter = new DosFilterWrapper( this.config, this.delegate );

        // the delegate needs a servlet context, which only a request can provide
        Mockito.verify( this.delegate, Mockito.never() ).init( Mockito.any() );

        filter.doFilter( this.request, this.response, this.filterChain );
        filter.doFilter( this.request, this.response, this.filterChain );

        Mockito.verify( this.delegate, Mockito.times( 1 ) ).init( Mockito.any() );
        Mockito.verify( this.delegate, Mockito.times( 2 ) ).doFilter( this.request, this.response, this.filterChain );
    }

    @Test
    void deactivate_destroysInitializedDelegate()
        throws Exception
    {
        Mockito.when( this.request.getServletContext() ).thenReturn( Mockito.mock( ServletContext.class ) );

        final DosFilterWrapper filter = new DosFilterWrapper( this.config, this.delegate );
        filter.doFilter( this.request, this.response, this.filterChain );

        filter.deactivate();

        Mockito.verify( this.delegate, Mockito.times( 1 ) ).destroy();
    }

    @Test
    void deactivate_withoutRequest()
    {
        // nothing initialized the delegate, so there is nothing to destroy
        new DosFilterWrapper( this.config, this.delegate ).deactivate();

        Mockito.verify( this.delegate, Mockito.never() ).destroy();
    }
}
