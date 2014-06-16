package com.enonic.wem.api.entity;

import com.enonic.wem.api.context.Context;

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

    Nodes getByParent( NodePath parent, Context context );

    Node deleteById( EntityId id, Context context );

    Node deleteByPath( NodePath path, Context context );

    EntityComparison compare(EntityId id, Workspace target, Context context);
}
