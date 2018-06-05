package com.enonic.xp.cluster;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Clusters
    implements Iterable<Cluster>
{
    private final CopyOnWriteArrayList<Cluster> clusters = new CopyOnWriteArrayList<>();

    private final List<ClusterId> requiredClusters;

    public Clusters( final List<ClusterId> requiredClusters )
    {
        this.requiredClusters = requiredClusters;
    }

    public boolean hasRequiredProviders()
    {
        return this.clusters.stream().map( Cluster::getId ).collect( Collectors.toList() ).containsAll( requiredClusters );
    }

    @Override
    public Iterator<Cluster> iterator()
    {
        return clusters.iterator();
    }

    public void add( final Cluster cluster )
    {
        this.clusters.add( cluster );
    }

    public boolean remove( final Cluster cluster )
    {
        return this.clusters.remove( cluster );
    }
}
