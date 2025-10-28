package com.enonic.xp.web.impl.dispatch.pipeline;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.enonic.xp.web.impl.dispatch.mapping.ResourceDefinition;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class ResourcePipelineImplTest<D extends ResourceDefinition<?>, P extends ResourcePipelineImpl<D>>
{
    P pipeline;

    private ServletContext context;

    HttpServletRequest request;

    HttpServletResponse response;

    @BeforeEach
    void setup()
    {
        this.pipeline = newPipeline();
        this.context = Mockito.mock( ServletContext.class );
        this.request = Mockito.mock( HttpServletRequest.class );
        this.response = Mockito.mock( HttpServletResponse.class );
    }

    abstract P newPipeline();

    abstract D newDefinition();

    @Test
    void testAddNull()
    {
        this.pipeline.add( null );
        assertThat( this.pipeline.list.snapshot() ).isEmpty();
    }

    @Test
    void testInit()
        throws Exception
    {
        final D def1 = newDefinition();
        this.pipeline.add( def1 );
        Mockito.verify( def1, Mockito.times( 0 ) ).init( Mockito.any() );

        this.pipeline.init( this.context );
        Mockito.verify( def1, Mockito.times( 1 ) ).init( this.context );

        final D def2 = newDefinition();
        this.pipeline.add( def2 );
        Mockito.verify( def2, Mockito.times( 1 ) ).init( this.context );
    }

    @Test
    void testDestroy()
    {
        final D def = newDefinition();
        this.pipeline.add( def );

        this.pipeline.destroy();
        Mockito.verify( def, Mockito.times( 1 ) ).destroy();
    }
}
