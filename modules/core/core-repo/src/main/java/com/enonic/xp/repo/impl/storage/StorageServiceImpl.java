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
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.branch.MoveBranchDocument;
import com.enonic.xp.repo.impl.branch.StoreBranchDocument;
import com.enonic.xp.repo.impl.branch.storage.BranchNodeVersion;
import com.enonic.xp.repo.impl.branch.storage.BranchNodeVersions;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.index.IndexServiceInternal;
import com.enonic.xp.repo.impl.node.dao.NodeVersionDao;
import com.enonic.xp.repo.impl.version.NodeVersionDocument;
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

    @Override
    public Node store( final Node node, final InternalContext context )
    {
        final NodeVersionId nodeVersionId = nodeVersionDao.store( node );

        storeVersionMetadata( node, context, nodeVersionId );

        return storeBranchAndIndex( node, context, nodeVersionId );
    }

    private void storeVersionMetadata( final Node node, final InternalContext context, final NodeVersionId nodeVersionId )
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
            nodeVersionId = nodeVersionDao.store( params.getNode() );
        }

        storeVersionMetadata( params.getNode(), context, nodeVersionId );

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
        final NodeVersion nodeVersion = NodeVersion.from( node );

        final StoreBranchDocument storeBranchDocument = new StoreBranchDocument( nodeVersion, BranchNodeVersion.create().
            nodeVersionId( nodeVersionId ).
            nodeId( nodeVersion.getId() ).
            nodeState( node.getNodeState() ).
            timestamp( node.getTimestamp() ).
            nodePath( node.path() ).
            build() );

        this.branchService.store( storeBranchDocument, context );

        this.indexServiceInternal.store( node, context );
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
    public NodeVersion get( final NodeVersionMetadata nodeVersionMetadata )
    {
        return this.nodeVersionDao.get( nodeVersionMetadata.getNodeVersionId() );
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
    public NodeVersionMetadata getVersion( final NodeVersionDocumentId versionId, final InternalContext context )
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

        final NodeVersion nodeVersion = nodeVersionDao.get( branchNodeVersion.getVersionId() );

        final Node node = NodeFactory.create( nodeVersion, branchNodeVersion );

        return canRead( node.getPermissions() ) ? node : null;
    }

    private Nodes doReturnNodes( final BranchNodeVersions branchNodeVersions )
    {
        final NodeVersionIds.Builder builder = NodeVersionIds.create();
        branchNodeVersions.forEach( ( nodeBranchVersion ) -> builder.add( nodeBranchVersion.getVersionId() ) );

        final NodeVersions nodeVersions = nodeVersionDao.get( builder.build() );

        final Nodes.Builder filteredNodes = Nodes.create();

        nodeVersions.stream().filter( ( nodeVersion ) -> canRead( nodeVersion.getPermissions() ) ).forEach(
            ( nodeVersion ) -> filteredNodes.add( NodeFactory.create( nodeVersion, branchNodeVersions.get( nodeVersion.getId() ) ) ) );

        return filteredNodes.build();
    }

    private Node storeBranchAndIndex( final Node node, final InternalContext context, final NodeVersionId nodeVersionId )
    {
        final NodeVersion nodeVersion = NodeVersion.from( node );

        final BranchNodeVersion branchNodeVersion = BranchNodeVersion.create().
            nodeVersionId( nodeVersionId ).
            nodeId( nodeVersion.getId() ).
            nodeState( node.getNodeState() ).
            timestamp( node.getTimestamp() ).
            nodePath( node.path() ).
            build();

        final StoreBranchDocument storeBranchDocument = new StoreBranchDocument( nodeVersion, branchNodeVersion );

        this.branchService.store( storeBranchDocument, context );

        this.indexServiceInternal.store( node, context );

        return NodeFactory.create( nodeVersion, branchNodeVersion );
    }

    private Node moveInBranchAndIndex( final Node node, final NodeVersionId nodeVersionId, final NodePath previousPath,
                                       final InternalContext context )
    {
        final NodeVersion nodeVersion = NodeVersion.from( node );

        this.branchService.move( MoveBranchDocument.create().
            nodeVersion( nodeVersion ).
            branchNodeVersion( BranchNodeVersion.create().
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
}
