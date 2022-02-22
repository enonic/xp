package com.enonic.xp.portal.impl.handler.render;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.content.ContentService;
import com.enonic.xp.page.PageTemplateService;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.portal.impl.ContentResolver;
import com.enonic.xp.portal.impl.rendering.RendererDelegate;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.region.ComponentPath;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class ComponentHandler
    extends EndpointHandler
{
    private final RendererDelegate rendererDelegate;

    private final PageTemplateService pageTemplateService;

    private final PostProcessor postProcessor;

    private final ContentService contentService;

    @Activate
    public ComponentHandler( @Reference final ContentService contentService, @Reference final RendererDelegate rendererDelegate,
                             @Reference final PageTemplateService pageTemplateService, @Reference final PostProcessor postProcessor )
    {
        super( "component" );
        this.contentService = contentService;
        this.rendererDelegate = rendererDelegate;
        this.pageTemplateService = pageTemplateService;
        this.postProcessor = postProcessor;
    }

    @Override
    public boolean canHandle( final WebRequest webRequest )
    {
        return super.canHandle( webRequest ) && isSiteBase( webRequest );
    }

    @Override
    protected PortalResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        WebHandlerHelper.checkAdminAccess( webRequest );

        final String restPath = findRestPath( webRequest );

        final ComponentHandlerWorker worker = new ComponentHandlerWorker( (PortalRequest) webRequest );
        worker.componentPath = ComponentPath.from( restPath );
        worker.contentService = contentService;
        worker.contentResolver = new ContentResolver( contentService );
        worker.rendererDelegate = rendererDelegate;
        worker.pageResolver = new PageResolver( pageTemplateService );
        worker.postProcessor = postProcessor;
        final Trace trace = Tracer.newTrace( "renderComponent" );
        if ( trace == null )
        {
            return worker.execute();
        }
        return Tracer.traceEx( trace, worker::execute );
    }
}
