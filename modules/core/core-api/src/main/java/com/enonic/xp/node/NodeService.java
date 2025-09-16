package com.enonic.xp.node;

import com.google.common.io.ByteSource;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.blob.NodeVersionKey;
import com.enonic.xp.branch.Branch;
import com.enonic.xp.util.BinaryReference;

@PublicApi
public interface NodeService
{
    Node create( CreateNodeParams params );

    Node update( UpdateNodeParams params );

    PatchNodeResult patch( PatchNodeParams params );

    Node rename( RenameNodeParams params );

    PushNodesResult push( NodeIds ids, Branch target );

    PushNodesResult push( NodeIds ids, Branch target, PushNodesListener pushListener );

    DeleteNodeResult delete( DeleteNodeParams deleteNodeParams );

    Node getById( NodeId id );

    Node getByIdAndVersionId( NodeId id, NodeVersionId versionId );

    Nodes getByIds( NodeIds ids );

    Node getByPath( NodePath path );

    Nodes getByPaths( NodePaths paths );

    Node duplicate( DuplicateNodeParams params );

    Node move( MoveNodeParams params );

    FindNodesByParentResult findByParent( FindNodesByParentParams params );

    FindNodesByQueryResult findByQuery( NodeQuery nodeQuery );

    FindNodesByMultiRepoQueryResult findByQuery( MultiRepoNodeQuery nodeQuery );

    NodeComparison compare( NodeId id, Branch target );

    NodeComparisons compare( NodeIds ids, Branch target );

    NodeVersionQueryResult findVersions( GetNodeVersionsParams params );

    NodeVersionQueryResult findVersions( NodeVersionQuery nodeVersionQuery );

    NodeCommitQueryResult findCommits( NodeCommitQuery nodeCommitQuery );

    GetActiveNodeVersionsResult getActiveVersions( GetActiveNodeVersionsParams params );

    SortNodeResult sort( SortNodeParams params );

    NodeVersion getByNodeVersionKey( NodeVersionKey nodeVersionKey );

    ResolveSyncWorkResult resolveSyncWork( SyncWorkResolverParams params );

    void refresh( RefreshMode refreshMode );

    ApplyNodePermissionsResult applyPermissions( ApplyNodePermissionsParams params );

    ByteSource getBinary( NodeId nodeId, BinaryReference reference );

    ByteSource getBinary( NodeId nodeId, NodeVersionId nodeVersionId, BinaryReference reference );

    ImportNodeResult importNode( ImportNodeParams params );

    LoadNodeResult loadNode( LoadNodeParams params );

    NodeCommitEntry commit( NodeCommitEntry nodeCommitEntry, RoutableNodeVersionIds routableNodeVersionIds );

    NodeCommitEntry commit( NodeCommitEntry nodeCommitEntry, NodeIds nodeIds );

    NodeCommitEntry getCommit( NodeCommitId nodeCommitId );

    boolean hasChildren( Node node );

    boolean nodeExists( NodeId nodeId );

    boolean nodeExists( NodePath nodePath );

    boolean hasUnpublishedChildren( NodeId parent, Branch target );

    void importNodeVersion( ImportNodeVersionParams params );

    void importNodeCommit( ImportNodeCommitParams params );
}
