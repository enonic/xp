package com.enonic.wem.admin.rest.resource.space.model;

import java.util.List;

import com.google.common.collect.Lists;

public class SpaceResultJson
{
    private final List<SpaceSummaryJson> list;

    public SpaceResultJson()
    {
        this.list = Lists.newArrayList();
    }

    public int getTotal()
    {
        return this.list.size();
    }

    public List<SpaceSummaryJson> getSpaces()
    {
        return this.list;
    }

    public void addSpace( final SpaceSummaryJson space )
    {
        this.list.add( space );
    }
}
