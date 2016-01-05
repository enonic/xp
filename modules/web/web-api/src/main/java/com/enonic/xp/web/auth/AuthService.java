package com.enonic.xp.web.auth;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface AuthService
{
    String getKey();

    String getDisplayName();

    void authenticate( HttpServletRequest request, HttpServletResponse response );
}
