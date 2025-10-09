package com.enonic.xp.repo.impl.storage;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.blob.BlobKeys;
import com.enonic.xp.node.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeCommitEntry;
import com.enonic.xp.node.NodeCommitId;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.NodeVersionId;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.Nodes;
import com.enonic.xp.node.PushNodesListener;
import com.enonic.xp.node.RoutableNodeVersionId;
import com.enonic.xp.node.RoutableNodeVersionIds;
import com.enonic.xp.repo.impl.InternalContext;
import com.enonic.xp.repo.impl.NodeBranchEntries;
import com.enonic.xp.repo.impl.NodeBranchEntry;
import com.enonic.xp.repo.impl.branch.BranchService;
import com.enonic.xp.repo.impl.branch.storage.NodeFactory;
import com.enonic.xp.repo.impl.commit.CommitService;
import com.enonic.xp.repo.impl.node.NodePermissionsResolver;
import com.enonic.xp.repo.impl.node.dao.NodeVersionService;
import com.enonic.xp.repo.impl.version.VersionService;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static com.enonic.xp.repo.impl.node.NodeConstants.CLOCK;

@Component
public class NodeStorageServiceImpl
    implements NodeStorageService
{
    private final VersionService versionService;

    private final BranchService branchService;

    private final CommitService commitService;

    private final NodeVersionService nodeVersionService;

    private final IndexDataService indexDataService;

    @Activate
    public NodeStorageServiceImpl( @Reference final VersionService versionService, @Reference final BranchService branchService,
                                   @Reference final CommitService commitService, @Reference final NodeVersionService nodeVersionService,
                                   @Reference final IndexDataService indexDataService )
    {
        this.versionService = versionService;
        this.branchService = branchService;
        this.commitService = commitService;
        this.nodeVersionService = nodeVersionService;
        this.indexDataService = indexDataService;
    }

    @Override
    public NodeVersionData store( final StoreNodeParams params, final InternalContext context )
    {
        final Node node = params.getNode();

        final Instant timestamp = node.getTimestamp() == null ? Instant.now( CLOCK ) : node.getTimestamp();
        final NodeVersionId nodeVersionId = params.isNewVersion() ? new NodeVersionId() : node.getNodeVersionId();
        final NodeVersionKey nodeVersionKey = nodeVersionService.store( NodeVersion.from( node ), context );

        final NodeVersionMetadata nodeVersionMetadata = NodeVersionMetadata.create()
            .nodeId( node.id() )
            .nodeVersionId( nodeVersionId )
            .nodeVersionKey( nodeVersionKey )
            .binaryBlobKeys( getBinaryBlobKeys( node.getAttachedBinaries() ) )
            .nodePath( node.path() )
            .nodeCommitId( params.getNodeCommitId() )
            .timestamp( timestamp )
            .build();

        this.versionService.store( nodeVersionMetadata, context );

        this.branchService.store( NodeBranchEntry.create()
                                      .nodeId( node.id() )
                                      .nodeVersionId( nodeVersionId )
                                      .nodeVersionKey( nodeVersionKey )
                                      .nodePath( node.path() )
                                      .timestamp( timestamp )
                                      .build(), context );
        final Node newNode = Node.create( node ).timestamp( timestamp ).nodeVersionId( nodeVersionId ).build();

        this.indexDataService.store( newNode, context );

        return new NodeVersionData( newNode, nodeVersionMetadata );
    }

    @Override
    public void push( final Collection<NodeBranchEntry> entries, final Branch target, final PushNodesListener pushListener,
                      final InternalContext context )
    {
        final InternalContext targetContext = InternalContext.create( context ).skipConstraints( true ).branch( target ).build();

        for ( final NodeBranchEntry entry : entries )
        {
            this.branchService.store( entry, targetContext );
            pushListener.nodesPushed( 1 );
        }

        final Collection<NodeId> nodeIds =
            entries.stream().map( NodeBranchEntry::getNodeId ).collect( Collectors.toList() );

        this.indexDataService.push( IndexPushNodeParams.create().nodeIds( nodeIds ).targetBranch( target ).build(), context );
    }

    @Override
    public void storeVersion( final StoreNodeVersionParams params, final InternalContext context )
    {
        final NodeVersionKey nodeVersionKey = this.nodeVersionService.store( params.getNodeVersion(), context );

        this.versionService.store( NodeVersionMetadata.create().
            nodeId( params.getNodeId() ).
            nodeVersionId( params.getNodeVersionId() ).
            nodeVersionKey( nodeVersionKey ).
            binaryBlobKeys( getBinaryBlobKeys( params.getNodeVersion().getAttachedBinaries() ) ).
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
    public void delete( final Collection<NodeBranchEntry> entries, final InternalContext context )
    {
        if ( entries.isEmpty() )
        {
            return;
        }

        branchService.delete( entries, context );
        indexDataService.delete( entries.stream().map( NodeBranchEntry::getNodeId ).collect( Collectors.toList() ), context );
    }

    @Override
    public void deleteFromIndex( final NodeId nodeId, final InternalContext internalContext )
    {
        indexDataService.delete( Collections.singleton( nodeId ), internalContext );
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

        if ( nodeBranchEntry == null )
        {
            return null;
        }

        final NodeVersion nodeVersion = nodeVersionService.get( nodeBranchEntry.getNodeVersionKey(), context );

        return canRead( nodeVersion.getPermissions(), context )
            ? NodeFactory.create( nodeVersion, nodeBranchEntry )
            : null;
    }

    @Override
    public Node get( final NodePath nodePath, final InternalContext context )
    {
        final NodeBranchEntry nodeBranchEntry = this.branchService.get( nodePath, context );

        if ( nodeBranchEntry == null )
        {
            return null;
        }

        final NodeVersion nodeVersion = nodeVersionService.get( nodeBranchEntry.getNodeVersionKey(), context );

        return canRead( nodeVersion.getPermissions(), context )
            ? NodeFactory.create( nodeVersion, nodeBranchEntry )
            : null;
    }

    @Override
    public Nodes get( final NodeIds nodeIds, final InternalContext context )
    {
        if ( nodeIds.isEmpty() )
        {
            return Nodes.empty();
        }
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
    public Node get( final NodeVersionId nodeVersionId, final InternalContext context )
    {
        final NodeVersionMetadata nodeVersionMetadata = versionService.getVersion( nodeVersionId, context );

        if ( nodeVersionMetadata == null )
        {
            return null;
        }

        final NodeVersion nodeVersion = nodeVersionService.get( nodeVersionMetadata.getNodeVersionKey(), context );

        if ( nodeVersion == null || !canRead( nodeVersion.getPermissions(), context ) )
        {
            return null;
        }

        return NodeFactory.create( nodeVersion, nodeVersionMetadata );
    }

    @Override
    public NodeVersion getNodeVersion( final NodeVersionKey nodeVersionKey, final InternalContext context )
    {
        return this.nodeVersionService.get( nodeVersionKey, context );
    }

    @Override
    public AccessControlList getNodePermissions( final NodeVersionKey nodeVersionKey, final InternalContext context )
    {
        return this.nodeVersionService.getPermissions( nodeVersionKey, context );
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
    public NodeVersionMetadata getVersion( final NodeVersionId nodeVersionId, final InternalContext context )
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
    public void invalidatePath( NodePath nodePath, InternalContext context )
    {
        this.branchService.evictPath( nodePath, context );
    }

    private BlobKeys getBinaryBlobKeys( final AttachedBinaries attachedBinaries )
    {
        return attachedBinaries != null ? attachedBinaries.stream()
            .map( AttachedBinary::getBlobKey )
            .map( BlobKey::from )
            .collect( BlobKeys.collector() ) : BlobKeys.empty();
    }

    private Nodes doReturnNodes( final Stream<NodeBranchEntry> nodeBranchEntries, final InternalContext context )
    {
        return nodeBranchEntries.map(
                nodeBranchEntry -> Map.entry( nodeBranchEntry, nodeVersionService.get( nodeBranchEntry.getNodeVersionKey(), context ) ) )
            .filter( entry -> canRead( entry.getValue().getPermissions(), context ) )
            .map( entry -> NodeFactory.create( entry.getValue(), entry.getKey() ) )
            .collect( Nodes.collector() );
    }

    private boolean canRead( final AccessControlList permissions, final InternalContext context )
    {
        return NodePermissionsResolver.hasPermission( context.getPrincipalsKeys(), Permission.READ, permissions );
    }
}
