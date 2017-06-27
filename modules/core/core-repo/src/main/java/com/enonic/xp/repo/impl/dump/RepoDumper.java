package com.enonic.xp.repo.impl.dump;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.data.Value;
import com.enonic.xp.data.ValueFactory;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpError;
import com.enonic.xp.dump.RepoDumpResult;
import com.enonic.xp.dump.SystemDumpListener;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQuery;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.query.filter.RangeFilter;
import com.enonic.xp.repo.impl.dump.model.DumpEntry;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repo.impl.node.executor.BatchedGetChildrenExecutor;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

class RepoDumper
{
    private static final int DEFAULT_BATCH_SIZE = 5000;

    private final Logger LOG = LoggerFactory.getLogger( RepoDumper.class );

    private final RepositoryId repositoryId;

    private final boolean includeVersions;

    private final boolean includeBinaries;

    private final Integer maxAge;

    private final Integer maxVersions;

    private final NodeService nodeService;

    private final RepositoryService repositoryService;

    private final DumpWriter writer;

    private final RepoDumpResult.Builder dumpResult;

    private final String xpVersion;

    private final SystemDumpListener listener;

    private RepoDumper( final Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.includeVersions = builder.includeVersions;
        this.includeBinaries = builder.includeBinaries;
        this.nodeService = builder.nodeService;
        this.repositoryService = builder.repositoryService;
        this.writer = builder.writer;
        this.xpVersion = builder.xpVersion;
        this.dumpResult = RepoDumpResult.create( this.repositoryId );
        this.maxAge = builder.maxAge;
        this.maxVersions = builder.maxVersions;
        this.listener = builder.listener;
    }

    public RepoDumpResult execute()
    {
        getBranches().forEach( ( branch ) -> setContext( branch ).runWith( this::doExecute ) );

        return this.dumpResult.build();
    }

    private Branches getBranches()
    {
        final Repository repository = this.repositoryService.get( this.repositoryId );

        if ( repository == null )
        {
            throw new RepoDumpException( String.format( "Repository [%s] not found", this.repositoryId ) );
        }

        return repository.getBranches();
    }

    private Context setContext( final Branch branch )
    {
        return ContextBuilder.from( ContextAccessor.current() ).
            repositoryId( this.repositoryId ).
            branch( branch ).
            build();
    }

    private void doExecute()
    {
        final Branch branch = ContextAccessor.current().getBranch();
        writer.writeDumpMeta( new DumpMeta( this.xpVersion ) );
        final BranchDumpResult.Builder branchDumpResult = BranchDumpResult.create( branch );
        try
        {
            writer.open( this.repositoryId, branch );
            final Node rootNode = this.nodeService.getRoot();
            doDumpNode( rootNode.id(), branchDumpResult );
            dumpChildren( rootNode.id(), branchDumpResult );
        }
        catch ( Exception e )
        {
            throw new RepoDumpException( "Error occurred when dumping repository " + repositoryId, e );
        }
        finally
        {
            writer.close();
        }

        this.dumpResult.add( branchDumpResult.build() );
    }


    private void dumpChildren( final NodeId nodeId, final BranchDumpResult.Builder dumpResult )
    {
        final BatchedGetChildrenExecutor executor = BatchedGetChildrenExecutor.create().
            nodeService( this.nodeService ).
            parentId( nodeId ).
            recursive( true ).
            batchSize( DEFAULT_BATCH_SIZE ).
            childOrder( ChildOrder.from( "_path asc" ) ).
            build();

        reportDumpingBranch( ContextAccessor.current().getBranch(), executor.getTotalHits() );

        while ( executor.hasMore() )
        {
            final NodeIds children = executor.execute();

            for ( final NodeId child : children )
            {
                doDumpNode( child, dumpResult );
            }
        }
    }

    private void doDumpNode( final NodeId nodeId, final BranchDumpResult.Builder dumpResult )
    {
        final DumpEntry dumpEntry = createDumpEntry( nodeId );
        writer.writeMetaData( dumpEntry );
        writeVersions( dumpResult, dumpEntry );
        writeBinaries( dumpResult, dumpEntry );
        dumpResult.addedNode();
        dumpResult.addedVersions( dumpEntry.getAllVersionIds().size() );
        reportNodeDumped();
    }

    private void writeBinaries( final BranchDumpResult.Builder dumpResult, final DumpEntry dumpEntry )
    {
        dumpEntry.getBinaryReferences().forEach( ( ref ) -> {
            try
            {
                this.writer.writeBinary( ref );
            }
            catch ( RepoDumpException e )
            {
                LOG.error( "Cannot dump binary:", e );
                dumpResult.error( DumpError.error( "Cannot dump binary: " + e.getMessage() ) );
            }
        } );
    }

