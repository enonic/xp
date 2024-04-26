package com.enonic.xp.admin.impl.portal;

import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.PortalHandlerWorker;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.web.WebException;

final class WidgetHandlerWorker
    extends PortalHandlerWorker<PortalRequest>
{
    ControllerScriptFactory controllerScriptFactory;

    WidgetDescriptorService widgetDescriptorService;

    DescriptorKey descriptorKey;

    WidgetHandlerWorker( final PortalRequest request )
    {
        super( request );
    }

    @Override
    public PortalResponse execute()
        throws Exception
    {
        if ( this.request.getMode() != RenderMode.ADMIN )
        {
            throw WebException.forbidden( "Render mode must be ADMIN." );
        }

        //Retrieves the WidgetDescriptor
        final WidgetDescriptor widgetDescriptor = widgetDescriptorService.getByKey( descriptorKey );
        if ( widgetDescriptor == null )
        {
            throw WebException.notFound( String.format( "Widget [%s] not found", descriptorKey ) );
        }

        //Checks if the access to WidgetDescriptor is allowed
        final PrincipalKeys principals = ContextAccessor.current().getAuthInfo().getPrincipals();
        if ( !widgetDescriptor.isAccessAllowed( principals ) )
        {
            throw WebException.forbidden( String.format( "You don't have permission to access [%s]", descriptorKey ) );
        }

        //Renders the widget
        this.request.setApplicationKey( this.descriptorKey.getApplicationKey() );

        final ResourceKey script = ResourceKey.from( descriptorKey.getApplicationKey(),
                                                     "admin/widgets/" + descriptorKey.getName() + "/" + descriptorKey.getName() +
                                                         ".js" );
        final ControllerScript controllerScript = this.controllerScriptFactory.fromScript( script );
        return controllerScript.execute( this.request );
    }
}
