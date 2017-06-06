package com.enonic.xp.repo.impl.dump;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.dump.BranchDumpResult;
import com.enonic.xp.dump.DumpResult;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.repo.impl.dump.model.DumpEntry;
import com.enonic.xp.repo.impl.dump.model.DumpMeta;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repo.impl.node.executor.BatchedGetChildrenExecutor;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

class RepoDumper
{
    private final RepositoryId repositoryId;

    private final boolean includeVersions;

    private final boolean includeBinaries;

    private final NodeService nodeService;

    private final RepositoryService repositoryService;

    private final DumpWriter writer;

    private final Logger LOG = LoggerFactory.getLogger( RepoDumper.class );

    private final DumpResult.Builder dumpResult;

    private final String xpVersion;

    private RepoDumper( final Builder builder )
    {
        this.repositoryId = builder.repositoryId;
        this.includeVersions = builder.includeVersions;
        this.includeBinaries = builder.includeBinaries;
        this.nodeService = builder.nodeService;
        this.repositoryService = builder.repositoryService;
        this.writer = builder.writer;
        this.xpVersion = builder.xpVersion;
        this.dumpResult = DumpResult.create( this.repositoryId );
    }

    public DumpResult execute()
    {
        getBranches().forEach( ( branch ) -> {
            setContext( branch ).runWith( this::doExecute );
        } );

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
        final BranchDumpResult.Builder branchDumpResult = BranchDumpResult.create( ContextAccessor.current().getBranch() );

        writer.writeDumpMeta( new DumpMeta( this.xpVersion ) );

        try
        {
            writer.open( this.repositoryId, ContextAccessor.current().getBranch() );
            final Node rootNode = this.nodeService.getRoot();
            dumpNode( rootNode.id(), branchDumpResult );
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

    private void dumpNode( final NodeId nodeId, final BranchDumpResult.Builder dumpResult )
    {
        final BatchedGetChildrenExecutor executor = BatchedGetChildrenExecutor.create().
            nodeService( this.nodeService ).
            parentId( nodeId ).
            recursive( true ).
            batchSize( 5000 ).
            childOrder( ChildOrder.from( "_path asc" ) ).
            build();

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
        dumpEntry.getAllVersionIds().forEach( writer::writeVersion );
        dumpEntry.getBinaryReferences().forEach( writer::writeBinary );
        dumpResult.metaWritten();
        dumpResult.addedVersions( dumpEntry.getAllVersionIds().size() );
    }

    private DumpEntry createDumpEntry( final NodeId nodeId )
    {
        final DumpEntry.Builder builder = DumpEntry.create().
            nodeId( nodeId );

        final Node currentNode = this.nodeService.getById( nodeId );

        builder.addVersion( MetaFactory.create( currentNode ) );

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
        final NodeVersionQueryResult result = this.nodeService.findVersions( GetNodeVersionsParams.create().
            size( -1 ).
            from( 0 ).
            nodeId( nodeId ).
            build() );

        for ( final NodeVersionMetadata metaData : result.getNodeVersionsMetadata() )
        {
            if ( metaData.getNodeVersionId().equals( currentVersion ) )
            {
                // Skip current, since this is dumped anyway
                continue;
            }

            if ( this.includeBinaries )
            {
                addVersionWithBinaries( builder, metaData );
            }
            else
            {
                builder.addVersion( MetaFactory.create( metaData ) );
            }
        }
    }

    private void addVersionWithBinaries( final DumpEntry.Builder builder, final NodeVersionMetadata metaData )
    {
        final NodeVersion nodeVersion = this.nodeService.getByNodeVersion( metaData.getNodeVersionId() );
        builder.addVersion( MetaFactory.create( metaData, nodeVersion ) );
        builder.addBinaryReferences(
            nodeVersion.getAttachedBinaries().stream().map( AttachedBinary::getBlobKey ).collect( Collectors.toSet() ) );
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

        public RepoDumper build()
        {
            return new RepoDumper( this );
        }
    }
}
