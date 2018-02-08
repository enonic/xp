package com.enonic.xp.cluster;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class ClusterProviders
    implements Iterable<ClusterProvider>
{
    private final CopyOnWriteArrayList<ClusterProvider> providers = new CopyOnWriteArrayList<>();

    private final List<ClusterProviderId> requiredProviders;

    public ClusterProviders( final List<ClusterProviderId> requiredProviders )
    {
        this.requiredProviders = requiredProviders;
    }

    public boolean hasRequiredProviders()
    {
        return this.providers.stream().map( ClusterProvider::getId ).collect( Collectors.toList() ).containsAll( requiredProviders );
    }

    @Override
    public Iterator<ClusterProvider> iterator()
    {
        return providers.iterator();
    }

    public void add( final ClusterProvider clusterProvider )
    {
        this.providers.add( clusterProvider );
    }

    public boolean remove( final ClusterProvider clusterProvider )
    {
        return this.providers.remove( clusterProvider );
    }
}
