package com.enonic.xp.portal.impl.rendering;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.impl.processor.ProcessorChainResolver;
import com.enonic.xp.portal.impl.processor.ResponseProcessorExecutor;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.site.processor.ResponseProcessorDescriptor;
import com.enonic.xp.site.processor.ResponseProcessorDescriptors;
import com.enonic.xp.trace.TestTrace;
import com.enonic.xp.trace.Tracer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PostProcessingRendererTest
{
    private PostProcessor postProcessor;

    private ResponseProcessorExecutor processorExecutor;

    private ProcessorChainResolver processorChainResolver;

    private PortalRequest portalRequest;

    private PostProcessingRenderer<Object> renderer;

    @BeforeEach
    void setup()
    {
        this.postProcessor = mock( PostProcessor.class );
        this.processorExecutor = mock( ResponseProcessorExecutor.class );
        this.processorChainResolver = mock( ProcessorChainResolver.class );

        this.portalRequest = new PortalRequest();

        this.renderer = new PostProcessingRenderer<>( postProcessor, processorExecutor, processorChainResolver )
        {
            @Override
            public Class<Object> getType()
            {
                return Object.class;
            }

            @Override
            protected PortalResponse doRender( final Object component, final PortalRequest portalRequest )
            {
                return PortalResponse.create().body( "rendered" ).build();
            }
        };

        when( this.postProcessor.processResponseInstructions( same( this.portalRequest ), any( PortalResponse.class ) ) ).thenAnswer(
            invocation -> invocation.getArgument( 1 ) );
        when( this.postProcessor.processResponseContributions( same( this.portalRequest ), any( PortalResponse.class ) ) ).thenAnswer(
            invocation -> invocation.getArgument( 1 ) );
    }

    @Test
    void executeProcessor_recordsTraceAttributes()
    {
        final ResponseProcessorDescriptor filter =
            ResponseProcessorDescriptor.create().application( ApplicationKey.from( "myapplication" ) ).name( "filter1" ).build();

        when( this.processorChainResolver.resolve( this.portalRequest ) ).thenReturn( ResponseProcessorDescriptors.from( filter ) );
        when( this.processorExecutor.execute( same( filter ), same( this.portalRequest ), any( PortalResponse.class ) ) ).thenAnswer(
            invocation -> invocation.getArgument( 2 ) );

        // outside OSGi the @Traced wrapper is inert; a manually bound trace exercises the attribute enrichment code
        final TestTrace trace = TestTrace.of( "renderFilter" );
        final PortalResponse response = Tracer.trace( trace, () -> this.renderer.render( new Object(), this.portalRequest ) );

        assertEquals( "rendered", response.getBody() );
        verify( this.processorExecutor ).execute( same( filter ), same( this.portalRequest ), any( PortalResponse.class ) );
        assertEquals( "myapplication", trace.get( "app" ) );
        assertEquals( "filter1", trace.get( "name" ) );
        assertEquals( "filter", trace.get( "type" ) );
    }
}
