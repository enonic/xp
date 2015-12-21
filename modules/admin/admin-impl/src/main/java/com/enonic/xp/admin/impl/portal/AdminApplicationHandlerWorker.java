package com.enonic.xp.admin.impl.portal;

import com.enonic.xp.admin.adminapp.AdminApplicationDescriptor;
import com.enonic.xp.admin.adminapp.AdminApplicationDescriptorService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.ControllerHandlerWorker;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;

final class AdminApplicationHandlerWorker
    extends ControllerHandlerWorker
{

    protected ControllerScriptFactory controllerScriptFactory;

    protected AdminApplicationDescriptorService adminApplicationDescriptorService;

    protected DescriptorKey descriptorKey;

    @Override
    public void execute()
        throws Exception
    {
        //Retrieves the AdminApplicationDescriptor
        final AdminApplicationDescriptor adminApplicationDescriptor = adminApplicationDescriptorService.getByKey( descriptorKey );
        if ( adminApplicationDescriptor == null )
        {
            throw notFound( "Admin application [%s] not found", descriptorKey.toString() );
        }

        //Checks if the access to AdminApplicationDescriptor is allowed
        final PrincipalKeys principals = ContextAccessor.current().
            getAuthInfo().
            getPrincipals();
        if ( !adminApplicationDescriptor.isAccessAllowed( principals ) )
        {
            throw forbidden( "You don't have permission to access [%s]", descriptorKey.toString() );
        }

        //Render the Admin application
        this.request.setApplicationKey( descriptorKey.getApplicationKey() );
        final ResourceKey scriptDir = ResourceKey.from( descriptorKey.getApplicationKey(), "admin/apps/" + descriptorKey.getName() );
        final ControllerScript controllerScript = this.controllerScriptFactory.fromDir( scriptDir );
        this.response = PortalResponse.create( controllerScript.execute( this.request ) );
    }
}
