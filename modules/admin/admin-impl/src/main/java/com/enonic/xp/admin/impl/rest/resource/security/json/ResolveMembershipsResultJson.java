package com.enonic.xp.admin.impl.rest.resource.security.json;


import java.util.List;

import com.google.common.collect.Lists;

public final class ResolveMembershipsResultJson
{
    private final List<ResolveMembershipResultJson> results;

    public ResolveMembershipsResultJson()
    {
        this.results = Lists.newArrayList();
    }

    public void add( ResolveMembershipResultJson resolveResult )
    {
        this.results.add( resolveResult );
    }

    public List<ResolveMembershipResultJson> getResults()
    {
        return results;
    }

}
