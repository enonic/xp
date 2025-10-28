package com.enonic.xp.portal.impl.rendering;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.google.common.net.MediaType;

import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.impl.postprocess.PostProcessorImpl;
import com.enonic.xp.portal.impl.postprocess.TestPostProcessInjection;
import com.enonic.xp.portal.impl.processor.ProcessorChainResolver;
import com.enonic.xp.portal.script.PortalScriptService;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.site.processor.ResponseProcessorDescriptors;
import com.enonic.xp.web.HttpStatus;
import com.enonic.xp.web.websocket.WebSocketEvent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ControllerMappingRendererTest
{
    private PortalRequest portalRequest;

    private ControllerMappingRenderer renderer;

    ControllerScriptFactory controllerScriptFactory;

    ProcessorChainResolver processorChainResolver;

    PortalScriptService portalScriptService;

    PostProcessorImpl postProcessor;

    @BeforeEach
    void before()
    {

        final PostProcessorImpl processorImpl = new PostProcessorImpl();
        postProcessor = Mockito.spy( processorImpl );
        postProcessor.addInjection( new TestPostProcessInjection() );
        controllerScriptFactory = mock( ControllerScriptFactory.class );
        processorChainResolver = mock( ProcessorChainResolver.class );
        portalScriptService = mock( PortalScriptService.class );

        renderer = new ControllerMappingRenderer( postProcessor, portalScriptService, processorChainResolver );

        this.portalRequest = new PortalRequest();
        this.portalRequest.setControllerScript( makeControllerScript() );

        when( processorChainResolver.resolve( this.portalRequest ) ).thenReturn( ResponseProcessorDescriptors.empty() );
    }

    @Test
    void pageContributionsAndResponseProcessorsAreIgnoredFortServiceMappings()
    {
        // setup
        final ControllerMappingDescriptor mappingDescriptor = ControllerMappingDescriptor.create()
            .controller( ResourceKey.from( "com.enonic.app.myapp:/site/foobar/component_override.js" ) )
            .service( "anyService/Component/Image/Attachment" )
            .build();

        // exercise
        final PortalResponse portalResponse = renderer.render( mappingDescriptor, portalRequest );

        // verify
        assertEquals( getComponentHtml(), portalResponse.getBody() );
        Mockito.verify( processorChainResolver, Mockito.times( 0 ) ).resolve( Mockito.any( PortalRequest.class) );
        Mockito.verify( postProcessor, Mockito.times( 0 ) )
            .processResponseContributions( Mockito.any( PortalRequest.class), Mockito.any( PortalResponse.class ) );
    }

    @Test
    void pageContributionsAndResponseProcessorsAreNotIgnored()
    {
        // setup
        final ControllerMappingDescriptor mappingDescriptor = ControllerMappingDescriptor.create()
            .controller( ResourceKey.from( "com.enonic.app.myapp:/site/foobar/component_override.js" ) )
            .build();

        // exercise
        final PortalResponse portalResponse = renderer.render( mappingDescriptor, portalRequest );

        // verify
        assertEquals( getComponentHtml(), portalResponse.getBody() );
        Mockito.verify( processorChainResolver, Mockito.times( 1 ) ).resolve( Mockito.any( PortalRequest.class) );
        Mockito.verify( postProcessor, Mockito.times( 1 ) )
            .processResponseContributions( Mockito.any( PortalRequest.class), Mockito.any( PortalResponse.class ) );
    }

    private ControllerScript makeControllerScript()
    {
        return new ControllerScript()
        {
            @Override
            public PortalResponse execute( final PortalRequest portalRequest )
            {
                return PortalResponse.create()
                    .body(getComponentHtml() )
                    .contentType( MediaType.HTML_UTF_8 )
                    .status( HttpStatus.OK )
                    .build();
            }

            @Override
            public void onSocketEvent( final WebSocketEvent event )
            {
            }
        };
    }

    private String getComponentHtml()
    {
        return "<div class=\"row\"><div data-portal-region=\"left\" class=\"col-left\"></div><div data-portal-region=\"right\" class=\"col-right\"></div></div>";
    }
}
