package com.enonic.xp.repo.impl.storage;

import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.blob.NodeVersionKeys;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.branch.Branches;
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
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersions;
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
        storeBranchMetadata( storeBranchMetadataParams, false );

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
        storeBranchMetadata( storeBranchMetadataParams, true );

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
    public Node move( final MoveNodeParams params, final InternalContext context )
    {
        final NodeVersionKey nodeVersionKey;
        final NodeBranchEntry nodeBranchEntry = this.branchService.get( params.getNode().id(), context );
        if ( params.isUpdateMetadataOnly() )
        {
            nodeVersionKey = nodeBranchEntry.getNodeVersionKey();
        }
        else
        {
            this.removeBranchFromCurrentVersion( nodeBranchEntry.getNodeId(), context );

            nodeVersionKey = nodeVersionService.store( NodeVersion.from( params.getNode() ), context );
        }

        NodeVersionId nodeVersionId = params.getNodeVersionId();
        if ( nodeVersionId == null )
        {
            nodeVersionId = new NodeVersionId();
            storeVersionMetadata( params.getNode(), nodeVersionId, nodeVersionKey, context );
        }

        final Node movedNode =
            moveInBranchAndReIndex( params.getNode(), nodeVersionId, nodeVersionKey, nodeBranchEntry.getNodePath(), context );

        this.addBranchToNewVersion( movedNode, nodeVersionId, context );

        return movedNode;
    }

    @Override
    public void delete( final NodeIds nodeIds, final InternalContext context )
    {
        final NodeBranchEntries nodeBranchEntries = branchService.get( nodeIds, true, context );
        nodeBranchEntries.forEach( nodeBranchEntry -> this.removeBranchFromCurrentVersion( nodeBranchEntry.getNodeId(), context ) );

        branchService.delete( nodeIds, context );
        indexDataService.delete( nodeIds, context );
    }

    @Override
    public Node updateMetadata( final Node node, final InternalContext context )
    {
        final NodeBranchEntry nodeBranchEntry = this.branchService.get( node.id(), context );

        if ( nodeBranchEntry == null )
        {
            throw new NodeNotFoundException( "Cannot find node with id: " + node.id() + " in branch " + context.getBranch() );
        }

        final NodeVersionId nodeVersionId = nodeBranchEntry.getVersionId();
        final NodeVersionKey nodeVersionKey = nodeBranchEntry.getNodeVersionKey();

        final StoreBranchMetadataParams storeBranchMetadataParams = StoreBranchMetadataParams.create().
            node( node ).
            nodeVersionId( nodeVersionId ).
            nodeVersionKey( nodeVersionKey ).
            context( context ).
            build();
        storeBranchMetadata( storeBranchMetadataParams, true );

        indexNode( node, nodeVersionId, context );

        return Node.create( node ).
            nodeVersionId( nodeVersionId ).
            build();
    }

    @Override
    public void updateVersion( final Node node, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        //TODO Check
        final NodeVersionMetadata nodeVersionMetadata = this.versionService.getVersion( node.id(), nodeVersionId, context );

        if ( nodeVersionMetadata == null )
        {
            throw new NodeNotFoundException( "Cannot find node version with id: " + nodeVersionId );
        }

        this.addBranchToNewVersion( node, nodeVersionId, context );

        this.branchService.store( NodeBranchEntry.create().
            nodeVersionId( nodeVersionId ).
            nodeVersionKey( nodeVersionMetadata.getNodeVersionKey() ).
            nodeId( node.id() ).
            nodeState( node.getNodeState() ).
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
            this.branchService.store( nodeBranchEntry, entry.getCurrentTargetPath(), InternalContext.create( context ).
                branch( entries.getTargetBranch() ).
                build() );
            if ( pushListener != null )
            {
                pushListener.nodesPushed( 1 );
            }

            final InternalContext internalContext = InternalContext.create( context ).branch( entries.getTargetBranch() ).build();
            this.addBranchToNewVersion( nodeBranchEntry, internalContext );
        }

        this.indexDataService.push( IndexPushNodeParams.create().
            nodeIds( entries.getNodeIds() ).
            targetBranch( entries.getTargetBranch() ).
            targetRepo( entries.getTargetRepo() ).
            pushListener( pushListener ).
            build(), context );
    }

    @Override
    public void push( final Node node, final Branch target, final InternalContext context )
    {
        final NodeBranchEntry entry = this.branchService.get( node.id(), context );

        this.branchService.store( entry, InternalContext.create( context ).
            branch( target ).
            build() );

        final InternalContext internalContext = InternalContext.create( context ).branch( target ).build();
        this.addBranchToNewVersion( node, entry.getVersionId(), internalContext );

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
                this.versionService.getVersion( routableNodeVersionId.getNodeId(), routableNodeVersionId.getNodeVersionId(), context );
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
    public Nodes get( final NodeIds nodeIds, final boolean keepOrder, final InternalContext context )
    {
        final NodeBranchEntries nodeBranchEntries = this.branchService.get( nodeIds, keepOrder, context );

        return doReturnNodes( nodeBranchEntries, context );
    }

    @Override
    public Nodes get( final NodePaths nodePaths, final InternalContext context )
    {
        final NodeBranchEntries nodeBranchEntries = this.branchService.get( nodePaths, context );

        return doReturnNodes( nodeBranchEntries, context );
    }

    @Override
    public Node get( final NodeId nodeId, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        final NodeVersionMetadata nodeVersionMetadata = versionService.getVersion( nodeId, nodeVersionId, context );

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
    public NodeBranchEntries getBranchNodeVersions( final NodeIds nodeIds, final boolean keepOrder, final InternalContext context )
    {
        return this.branchService.get( nodeIds, keepOrder, context );
    }

    @Override
    public NodeVersionMetadata getVersion( final NodeId nodeId, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        return this.versionService.getVersion( nodeId, nodeVersionId, context );
    }

    @Override
    public NodeCommitEntry getCommit( final NodeCommitId nodeCommitId, final InternalContext context )
    {
        return this.commitService.get( nodeCommitId, context );
    }

    @Override
    public NodeId getIdForPath( final NodePath nodePath, final InternalContext context )
    {
        final NodeBranchEntry nodeBranchEntry = this.branchService.get( nodePath, context );

        return nodeBranchEntry != null ? nodeBranchEntry.getNodeId() : null;
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
        final NodeVersionMetadata nodeVersionMetadata = versionService.getVersion( nodeId, nodeVersionId, context );

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

    @Override
    public Node getNode( final NodePath nodePath, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        final NodeId nodeId = getIdForPath( nodePath, context );

        if ( nodeId == null )
        {
            return null;
        }

        return getNode( nodeId, nodeVersionId, context );
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
        final Node node = NodeFactory.create( nodeVersion, nodeBranchEntry );

        return canRead( node.getPermissions() ) ? node : null;
    }

    private Node constructNode( final NodeVersion nodeVersion, final NodeVersionMetadata nodeVersionMetadata )
    {
        final Node node = NodeFactory.create( nodeVersion, nodeVersionMetadata );

        return canRead( node.getPermissions() ) ? node : null;
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
            setBranches( Branches.from( context.getBranch() ) ).
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

    private Nodes doReturnNodes( final NodeBranchEntries nodeBranchEntries, final InternalContext context )
    {
        final NodeVersionKeys.Builder builder = NodeVersionKeys.create();
        nodeBranchEntries.stream().
            map( NodeBranchEntry::getNodeVersionKey ).
            forEach( builder::add );

        final NodeVersions nodeVersions = nodeVersionService.get( builder.build(), context );

        final Nodes.Builder filteredNodes = Nodes.create();

        nodeVersions.stream().filter( ( nodeVersion ) -> canRead( nodeVersion.getPermissions() ) ).forEach(
            ( nodeVersion ) -> filteredNodes.add( NodeFactory.create( nodeVersion, nodeBranchEntries.get( nodeVersion.getId() ) ) ) );

        return filteredNodes.build();
    }

    private void storeBranchMetadata( final StoreBranchMetadataParams storeBranchMetadataParams, boolean updateVersion )
    {
        final Node node = storeBranchMetadataParams.getNode();

        this.updateBranchesInVersion( node, storeBranchMetadataParams.getNodeVersionId(), storeBranchMetadataParams.getContext(),
                                      updateVersion );

        this.branchService.store( NodeBranchEntry.create().
            nodeVersionId( storeBranchMetadataParams.getNodeVersionId() ).
            nodeVersionKey( storeBranchMetadataParams.getNodeVersionKey() ).
            nodeId( node.id() ).
            nodeState( node.getNodeState() ).
            timestamp( node.getTimestamp() ).
            nodePath( node.path() ).
            build(), storeBranchMetadataParams.getContext() );
    }

    private void updateBranchesInVersion( final Node node, final NodeVersionId newVersionId, final InternalContext internalContext,
                                          boolean updateVersion )
    {
        this.removeBranchFromCurrentVersion( node.id(), internalContext );
        if ( updateVersion )
        {
            this.addBranchToNewVersion( node, newVersionId, internalContext );
        }
    }

    private void removeBranchFromCurrentVersion( final NodeId nodeId, InternalContext internalContext )
    {
        final NodeBranchEntry oldNodeBranchEntry = branchService.get( nodeId, internalContext );
        if ( oldNodeBranchEntry != null )
        {
            final NodeVersionMetadata oldNodeVersionMetadata =
                this.versionService.getVersion( oldNodeBranchEntry.getNodeId(), oldNodeBranchEntry.getVersionId(), internalContext );

            if ( oldNodeVersionMetadata != null )
            {
                final Branches newBranches = Branches.from( oldNodeVersionMetadata.getBranches().
                    stream().
                    filter( branch -> !branch.equals( internalContext.getBranch() ) ).
                    collect( Collectors.toSet() ) );

                this.versionService.store( NodeVersionMetadata.create( oldNodeVersionMetadata ).
                    setBranches( newBranches ).build(), internalContext );
            }
        }
    }

    private void addBranchToNewVersion( final NodeBranchEntry node, final InternalContext internalContext )
    {
        this.doAddBranchToNewVersion( node.getNodeId(), node.getNodeState(), node.getVersionId(), internalContext );
    }

    private void addBranchToNewVersion( final Node node, final NodeVersionId newVersionId, final InternalContext internalContext )
    {
        this.doAddBranchToNewVersion( node.id(), node.getNodeState(), newVersionId, internalContext );
    }

    private void doAddBranchToNewVersion( final NodeId nodeId, final NodeState nodeState, final NodeVersionId newVersionId,
                                          final InternalContext internalContext )
    {
        final NodeVersionMetadata nodeVersionMetadata = this.versionService.getVersion( nodeId, newVersionId, internalContext );

        final Branches newBranches = Branches.from(
            Stream.<Branch>concat( nodeVersionMetadata.getBranches().stream(), Stream.<Branch>of( internalContext.getBranch() ) ).
                filter( branch -> !NodeState.PENDING_DELETE.equals( nodeState ) || !branch.equals( internalContext.getBranch() ) ).collect(
                Collectors.toSet() ) );

        this.versionService.store( NodeVersionMetadata.create( nodeVersionMetadata ).
            setBranches( newBranches ).
            build(), internalContext );
    }

    private Node moveInBranchAndReIndex( final Node node, final NodeVersionId nodeVersionId, final NodeVersionKey nodeVersionKey,
                                         final NodePath previousPath, final InternalContext context )
    {
        this.branchService.store( NodeBranchEntry.create().
            nodeVersionId( nodeVersionId ).
            nodeVersionKey( nodeVersionKey ).
            nodeId( node.id() ).
            nodeState( node.getNodeState() ).
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
