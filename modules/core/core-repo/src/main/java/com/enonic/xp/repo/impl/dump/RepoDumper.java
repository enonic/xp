package com.enonic.xp.repo.impl.dump;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Stopwatch;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobRecord;
import com.enonic.xp.blob.BlobStore;
import com.enonic.xp.blob.Segment;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.context.ContextBuilder;
import com.enonic.xp.node.FindNodesByParentParams;
import com.enonic.xp.node.FindNodesByParentResult;
import com.enonic.xp.node.GetNodeVersionsParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeService;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersionQueryResult;
import com.enonic.xp.repo.impl.dump.model.DumpEntry;
import com.enonic.xp.repo.impl.dump.model.Meta;
import com.enonic.xp.repo.impl.dump.writer.DumpWriter;
import com.enonic.xp.repository.Repository;
import com.enonic.xp.repository.RepositoryId;
import com.enonic.xp.repository.RepositoryService;

public class RepoDumper
{
    private final static String LINE_SEPARATOR = System.getProperty( "line.separator" );

    private final RepositoryId repositoryId;

    private final boolean includeVersions;

    private final boolean includeBinaries;

    private final BlobStore blobStore;

    private final NodeService nodeService;

    private final RepositoryService repositoryService;

    private final DumpWriter writer;

    private final Logger LOG = LoggerFactory.getLogger( RepoDumper.class );

    private RepoDumper( final Builder builder )
    {
        repositoryId = builder.repositoryId;
        includeVersions = builder.includeVersions;
        includeBinaries = builder.includeBinaries;
        nodeService = builder.nodeService;
        repositoryService = builder.repositoryService;
        this.blobStore = builder.blobStore;
        this.writer = builder.writer;
    }

    public DumpResult execute()
    {
        getBranches().forEach( ( branch ) -> {
            setContext( branch ).runWith( this::doExecute );
        } );

        return null;
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
        final Stopwatch timer = Stopwatch.createStarted();

        try
        {
            writer.open( this.repositoryId, ContextAccessor.current().getBranch() );
            final Node rootNode = this.nodeService.getRoot();
            dumpNode( rootNode.id() );
        }
        catch ( Exception e )
        {
            throw new RepoDumpException( "Error occured when dumping repository " + repositoryId, e );
        }
        finally
        {
            writer.close();
        }

        LOG.info( String.format( "dumped repo [%s (%s)] in %s", this.repositoryId, ContextAccessor.current().getBranch(), timer.stop() ) );
    }

    private void dumpNode( final NodeId nodeId )
    {
        final FindNodesByParentResult result = this.nodeService.findByParent( FindNodesByParentParams.create().
            from( 0 ).
            size( -1 ).
            parentId( nodeId ).
            build() );

        for ( final NodeId child : result.getNodeIds() )
        {
            dumpVersions( child );
            dumpNode( child );
        }
    }

    private void dumpVersions( final NodeId nodeId )
    {
        final Node currentNode = this.nodeService.getById( nodeId );

        if ( this.includeVersions )
        {
            dumpWithVersions( nodeId, currentNode );
        }
        else
        {
            dumpMainVersion( nodeId, currentNode );
        }
    }

    private void dumpMainVersion( final NodeId nodeId, final Node currentNode )
    {
        final DumpEntry entry = DumpEntry.create().
            nodeId( nodeId ).
            currentVersion( Meta.create().
                timestamp( currentNode.getTimestamp() ).
                nodePath( currentNode.path() ).
                version( currentNode.getNodeVersionId() ).
                nodeState( currentNode.getNodeState() ).
                build() ).
            build();

        addBlob( NodeVersionMetadata.create().
            nodeId( currentNode.id() ).
            nodePath( currentNode.path() ).
            nodeVersionId( currentNode.getNodeVersionId() ).
            timestamp( currentNode.getTimestamp() ).
            build() );

        writer.write( entry );
    }

    private void dumpWithVersions( final NodeId nodeId, final Node currentNode )
    {
        final NodeVersionQueryResult result = this.nodeService.findVersions( GetNodeVersionsParams.create().
            size( -1 ).
            from( 0 ).
            nodeId( nodeId ).
            build() );

        final DumpEntry.Builder builder = DumpEntry.create().
            nodeId( nodeId );

        for ( final NodeVersionMetadata metaData : result.getNodeVersionsMetadata() )
        {
            addVersionMetaData( currentNode, builder, metaData );
            addBlob( metaData );
        }

        writer.write( builder.build() );
    }

    private void addBlob( final NodeVersionMetadata metaData )
    {
        final BlobRecord blobRecord =
            this.blobStore.getRecord( Segment.from( "node" ), BlobKey.from( metaData.getNodeVersionId().toString() ) );

        writer.writeVersion( blobRecord.getKey(), blobRecord.getBytes() );
    }

    private void addVersionMetaData( final Node currentNode, final DumpEntry.Builder builder, final NodeVersionMetadata metaData )
    {
        if ( metaData.getNodeVersionId().equals( currentNode.getNodeVersionId() ) )
        {
            builder.currentVersion( doCreateMeta( metaData ) );
        }
        else
        {
            builder.addVersion( doCreateMeta( metaData ) );
        }
    }

    private Meta doCreateMeta( final NodeVersionMetadata metaData )
    {
        return Meta.create().
            nodePath( metaData.getNodePath() ).
            version( metaData.getNodeVersionId() ).
            nodeState( NodeState.DEFAULT ).
            timestamp( metaData.getTimestamp() ).
            build();
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

        private BlobStore blobStore;

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

        public Builder blobStore( final BlobStore blobStore )
        {
            this.blobStore = blobStore;
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
