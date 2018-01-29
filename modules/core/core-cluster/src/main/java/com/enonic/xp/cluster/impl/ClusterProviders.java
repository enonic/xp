package com.enonic.xp.cluster.impl;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import com.enonic.xp.cluster.ClusterProvider;
import com.enonic.xp.cluster.ClusterProviderId;

public class ClusterProviders
    implements Iterable<ClusterProvider>
{

    private final CopyOnWriteArrayList<ClusterProvider> providers = new CopyOnWriteArrayList<>();

    private final List<ClusterProviderId> requiredProviders;

    public ClusterProviders( final List<ClusterProviderId> requiredProviders )
    {
        this.requiredProviders = requiredProviders;
    }

    boolean hasRequiredProviders()
    {
        return this.providers.stream().map( ClusterProvider::getId ).collect( Collectors.toList() ).containsAll( requiredProviders );
    }

    @Override
    public Iterator<ClusterProvider> iterator()
    {
        return providers.iterator();
    }

    void add( final ClusterProvider clusterProvider )
    {
        this.providers.add( clusterProvider );
    }

    boolean remove( final ClusterProvider clusterProvider )
    {
        return this.providers.remove( clusterProvider );
    }
}