    private void writeVersions( final BranchDumpResult.Builder dumpResult, final DumpEntry dumpEntry )
    {
        dumpEntry.getAllVersionIds().forEach( ( versionId ) -> {
            try
            {
                this.writer.writeVersion( versionId );
            }
            catch ( RepoDumpException e )
            {
                LOG.error( "Cannot dump version", e );
                dumpResult.error( DumpError.error( "Cannot dump version: " + e.getMessage() ) );
            }
        } );
    }

    private void reportNodeDumped()
    {
        if ( this.listener != null )
        {
            this.listener.nodeDumped();
        }
    }

    private DumpEntry createDumpEntry( final NodeId nodeId )
    {
        final DumpEntry.Builder builder = DumpEntry.create().
            nodeId( nodeId );

        final Node currentNode = this.nodeService.getById( nodeId );

        builder.addVersion( MetaFactory.create( currentNode, true ) );

        if ( this.includeBinaries )
        {
            builder.addBinaryReferences(
                currentNode.getAttachedBinaries().stream().map( AttachedBinary::getBlobKey ).collect( Collectors.toSet() ) );
        }

        if ( this.includeVersions )
        {
            addVersions( nodeId, builder, currentNode.getNodeVersionId() );
        }

        return builder.build();
    }

    private void addVersions( final NodeId nodeId, final DumpEntry.Builder builder, final NodeVersionId currentVersion )
    {
        final NodeVersionQueryResult result = getVersions( nodeId );

        for ( final NodeVersionMetadata metaData : result.getNodeVersionsMetadata() )
        {
            if ( metaData.getNodeVersionId().equals( currentVersion ) )
            {
                // Skip current, since this will be dumped anyway
                continue;
            }

            if ( this.includeBinaries )
            {
                addVersionWithBinaries( builder, metaData );
            }
            else
            {
                builder.addVersion( MetaFactory.create( metaData, false ) );
            }
        }
    }

    private NodeVersionQueryResult getVersions( final NodeId nodeId )
    {
        final NodeVersionQuery.Builder queryBuilder = NodeVersionQuery.create().
            nodeId( nodeId ).
            size( this.maxVersions != null ? this.maxVersions : -1 );

        if ( this.maxAge != null )
        {
            final Value ageValue = ValueFactory.newDateTime( Instant.now().minus( Duration.ofDays( this.maxAge ) ) );
            queryBuilder.addQueryFilter( RangeFilter.create().
                from( ageValue ).
                build() );
        }

        return this.nodeService.findVersions( queryBuilder.build() );
    }

    private void addVersionWithBinaries( final DumpEntry.Builder builder, final NodeVersionMetadata metaData )
    {
        final NodeVersion nodeVersion = this.nodeService.getByNodeVersion( metaData.getNodeVersionId() );
        builder.addVersion( MetaFactory.create( metaData, false ) );
        builder.addBinaryReferences(
            nodeVersion.getAttachedBinaries().stream().map( AttachedBinary::getBlobKey ).collect( Collectors.toSet() ) );
    }

    private void reportDumpingBranch( final Branch branch, final Long totalHits )
    {
        if ( this.listener != null )
        {
            this.listener.dumpingBranch( this.repositoryId, branch, totalHits );
        }
        else
        {
            LOG.info( "Dumping repository [" + this.repositoryId + "], branch [" + branch + "]  " );
        }
    }

    public static Builder create()
    {
        return new Builder();
    }

    public static final class Builder
    {
        private RepositoryId repositoryId;

        private boolean includeVersions;

        private boolean includeBinaries;

        private NodeService nodeService;

        private RepositoryService repositoryService;

        private DumpWriter writer;

        private String xpVersion;

        private Integer maxAge;

        private Integer maxVersions;

        private SystemDumpListener listener;

        private Builder()
        {
        }

        public Builder repositoryId( final RepositoryId val )
        {
            repositoryId = val;
            return this;
        }

        public Builder includeVersions( final boolean val )
        {
            includeVersions = val;
            return this;
        }

        public Builder includeBinaries( final boolean val )
        {
            includeBinaries = val;
            return this;
        }

        public Builder nodeService( final NodeService val )
        {
            nodeService = val;
            return this;
        }

        public Builder repositoryService( final RepositoryService val )
        {
            repositoryService = val;
            return this;
        }

        public Builder writer( final DumpWriter writer )
        {
            this.writer = writer;
            return this;
        }

        public Builder xpVersion( final String xpVersion )
        {
            this.xpVersion = xpVersion;
            return this;
        }

        public Builder maxAge( final Integer maxAge )
        {
            this.maxAge = maxAge;
            return this;
        }

        public Builder maxVersions( final Integer maxVersions )
        {
            this.maxVersions = maxVersions;
            return this;
        }

        public Builder listener( final SystemDumpListener listener )
        {
            this.listener = listener;
            return this;
        }

        public RepoDumper build()
        {
            return new RepoDumper( this );
        }
    }
}
