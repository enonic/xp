package com.enonic.xp.core.nodb;

import com.enonic.xp.storage.nodb.NodbNodeStore;
import com.enonic.xp.storage.nodb.NodbRepositoryStorageAdmin;
import com.enonic.xp.storage.nodb.NodbStorageClient;
import com.enonic.xp.storage.spi.NodeStore;
import com.enonic.xp.storage.spi.RepositoryStorageAdmin;

/**
 * One provisioned NoDB tenant (see {@link NodbTestCluster#freshTenant()}): its own gRPC
 * channel + bearer token, and the two SPI implementations ({@link NodeStore}/
 * {@link RepositoryStorageAdmin}) an {@code AbstractNodeTest} subclass wires in place of
 * {@code ElasticsearchNodeStore}/{@code IndexServiceInternalImpl} when
 * {@code xp.itest.storage=nodb}. {@link #close()} releases the gRPC channel (the
 * Postgres schema itself is left in place -- dropping it isn't worth the round trip since
 * the whole container is torn down at JVM exit anyway, see {@link NodbTestCluster}).
 */
public final class NodbTenant
    implements AutoCloseable
{
    private final String tenantId;

    private final NodbStorageClient client;

    private final NodeStore nodeStore;

    private final RepositoryStorageAdmin repositoryStorageAdmin;

    NodbTenant( final String tenantId, final NodbStorageClient client, final NodbNodeStore nodeStore,
                final NodbRepositoryStorageAdmin repositoryStorageAdmin )
    {
        this.tenantId = tenantId;
        this.client = client;
        this.nodeStore = nodeStore;
        this.repositoryStorageAdmin = repositoryStorageAdmin;
    }

    public String tenantId()
    {
        return tenantId;
    }

    public NodeStore nodeStore()
    {
        return nodeStore;
    }

    public RepositoryStorageAdmin repositoryStorageAdmin()
    {
        return repositoryStorageAdmin;
    }

    @Override
    public void close()
    {
        client.deactivate();
    }
}
