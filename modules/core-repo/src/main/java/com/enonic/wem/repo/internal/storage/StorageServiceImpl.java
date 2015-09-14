package com.enonic.wem.repo.internal.storage;

import org.osgi.service.component.annotations.Reference;

import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.wem.repo.internal.branch.BranchService;
import com.enonic.wem.repo.internal.branch.StoreBranchDocument;
import com.enonic.wem.repo.internal.entity.dao.NodeDao;
import com.enonic.wem.repo.internal.index.IndexContext;
import com.enonic.wem.repo.internal.index.IndexServiceInternal;
import com.enonic.wem.repo.internal.storage.branch.NodeBranchVersion;
import com.enonic.wem.repo.internal.version.NodeVersionDocument;
import com.enonic.wem.repo.internal.version.VersionService;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.Nodes;

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
        final NodeVersionId nodeVersionId;

        nodeVersionId = nodeDao.store( node );

        this.versionService.store( NodeVersionDocument.create().
            nodeId( node.id() ).
            nodeVersionId( nodeVersionId ).
            nodePath( node.path() ).
            timestamp( node.getTimestamp() ).
            build(), context );

        return storeBranchAndIndex( node, context, nodeVersionId );
    }

    @Override
    public Node updateMetadata( final Node node, final InternalContext context )
    {
        final NodeBranchVersion nodeBranchVersion = this.branchService.get( node.id(), context );

        if ( nodeBranchVersion == null )
        {
            throw new NodeNotFoundException( "Cannot find node with id: " + node.id() + " in branch " + context.getBranch() );
        }

        final NodeVersionId nodeVersionId = nodeBranchVersion.getVersionId();

        return storeBranchAndIndex( node, context, nodeVersionId );
    }

    @Override
    public void updateVersion( final Node node, final NodeVersionId nodeVersionId, final InternalContext context )
    {
        this.branchService.store( StoreBranchDocument.create().
            nodeVersionId( nodeVersionId ).
            node( node ).
            build(), context );

        this.indexServiceInternal.store( node, nodeVersionId, IndexContext.create().
            branch( context.getBranch() ).
            repositoryId( context.getRepositoryId() ).
            principalsKeys( context.getPrincipalsKeys() ).
            build() );
    }


    @Override
    public boolean delete( final NodeId nodeId, final InternalContext context )
    {
        return false;
    }

    @Override
    public Node getById( final NodeId nodeId, final InternalContext context )
    {
        return null;
    }

    @Override
    public Node getByPath( final NodePath nodePath, final InternalContext context )
    {
        return null;
    }

    @Override
    public Nodes getByIds( final NodeIds nodeIds, final InternalContext context )
    {
        return null;
    }

    @Override
    public Nodes getByPaths( final NodePaths nodePaths, final InternalContext context )
    {
        return null;
    }


    private Node storeBranchAndIndex( final Node node, final InternalContext context, final NodeVersionId nodeVersionId )
    {
        this.branchService.store( StoreBranchDocument.create().
            node( node ).
            nodeVersionId( nodeVersionId ).
            build(), context );

        this.indexServiceInternal.store( node, nodeVersionId, IndexContext.create().
            branch( context.getBranch() ).
            repositoryId( context.getRepositoryId() ).
            principalsKeys( context.getPrincipalsKeys() ).
            build() );

        return this.nodeDao.getByVersionId( nodeVersionId );
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
