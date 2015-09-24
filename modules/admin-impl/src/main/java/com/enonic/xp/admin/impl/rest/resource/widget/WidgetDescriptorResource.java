package com.enonic.xp.admin.impl.rest.resource.widget;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.AdminResource;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.widget.json.WidgetDescriptorsJson;
import com.enonic.xp.admin.impl.widget.WidgetDescriptorService;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "widget/descriptor")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public class WidgetDescriptorResource
    implements AdminResource
{

    private WidgetDescriptorService widgetDescriptorService;

    @GET
    @Path("byInterface")
    public WidgetDescriptorsJson getByInterface( @QueryParam("interface") final String widgetInterface )
    {
        return new WidgetDescriptorsJson( widgetDescriptorService.getByInterface( widgetInterface ) );
    }

    @Reference
    public void setWidgetDescriptorService( final WidgetDescriptorService widgetDescriptorService )
    {
        this.widgetDescriptorService = widgetDescriptorService;
    }
}
