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

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.schema.content.LocaleMessageResolver;
import com.enonic.xp.admin.impl.rest.resource.tool.json.AdminToolJson;
import com.enonic.xp.admin.tool.AdminToolDescriptor;
import com.enonic.xp.admin.tool.AdminToolDescriptorService;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.i18n.LocaleService;
import com.enonic.xp.jaxrs.JaxRsComponent;
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

    private LocaleService localeService;

    @GET
    @Path("list")
    public List<AdminToolJson> getAllowedAdminToolDescriptors()
    {
        final PrincipalKeys principals = ContextAccessor.current().
            getAuthInfo().
            getPrincipals();
        return adminToolDescriptorService.getAllowedAdminToolDescriptors( principals ).
            stream().
            map( this::mapAdminToolDescriptorToJson ).
            collect( Collectors.toList() );
    }

    private AdminToolJson mapAdminToolDescriptorToJson( final AdminToolDescriptor adminToolDescriptor )
    {
        return new AdminToolJson( adminToolDescriptor,
                                  new LocaleMessageResolver( this.localeService, adminToolDescriptor.getKey().getApplicationKey() ) );
    }

    @Reference
    public void setAdminToolDescriptorService( final AdminToolDescriptorService adminToolDescriptorService )
    {
        this.adminToolDescriptorService = adminToolDescriptorService;
    }

    @Reference
    public void setLocaleService( final LocaleService localeService )
    {
        this.localeService = localeService;
    }
}
