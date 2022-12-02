package com.enonic.xp.repo.impl.storage;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.google.common.collect.ImmutableSet;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeBranchEntries;
import com.enonic.xp.node.NodeBranchEntry;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.PushNodeEntries;
import com.enonic.xp.node.PushNodeEntry;
import com.enonic.xp.node.PushNodesListener;
import com.enonic.xp.node.RoutableNodeVersionId;
import com.enonic.xp.node.RoutableNodeVersionIds;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.commit.CommitService;
import com.enonic.xp.repo.impl.node.dao.NodeVersionService;
import com.enonic.xp.repo.impl.version.VersionService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component
public class NodeStorageServiceImpl
    implements NodeStorageService
{
    private VersionService versionService;

    private BranchService branchService;

    private CommitService commitService;

    private NodeVersionService nodeVersionService;

    private IndexDataService indexDataService;

    @Override
    public Node store( final Node node, final InternalContext context )
    {
        final NodeVersionId nodeVersionId = new NodeVersionId();
        final NodeVersionKey nodeVersionKey = nodeVersionService.store( NodeVersion.from( node ), context );

        storeVersionMetadata( node, nodeVersionId, nodeVersionKey, context );

        final StoreBranchMetadataParams storeBranchMetadataParams = StoreBranchMetadataParams.create().
            node( node ).
            nodeVersionId( nodeVersionId ).
            nodeVersionKey( nodeVersionKey ).
            context( context ).
            build();
        storeBranchMetadata( storeBranchMetadataParams );

        indexNode( node, nodeVersionId, context );

        return Node.create( node ).
            nodeVersionId( nodeVersionId ).
            build();
    }

    @Override
    public Node load( final LoadNodeParams params, final InternalContext context )
    {
        final Node node = params.getNode();
        final NodeVersion nodeVersion = NodeVersion.create().
            id( node.id() ).
            nodeType( node.getNodeType() ).
            data( node.data() ).
            indexConfigDocument( node.getIndexConfigDocument() ).
            childOrder( node.getChildOrder() ).
            manualOrderValue( node.getManualOrderValue() ).
            permissions( node.getPermissions() ).
            inheritPermissions( node.inheritsPermissions() ).
            attachedBinaries( node.getAttachedBinaries() ).
            build();

        final NodeVersionId nodeVersionId = node.getNodeVersionId();
        final NodeVersionKey nodeVersionKey = nodeVersionService.store( nodeVersion, context );

        final LoadVersionMetadataParams loadVersionMetadataParams = LoadVersionMetadataParams.create().
            node( node ).
            nodeVersionId( nodeVersionId ).
            nodeVersionKey( nodeVersionKey ).
            nodeCommitId( params.getNodeCommitId() ).
            context( context ).
            build();
        loadVersionMetadata( loadVersionMetadataParams );

        final StoreBranchMetadataParams storeBranchMetadataParams = StoreBranchMetadataParams.create().
            node( node ).
            nodeVersionId( nodeVersionId ).
            nodeVersionKey( nodeVersionKey ).
            context( context ).
            build();
        storeBranchMetadata( storeBranchMetadataParams );

        indexNode( node, nodeVersionId, context );

        return Node.create( node ).
            nodeVersionId( nodeVersionId ).
            build();
    }

    @Override
    public void storeVersion( final StoreNodeVersionParams params, final InternalContext context )
    {
        final NodeVersionKey nodeVersionKey = this.nodeVersionService.store( params.getNodeVersion(), context );

        this.versionService.store( NodeVersionMetadata.create().
            nodeVersionId( params.getNodeVersionId() ).
            nodeVersionKey( nodeVersionKey ).
            binaryBlobKeys( getBinaryBlobKeys( params.getNodeVersion().getAttachedBinaries() ) ).
            nodeId( params.getNodeId() ).
            nodePath( params.getNodePath() ).
            nodeCommitId( params.getNodeCommitId() ).
            timestamp( params.getTimestamp() ).
            build(), context );
    }

    @Override
    public void storeCommit( final StoreNodeCommitParams params, final InternalContext context )
    {
        final NodeCommitEntry nodeCommitEntry = NodeCommitEntry.create().
            nodeCommitId( params.getNodeCommitId() ).
            message( params.getMessage() ).
            committer( params.getCommitter() ).
            timestamp( params.getTimestamp() ).
            build();
        this.commitService.store( nodeCommitEntry, context );
    }

    @Override
    public Node move( final StoreMovedNodeParams params, final InternalContext context )
    {
        final NodeBranchEntry nodeBranchEntry = this.branchService.get( params.getNode().id(), context );

        final NodeVersionKey nodeVersionKey = nodeVersionService.store( NodeVersion.from( params.getNode() ), context );

        NodeVersionId nodeVersionId = params.getNodeVersionId();

        if ( nodeVersionId == null )
        {
            nodeVersionId = new NodeVersionId();
            storeVersionMetadata( params.getNode(), nodeVersionId, nodeVersionKey, context );
        }

        return moveInBranchAndReIndex( params.getNode(), nodeVersionId, nodeVersionKey, nodeBranchEntry.getNodePath(), context );
    }

    @Override
    public void delete( final List<NodeBranchEntry> entries, final InternalContext context )
    {
        if ( entries.isEmpty() )
        {
            return;
        }

        branchService.delete( entries, context );
        indexDataService.delete(
            NodeIds.from( entries.stream().map( NodeBranchEntry::getNodeId ).collect( ImmutableSet.toImmutableSet() ) ), context );
    }

    @Override
    public void updateVersion( final Node node, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        //TODO Check
        final NodeVersionMetadata nodeVersionMetadata = this.versionService.getVersion( nodeVersionId, context );

        if ( nodeVersionMetadata == null )
        {
            throw new NodeNotFoundException( "Cannot find node version with id: " + nodeVersionId );
        }

        this.branchService.store( NodeBranchEntry.create().
            nodeVersionId( nodeVersionId ).
            nodeVersionKey( nodeVersionMetadata.getNodeVersionKey() ).
            nodeId( node.id() ).
            timestamp( node.getTimestamp() ).
            nodePath( node.path() ).
            build(), context );

        this.indexDataService.store( node, context );
    }

    @Override
    public void push( final PushNodeEntries entries, final PushNodesListener pushListener, final InternalContext context )
    {
        for ( final PushNodeEntry entry : entries )
        {
            final NodeBranchEntry nodeBranchEntry = entry.getNodeBranchEntry();
            this.branchService.store( nodeBranchEntry, entry.getCurrentTargetPath(),
                                      InternalContext.create( context ).branch( entries.getTargetBranch() ).build() );
            pushListener.nodesPushed( 1 );
        }

        final NodeIds nodeIds = NodeIds.from(
            entries.stream().map( entry -> entry.getNodeBranchEntry().getNodeId() ).collect( ImmutableSet.toImmutableSet() ) );

        this.indexDataService.push( IndexPushNodeParams.create()
                                        .nodeIds( nodeIds )
                                        .targetBranch( entries.getTargetBranch() )
                                        .targetRepo( entries.getTargetRepo() )
                                        .build(), context );
    }

    @Override
    public void push( final Node node, final Branch target, final InternalContext context )
    {
        final NodeBranchEntry entry = this.branchService.get( node.id(), context );

        this.branchService.store( entry, InternalContext.create( context ).
            branch( target ).
            build() );

        this.indexDataService.push( IndexPushNodeParams.create().
            nodeIds( NodeIds.from( node.id() ) ).
            targetBranch( target ).
            targetRepo( context.getRepositoryId() ).
            build(), context );
    }

    @Override
    public NodeCommitEntry commit( final NodeCommitEntry nodeCommitEntry, final RoutableNodeVersionIds routableNodeVersionIds,
                                   final InternalContext context )
    {
        final NodeCommitId nodeCommitId = new NodeCommitId();
        final NodeCommitEntry updatedCommitEntry = NodeCommitEntry.create( nodeCommitEntry ).
            nodeCommitId( nodeCommitId ).
            build();
        this.commitService.store( updatedCommitEntry, context );
        for ( RoutableNodeVersionId routableNodeVersionId : routableNodeVersionIds )
        {
            final NodeVersionMetadata existingVersion =
                this.versionService.getVersion( routableNodeVersionId.getNodeVersionId(), context );
            final NodeVersionMetadata updatedVersion = NodeVersionMetadata.create( existingVersion ).
                nodeCommitId( nodeCommitId ).
                build();
            this.versionService.store( updatedVersion, context );
        }
        return updatedCommitEntry;
    }

    @Override
    public Node get( final NodeId nodeId, final InternalContext context )
    {
        final NodeBranchEntry nodeBranchEntry = this.branchService.get( nodeId, context );

        return doGetNode( nodeBranchEntry, context );
    }

    @Override
    public Node get( final NodePath nodePath, final InternalContext context )
    {
        final NodeBranchEntry nodeBranchEntry = this.branchService.get( nodePath, context );

        return doGetNode( nodeBranchEntry, context );
    }

    @Override
    public Nodes get( final NodeIds nodeIds, final InternalContext context )
    {
        final Stream<NodeBranchEntry> stream = this.branchService.get( nodeIds, context ).stream();
        return doReturnNodes( stream, context );
    }

    @Override
    public Nodes get( final NodePaths nodePaths, final InternalContext context )
    {
        final Stream<NodeBranchEntry> stream = nodePaths.stream()
            .map( nodePath -> this.branchService.get( nodePath, context ) )
            .filter( Objects::nonNull );
        return doReturnNodes( stream, context );
    }

    @Override
    public Node get( final NodeId nodeId, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        final NodeVersionMetadata nodeVersionMetadata = versionService.getVersion(  nodeVersionId, context );

        if ( nodeVersionMetadata == null )
        {
            return null;
        }

        final NodeVersion nodeVersion = nodeVersionService.get( nodeVersionMetadata.getNodeVersionKey(), context );

        if ( nodeVersion == null )
        {
            return null;
        }

        final NodeBranchEntry nodeBranchEntry = branchService.get( nodeVersionMetadata.getNodeId(), context );

        if ( nodeBranchEntry == null )
        {
            return null;
        }

        return constructNode( nodeBranchEntry, nodeVersion );
    }

    @Override
    public NodeVersion getNodeVersion( final NodeVersionKey nodeVersionKey, final InternalContext context )
    {
        return this.nodeVersionService.get( nodeVersionKey, context );
    }

    @Override
    public NodeBranchEntry getBranchNodeVersion( final NodeId nodeId, final InternalContext context )
    {
        return this.branchService.get( nodeId, context );
    }

    @Override
    public NodeBranchEntries getBranchNodeVersions( final NodeIds nodeIds, final InternalContext context )
    {
        return this.branchService.get( nodeIds, context );
    }

    @Override
    public NodeVersionMetadata getVersion( final NodeId nodeId, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        return this.versionService.getVersion( nodeVersionId, context );
    }

    @Override
    public NodeCommitEntry getCommit( final NodeCommitId nodeCommitId, final InternalContext context )
    {
        return this.commitService.get( nodeCommitId, context );
    }

    @Override
    public NodeBranchEntry getBranchNodeVersion( final NodePath nodePath, final InternalContext context )
    {
        return this.branchService.get( nodePath, context );
    }

    @Override
    public void invalidate()
    {
        this.branchService.evictAllPaths();
    }

    @Override
    public void handleNodeCreated( final NodeId nodeId, final NodePath nodePath, final InternalContext context )
    {
        this.branchService.cachePath( nodeId, nodePath, context );
    }

    @Override
    public void handleNodeDeleted( final NodeId nodeId, final NodePath nodePath, final InternalContext context )
    {
        this.branchService.evictPath( nodePath, context );
    }

    @Override
    public void handleNodeMoved( final NodeMovedParams params, final InternalContext context )
    {
        this.branchService.evictPath( params.getExistingPath(), context );
        this.branchService.cachePath( params.getNodeId(), params.getNewPath(), context );
    }

    @Override
    public void handleNodePushed( final NodeId nodeId, final NodePath nodePath, final NodePath currentTargetPath,
                                  final InternalContext context )
    {
        if ( !nodePath.equals( currentTargetPath ) )
        {
            if ( currentTargetPath != null )
            {
                this.branchService.evictPath( currentTargetPath, context );
            }
            this.branchService.cachePath( nodeId, nodePath, context );
        }
    }

    @Override
    public Node getNode( final NodeId nodeId, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        final NodeVersionMetadata nodeVersionMetadata = versionService.getVersion( nodeVersionId, context );

        if ( nodeVersionMetadata == null )
        {
            return null;
        }

        final NodeVersion nodeVersion = nodeVersionService.get( nodeVersionMetadata.getNodeVersionKey(), context );

        if ( nodeVersion == null )
        {
            return null;
        }

        return constructNode( nodeVersion, nodeVersionMetadata );
    }

    private Node doGetNode( final NodeBranchEntry nodeBranchEntry, final InternalContext context )
    {
        if ( nodeBranchEntry == null )
        {
            return null;
        }

        final NodeVersion nodeVersion = nodeVersionService.get( nodeBranchEntry.getNodeVersionKey(), context );

        return constructNode( nodeBranchEntry, nodeVersion );
    }

    private Node constructNode( final NodeBranchEntry nodeBranchEntry, final NodeVersion nodeVersion )
    {
        return canRead( nodeVersion.getPermissions() ) ? NodeFactory.create( nodeVersion, nodeBranchEntry ) : null;
    }

    private Node constructNode( final NodeVersion nodeVersion, final NodeVersionMetadata nodeVersionMetadata )
    {
        return canRead( nodeVersion.getPermissions() ) ? NodeFactory.create( nodeVersion, nodeVersionMetadata ) : null;
    }

    private void indexNode( final Node node, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        this.indexDataService.store( Node.create( node ).
            nodeVersionId( nodeVersionId ).
            build(), context );
    }

    private void storeVersionMetadata( final Node node, final NodeVersionId nodeVersionId, final NodeVersionKey nodeVersionKey,
                                       final InternalContext context )
    {
        this.versionService.store( NodeVersionMetadata.create().
            nodeId( node.id() ).
            nodeVersionId( nodeVersionId ).
            nodeVersionKey( nodeVersionKey ).
            binaryBlobKeys( getBinaryBlobKeys( node.getAttachedBinaries() ) ).
            nodePath( node.path() ).
            timestamp( node.getTimestamp() ).
            build(), context );
    }

    private BlobKeys getBinaryBlobKeys( final AttachedBinaries attachedBinaries )
    {
        final BlobKeys.Builder blobKeys = BlobKeys.create();
        if ( attachedBinaries != null )
        {
            attachedBinaries.stream().
                map( AttachedBinary::getBlobKey ).
                map( BlobKey::from ).
                forEach( blobKeys::add );
        }
        return blobKeys.build();
    }

    private void loadVersionMetadata( final LoadVersionMetadataParams loadVersionMetadataParams )
    {
        final Node node = loadVersionMetadataParams.getNode();
        this.versionService.store( NodeVersionMetadata.create().
            nodeId( node.id() ).
            nodeVersionId( loadVersionMetadataParams.getNodeVersionId() ).
            nodeVersionKey( loadVersionMetadataParams.getNodeVersionKey() ).
            binaryBlobKeys( getBinaryBlobKeys( node.getAttachedBinaries() ) ).
            nodePath( node.path() ).
            nodeCommitId( loadVersionMetadataParams.getNodeCommitId() ).
            timestamp( node.getTimestamp() ).
            build(), loadVersionMetadataParams.getContext() );
    }

    private Nodes doReturnNodes( final Stream<NodeBranchEntry> nodeBranchEntries, final InternalContext context )
    {
        final Nodes.Builder filteredNodes = Nodes.create();

        nodeBranchEntries
            .map( nodeBranchEntry -> Map.entry( nodeBranchEntry, nodeVersionService.get( nodeBranchEntry.getNodeVersionKey(), context ) ) )
            .filter( entry -> canRead( entry.getValue().getPermissions() ) )
            .map( entry -> NodeFactory.create( entry.getValue(), entry.getKey() ) )
            .forEach( filteredNodes::add );

        return filteredNodes.build();
    }

    private void storeBranchMetadata( final StoreBranchMetadataParams storeBranchMetadataParams )
    {
        final Node node = storeBranchMetadataParams.getNode();
        this.branchService.store( NodeBranchEntry.create().
            nodeVersionId( storeBranchMetadataParams.getNodeVersionId() ).
            nodeVersionKey( storeBranchMetadataParams.getNodeVersionKey() ).
            nodeId( node.id() ).
            timestamp( node.getTimestamp() ).
            nodePath( node.path() ).
            build(), storeBranchMetadataParams.getContext() );
    }

    private Node moveInBranchAndReIndex( final Node node, final NodeVersionId nodeVersionId, final NodeVersionKey nodeVersionKey,
                                         final NodePath previousPath, final InternalContext context )
    {
        this.branchService.store( NodeBranchEntry.create().
            nodeVersionId( nodeVersionId ).
            nodeVersionKey( nodeVersionKey ).
            nodeId( node.id() ).
            timestamp( node.getTimestamp() ).
            nodePath( node.path() ).
            build(), previousPath, context );

        this.indexDataService.store( node, context );

        return node;
    }

    private boolean canRead( final AccessControlList permissions )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        if ( authInfo.getPrincipals().contains( RoleKeys.ADMIN ) )
        {
            return true;
        }

        return permissions.isAllowedFor( authInfo.getPrincipals(), Permission.READ );
    }

    @Reference
    public void setVersionService( final VersionService versionService )
    {
        this.versionService = versionService;
    }

    @Reference
    public void setBranchService( final BranchService branchService )
    {
        this.branchService = branchService;
    }

    @Reference
    public void setCommitService( final CommitService commitService )
    {
        this.commitService = commitService;
    }

    @Reference
    public void setNodeVersionService( final NodeVersionService nodeVersionService )
    {
        this.nodeVersionService = nodeVersionService;
    }

    @Reference
    public void setIndexDataService( final IndexDataService indexDataService )
    {
        this.indexDataService = indexDataService;
    }
}
