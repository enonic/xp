package com.enonic.xp.admin.impl.rest.resource.security.json;


import com.enonic.xp.admin.impl.rest.resource.content.ContentPrincipalsResolver;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.Principals;

public final class ResolveMembershipResultJson
{
    private final PrincipalKey principalKey;

    private final Principals members;

    public ResolveMembershipResultJson( final PrincipalKey principalKey, final PrincipalKeys members,
                                        final ContentPrincipalsResolver resolver )
    {
        this.principalKey = principalKey;
        this.members = resolver.findPrincipals( members );
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
