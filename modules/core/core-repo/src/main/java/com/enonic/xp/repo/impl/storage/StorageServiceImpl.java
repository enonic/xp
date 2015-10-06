package com.enonic.xp.repo.impl.storage;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.context.ContextAccessor;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeState;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionIds;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.RootNode;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.branch.MoveBranchDocument;
import com.enonic.xp.repo.impl.branch.StoreBranchDocument;
import com.enonic.xp.repo.impl.branch.storage.BranchNodeVersion;
import com.enonic.xp.repo.impl.branch.storage.BranchNodeVersions;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.node.dao.NodeDao;
import com.enonic.xp.repo.impl.version.NodeVersionDocument;
import com.enonic.xp.repo.impl.version.NodeVersionDocumentId;
import com.enonic.xp.repo.impl.version.VersionService;
import com.enonic.xp.security.RoleKeys;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.security.auth.AuthenticationInfo;

@Component
public class StorageServiceImpl
    implements StorageService
{
    private VersionService versionService;

    private BranchService branchService;

    private NodeDao nodeDao;

    private IndexServiceInternal indexServiceInternal;

    @Override
    public Node store( final Node node, final InternalContext context )
    {
        final NodeVersionId nodeVersionId = nodeDao.store( node );
        doStoreVersion( node, context, nodeVersionId );

        return storeBranchAndIndex( node, context, nodeVersionId );
    }

    private void doStoreVersion( final Node node, final InternalContext context, final NodeVersionId nodeVersionId )
    {
        this.versionService.store( NodeVersionDocument.create().
            nodeId( node.id() ).
            nodeVersionId( nodeVersionId ).
            nodePath( node.path() ).
            timestamp( node.getTimestamp() ).
            build(), context );
    }

    @Override
    public Node move( final MoveNodeParams params, final InternalContext context )
    {
        final BranchNodeVersion branchNodeVersion = this.branchService.get( params.getNode().id(), context );

        final NodeVersionId nodeVersionId;

        if ( params.isUpdateMetadataOnly() )
        {
            nodeVersionId = branchNodeVersion.getVersionId();

        }
        else
        {
            nodeVersionId = nodeDao.store( params.getNode() );
        }

        doStoreVersion( params.getNode(), context, nodeVersionId );

        return moveInBranchAndIndex( params.getNode(), nodeVersionId, branchNodeVersion.getNodePath(), context );
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
        final BranchNodeVersion branchNodeVersion = this.branchService.get( node.id(), context );

        if ( branchNodeVersion == null )
        {
            throw new NodeNotFoundException( "Cannot find node with id: " + node.id() + " in branch " + context.getBranch() );
        }

        final NodeVersionId nodeVersionId = branchNodeVersion.getVersionId();

        return storeBranchAndIndex( node, context, nodeVersionId );
    }

    @Override
    public void updateVersion( final Node node, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        this.branchService.store( StoreBranchDocument.create().
            nodeVersionId( nodeVersionId ).
            node( node ).
            build(), context );

        this.indexServiceInternal.store( node, nodeVersionId, context );
    }

    @Override
    public Node get( final NodeId nodeId, final InternalContext context )
    {
        final BranchNodeVersion branchNodeVersion = this.branchService.get( nodeId, context );

        return doGetNode( branchNodeVersion );
    }

    @Override
    public Node get( final NodePath nodePath, final InternalContext context )
    {
        final BranchNodeVersion branchNodeVersion = this.branchService.get( nodePath, context );

        return doGetNode( branchNodeVersion );
    }

    @Override
    public Nodes get( final NodeIds nodeIds, final InternalContext context )
    {
        final BranchNodeVersions branchNodeVersions = this.branchService.get( nodeIds, InternalContext.from( ContextAccessor.current() ) );

        return doReturnNodes( branchNodeVersions );
    }

    @Override
    public Nodes get( final NodePaths nodePaths, final InternalContext context )
    {
        final BranchNodeVersions branchNodeVersions =
            this.branchService.get( nodePaths, InternalContext.from( ContextAccessor.current() ) );

        return doReturnNodes( branchNodeVersions );
    }

    @Override
    public Node get( final NodeVersion nodeVersion )
    {
        final Node node = this.nodeDao.get( nodeVersion.getNodeVersionId() );

        return populateWithMetaData( node, nodeVersion );
    }

    @Override
    public BranchNodeVersion getBranchNodeVersion( final NodeId nodeId, final InternalContext context )
    {
        return this.branchService.get( nodeId, context );
    }

    @Override
    public BranchNodeVersions getBranchNodeVersions( final NodeIds nodeIds, final InternalContext context )
    {
        return this.branchService.get( nodeIds, context );
    }

    @Override
    public NodeVersion getVersion( final NodeVersionDocumentId versionId, final InternalContext context )
    {
        return this.versionService.getVersion( versionId, context );
    }

    @Override
    public NodeId getIdForPath( final NodePath nodePath, final InternalContext context )
    {
        final BranchNodeVersion branchNodeVersion = this.branchService.get( nodePath, context );

        return branchNodeVersion != null ? branchNodeVersion.getNodeId() : null;
    }

    @Override
    public NodePath getParentPath( final NodeId nodeId, final InternalContext context )
    {
        final BranchNodeVersion branchNodeVersion = this.branchService.get( nodeId, context );

        return branchNodeVersion != null ? branchNodeVersion.getNodePath().getParentPath() : null;
    }

    private Node doGetNode( final BranchNodeVersion branchNodeVersion )
    {
        if ( branchNodeVersion == null )
        {
            return null;
        }

        final Node node = nodeDao.get( branchNodeVersion.getVersionId() );

        return canRead( node ) ? populateWithMetaData( node, branchNodeVersion ) : null;
    }


    private Node populateWithMetaData( final Node node, final BranchNodeVersion branchNodeVersion )
    {
        if ( node instanceof RootNode )
        {
            return node;
        }

        final NodePath nodePath = branchNodeVersion.getNodePath();
        final NodePath parentPath = nodePath.getParentPath();
        final NodeName nodeName = NodeName.from( nodePath.getLastElement().toString() );

        return Node.create( node ).
            parentPath( parentPath ).
            name( nodeName ).
            nodeState( branchNodeVersion.getNodeState() ).
            timestamp( branchNodeVersion.getTimestamp() ).
            build();
    }

    private Node populateWithMetaData( final Node node, final NodeVersion nodeVersion )
    {
        if ( node instanceof RootNode )
        {
            return node;
        }

        final NodePath nodePath = nodeVersion.getNodePath();
        final NodePath parentPath = nodePath.getParentPath();
        final NodeName nodeName = NodeName.from( nodePath.getLastElement().toString() );

        return Node.create( node ).
            parentPath( parentPath ).
            name( nodeName ).
            nodeState( NodeState.ARCHIVED ).
            timestamp( nodeVersion.getTimestamp() ).
            build();
    }

    private Nodes doReturnNodes( final BranchNodeVersions branchNodeVersions )
    {
        final NodeVersionIds.Builder builder = NodeVersionIds.create();
        branchNodeVersions.forEach( ( nodeBranchVersion ) -> builder.add( nodeBranchVersion.getVersionId() ) );

        final Nodes nodes = nodeDao.get( builder.build() );

        final Nodes.Builder filteredNodes = Nodes.create();

        nodes.stream().filter( this::canRead ).forEach(
            ( node ) -> filteredNodes.add( populateWithMetaData( node, branchNodeVersions.get( node.id() ) ) ) );

        return filteredNodes.build();
    }

    private Node storeBranchAndIndex( final Node node, final InternalContext context, final NodeVersionId nodeVersionId )
    {
        this.branchService.store( StoreBranchDocument.create().
            node( node ).
            nodeVersionId( nodeVersionId ).
            build(), context );

        this.indexServiceInternal.store( node, nodeVersionId, context );

        return node;
    }

    private Node moveInBranchAndIndex( final Node node, final NodeVersionId nodeVersionId, final NodePath previousPath,
                                       final InternalContext context )
    {
        this.branchService.move( MoveBranchDocument.create().
            node( node ).
            nodeVersionId( nodeVersionId ).
            previousPath( previousPath ).
            build(), context );

        this.indexServiceInternal.store( node, nodeVersionId, context );

        return node;
    }


    private boolean canRead( final Node node )
    {
        final AuthenticationInfo authInfo = ContextAccessor.current().getAuthInfo();

        if ( authInfo.getPrincipals().contains( RoleKeys.ADMIN ) )
        {
            return true;
        }

        return node.getPermissions().isAllowedFor( authInfo.getPrincipals(), Permission.READ );
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
    public void setNodeDao( final NodeDao nodeDao )
    {
        this.nodeDao = nodeDao;
    }

    @Reference
    public void setIndexServiceInternal( final IndexServiceInternal indexServiceInternal )
    {
        this.indexServiceInternal = indexServiceInternal;
    }
}
