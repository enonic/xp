package com.enonic.wem.admin.rest.resource.security.json;


import com.enonic.wem.api.security.User;

public class UserJson
{
    private final User user;

    public UserJson( final User user )
    {
        this.user = user;

    }

    public String getKey()
    {
        return user.getKey().toString();
    }

    public String getDisplayName()
    {
        return user.getDisplayName();
    }

    public String getEmail()
    {
        return user.getEmail();
    }

    public String getLogin()
    {
        return user.getLogin();
    }

    public boolean isDisabled()
    {
        return user.isDisabled();
    }

}
