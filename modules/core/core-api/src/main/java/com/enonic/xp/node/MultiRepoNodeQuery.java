package com.enonic.xp.node;

import static java.util.Objects.requireNonNull;

public final class MultiRepoNodeQuery
{
    private final SearchTargets searchTargets;

    private final NodeQuery nodeQuery;

    public MultiRepoNodeQuery( final SearchTargets searchTargets, final NodeQuery nodeQuery )
    {
        this.searchTargets = requireNonNull( searchTargets );
        this.nodeQuery = requireNonNull( nodeQuery );
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
