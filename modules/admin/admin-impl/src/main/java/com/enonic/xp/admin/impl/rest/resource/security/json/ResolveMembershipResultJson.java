package com.enonic.xp.admin.impl.rest.resource.security.json;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.Principals;

public final class ResolveMembershipResultJson
{
    private final PrincipalKey principalKey;

    private final Principals members;

    public ResolveMembershipResultJson( final PrincipalKey principalKey, final Principals members )
    {
        this.principalKey = principalKey;
        this.members = members;
    }


    public String getPrincipalKey()
    {
        return principalKey.toString();
    }

    public PrincipalsJson getMembers()
    {
        return new PrincipalsJson( members );
    }
}
