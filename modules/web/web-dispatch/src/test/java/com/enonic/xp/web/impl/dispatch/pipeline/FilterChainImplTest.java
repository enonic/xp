package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.List;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.enonic.xp.web.impl.dispatch.mapping.FilterDefinition;

public class FilterChainImplTest
{
    private ServletPipeline servletPipeline;

    private HttpServletRequest request;

    private HttpServletResponse response;

    private FilterDefinition filter;

    private FilterChainImpl chain;

    @BeforeEach
    public void setup()
    {
        this.servletPipeline = Mockito.mock( ServletPipeline.class );
        this.request = Mockito.mock( HttpServletRequest.class );
        this.response = Mockito.mock( HttpServletResponse.class );
        this.filter = Mockito.mock( FilterDefinition.class );
        this.chain = new FilterChainImpl( List.of( this.filter ), this.servletPipeline );
    }

    @Test
    public void doFilter()
        throws Exception
    {
        this.chain.doFilter( this.request, this.response );
        Mockito.verify( this.filter, Mockito.times( 1 ) ).doFilter( this.request, this.response, this.chain );
        Mockito.verify( this.servletPipeline, Mockito.times( 1 ) ).service( this.request, this.response );
    }

    @Test
    public void doFilter_not_http()
        throws Exception
    {
        this.chain.doFilter( Mockito.mock( ServletRequest.class ), Mockito.mock( ServletResponse.class ) );
    }
}
