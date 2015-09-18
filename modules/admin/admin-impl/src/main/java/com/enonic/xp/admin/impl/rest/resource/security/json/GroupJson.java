package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.enonic.xp.security.Group;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.PrincipalKeys;

import static java.util.stream.Collectors.toList;

public final class GroupJson
    extends PrincipalJson
{
    private final Group group;

    private final List<String> members;

    public GroupJson( final Group group, final PrincipalKeys members )
    {
        super( group );
        this.group = group;
        this.members = members.stream().map( PrincipalKey::toString ).collect( toList() );
    }

    public List<String> getMembers()
    {
        return members;
    }
}
