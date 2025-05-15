package com.enonic.xp.node;

import java.util.Objects;

public final class MultiRepoNodeQuery
{
    private final SearchTargets searchTargets;

    private final NodeQuery nodeQuery;

    public MultiRepoNodeQuery( final SearchTargets searchTargets, final NodeQuery nodeQuery )
    {
        this.searchTargets = Objects.requireNonNull( searchTargets );
        this.nodeQuery = Objects.requireNonNull( nodeQuery );
    }

    public SearchTargets getSearchTargets()
    {
        return searchTargets;
    }

    public NodeQuery getNodeQuery()
    {
        return nodeQuery;
    }
}
