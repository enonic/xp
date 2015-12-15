package com.enonic.xp.admin.impl.rest.resource.adminapp;

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

import com.enonic.xp.admin.adminapp.AdminApplicationDescriptor;
import com.enonic.xp.admin.adminapp.AdminApplicationDescriptorService;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.adminapp.json.AdminApplicationJson;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "adminapp")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public class AdminApplicationResource
    implements JaxRsComponent
{

    private AdminApplicationDescriptorService adminApplicationDescriptorService;

    @GET
    @Path("list")
    public List<AdminApplicationJson> getAllowedAdminApplicationDescriptors()
    {
        final PrincipalKeys principals = ContextAccessor.current().
            getAuthInfo().
            getPrincipals();
        return adminApplicationDescriptorService.getAllowedAdminApplicationDescriptors( principals ).
            stream().
            map( this::mapAdminApplicationDescriptorToJson ).
            collect( Collectors.toList() );
    }

    private AdminApplicationJson mapAdminApplicationDescriptorToJson( final AdminApplicationDescriptor adminApplicationDescriptor )
    {
        final AdminApplicationJson jsonEntry = new AdminApplicationJson();
        jsonEntry.key = adminApplicationDescriptor.getKey().toString();
        jsonEntry.displayName = adminApplicationDescriptor.getDisplayName();
        jsonEntry.icon = adminApplicationDescriptor.getIcon();
        final ImmutableSet.Builder<String> allowedPrincipals = ImmutableSet.builder();
        for ( PrincipalKey principalKey : adminApplicationDescriptor.getAllowedPrincipals() )
        {
            allowedPrincipals.add( principalKey.toString() );
        }
        jsonEntry.allow = allowedPrincipals.build();
        return jsonEntry;
    }

    @Reference
    public void setAdminApplicationDescriptorService( final AdminApplicationDescriptorService adminApplicationDescriptorService )
    {
        this.adminApplicationDescriptorService = adminApplicationDescriptorService;
    }
}
