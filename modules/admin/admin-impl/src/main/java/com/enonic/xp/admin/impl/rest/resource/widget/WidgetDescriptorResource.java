package com.enonic.xp.admin.impl.rest.resource.widget;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.widget.json.WidgetDescriptorJson;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.admin.widget.WidgetDescriptors;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;

import static java.util.stream.Collectors.toList;

@Path(ResourceConstants.REST_ROOT + "widget")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public class WidgetDescriptorResource
    implements JaxRsComponent
{

    private WidgetDescriptorService widgetDescriptorService;

    @GET
    public List<WidgetDescriptorJson> getByInterface( @QueryParam("interface") final String widgetInterface )
    {
        return widgetDescriptorsToJsonList( widgetDescriptorService.getByInterface( widgetInterface ) );
    }

    @Reference
    public void setWidgetDescriptorService( final WidgetDescriptorService widgetDescriptorService )
    {
        this.widgetDescriptorService = widgetDescriptorService;
    }

    private List<WidgetDescriptorJson> widgetDescriptorsToJsonList( final WidgetDescriptors widgetDescriptors )
    {
        return widgetDescriptors.stream().map( this::mapWidgetDescriptorToJson ).collect( toList() );
    }

    private WidgetDescriptorJson mapWidgetDescriptorToJson( final WidgetDescriptor widgetDescriptor )
    {
        final WidgetDescriptorJson jsonEntry = new WidgetDescriptorJson();
        jsonEntry.key = widgetDescriptor.getKeyString();
        jsonEntry.displayName = widgetDescriptor.getDisplayName();
        jsonEntry.url = widgetDescriptor.getUrl();
        jsonEntry.interfaces = ImmutableSet.copyOf( widgetDescriptor.getInterfaces() );
        return jsonEntry;
    }
}
