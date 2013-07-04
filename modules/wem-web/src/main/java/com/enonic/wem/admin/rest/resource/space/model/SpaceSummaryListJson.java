package com.enonic.wem.admin.rest.resource.space.model;

import java.util.List;

import com.google.common.collect.ImmutableList;

import com.enonic.wem.api.space.Space;
import com.enonic.wem.api.space.Spaces;

public class SpaceSummaryListJson
{
    private final ImmutableList<SpaceSummaryJson> list;

    public SpaceSummaryListJson( final Spaces spaces )
    {
        final ImmutableList.Builder<SpaceSummaryJson> builder = ImmutableList.builder();
        for ( final Space space : spaces )
        {
            builder.add( new SpaceSummaryJson( space ) );
        }

        this.list = builder.build();
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
