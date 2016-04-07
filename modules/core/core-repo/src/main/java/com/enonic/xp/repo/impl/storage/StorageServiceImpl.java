package com.enonic.xp.repo.impl.storage;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.NodeVersions;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.ReturnFields;
import com.enonic.xp.repo.impl.ReturnValues;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.branch.storage.MoveBranchParams;
import com.enonic.xp.repo.impl.branch.storage.NodeBranchMetadata;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.branch.storage.NodesBranchMetadata;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.node.dao.NodeVersionDao;
import com.enonic.xp.repo.impl.version.NodeVersionDocumentId;
import com.enonic.xp.repo.impl.version.VersionService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component
public class StorageServiceImpl
    implements StorageService
{
    private VersionService versionService;

    private BranchService branchService;

    private NodeVersionDao nodeVersionDao;

    private IndexServiceInternal indexServiceInternal;

    private IndexedDataService indexedDataService;

    @Override
    public Node store( final Node node, final InternalContext context )
    {
        final NodeVersionId nodeVersionId = nodeVersionDao.store( node );

        storeVersionMetadata( node, context, nodeVersionId );

        storeBranchMetadata( node, context, nodeVersionId );

        indexNode( node, nodeVersionId, context );

        return Node.create( node ).
            nodeVersionId( nodeVersionId ).
            build();
    }

    @Override
    public Node move( final MoveNodeParams params, final InternalContext context )
    {
        final NodeBranchMetadata nodeBranchMetadata = this.branchService.get( params.getNode().id(), context );

        final NodeVersionId nodeVersionId;

        if ( params.isUpdateMetadataOnly() )
        {
            nodeVersionId = nodeBranchMetadata.getVersionId();

        }
        else
        {
            nodeVersionId = nodeVersionDao.store( params.getNode() );
        }

        storeVersionMetadata( params.getNode(), context, nodeVersionId );

        return moveInBranchAndReIndex( params.getNode(), nodeVersionId, nodeBranchMetadata.getNodePath(), context );
    }

    @Override
    public void delete( final NodeId nodeId, final InternalContext context )
    {
        branchService.delete( nodeId, context );

        indexServiceInternal.delete( nodeId, context );
    }

    @Override
    public Node updateMetadata( final Node node, final InternalContext context )
    {
        final NodeBranchMetadata nodeBranchMetadata = this.branchService.get( node.id(), context );

        if ( nodeBranchMetadata == null )
        {
            throw new NodeNotFoundException( "Cannot find node with id: " + node.id() + " in branch " + context.getBranch() );
        }

        final NodeVersionId nodeVersionId = nodeBranchMetadata.getVersionId();

        storeBranchMetadata( node, context, nodeVersionId );

        indexNode( node, nodeVersionId, context );

        return Node.create( node ).
            nodeVersionId( nodeVersionId ).
            build();
    }

    @Override
    public void updateVersion( final Node node, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        this.branchService.store( NodeBranchMetadata.create().
            nodeVersionId( nodeVersionId ).
            nodeId( node.id() ).
            nodeState( node.getNodeState() ).
            timestamp( node.getTimestamp() ).
            nodePath( node.path() ).
            build(), context );

        this.indexServiceInternal.store( node, context );
    }

    @Override
    public Node get( final NodeId nodeId, final InternalContext context )
    {
        final NodeBranchMetadata nodeBranchMetadata = this.branchService.get( nodeId, context );

        return doGetNode( nodeBranchMetadata );
    }

    @Override
    public Node get( final NodePath nodePath, final InternalContext context )
    {
        final NodeBranchMetadata nodeBranchMetadata = this.branchService.get( nodePath, context );

        return doGetNode( nodeBranchMetadata );
    }

    @Override
    public Nodes get( final NodeIds nodeIds, final InternalContext context )
    {
        final NodesBranchMetadata nodesBranchMetadata = this.branchService.get( nodeIds, context );

        return doReturnNodes( nodesBranchMetadata );
    }

    @Override
    public Nodes get( final NodePaths nodePaths, final InternalContext context )
    {
        final NodesBranchMetadata nodesBranchMetadata = this.branchService.get( nodePaths, context );

        return doReturnNodes( nodesBranchMetadata );
    }

    @Override
    public Node get( final NodeVersionId nodeVersionId, final InternalContext context )
    {
        final NodeVersion nodeVersion = nodeVersionDao.get( nodeVersionId );

        if ( nodeVersion == null )
        {
            return null;
        }

        final NodeBranchMetadata nodeBranchMetadata = branchService.get( nodeVersion.getId(), context );

        if ( nodeBranchMetadata == null )
        {
            return null;
        }

        return constructNode( nodeBranchMetadata, nodeVersion );
    }

    @Override
    public NodeVersion get( final NodeVersionMetadata nodeVersionMetadata )
    {
        return this.nodeVersionDao.get( nodeVersionMetadata.getNodeVersionId() );
    }

    @Override
    public ReturnValues getIndexedData( final NodeId nodeId, final ReturnFields returnFields, final InternalContext context )
    {
        return this.indexedDataService.get( nodeId, returnFields, context );
    }

    @Override
    public NodeBranchMetadata getBranchNodeVersion( final NodeId nodeId, final InternalContext context )
    {
        return this.branchService.get( nodeId, context );
    }

    @Override
    public NodesBranchMetadata getBranchNodeVersions( final NodeIds nodeIds, final InternalContext context )
    {
        return this.branchService.get( nodeIds, context );
    }

    @Override
    public NodeVersionMetadata getVersion( final NodeVersionDocumentId versionId, final InternalContext context )
    {
        return this.versionService.getVersion( versionId, context );
    }

    @Override
    public NodeId getIdForPath( final NodePath nodePath, final InternalContext context )
    {
        final NodeBranchMetadata nodeBranchMetadata = this.branchService.get( nodePath, context );

        return nodeBranchMetadata != null ? nodeBranchMetadata.getNodeId() : null;
    }

    @Override
    public NodePath getParentPath( final NodeId nodeId, final InternalContext context )
    {
        final NodeBranchMetadata nodeBranchMetadata = this.branchService.get( nodeId, context );

        return nodeBranchMetadata != null ? nodeBranchMetadata.getNodePath().getParentPath() : null;
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

    private Node doGetNode( final NodeBranchMetadata nodeBranchMetadata )
    {
        if ( nodeBranchMetadata == null )
        {
            return null;
        }

        final NodeVersion nodeVersion = nodeVersionDao.get( nodeBranchMetadata.getVersionId() );

        return constructNode( nodeBranchMetadata, nodeVersion );
    }

    private Node constructNode( final NodeBranchMetadata nodeBranchMetadata, final NodeVersion nodeVersion )
    {
        final Node node = NodeFactory.create( nodeVersion, nodeBranchMetadata );

        return canRead( node.getPermissions() ) ? node : null;
    }

    private void indexNode( final Node node, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        this.indexServiceInternal.store( Node.create( node ).
            nodeVersionId( nodeVersionId ).
            build(), context );
    }

    private void storeVersionMetadata( final Node node, final InternalContext context, final NodeVersionId nodeVersionId )
    {
        this.versionService.store( NodeVersionMetadata.create().
            nodeId( node.id() ).
            nodeVersionId( nodeVersionId ).
            nodePath( node.path() ).
            timestamp( node.getTimestamp() ).
            build(), context );
    }


    private Nodes doReturnNodes( final NodesBranchMetadata nodesBranchMetadata )
    {
        final NodeVersionIds.Builder builder = NodeVersionIds.create();
        nodesBranchMetadata.forEach( ( nodeBranchVersion ) -> builder.add( nodeBranchVersion.getVersionId() ) );

        final NodeVersions nodeVersions = nodeVersionDao.get( builder.build() );

        final Nodes.Builder filteredNodes = Nodes.create();

        nodeVersions.stream().filter( ( nodeVersion ) -> canRead( nodeVersion.getPermissions() ) ).forEach(
            ( nodeVersion ) -> filteredNodes.add( NodeFactory.create( nodeVersion, nodesBranchMetadata.get( nodeVersion.getId() ) ) ) );

        return filteredNodes.build();
    }

    private void storeBranchMetadata( final Node node, final InternalContext context, final NodeVersionId nodeVersionId )
    {
        this.branchService.store( NodeBranchMetadata.create().
            nodeVersionId( nodeVersionId ).
            nodeId( node.id() ).
            nodeState( node.getNodeState() ).
            timestamp( node.getTimestamp() ).
            nodePath( node.path() ).
            build(), context );
    }

    private Node moveInBranchAndReIndex( final Node node, final NodeVersionId nodeVersionId, final NodePath previousPath,
                                         final InternalContext context )
    {
        final NodeVersion nodeVersion = NodeVersion.from( node );

        this.branchService.move( MoveBranchParams.create().
            branchNodeVersion( NodeBranchMetadata.create().
                nodeVersionId( nodeVersionId ).
                nodeId( nodeVersion.getId() ).
                nodeState( node.getNodeState() ).
                timestamp( node.getTimestamp() ).
                nodePath( node.path() ).
                build() ).
            previousPath( previousPath ).
            build(), context );

        this.indexServiceInternal.store( node, context );

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
    public void setNodeVersionDao( final NodeVersionDao nodeVersionDao )
    {
        this.nodeVersionDao = nodeVersionDao;
    }

    @Reference
    public void setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }

    @Reference
    public void setIndexedDataService( final IndexedDataService indexedDataService )
    {
        this.indexedDataService = indexedDataService;
    }
}
