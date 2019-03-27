package com.enonic.xp.admin.impl.rest.resource.auth;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.admin.impl.rest.resource.ResourceConstants;
import com.enonic.xp.admin.impl.rest.resource.auth.json.LoginResultJson;
import com.enonic.xp.admin.impl.security.AuthHelper;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.jaxrs.JaxRsComponent;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.SecurityService;
import com.enonic.xp.security.auth.AuthenticationInfo;
import com.enonic.xp.session.Session;

@Path(ResourceConstants.REST_ROOT + "auth")
@Produces(MediaType.APPLICATION_JSON)
@Component(immediate = true, property = "group=admin")
public final class AuthResource
    implements JaxRsComponent
{

    private SecurityService securityService;

    @POST
    @Path("login")
    public LoginResultJson login( final LoginRequest request )
    {
        final AuthHelper helper = new AuthHelper( this.securityService );
        final AuthenticationInfo authInfo =
            helper.login( request.getUser(), request.getPassword(), request.getIdProvider(), request.isRememberMe() );

        if ( authInfo.isAuthenticated() && !authInfo.hasRole( RoleKeys.ADMIN_LOGIN ) && !authInfo.hasRole( RoleKeys.ADMIN ) )
        {
            AuthHelper.logout();
            return new LoginResultJson( AuthenticationInfo.unAuthenticated(), "Access Denied" );
        }
        if ( !authInfo.isAuthenticated() )
        {
            return new LoginResultJson( AuthenticationInfo.unAuthenticated() );
        }

        return new LoginResultJson( authInfo );
    }

    @POST
    @Path("logout")
    public void logout()
    {
        AuthHelper.logout();
    }

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

    @Reference
    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }
}
