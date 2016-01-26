package com.enonic.xp.admin.impl.rest.resource.content.page.fragment;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.json.content.ContentJson;
import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.content.ContentIconUrlResolver;
import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.content.Content;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.region.CreateFragmentParams;
import com.enonic.xp.region.FragmentService;
import com.enonic.xp.schema.content.ContentTypeService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;

@Path(ResourceConstants.REST_ROOT + "content/page/fragment")
@Produces(MediaType.APPLICATION_JSON)
@RolesAllowed(RoleKeys.ADMIN_LOGIN_ID)
@Component(immediate = true)
public final class FragmentResource
    implements JaxRsComponent
{
    private FragmentService fragmentService;

    private ContentPrincipalsResolver principalsResolver;

    private ContentIconUrlResolver contentIconUrlResolver;

    @POST
    @Path("create")
    @Consumes(MediaType.APPLICATION_JSON)
    public ContentJson createFragment( final CreateFragmentJson params )
    {
        final CreateFragmentParams command = params.getCreateFragmentParams();
        final Content fragmentContent = this.fragmentService.create( command );

        return new ContentJson( fragmentContent, contentIconUrlResolver, principalsResolver );
    }

    @Reference
    public void setFragmentService( final FragmentService fragmentService )
    {
        this.fragmentService = fragmentService;
    }

    @Reference
    public void setContentTypeService( final ContentTypeService contentTypeService )
    {
        this.contentIconUrlResolver = new ContentIconUrlResolver( contentTypeService );
    }

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.principalsResolver = new ContentPrincipalsResolver( securityService );
    }
}
