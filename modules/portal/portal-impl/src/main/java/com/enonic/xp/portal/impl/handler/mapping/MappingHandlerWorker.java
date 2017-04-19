package com.enonic.xp.portal.impl.handler.mapping;

import com.enonic.xp.content.Content;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.portal.impl.rendering.Renderer;
import com.enonic.xp.portal.impl.rendering.RendererFactory;
import com.enonic.xp.resource.Resource;
import com.enonic.xp.resource.ResourceService;
import com.enonic.xp.site.mapping.ControllerMappingDescriptor;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;

final class MappingHandlerWorker
    extends PortalHandlerWorker<PortalRequest>
{
    protected ResourceService resourceService;

    protected ControllerScriptFactory controllerScriptFactory;

    protected ControllerMappingDescriptor mappingDescriptor;

    protected RendererFactory rendererFactory;

    public MappingHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        final ControllerScript controllerScript = getScript();

        this.request.setApplicationKey( mappingDescriptor.getApplication() );

        final Trace trace = Tracer.current();
        if ( trace != null )
        {
            trace.put( "contentPath", this.request.getContent().getPath().toString() );
            trace.put( "type", "mapping" );
        }

        if ( this.request.getContent().hasPage() )
        {
            return renderPage( controllerScript );
        }
        else
        {
            return renderController( controllerScript );
        }
    }

    private PortalResponse renderPage( final ControllerScript controllerScript )
    {
        this.request.setControllerScript( controllerScript );

        final Content content = this.request.getContent();
        final Renderer<Content> renderer = this.rendererFactory.getRenderer( content );
        return renderer.render( content, this.request );
    }

    private PortalResponse renderController( final ControllerScript controllerScript )
    {
        return controllerScript.execute( this.request );
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

}
