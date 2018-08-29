package com.enonic.xp.admin.impl.portal;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.ControllerHandlerWorker;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;

final class AdminToolHandlerWorker
    extends ControllerHandlerWorker
{

    protected ControllerScriptFactory controllerScriptFactory;

    protected AdminToolDescriptorService adminToolDescriptorService;

    protected DescriptorKey descriptorKey;

    public AdminToolHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        //Retrieves the AdminToolDescriptor
        final AdminToolDescriptor adminToolDescriptor = adminToolDescriptorService.getByKey( descriptorKey );
        if ( adminToolDescriptor == null )
        {
            throw notFound( "Admin application [%s] not found", descriptorKey.toString() );
        }

        //Checks if the access to AdminToolDescriptor is allowed
        final PrincipalKeys principals = ContextAccessor.current().
            getAuthInfo().
            getPrincipals();
        if ( !adminToolDescriptor.isAccessAllowed( principals ) )
        {
            throw forbidden( "You don't have permission to access [%s]", descriptorKey.toString() );
        }

        //Renders the Admin application
        final ResourceKey scriptDir = ResourceKey.from( descriptorKey.getApplicationKey(), "admin/tools/" + descriptorKey.getName() );
        final ControllerScript controllerScript = this.controllerScriptFactory.fromDir( scriptDir );
        return controllerScript.execute( this.request );
    }
}
