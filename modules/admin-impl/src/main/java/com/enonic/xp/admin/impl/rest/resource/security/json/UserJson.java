package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.enonic.xp.core.security.Principals;
import com.enonic.xp.core.security.User;

public final class UserJson
    extends PrincipalJson
{
    private final User user;

    private final List<PrincipalJson> memberships;

    public UserJson( final User user )
    {
        this( user, Principals.empty() );
    }

    public UserJson( final User user, final Principals memberships )
    {
        super( user );
        this.user = user;
        this.memberships = new PrincipalsJson( memberships ).getPrincipals();
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

    public List<PrincipalJson> getMemberships()
    {
        return memberships;
    }
}
