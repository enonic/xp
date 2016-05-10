package com.enonic.xp.portal.impl.handler.mapping;

import com.enonic.xp.content.Content;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.PortalWebResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.portal.rendering.Renderer;
import com.enonic.xp.portal.rendering.RendererFactory;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.web.handler.WebResponse;

final class MappingHandlerWorker
    extends PortalHandlerWorker<PortalWebRequest, WebResponse>
{
    private final ResourceService resourceService;

    private final ControllerScriptFactory controllerScriptFactory;

    private final ControllerMappingDescriptor mappingDescriptor;

    private final RendererFactory rendererFactory;

    private MappingHandlerWorker( final Builder builder )
    {
        super( builder );
        resourceService = builder.resourceService;
        controllerScriptFactory = builder.controllerScriptFactory;
        mappingDescriptor = builder.mappingDescriptor;
        rendererFactory = builder.rendererFactory;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public WebResponse execute()
    {
        final ControllerScript controllerScript = getScript();

        final PortalWebRequest.Builder portalWebRequestBuilder = PortalWebRequest.create( webRequest ).
            applicationKey( mappingDescriptor.getApplication() );

        if ( this.webRequest.getContent().hasPage() )
        {
            return renderPage( portalWebRequestBuilder, controllerScript );
        }
        else
        {
            return renderController( portalWebRequestBuilder, controllerScript );
        }
    }

    private ControllerScript getScript()
    {
        final Resource resource = this.resourceService.getResource( mappingDescriptor.getController() );
        if ( !resource.exists() )
        {
            throw notFound( "Controller [%s] not found", mappingDescriptor.getController().toString() );
        }
        return this.controllerScriptFactory.fromScript( resource.getKey() );
    }

    private PortalWebResponse renderPage( final PortalWebRequest.Builder portalWebRequestBuilder, final ControllerScript controllerScript )
    {
        final PortalWebRequest portalWebRequest = portalWebRequestBuilder.controllerScript( controllerScript ).build();
        final PortalRequest portalRequest = PortalWebRequest.convertToPortalRequest( portalWebRequest );

        final Content content = this.webRequest.getContent();
        final Renderer<Content> renderer = this.rendererFactory.getRenderer( content );
        final PortalResponse response = renderer.render( content, portalRequest );
        final PortalResponse portalResponse = PortalResponse.create( response ).build();
        return PortalWebResponse.convertToPortalWebResponse( portalResponse );
    }

    private PortalWebResponse renderController( final PortalWebRequest.Builder portalWebRequestBuilder,
                                                final ControllerScript controllerScript )
    {
        final PortalWebRequest portalWebRequest = portalWebRequestBuilder.controllerScript( controllerScript ).build();
        final PortalRequest portalRequest = PortalWebRequest.convertToPortalRequest( portalWebRequest );

        final PortalResponse portalResponse = PortalResponse.create( controllerScript.execute( portalRequest ) ).build();
        return PortalWebResponse.convertToPortalWebResponse( portalResponse );
    }

    public static final class Builder
        extends PortalHandlerWorker.Builder<Builder, PortalWebRequest, WebResponse>
    {
        private ResourceService resourceService;

        private ControllerScriptFactory controllerScriptFactory;

        private ControllerMappingDescriptor mappingDescriptor;

        private RendererFactory rendererFactory;

        private Builder()
        {
        }

        public Builder resourceService( final ResourceService resourceService )
        {
            this.resourceService = resourceService;
            return this;
        }

        public Builder controllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
        {
            this.controllerScriptFactory = controllerScriptFactory;
            return this;
        }

        public Builder mappingDescriptor( final ControllerMappingDescriptor mappingDescriptor )
        {
            this.mappingDescriptor = mappingDescriptor;
            return this;
        }

        public Builder rendererFactory( final RendererFactory rendererFactory )
        {
            this.rendererFactory = rendererFactory;
            return this;
        }

        public MappingHandlerWorker build()
        {
            return new MappingHandlerWorker( this );
        }
    }
}
