package com.enonic.xp.admin.impl.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.service.component.annotations.Component;

import com.enonic.xp.web.auth.AuthService;

@Component(immediate = true)
public class AdminAuthService
    implements AuthService
{

    @Override
    public String getKey()
    {
        return "com.enonic.xp.admin.login";
    }

    @Override
    public String getDisplayName()
    {
        return "Admin Login";
    }

    @Override
    public void authenticate( final HttpServletRequest request, final HttpServletResponse response )
    {
        try
        {
            response.setStatus( 303 );
            response.setHeader( "Location", "/admin" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
    }
}
