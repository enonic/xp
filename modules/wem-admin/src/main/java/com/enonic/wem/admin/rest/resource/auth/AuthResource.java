package com.enonic.wem.admin.rest.resource.auth;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.enonic.wem.api.security.SecurityService;
import com.enonic.wem.api.security.auth.AuthenticationInfo;
import com.enonic.wem.api.security.auth.EmailPasswordAuthToken;
import com.enonic.wem.api.security.auth.UsernamePasswordAuthToken;

@Path("auth")
@Produces(MediaType.APPLICATION_JSON)
public final class AuthResource
{
    private SecurityService securityService;

    @POST
    @Path("login")
    public LoginResultJson login( final LoginJson login )
    {
        final String user = login.getUser();

        AuthenticationInfo authInfo = null;
        if ( user.contains( "@" ) )
        {
            final EmailPasswordAuthToken emailAuthToken = new EmailPasswordAuthToken();
            emailAuthToken.setEmail( user );
            emailAuthToken.setPassword( login.getPassword() );
            emailAuthToken.setUserStore( login.getUserStoreKey() );
            emailAuthToken.setRememberMe( login.isRememberMe() );

            authInfo = securityService.authenticate( emailAuthToken );
        }
        if ( authInfo == null || !authInfo.isAuthenticated() )
        {
            final UsernamePasswordAuthToken usernameAuthToken = new UsernamePasswordAuthToken();
            usernameAuthToken.setUsername( user );
            usernameAuthToken.setPassword( login.getPassword() );
            usernameAuthToken.setUserStore( login.getUserStoreKey() );
            usernameAuthToken.setRememberMe( login.isRememberMe() );

            authInfo = securityService.authenticate( usernameAuthToken );
        }

        return new LoginResultJson( authInfo );
    }

    public void setSecurityService( final SecurityService securityService )
    {
        this.securityService = securityService;
    }
}
