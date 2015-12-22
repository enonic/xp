package com.enonic.xp.admin.impl.portal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.adminapp.AdminApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.EndpointHandler;
import com.enonic.xp.portal.handler.PortalHandler;
import com.enonic.xp.portal.handler.PortalHandlerWorker;

@Component(immediate = true, service = PortalHandler.class)
public final class AdminApplicationHandler
    extends EndpointHandler
{
    private final static Pattern PATTERN = Pattern.compile( "([^/]+)/([^/]+)" );

    private final static DescriptorKey DEFAULT_DESCRIPTOR_KEY = DescriptorKey.from( "com.enonic.xp.admin.ui:app-launcher" );

    private AdminApplicationDescriptorService adminApplicationDescriptorService;

    private ControllerScriptFactory controllerScriptFactory;

    public AdminApplicationHandler()
    {
        super( "adminapp" );
    }

    @Override
    protected PortalHandlerWorker newWorker( final PortalRequest req )
        throws Exception
    {
        final String restPath = findRestPath( req );
        final Matcher matcher = PATTERN.matcher( restPath );

        final DescriptorKey descriptorKey;
        if ( matcher.find() )
        {
            final ApplicationKey applicationKey = ApplicationKey.from( matcher.group( 1 ) );
            final String adminApplicationName = matcher.group( 2 );
            descriptorKey = DescriptorKey.from( applicationKey, adminApplicationName );
        }
        else
        {
            descriptorKey = DEFAULT_DESCRIPTOR_KEY;
        }

        final AdminApplicationHandlerWorker worker = new AdminApplicationHandlerWorker();
        worker.controllerScriptFactory = this.controllerScriptFactory;
        worker.adminApplicationDescriptorService = adminApplicationDescriptorService;
        worker.descriptorKey = descriptorKey;
        return worker;
    }

    @Reference
    public void setAdminApplicationDescriptorService( final AdminApplicationDescriptorService adminApplicationDescriptorService )
    {
        this.adminApplicationDescriptorService = adminApplicationDescriptorService;
    }

    @Reference
    public void setControllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
    {
        this.controllerScriptFactory = controllerScriptFactory;
    }
}
