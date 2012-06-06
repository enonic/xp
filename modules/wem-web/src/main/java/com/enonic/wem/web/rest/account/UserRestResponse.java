package com.enonic.wem.web.rest.account;

import com.enonic.wem.web.rest.common.RestResponse;

public class UserRestResponse extends RestResponse
{

    private String username;

    private boolean emailInUse;

    private String userkey;

    public String getUsername()
    {
        return username;
    }

    public void setUsername( String username )
    {
        this.username = username;
    }

    public boolean isEmailInUse()
    {
        return emailInUse;
    }

    public void setEmailInUse( boolean emailInUse )
    {
        this.emailInUse = emailInUse;
    }

    public String getUserkey()
    {
        return userkey;
    }

    public void setUserkey( String userkey )
    {
        this.userkey = userkey;
    }
}
