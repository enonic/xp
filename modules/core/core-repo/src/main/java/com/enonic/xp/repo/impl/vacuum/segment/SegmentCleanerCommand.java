package com.enonic.xp.repo.impl.vacuum.segment;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;

import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.content.ContentConstants;
import com.enonic.xp.repo.impl.vacuum.VacuumTaskParams;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryIds;
import com.enonic.xp.repository.RepositorySegmentUtils;
import com.enonic.xp.repository.RepositoryService;
import com.enonic.xp.security.SystemConstants;
import com.enonic.xp.vacuum.VacuumTaskResult;

public class SegmentCleanerCommand
{
    private static final Logger LOG = LoggerFactory.getLogger( SegmentCleanerCommand.class );

    private RepositoryIds BUILTIN_REPOSITORIES = RepositoryIds.from( SystemConstants.SYSTEM_REPO_ID, ContentConstants.CONTENT_REPO_ID );

    private RepositoryService repositoryService;

    private BlobStore blobStore;

    private VacuumTaskResult.Builder result;

    private SegmentCleanerCommand( final Builder builder )
    {
        blobStore = builder.blobStore;
        repositoryService = builder.repositoryService;
        result = VacuumTaskResult.create().
            taskName( builder.name );
    }

    public VacuumTaskResult execute( final VacuumTaskParams params )
    {
        LOG.info( "Traversing segments....." );

        final RepositoryIds repositoryIds = getRepositoryIds();
        List<Segment> toBeRemoved = Lists.newLinkedList();
        blobStore.listSegments().
            forEach( segment -> {
                final RepositoryId repositoryId = RepositorySegmentUtils.toRepositoryId( segment );
                if ( repositoryIds.contains( repositoryId ) )
                {
                    result.inUse();
                }
                else
                {
                    toBeRemoved.add( segment );
                }
                result.processed();
            } );

        toBeRemoved.forEach( segment -> {
            try
            {
                blobStore.deleteSegment( segment );
                result.deleted();
            }
            catch ( Exception e )
            {
                LOG.error( "Failed to delete segment [" + segment + "]", e );
                result.failed();
            }
        } );

        return result.build();
    }

    private RepositoryIds getRepositoryIds()
    {
        final RepositoryIds.Builder repositoryIds = RepositoryIds.create().
            addAll( BUILTIN_REPOSITORIES );
        repositoryService.list().
            forEach( repository -> repositoryIds.add( repository.getId() ) );
        return repositoryIds.build();
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private BlobStore blobStore;

        private RepositoryService repositoryService;

        private String name;

        public Builder()
        {
        }

        public Builder blobStore( final BlobStore val )
        {
            blobStore = val;
            return this;
        }

        public Builder repositoryService( final RepositoryService val )
        {
            repositoryService = val;
            return this;
        }

        public Builder name( final String val )
        {
            name = val;
            return this;
        }

        public SegmentCleanerCommand build()
        {
            return new SegmentCleanerCommand( this );
        }
    }
}
