package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.impl.dispatch.mapping.FilterDefinition;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilterChainImplTest
{
    private ServletPipeline servletPipeline;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private FilterDefinition filter;

    @BeforeEach
    void setup()
    {
        this.servletPipeline = Mockito.mock( ServletPipeline.class );
        this.request = Mockito.mock( HttpServletRequest.class );
        this.response = Mockito.mock( HttpServletResponse.class );
        this.filter = Mockito.mock( FilterDefinition.class );

        Mockito.when( this.request.getServletPath() ).thenReturn( "" );
        Mockito.when( this.request.getPathInfo() ).thenReturn( "/a/b" );
    }

    private FilterChainImpl chain( final FilterDefinition... filters )
    {
        return new FilterChainImpl( List.of( filters ), this.servletPipeline );
    }

    @Test
    void doFilter()
        throws Exception
    {
        Mockito.when( this.filter.matches( "/a/b" ) ).thenReturn( true );

        final FilterChainImpl chain = chain( this.filter );
        chain.doFilter( this.request, this.response );

        Mockito.verify( this.filter, Mockito.times( 1 ) ).doFilter( this.request, this.response, chain );
        Mockito.verifyNoInteractions( this.servletPipeline );
    }

    @Test
    void doFilter_noMatch()
        throws Exception
    {
        final FilterChainImpl chain = chain( this.filter );
        chain.doFilter( this.request, this.response );

        Mockito.verify( this.filter, Mockito.never() ).doFilter( this.request, this.response, chain );
        Mockito.verify( this.servletPipeline, Mockito.times( 1 ) ).service( this.request, this.response );
    }

    @Test
    void doFilter_skipsToTheFirstMatch()
        throws Exception
    {
        final FilterDefinition skipped = Mockito.mock( FilterDefinition.class );
        Mockito.when( this.filter.matches( "/a/b" ) ).thenReturn( true );

        final FilterChainImpl chain = chain( skipped, this.filter );
        chain.doFilter( this.request, this.response );

        Mockito.verify( skipped, Mockito.never() ).doFilter( this.request, this.response, chain );
        Mockito.verify( this.filter, Mockito.times( 1 ) ).doFilter( this.request, this.response, chain );
    }

    @Test
    void doFilter_matchesTheDecodedPath()
        throws Exception
    {
        // the raw uri still carries what the container normalized away before it routed the request
        Mockito.when( this.request.getRequestURI() ).thenReturn( "/x/../a/b;jsessionid=1" );
        Mockito.when( this.filter.matches( "/a/b" ) ).thenReturn( true );

        final FilterChainImpl chain = chain( this.filter );
        chain.doFilter( this.request, this.response );

        Mockito.verify( this.filter, Mockito.times( 1 ) ).doFilter( this.request, this.response, chain );
        Mockito.verify( this.request, Mockito.never() ).getRequestURI();
    }

    @Test
    void doFilter_not_http()
    {
        final FilterChainImpl chain = chain( this.filter );

        assertThrows( ClassCastException.class,
                      () -> chain.doFilter( Mockito.mock( ServletRequest.class ), Mockito.mock( ServletResponse.class ) ) );
    }
}
