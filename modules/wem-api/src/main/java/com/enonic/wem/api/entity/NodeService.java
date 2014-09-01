package com.enonic.wem.api.entity;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.entity.query.NodeQuery;

public interface NodeService
{
    Node create( CreateNodeParams params, final Context context );

    Node update( UpdateNodeParams params, final Context context );

    Node rename( RenameNodeParams params, final Context context );

    Node getById( EntityId id, Context context );

    Node push( EntityId id, Workspace target, Context context );

    Nodes getByIds( EntityIds ids, Context context );

    Node getByPath( NodePath path, Context context );

    Nodes getByPaths( NodePaths paths, Context context );

    FindNodesByParentResult findByParent( FindNodesByParentParams params, Context context );

    FindNodesByQueryResult findByQuery( NodeQuery nodeQuery, Context context );

    Node deleteById( EntityId id, Context context );

    Node deleteByPath( NodePath path, Context context );

    NodeComparison compare( EntityId id, Workspace target, Context context );

    NodeComparisons compare( final EntityIds ids, final Workspace target, final Context context );

    FindNodeVersionsResult findVersions( final GetNodeVersionsParams params, final Context context );

    GetActiveNodeVersionsResult getActiveVersions( final GetActiveNodeVersionsParams params, final Context context );

    Node getByBlobKey( BlobKey blobKey, Context context );
}
