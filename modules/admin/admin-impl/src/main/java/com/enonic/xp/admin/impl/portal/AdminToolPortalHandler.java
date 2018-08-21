package com.enonic.xp.admin.impl.portal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.handler.BasePortalHandler;
import com.enonic.xp.web.WebRequest;
import com.enonic.xp.web.WebResponse;
import com.enonic.xp.web.exception.ExceptionMapper;
import com.enonic.xp.web.exception.ExceptionRenderer;
import com.enonic.xp.web.handler.WebHandler;

@Component(immediate = true, service = WebHandler.class)
public class AdminToolPortalHandler
    extends BasePortalHandler
{
    public final static String ADMIN_TOOL_START = "/admin/tool";

    public final static String ADMIN_TOOL_PREFIX = ADMIN_TOOL_START + "/";

    public final static DescriptorKey DEFAULT_DESCRIPTOR_KEY = DescriptorKey.from( "com.enonic.xp.app.main:home" );

    public final static Pattern PATTERN = Pattern.compile( "^([^/^_]+)/([^/^_]+)" );

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return webRequest.getRawPath().startsWith( ADMIN_TOOL_START );
    }

    @Override
    protected PortalRequest createPortalRequest( final WebRequest webRequest, final WebResponse webResponse )
    {
        final PortalRequest portalRequest = new PortalRequest( webRequest );

        final DescriptorKey descriptorKey = getDescriptorKey( webRequest );
        if ( descriptorKey == null )
        {
            portalRequest.setBaseUri( ADMIN_TOOL_PREFIX );
            portalRequest.setApplicationKey( DEFAULT_DESCRIPTOR_KEY.getApplicationKey() );
        }
        else
        {
            portalRequest.setBaseUri( ADMIN_TOOL_PREFIX + descriptorKey.getApplicationKey() + "/" + descriptorKey.getName() );
            portalRequest.setApplicationKey( descriptorKey.getApplicationKey() );
        }
        portalRequest.setMode( RenderMode.ADMIN );
        return portalRequest;
    }

    public static DescriptorKey getDescriptorKey( final WebRequest webRequest )
    {
        final String path = webRequest.getRawPath();
        if ( path.startsWith( ADMIN_TOOL_PREFIX ) )
        {
            final String subPath = path.substring( ADMIN_TOOL_PREFIX.length() );
            final Matcher matcher = PATTERN.matcher( subPath );
            if ( matcher.find() )
            {
                final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( 1 ) );
                final String adminToolName = matcher.group( 2 );
                return DescriptorKey.from( applicationKey, adminToolName );
            }
        }
        return null;
    }

    @Reference
    public void setWebExceptionMapper( final ExceptionMapper exceptionMapper )
    {
        this.exceptionMapper = exceptionMapper;
    }

    @Reference
    public void setExceptionRenderer( final ExceptionRenderer exceptionRenderer )
    {
        this.exceptionRenderer = exceptionRenderer;
    }
}