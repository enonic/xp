package com.enonic.xp.admin.impl.portal;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.PortalWebRequest;
import com.enonic.xp.portal.PortalWebResponse;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.PortalWebHandlerWorker;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.handler.WebRequest;
import com.enonic.xp.web.handler.WebResponse;

final class ToolWebHandlerWorker
    extends PortalWebHandlerWorker
{

    private final ControllerScriptFactory controllerScriptFactory;

    private final AdminToolDescriptorService adminToolDescriptorService;

    private final DescriptorKey descriptorKey;

    private ToolWebHandlerWorker( final Builder builder )
    {
        super( builder );
        controllerScriptFactory = builder.controllerScriptFactory;
        adminToolDescriptorService = builder.adminToolDescriptorService;
        descriptorKey = builder.descriptorKey;
    }

    public static Builder create()
    {
        return new Builder();
    }

    @Override
    public PortalWebResponse execute()
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

        final ResourceKey scriptDir = ResourceKey.from( descriptorKey.getApplicationKey(), "admin/tools/" + descriptorKey.getName() );
        final ControllerScript controllerScript = this.controllerScriptFactory.fromDir( scriptDir );
        final PortalWebRequest portalWebRequest = PortalWebRequest.create( this.webRequest ).
            applicationKey( descriptorKey.getApplicationKey() ).
            baseUri( ToolWebHandler.ADMIN_TOOL_PREFIX + descriptorKey.getApplicationKey() + "/" + descriptorKey.getName() ).
            build();

        final PortalRequest portalRequest = convertToPortalRequest( portalWebRequest );

        //Render the Admin application
        final PortalResponse portalResponse = PortalResponse.create( controllerScript.execute( portalRequest ) ).build();
        return convertToPortalWebResponse( portalResponse );
    }

    public static final class Builder
        extends PortalWebHandlerWorker.Builder<Builder, WebRequest, WebResponse>
    {
        private ControllerScriptFactory controllerScriptFactory;

        private AdminToolDescriptorService adminToolDescriptorService;

        private DescriptorKey descriptorKey;

        private Builder()
        {
        }

        public Builder controllerScriptFactory( final ControllerScriptFactory controllerScriptFactory )
        {
            this.controllerScriptFactory = controllerScriptFactory;
            return this;
        }

        public Builder adminToolDescriptorService( final AdminToolDescriptorService adminToolDescriptorService )
        {
            this.adminToolDescriptorService = adminToolDescriptorService;
            return this;
        }

        public Builder descriptorKey( final DescriptorKey descriptorKey )
        {
            this.descriptorKey = descriptorKey;
            return this;
        }

        public ToolWebHandlerWorker build()
        {
            return new ToolWebHandlerWorker( this );
        }
    }
}
