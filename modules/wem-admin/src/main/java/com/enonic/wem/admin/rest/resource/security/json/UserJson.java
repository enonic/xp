package com.enonic.wem.admin.rest.resource.security.json;


import com.enonic.wem.api.security.User;

public final class UserJson
    extends PrincipalJson
{
    private final User user;

    public UserJson( final User user )
    {
        super( user );
        this.user = user;
    }

    public String getEmail()
    {
        return user.getEmail();
    }

    public String getLogin()
    {
        return user.getLogin();
    }

    public boolean isLoginDisabled()
    {
        return user.isDisabled();
    }
}
