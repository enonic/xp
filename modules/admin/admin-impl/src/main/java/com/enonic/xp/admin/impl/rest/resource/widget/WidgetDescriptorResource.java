package com.enonic.xp.admin.impl.rest.resource.widget;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.widget.json.WidgetDescriptorJson;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;

import static java.util.stream.Collectors.toList;

@Path(ResourceConstants.REST_ROOT + "widget")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true, property = "group=admin")
public class WidgetDescriptorResource
    implements JaxRsComponent
{
    private WidgetDescriptorService widgetDescriptorService;

    @POST
    @Path("list/byinterfaces")
    public List<WidgetDescriptorJson> getByInterfaces( final String[] widgetInterfaces )
    {
        final Descriptors<WidgetDescriptor> widgetDescriptors = this.widgetDescriptorService.getAllowedByInterfaces( widgetInterfaces );
        return widgetDescriptorsToJsonList( widgetDescriptors );
    }

    @Reference
    public void setWidgetDescriptorService( final WidgetDescriptorService widgetDescriptorService )
    {
        this.widgetDescriptorService = widgetDescriptorService;
    }

    private List<WidgetDescriptorJson> widgetDescriptorsToJsonList( final Descriptors<WidgetDescriptor> descriptors )
    {
        return descriptors.stream().map( WidgetDescriptorJson::new ).collect( toList() );
    }
}
