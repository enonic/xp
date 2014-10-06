package com.enonic.wem.core.entity;

import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.entity.query.NodeQuery;

public interface NodeService
{
    Node create( CreateNodeParams params );

    Node update( UpdateNodeParams params );

    Node rename( RenameNodeParams params );

    Node push( EntityId id, Workspace target );

    Node deleteById( EntityId id );

    Node deleteByPath( NodePath path );

    Node getById( EntityId id );

    Nodes getByIds( EntityIds ids );

    Node getByPath( NodePath path );

    Nodes getByPaths( NodePaths paths );

    FindNodesByParentResult findByParent( FindNodesByParentParams params );

    FindNodesByQueryResult findByQuery( NodeQuery nodeQuery );

    NodeComparison compare( EntityId id, Workspace target );

    NodeComparisons compare( final EntityIds ids, final Workspace target );

    FindNodeVersionsResult findVersions( final GetNodeVersionsParams params );

    GetActiveNodeVersionsResult getActiveVersions( final GetActiveNodeVersionsParams params );

    Node getByVersionId( NodeVersionId nodeVersionid );
}
