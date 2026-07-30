package com.enonic.xp.admin.impl.portal;

import com.enonic.xp.admin.impl.portal.extension.AdminExtensionResponseProcessorExecutor;
import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.descriptor.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.postprocess.PostProcessor;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.WebException;

final class AdminToolHandlerWorker
{
    private final PortalRequest request;

    ControllerScriptFactory controllerScriptFactory;

    AdminToolDescriptorService adminToolDescriptorService;

    AdminExtensionResponseProcessorExecutor extensionResponseProcessorExecutor;

    PostProcessor postProcessor;

    DescriptorKey descriptorKey;

    AdminToolHandlerWorker( final PortalRequest request )
    {
        this.request = request;
    }

    PortalResponse execute()
    {
        //Retrieves the AdminToolDescriptor
        final AdminToolDescriptor adminToolDescriptor = adminToolDescriptorService.getByKey( descriptorKey );
        if ( adminToolDescriptor == null )
        {
            throw WebException.notFound( String.format( "Admin application [%s] not found", descriptorKey ) );
        }

        //Checks if the access to AdminToolDescriptor is allowed
        final PrincipalKeys principals = ContextAccessor.current().
            getAuthInfo().
            getPrincipals();
        if ( !adminToolDescriptor.isAccessAllowed( principals ) )
        {
            throw WebException.forbidden( String.format( "You don't have permission to access [%s]", descriptorKey ) );
        }

        //Renders the Admin application
        final ResourceKey script = ResourceKey.from( descriptorKey.getApplicationKey(),
                                                     "admin/tools/" + descriptorKey.getName() + "/" + descriptorKey.getName() + ".js" );
        final ControllerScript controllerScript = this.controllerScriptFactory.fromScript( script );
        PortalResponse response = controllerScript.execute( this.request );

        //Lets extensions mounted to the admin tool process the response (adjust CSP, add page contributions)
        response = extensionResponseProcessorExecutor.execute( adminToolDescriptor, this.request, response );

        if ( response.hasContributions() )
        {
            response = postProcessor.processResponseContributions( this.request, response );
        }
        return response;
    }
}
