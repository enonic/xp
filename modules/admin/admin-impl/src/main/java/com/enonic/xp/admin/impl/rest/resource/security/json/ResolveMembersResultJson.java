package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.google.common.collect.Lists;

public final class ResolveMembersResultJson
{
    private final List<ResolveMemberResultJson> results;

    public ResolveMembersResultJson()
    {
        this.results = Lists.newArrayList();
    }

    public void add( ResolveMemberResultJson resolveResult )
    {
        this.results.add( resolveResult );
    }

    public List<ResolveMemberResultJson> getResults()
    {
        return results;
    }

}
