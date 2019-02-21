package com.enonic.xp.admin.impl.rest.resource.widget;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.widget.json.WidgetDescriptorJson;
import com.enonic.xp.admin.widget.WidgetDescriptor;
import com.enonic.xp.admin.widget.WidgetDescriptorService;
import com.enonic.xp.app.ApplicationDescriptor;
import com.enonic.xp.app.ApplicationDescriptorService;
import com.enonic.xp.app.ApplicationKey;
import com.enonic.xp.descriptor.Descriptors;
import com.enonic.xp.icon.Icon;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.page.DescriptorKey;
import com.enonic.xp.security.RoleKeys;

import static java.util.stream.Collectors.toList;

@Path(ResourceConstants.REST_ROOT + "widget")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed({RoleKeys.ADMIN_LOGIN_ID, RoleKeys.ADMIN_ID})
@Component(immediate = true, property = "group=admin")
public class WidgetResource
    implements JaxRsComponent
{
    private static final WidgetImageHelper HELPER = new WidgetImageHelper();

    private WidgetDescriptorService widgetDescriptorService;

    private ApplicationDescriptorService applicationDescriptorService;

    @POST
    @Path("list/byinterfaces")
    public List<WidgetDescriptorJson> getByInterfaces( final String[] widgetInterfaces )
    {
        final Descriptors<WidgetDescriptor> widgetDescriptors = this.widgetDescriptorService.getAllowedByInterfaces( widgetInterfaces );
        return widgetDescriptorsToJsonList( widgetDescriptors );
    }

    private List<WidgetDescriptorJson> widgetDescriptorsToJsonList( final Descriptors<WidgetDescriptor> descriptors )
    {
        return descriptors.stream().map( WidgetDescriptorJson::new ).collect( toList() );
    }

    @GET
    @Path("icon/{appKey}/{descriptorName}")
    @Produces("image/*")
    public Response getIcon( @PathParam("appKey") final String appKeyStr, @PathParam("descriptorName") final String descriptorName,
                             @QueryParam("hash") final String hash )
        throws Exception
    {
        final ApplicationKey appKey = ApplicationKey.from( appKeyStr );
        final DescriptorKey descriptorKey = DescriptorKey.from( appKey, descriptorName );
        final WidgetDescriptor widgetDescriptor = this.widgetDescriptorService.getByKey( descriptorKey );
        final Icon icon = widgetDescriptor == null ? null : widgetDescriptor.getIcon();

        final Response.ResponseBuilder responseBuilder;
        if ( icon == null )
        {
            final ApplicationDescriptor appDescriptor = this.applicationDescriptorService.get( appKey );
            final Icon appIcon = appDescriptor == null ? null : appDescriptor.getIcon();

            if ( appIcon == null )
            {
                final Icon defaultAppIcon = HELPER.getDefaultWidgetIcon();
                responseBuilder = Response.ok( defaultAppIcon.asInputStream(), defaultAppIcon.getMimeType() );
                applyMaxAge( responseBuilder );
            }
            else
            {
                responseBuilder = Response.ok( appIcon.toByteArray(), appIcon.getMimeType() );
                if ( StringUtils.isNotEmpty( hash ) )
                {
                    applyMaxAge( responseBuilder );
                }
            }
        }
        else
        {
            responseBuilder = Response.ok( icon.toByteArray(), icon.getMimeType() );
            if ( StringUtils.isNotEmpty( hash ) )
            {
                applyMaxAge( responseBuilder );
            }
        }

        return responseBuilder.build();
    }

    private void applyMaxAge( final Response.ResponseBuilder responseBuilder )
    {
        final CacheControl cacheControl = new CacheControl();
        cacheControl.setMaxAge( Integer.MAX_VALUE );
        responseBuilder.cacheControl( cacheControl );
    }

    @Reference
    public void setWidgetDescriptorService( final WidgetDescriptorService widgetDescriptorService )
    {
        this.widgetDescriptorService = widgetDescriptorService;
    }

    @Reference
    public void setApplicationDescriptorService( final ApplicationDescriptorService applicationDescriptorService )
    {
        this.applicationDescriptorService = applicationDescriptorService;
    }
}
