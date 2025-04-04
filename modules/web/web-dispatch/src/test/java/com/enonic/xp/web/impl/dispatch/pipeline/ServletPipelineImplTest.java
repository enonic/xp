package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.Map;

import jakarta.servlet.Servlet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;

import com.enonic.xp.web.dispatch.DispatchConstants;
import com.enonic.xp.web.dispatch.ServletMapping;
import com.enonic.xp.web.impl.dispatch.mapping.ServletDefinition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ServletPipelineImplTest
    extends ResourcePipelineImplTest<ServletDefinition, ServletPipelineImpl>
{
    @WebServlet
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
    public void addRemove_servlet()
    {
        final MyServlet servlet = new MyServlet();

        assertThat( this.pipeline.list.snapshot() ).isEmpty();
        this.pipeline.addServlet( servlet, Map.of() );

        assertThat( this.pipeline.list.snapshot().size() ).isEqualTo( 1 );
        this.pipeline.removeServlet( servlet );
    }

    @Test
    public void addRemove_mapping()
    {
        final ServletMapping mapping = mock( ServletMapping.class );
        when( mapping.getResource() ).thenReturn( mock( Servlet.class ) );

        assertThat( this.pipeline.list.snapshot() ).isEmpty();
        this.pipeline.addMapping( mapping );

        assertThat( this.pipeline.list.snapshot().size() ).isEqualTo( 1 );
        this.pipeline.removeMapping( mapping );
    }

    @Test
    public void testService()
        throws Exception
    {
        final ServletDefinition def1 = newDefinition();
        final ServletDefinition def2 = newDefinition();

        this.pipeline.add( def1 );
        this.pipeline.add( def2 );

        this.pipeline.service( this.request, this.response );

        verify( def1, times( 1 ) ).service( this.request, this.response );
        verify( def2, times( 1 ) ).service( this.request, this.response );

        when( def1.service( this.request, this.response ) ).thenReturn( true );
        this.pipeline.service( this.request, this.response );

        verify( def1, times( 2 ) ).service( this.request, this.response );
        verify( def2, times( 1 ) ).service( this.request, this.response );
    }

    @Test
    void no_service()
        throws Exception
    {
        this.pipeline.service( this.request, this.response );
        verify( this.response ).sendError( HttpServletResponse.SC_SERVICE_UNAVAILABLE );
    }
}
