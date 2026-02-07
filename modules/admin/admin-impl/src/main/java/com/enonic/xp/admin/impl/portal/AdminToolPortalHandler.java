package com.enonic.xp.admin.impl.portal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
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
    public static final String ADMIN_TOOL_BASE = "/admin";

    public static final String ADMIN_TOOL_PREFIX = ADMIN_TOOL_BASE + "/";

    public static final DescriptorKey DEFAULT_DESCRIPTOR_KEY = DescriptorKey.from( "com.enonic.xp.app.main:home" );

    public static final Pattern ADMIN_TOOL_PATH_PATTERN = Pattern.compile( "^/admin/(?<app>[^/]+)/(?<tool>[^/]+)" );

    @Override
    protected boolean canHandle( final WebRequest webRequest )
    {
        return !webRequest.getBasePath().startsWith( AdminSiteHandler.ADMIN_SITE_PREFIX ) &&
            ( webRequest.getBasePath().equals( ADMIN_TOOL_BASE ) || webRequest.getBasePath().startsWith( ADMIN_TOOL_PREFIX ) );
    }

    @Override
    protected PortalRequest createPortalRequest( final WebRequest webRequest, final WebResponse webResponse )
    {
        final PortalRequest portalRequest = new PortalRequest( webRequest );

        final DescriptorKey descriptorKey = getDescriptorKey( webRequest.getBasePath() );
        if ( descriptorKey == null || DEFAULT_DESCRIPTOR_KEY.equals( descriptorKey ) )
        {
            portalRequest.setBaseUri( ADMIN_TOOL_BASE );
            portalRequest.setApplicationKey( DEFAULT_DESCRIPTOR_KEY.getApplicationKey() );
        }
        else
        {
            portalRequest.setBaseUri( ADMIN_TOOL_BASE + "/" + descriptorKey.getApplicationKey() + "/" + descriptorKey.getName() );
            portalRequest.setApplicationKey( descriptorKey.getApplicationKey() );
        }
        return portalRequest;
    }

    public static DescriptorKey getDescriptorKey( final String path )
    {
        if ( path.equals( ADMIN_TOOL_BASE ) || path.equals( ADMIN_TOOL_PREFIX ) )
        {
            return DEFAULT_DESCRIPTOR_KEY;
        }
        else
        {
            final Matcher matcher = ADMIN_TOOL_PATH_PATTERN.matcher( path );
            if ( matcher.find() )
            {
                final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( "app" ) );
                final String adminToolName = matcher.group( "tool" );
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
