package com.enonic.xp.admin.impl.rest.resource.tool;

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

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.tool.json.AdminToolJson;
import com.enonic.xp.admin.impl.rest.resource.tool.json.AdminToolKeyJson;
import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.RoleKeys;

@Path(ResourceConstants.REST_ROOT + "tool")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true, property = "group=admin")
public class AdminToolResource
    implements JaxRsComponent
{

    private AdminToolDescriptorService adminToolDescriptorService;

    @GET
    @Path("list")
    public List<AdminToolJson> getAllowedAdminToolDescriptors()
    {
        final PrincipalKeys principals = ContextAccessor.current().
            getAuthInfo().
            getPrincipals();
        final boolean isAdmin = principals.contains( RoleKeys.ADMIN );
        return adminToolDescriptorService.getAllowedAdminToolDescriptors( principals ).
            stream().
            map( adminToolDescriptor -> mapAdminToolDescriptorToJson( adminToolDescriptor, isAdmin ) ).
            collect( Collectors.toList() );
    }

    private AdminToolJson mapAdminToolDescriptorToJson( final AdminToolDescriptor adminToolDescriptor, final boolean mapAllowedPrincipals )
    {
        final AdminToolJson jsonEntry = new AdminToolJson();
        jsonEntry.key = new AdminToolKeyJson();
        jsonEntry.key.application = adminToolDescriptor.getKey().getApplicationKey().getName();
        jsonEntry.key.name = adminToolDescriptor.getKey().getName();
        jsonEntry.displayName = adminToolDescriptor.getDisplayName();
        jsonEntry.description = adminToolDescriptor.getDescription();
        if ( mapAllowedPrincipals )
        {
            final ImmutableSet.Builder<String> allowedPrincipals = ImmutableSet.builder();
            for ( PrincipalKey principalKey : adminToolDescriptor.getAllowedPrincipals() )
            {
                allowedPrincipals.add( principalKey.toString() );
            }
            jsonEntry.allow = allowedPrincipals.build();
        }
        return jsonEntry;
    }

    @Reference
    public void setAdminToolDescriptorService( final AdminToolDescriptorService adminToolDescriptorService )
    {
        this.adminToolDescriptorService = adminToolDescriptorService;
    }
}
