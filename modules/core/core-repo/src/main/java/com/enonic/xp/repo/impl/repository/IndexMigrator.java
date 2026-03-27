package com.enonic.xp.repo.impl.repository;

import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.index.IndexType;
import com.enonic.xp.node.BinaryAttachments;
import com.enonic.xp.repo.impl.Model;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repository.RepositoryId;

/**
 * This class is useful for online repository migrations when needed.
 *
 */
@SuppressWarnings("unused")
public class IndexMigrator
{
    private static final Logger LOG = LoggerFactory.getLogger( IndexMigrator.class );

    private static final IndexResourceProvider DEFAULT_INDEX_RESOURCE_PROVIDER = new DefaultIndexResourceProvider();

    final RepositoryEntryService repositoryEntryService;

    final NodeRepositoryService nodeRepositoryService;

    final IndexServiceInternal indexServiceInternal;

    public IndexMigrator( final RepositoryEntryService repositoryEntryService, final NodeRepositoryService nodeRepositoryService,
                          final IndexServiceInternal indexServiceInternal )
    {
        this.repositoryEntryService = Objects.requireNonNull( repositoryEntryService );
        this.nodeRepositoryService = Objects.requireNonNull( nodeRepositoryService );
        this.indexServiceInternal = Objects.requireNonNull( indexServiceInternal );
    }

    public void upgradeRepository( final RepositoryId repositoryId )
    {
        final RepositoryEntry entry = repositoryEntryService.getRepositoryEntry( repositoryId );
        if ( entry != null && !Model.MODEL_VERSION.equals( entry.getModelVersion() ) )
        {
            putDefaultMapping( repositoryId, IndexType.VERSION );
            putDefaultMapping( repositoryId, IndexType.BRANCH );
            putDefaultMapping( repositoryId, IndexType.COMMIT );

            nodeRepositoryService.updateMappings( repositoryId, entry.getSettings() );

            final RepositoryEntry updatedEntry = RepositoryEntry.create( entry ).modelVersion( Model.MODEL_VERSION ).build();

            repositoryEntryService.updateRepositoryEntry( updatedEntry, BinaryAttachments.empty() );
        }
    }

    private void putDefaultMapping( final RepositoryId repository, final IndexType indexType )
    {
        try
        {
            indexServiceInternal.putIndexMapping( repository, indexType,
                                                  (Map<String, Object>) DEFAULT_INDEX_RESOURCE_PROVIDER.getMapping( indexType )
                                                      .getData()
                                                      .get( indexType.getName() ) );
        }
        catch ( Exception e )
        {
            LOG.error( "cannot migrate index mappings of repository [{}] index [{}]", repository, indexType, e );
        }
    }

}
