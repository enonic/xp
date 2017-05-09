package com.enonic.xp.node;

public class MultiRepoNodeQuery
{
    private final SearchTargets searchTargets;

    private final NodeQuery nodeQuery;

    public MultiRepoNodeQuery( final SearchTargets searchTargets, final NodeQuery nodeQuery )
    {
        this.searchTargets = searchTargets;
        this.nodeQuery = nodeQuery;
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
