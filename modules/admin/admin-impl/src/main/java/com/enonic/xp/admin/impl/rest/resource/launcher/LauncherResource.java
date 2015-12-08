package com.enonic.xp.admin.impl.rest.resource.launcher;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.admin.app.AdminApplicationDescriptor;
import com.enonic.xp.admin.app.AdminApplicationDescriptorService;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.launcher.json.AdminApplicationDescriptorJson;
import com.enonic.xp.admin.impl.rest.resource.launcher.json.AdminApplicationIconJson;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "launcher")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_ID)
@Component(immediate = true)
public class LauncherResource
    implements JaxRsComponent
{

    private AdminApplicationDescriptorService adminApplicationDescriptorService;

    @GET
    @Path("apps")
    public List<AdminApplicationDescriptorJson> getApps()
    {
        return adminApplicationDescriptorService.getAll().
            stream().
            map( this::toJson ).
            collect( Collectors.toList() );
    }

    private AdminApplicationDescriptorJson toJson( final AdminApplicationDescriptor adminApplicationDescriptor )
    {
        final AdminApplicationDescriptorJson jsonEntry = new AdminApplicationDescriptorJson();
        jsonEntry.key = adminApplicationDescriptor.getKeyString();
        jsonEntry.name = adminApplicationDescriptor.getName();
        jsonEntry.shortName = adminApplicationDescriptor.getShortName();
        jsonEntry.icon = adminApplicationDescriptor.getIcon();

        if ( adminApplicationDescriptor.getIconImage() != null )
        {
            jsonEntry.iconImage = new AdminApplicationIconJson();
            jsonEntry.iconImage.application = adminApplicationDescriptor.getIconImage().getApplicationKey().getName();
            jsonEntry.iconImage.path = adminApplicationDescriptor.getIconImage().getPath();
        }

        final ImmutableSet.Builder<String> allowedPrincipals = ImmutableSet.builder();

        adminApplicationDescriptor.getAllowedPrincipals().
            stream().
            map( PrincipalKey::toString ).
            forEach( allowedPrincipals::add );
        jsonEntry.allowedPrincipals = allowedPrincipals.build();
        return jsonEntry;
    }

    @Reference
    public void setAdminApplicationDescriptorService( final AdminApplicationDescriptorService adminApplicationDescriptorService )
    {
        this.adminApplicationDescriptorService = adminApplicationDescriptorService;
    }
}
