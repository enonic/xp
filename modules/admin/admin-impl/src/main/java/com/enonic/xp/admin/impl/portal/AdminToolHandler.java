package com.enonic.xp.admin.impl.portal;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.trace.Traced;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.WebException;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class AdminToolHandler
    extends BaseWebHandler
{
    private AdminToolDescriptorService adminToolDescriptorService;

    private ControllerScriptFactory controllerScriptFactory;

    public AdminToolHandler()
    {
        super( 50 );
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest instanceof PortalRequest portalRequest && portalRequest.getMode() == null &&
            ( AdminToolPortalHandler.ADMIN_TOOL_BASE.equals( portalRequest.getBaseUri() ) ||
                portalRequest.getBaseUri().startsWith( AdminToolPortalHandler.ADMIN_TOOL_PREFIX ) );
    }

    @Override
    @Traced("portalRequest")
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        Tracer.withCurrent( trace -> {
            trace.attribute( "path", webRequest.getPath() );
            trace.attribute( "method", webRequest.getMethod().toString() );
            trace.attribute( "host", webRequest.getHost() );
        } );

        WebHandlerHelper.checkAdminLoginRole( webRequest );

        final DescriptorKey descriptorKey = AdminToolPortalHandler.getDescriptorKey( webRequest.getBasePath() );
        if ( descriptorKey == null )
        {
            throw WebException.notFound( "Invalid admin tool mount" );
        }
        PortalRequest portalRequest = (PortalRequest) webRequest;
        portalRequest.setContextPath( portalRequest.getBaseUri() );

        final AdminToolHandlerWorker worker = new AdminToolHandlerWorker( portalRequest );
        worker.controllerScriptFactory = this.controllerScriptFactory;
        worker.adminToolDescriptorService = adminToolDescriptorService;
        worker.descriptorKey = descriptorKey;

        final PortalResponse response = worker.execute();
        Tracer.withCurrent( trace -> addTraceInfo( trace, response ) );
        return response;
    }

    @Reference
    public void setAdminToolDescriptorService( final AdminToolDescriptorService adminToolDescriptorService )
    {
        this.adminToolDescriptorService = adminToolDescriptorService;
    }

    @Reference
    public void setControllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
    {
        this.controllerScriptFactory = controllerScriptFactory;
    }
}
