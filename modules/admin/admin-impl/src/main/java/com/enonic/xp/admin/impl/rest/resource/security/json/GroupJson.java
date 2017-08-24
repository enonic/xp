package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.enonic.xp.security.Group;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;
import com.enonic.xp.security.Principals;

import static java.util.stream.Collectors.toList;

public final class GroupJson
    extends PrincipalJson
{
    private final Group group;

    private final List<String> members;

    private final List<PrincipalJson> memberships;

    public GroupJson( final Group group, final PrincipalKeys members )
    {
        this( group, members, Principals.empty() );
    }

    public GroupJson( final Group group, final PrincipalKeys members, final Principals memberships )
    {
        super( group );
        this.group = group;
        this.members = members.stream().map( PrincipalKey::toString ).collect( toList() );
        this.memberships = new PrincipalsJson( memberships ).getPrincipals();
    }

    public List<String> getMembers()
    {
        return members;
    }

    public List<PrincipalJson> getMemberships()
    {
        return memberships;
    }
}
