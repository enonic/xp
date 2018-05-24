package com.enonic.xp.admin.impl.portal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.WebHandlerHelper;
import com.enonic.xp.trace.Trace;
import com.enonic.xp.trace.Tracer;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.handler.BaseWebHandler;
import com.enonic.xp.web.handler.WebHandler;
import com.enonic.xp.web.handler.WebHandlerChain;

@Component(immediate = true, service = WebHandler.class)
public final class AdminToolHandler
    extends BaseWebHandler
{
    final static String ADMIN_TOOL_START = "/admin/tool";

    final static String ADMIN_TOOL_PREFIX = ADMIN_TOOL_START + "/";

    private final static Pattern PATTERN = Pattern.compile( "([^/]+)/([^/]+)" );

    private final static DescriptorKey DEFAULT_DESCRIPTOR_KEY = DescriptorKey.from( "com.enonic.xp.app.main:home" );

    private AdminToolDescriptorService adminToolDescriptorService;

    private ControllerScriptFactory controllerScriptFactory;

    public AdminToolHandler()
    {
        super( 50 );
    }

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getRawPath().startsWith( ADMIN_TOOL_START );
    }

    @Override
    protected WebResponse doHandle( final WebRequest webRequest, final WebResponse webResponse, final WebHandlerChain webHandlerChain )
        throws Exception
    {
        WebHandlerHelper.checkAdminAccess( webRequest );

        final String path = webRequest.getRawPath();

        final String subPath = path.length() > ADMIN_TOOL_PREFIX.length() ? path.substring( ADMIN_TOOL_PREFIX.length() ) : "";
        final Matcher matcher = PATTERN.matcher( subPath );

        final DescriptorKey descriptorKey;
        if ( matcher.find() )
        {
            final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( 1 ) );
            final String adminToolName = matcher.group( 2 );
            descriptorKey = DescriptorKey.from( applicationKey, adminToolName );
        }
        else
        {
            descriptorKey = DEFAULT_DESCRIPTOR_KEY;
        }

        final PortalRequest portalRequest =
            webRequest instanceof PortalRequest ? (PortalRequest) webRequest : new PortalRequest( webRequest );

        final AdminToolHandlerWorker worker = new AdminToolHandlerWorker( portalRequest );
        worker.controllerScriptFactory = this.controllerScriptFactory;
        worker.adminToolDescriptorService = adminToolDescriptorService;
        worker.descriptorKey = descriptorKey;

        final Trace trace = Tracer.newTrace( "portalRequest" );
        if ( trace == null )
        {
            return worker.execute();
        }

        trace.put( "path", webRequest.getPath() );
        trace.put( "method", webRequest.getMethod().toString() );
        trace.put( "host", webRequest.getHost() );
        trace.put( "httpRequest", webRequest );
        trace.put( "httpResponse", webResponse );
        trace.put( "context", ContextAccessor.current() );

        return Tracer.traceEx( trace, () -> {
            final PortalResponse response = worker.execute();
            addTraceInfo( trace, response );
            return response;
        } );
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
