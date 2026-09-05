package com.enonic.xp.web.impl.dispatch.pipeline;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class ResourcePipelineImplTest<D extends ResourceDefinition<?>, P extends ResourcePipelineImpl<D>>
{
    P pipeline;

    HttpServletRequest request;

    HttpServletResponse response;

    @BeforeEach
    void setup()
    {
        this.pipeline = newPipeline();
        this.request = Mockito.mock( HttpServletRequest.class );
        this.response = Mockito.mock( HttpServletResponse.class );

        Mockito.lenient().when( this.request.getServletPath() ).thenReturn( "" );
        Mockito.lenient().when( this.request.getPathInfo() ).thenReturn( "/a/b" );
    }

    abstract P newPipeline();

    abstract D newDefinition();

    @Test
    void testAddNull()
    {
        this.pipeline.add( null );
        assertThat( this.pipeline.list() ).isEmpty();
    }

    @Test
    void testAddRemove()
    {
        final D def = newDefinition();

        this.pipeline.add( def );
        assertThat( this.pipeline.list() ).containsExactly( def );

        this.pipeline.remove( def.getResource() );
        assertThat( this.pipeline.list() ).isEmpty();
    }

    @Test
    void remove_unknownResource()
    {
        final D def = newDefinition();
        this.pipeline.add( def );

        this.pipeline.remove( newDefinition().getResource() );

        assertThat( this.pipeline.list() ).containsExactly( def );
    }

    @Test
    void remove_resourceAddedTwice()
    {
        final D def = newDefinition();

        this.pipeline.add( def );
        this.pipeline.add( def );
        assertThat( this.pipeline.list() ).hasSize( 2 );

        // both definitions of the resource go, a stale one would keep serving requests forever
        this.pipeline.remove( def.getResource() );
        assertThat( this.pipeline.list() ).isEmpty();
    }

    @Test
    void definitionsOfOtherConnectorsAreIgnored()
    {
        final D def = newDefinition();
        Mockito.when( def.getConnectors() ).thenReturn( List.of( "other" ) );

        this.pipeline.add( def );

        assertThat( this.pipeline.list() ).isEmpty();
    }
}
