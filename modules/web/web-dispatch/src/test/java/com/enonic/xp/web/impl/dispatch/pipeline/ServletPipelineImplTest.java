package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import jakarta.servlet.Servlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.dispatch.ServletMapping;
import com.enonic.xp.web.impl.dispatch.mapping.ServletDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ServletPipelineImplTest
    extends ResourcePipelineImplTest<ServletDefinition, ServletPipelineImpl>
{
    @WebServlet("/*")
    private static final class MyServlet
        extends HttpServlet
    {
    }

    @Override
    ServletPipelineImpl newPipeline()
    {
        return new ServletPipelineImpl( Map.of( DispatchConstants.CONNECTOR_PROPERTY, "xp" ) );
    }

    @Override
    ServletDefinition newDefinition()
    {
        final ServletDefinition def = mock( ServletDefinition.class );
        when( def.getResource() ).thenReturn( new MyServlet() );
        return def;
    }

    @Test
    void addRemove_servlet()
    {
        final MyServlet servlet = new MyServlet();

        assertThat( this.pipeline.list() ).isEmpty();
        this.pipeline.addServlet( servlet, Map.of() );
        assertThat( this.pipeline.list() ).hasSize( 1 );

        this.pipeline.removeServlet( servlet );
        assertThat( this.pipeline.list() ).isEmpty();
    }

    @Test
    void addRemove_mapping()
    {
        final ServletMapping mapping = mock( ServletMapping.class );
        when( mapping.getResource() ).thenReturn( mock( Servlet.class ) );
        when( mapping.getUrlPatterns() ).thenReturn( Set.of( "/*" ) );

        assertThat( this.pipeline.list() ).isEmpty();
        this.pipeline.addMapping( mapping );
        assertThat( this.pipeline.list() ).hasSize( 1 );

        this.pipeline.removeMapping( mapping );
        assertThat( this.pipeline.list() ).isEmpty();
    }

    @Test
    void testService()
        throws Exception
    {
        final ServletDefinition def1 = newDefinition();
        final ServletDefinition def2 = newDefinition();

        this.pipeline.add( def1 );
        this.pipeline.add( def2 );

        // the first definition that serves the path handles the request, the rest are left alone
        when( def2.matches( "/a/b" ) ).thenReturn( true );
        this.pipeline.service( this.request, this.response );

        verify( def1, never() ).service( this.request, this.response );
        verify( def2, times( 1 ) ).service( this.request, this.response );

        when( def1.matches( "/a/b" ) ).thenReturn( true );
        this.pipeline.service( this.request, this.response );

        verify( def1, times( 1 ) ).service( this.request, this.response );
        verify( def2, times( 1 ) ).service( this.request, this.response );
    }

    @Test
    void service_matchesTheDecodedPath()
        throws Exception
    {
        final ServletDefinition def = newDefinition();
        this.pipeline.add( def );

        // the raw uri still carries what the container normalized away before it routed the request
        when( this.request.getRequestURI() ).thenReturn( "/a/b;jsessionid=1" );
        when( def.matches( "/a/b" ) ).thenReturn( true );

        this.pipeline.service( this.request, this.response );

        verify( def, times( 1 ) ).service( this.request, this.response );
        verify( this.request, never() ).getRequestURI();
    }

    @Test
    void no_service()
        throws Exception
    {
        this.pipeline.service( this.request, this.response );
        verify( this.response ).sendError( HttpServletResponse.SC_SERVICE_UNAVAILABLE );
    }

    @Test
    void no_matching_service()
        throws Exception
    {
        this.pipeline.add( newDefinition() );

        this.pipeline.service( this.request, this.response );

        verify( this.response ).sendError( HttpServletResponse.SC_SERVICE_UNAVAILABLE );
    }
}
