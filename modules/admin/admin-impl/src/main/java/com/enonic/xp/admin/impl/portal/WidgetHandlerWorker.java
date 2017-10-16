package com.enonic.xp.admin.impl.portal;

import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.content.Content;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.portal.PortalRequest;
import com.enonic.xp.portal.PortalResponse;
import com.enonic.xp.portal.RenderMode;
import com.enonic.xp.portal.controller.ControllerScript;
import com.enonic.xp.portal.controller.ControllerScriptFactory;
import com.enonic.xp.portal.handler.ControllerHandlerWorker;
import com.enonic.xp.resource.ResourceKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.site.Site;
import com.enonic.xp.web.WebException;

final class WidgetHandlerWorker
    extends ControllerHandlerWorker
{
    protected ControllerScriptFactory controllerScriptFactory;

    protected WidgetDescriptorService widgetDescriptorService;

    protected DescriptorKey descriptorKey;

    public WidgetHandlerWorker( final PortalRequest request )
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
            throw notFound( "Widget [%s] not found", descriptorKey.toString() );
        }

        //Checks if the access to WidgetDescriptor is allowed
        final PrincipalKeys principals = ContextAccessor.current().
            getAuthInfo().
            getPrincipals();
        if ( !widgetDescriptor.isAccessAllowed( principals ) )
        {
            throw forbidden( "You don't have permission to access [%s]", descriptorKey.toString() );
        }

        //Renders the widget
        this.request.setApplicationKey( this.descriptorKey.getApplicationKey() );
        final Content content = getContentOrNull( getContentSelector() );
        this.request.setContent( content );
        final Site site = getSiteOrNull( content );
        this.request.setSite( site );
        final ResourceKey scriptDir = ResourceKey.from( descriptorKey.getApplicationKey(), "admin/widgets/" + descriptorKey.getName() );
        final ControllerScript controllerScript = this.controllerScriptFactory.fromDir( scriptDir );
        return controllerScript.execute( this.request );
    }
}
