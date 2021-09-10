package com.enonic.xp.admin.impl.rest.resource.auth;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.auth.json.LoginResultJson;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.Session;

@Path(ResourceConstants.REST_ROOT + "auth")
@Produces(MediaType.APPLICATION_JSON)
@Component(immediate = true, property = "group=admin")
public final class AuthResource
    implements JaxRsComponent
{
    @GET
    @Path("authenticated")
    public LoginResultJson isAuthenticated()
    {
        final Session session = ContextAccessor.current().getLocalScope().getSession();
        if ( session == null )
        {
            return new LoginResultJson( AuthenticationInfo.unAuthenticated() );
        }

        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();
        return new LoginResultJson( authInfo );
    }
}
