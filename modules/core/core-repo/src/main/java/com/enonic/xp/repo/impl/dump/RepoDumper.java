package com.enonic.xp.repo.impl.dump;

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.repo.impl.dump.model.DumpEntry;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

public class RepoDumper
{
    private final RepositoryId repositoryId;

    private final boolean includeVersions;

    private final boolean includeBinaries;

    private final NodeService nodeService;

    private final RepositoryService repositoryService;

    private final DumpWriter writer;

    private final Logger LOG = LoggerFactory.getLogger( RepoDumper.class );

    private final DumpResult.Builder dumpResult;

    private ProgressReporter reporter;

    private RepoDumper( final Builder builder )
    {
        repositoryId = builder.repositoryId;
        includeVersions = builder.includeVersions;
        includeBinaries = builder.includeBinaries;
        nodeService = builder.nodeService;
        repositoryService = builder.repositoryService;
        this.writer = builder.writer;
        this.dumpResult = DumpResult.create();
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
        this.reporter = new ProgressReporter();

        final BranchDumpResult.Builder branchDumpResult = BranchDumpResult.create( ContextAccessor.current().getBranch() );

        try
        {
            writer.open( this.repositoryId, ContextAccessor.current().getBranch() );
            final Node rootNode = this.nodeService.getRoot();
            dumpNode( rootNode.id(), branchDumpResult );
        }
        catch ( Exception e )
        {
            throw new RepoDumpException( "Error occured when dumping repository " + repositoryId, e );
        }
        finally
        {
            writer.close();
        }

        System.out.println( "Creating dump-entries: " + this.reporter );

        this.dumpResult.add( branchDumpResult.build() );
    }

    private void dumpNode( final NodeId nodeId, final BranchDumpResult.Builder dumpResult )
    {
        // TODO: Dump root node?
        final FindNodesByParentResult result = this.nodeService.findByParent( FindNodesByParentParams.create().
            from( 0 ).
            size( -1 ).
            parentId( nodeId ).
            build() );

        for ( final NodeId child : result.getNodeIds() )
        {
            doDumpNode( child, dumpResult );
            dumpNode( child, dumpResult );
        }
    }

    private void doDumpNode( final NodeId nodeId, final BranchDumpResult.Builder dumpResult )
    {
        final DumpEntry dumpEntry = createDumpEntry( nodeId );

        final Stopwatch metaTimer = Stopwatch.createStarted();
        writer.writeMetaData( dumpEntry );
        this.reporter.writeMetaData( metaTimer.stop().elapsed( TimeUnit.NANOSECONDS ) );

        final Stopwatch versionTimer = Stopwatch.createStarted();
        dumpEntry.getAllVersionIds().forEach( writer::writeVersion );
        this.reporter.writeVersion( versionTimer.stop().elapsed( TimeUnit.NANOSECONDS ) );

        final Stopwatch binaryTimer = Stopwatch.createStarted();
        dumpEntry.getBinaryReferences().forEach( writer::writeBinary );
        this.reporter.writeBinary( binaryTimer.stop().elapsed( TimeUnit.NANOSECONDS ) );

        dumpResult.metaWritten();
        dumpResult.addedVersions( dumpEntry.getAllVersionIds().size() );
    }

    private DumpEntry createDumpEntry( final NodeId nodeId )
    {
        Stopwatch timer = Stopwatch.createStarted();

        final DumpEntry.Builder builder = DumpEntry.create().
            nodeId( nodeId );

        final Node currentNode = this.nodeService.getById( nodeId );

        builder.addVersion( MetaFactory.create( currentNode ) );
        builder.addBinaryReferences(
            currentNode.getAttachedBinaries().stream().map( AttachedBinary::getBlobKey ).collect( Collectors.toSet() ) );

        if ( this.includeVersions )
        {
            final NodeVersionQueryResult result = this.nodeService.findVersions( GetNodeVersionsParams.create().
                size( -1 ).
                from( 0 ).
                nodeId( nodeId ).
                build() );

            for ( final NodeVersionMetadata metaData : result.getNodeVersionsMetadata() )
            {
                if ( metaData.getNodeVersionId().equals( currentNode.getNodeVersionId() ) )
                {
                    continue;
                }

                if ( this.includeBinaries )
                {
                    final NodeVersion nodeVersion = this.nodeService.getByNodeVersion( metaData.getNodeVersionId() );
                    builder.addVersion( MetaFactory.create( metaData, nodeVersion ) );
                    builder.addBinaryReferences(
                        nodeVersion.getAttachedBinaries().stream().map( AttachedBinary::getBlobKey ).collect( Collectors.toSet() ) );
                }
                else
                {
                    builder.addVersion( MetaFactory.create( metaData ) );
                }
            }
        }

        timer.stop();

        reporter.createdDumpEntry( timer.elapsed( TimeUnit.NANOSECONDS ) );

        return builder.build();
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

        public RepoDumper build()
        {
            return new RepoDumper( this );
        }
    }
}
