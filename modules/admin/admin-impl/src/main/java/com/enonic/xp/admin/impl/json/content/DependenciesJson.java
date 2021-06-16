package com.enonic.xp.admin.impl.json.content;

import java.util.List;

public class DependenciesJson
{
    private final List<DependenciesAggregationJson> inbound;

    private final List<DependenciesAggregationJson> outbound;


    public DependenciesJson( final List<DependenciesAggregationJson> inbound, List<DependenciesAggregationJson> outbound )
    {
        this.inbound = inbound;
        this.outbound = outbound;
    }

    public List<DependenciesAggregationJson> getInbound()
    {
        return inbound;
    }

    public List<DependenciesAggregationJson> getOutbound()
    {
        return outbound;
    }
}
