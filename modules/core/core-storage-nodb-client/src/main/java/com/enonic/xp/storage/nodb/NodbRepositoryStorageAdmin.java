package com.enonic.xp.storage.nodb;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.enonic.nodb.proto.v1.CreateRepositoryRequest;
import com.enonic.nodb.proto.v1.DeleteRepositoryRequest;
import com.enonic.nodb.proto.v1.RepositoryExistsRequest;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.storage.spi.IndexMapping;
import com.enonic.xp.storage.spi.IndexSettings;
import com.enonic.xp.storage.spi.RepositoryStorageAdmin;
import com.enonic.xp.storage.spi.UpdateIndexSettings;

/**
 * gRPC-backed {@link RepositoryStorageAdmin}. Registered with {@code storage.backend=nodb}
 * and a positive {@code service.ranking} -- see {@link NodbStorageClient}'s class javadoc
 * for the selection mechanism shared with {@link NodbNodeStore}.
 * <p>
 * {@link #createIndex}/{@link #deleteIndex} map onto {@code RepositoryAdmin.CreateRepository}
 * /{@code DeleteRepository}, which {@code TenantAuthInterceptor} requires {@code operator}
 * scope for -- see {@link NodbStorageClient}'s "Token scope note".
 * <p>
 * {@link #refresh}/{@link #updateSettings}/{@link #putIndexMapping}/{@link #getIndexSettings}
 * are documented client-side no-ops (nodb/BUILD-PHASE-1.md Gate 0 reconciliation table):
 * ES-index-settings/mapping/refresh concepts have no NoDB equivalent (Postgres partitions
 * are static DDL, not tunable settings or dynamic mappings, and Postgres transactional
 * visibility is a strictly stronger guarantee than an ES refresh).
 */
@Component(service = RepositoryStorageAdmin.class, property = { "storage.backend=nodb", "service.ranking:Integer=100" })
public class NodbRepositoryStorageAdmin
    implements RepositoryStorageAdmin
{
    private static final Logger LOG = LoggerFactory.getLogger( NodbRepositoryStorageAdmin.class );

    private static final ObjectMapper JSON = new ObjectMapper();

    private final NodbStorageClient client;

    @Activate
    public NodbRepositoryStorageAdmin( @Reference final NodbStorageClient client )
    {
        this.client = client;
    }

    /**
     * {@code mappings} is dropped: {@code Map<IndexType, IndexMapping>} is a genuine
     * ES-only concept (Postgres's {@code node_version}/{@code branch_entry} columns are
     * static DDL, not per-repo mappings) -- permanently N/A for NoDB, not a to-do (see
     * nodb.proto's {@code CreateRepositoryRequest} comment).
     */
    @Override
    public void createIndex( final RepositoryId repositoryId, final IndexSettings settings, final Map<IndexType, IndexMapping> mappings )
    {
        final CreateRepositoryRequest.Builder builder = CreateRepositoryRequest.newBuilder().setRepoId( repositoryId.toString() );
        final Map<String, Object> data = settings == null ? Map.of() : settings.getData();
        if ( !data.isEmpty() )
        {
            builder.setSettingsJson( writeJson( data ) );
        }
        NodbStatusMapper.repoScopedVoid( () -> client.repositoryAdmin().createRepository( builder.build() ) );
    }

    @Override
    public void deleteIndex( final RepositoryId repositoryId )
    {
        final DeleteRepositoryRequest request = DeleteRepositoryRequest.newBuilder().setRepoId( repositoryId.toString() ).build();
        NodbStatusMapper.repoScopedVoid( () -> client.repositoryAdmin().deleteRepository( request ) );
    }

    @Override
    public boolean indexExists( final RepositoryId repositoryId )
    {
        final RepositoryExistsRequest request = RepositoryExistsRequest.newBuilder().setRepoId( repositoryId.toString() ).build();
        return NodbStatusMapper.existsCheck( () -> client.repositoryAdmin().repositoryExists( request ).getExists() );
    }

    /**
     * No-op, no RPC call: Postgres transactional visibility is a strictly stronger
     * guarantee than an ES refresh, so there is nothing to force (nodb.proto's
     * "never sent over the wire" note).
     */
    @Override
    public void refresh( final RepositoryId repositoryId )
    {
        // Intentionally empty.
    }

    /** No-op: raw ES index-settings JSON (replica count, refresh_interval) has no NoDB equivalent. */
    @Override
    public void updateSettings( final RepositoryId repositoryId, final UpdateIndexSettings settings )
    {
        LOG.debug( "updateSettings is a no-op for the nodb backend (repository [{}]): "
                       + "ES index settings have no NoDB equivalent (static DDL schema)", repositoryId );
    }

    /** No-op: ES dynamic-mapping concept, no NoDB equivalent (static DDL schema). */
    @Override
    public void putIndexMapping( final RepositoryId repositoryId, final IndexType indexType, final Map<String, Object> mapping )
    {
        LOG.debug( "putIndexMapping is a no-op for the nodb backend (repository [{}], type [{}]): "
                       + "ES dynamic mappings have no NoDB equivalent (static DDL schema)", repositoryId, indexType );
    }

    /**
     * Returns an empty map: no NoDB equivalent for ES index settings introspection (e.g.
     * seeding a new repository's replica count from an existing one's settings). Verified
     * (nodb/BUILD-PHASE-1.md Gate B) that the one caller of this return value in core-repo
     * ({@code NodeRepositoryServiceImpl#adjustNumberOfReplicas}) wraps the call in a
     * broad {@code catch (Exception e)} and falls back to its own default settings on any
     * failure -- an empty map here yields a {@code Map.of()}-related
     * {@code NullPointerException} inside that method (constructing {@code Map.of("index",
     * Map.of("number_of_replicas", null))} rejects the null value), which that catch block
     * swallows, logging one WARN per repository creation in nodb mode. Functionally safe,
     * cosmetically noisy -- documented rather than silently relied upon.
     */
    @Override
    public Map<String, String> getIndexSettings( final RepositoryId repositoryId, final IndexType indexType )
    {
        return Map.of();
    }

    private static String writeJson( final Map<String, Object> data )
    {
        try
        {
            return JSON.writeValueAsString( data );
        }
        catch ( com.fasterxml.jackson.core.JsonProcessingException e )
        {
            throw new java.io.UncheckedIOException( "Failed to encode index settings for createIndex", e );
        }
    }
}
