package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

public final class ResolveMembershipResultJson
{
    private final PrincipalKey principalKey;

    private final PrincipalKeys members;

    public ResolveMembershipResultJson( final PrincipalKey principalKey, final PrincipalKeys members )
    {
        this.principalKey = principalKey;
        this.members = members;
    }


    public String getPrincipalKey()
    {
        return principalKey.toString();
    }

    public List<String> getMembers()
    {
        return members.stream().map( key -> key.toString() ).collect( Collectors.toList() );
    }
}
