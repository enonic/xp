package com.enonic.wem.api.entity;

public interface NodeService
{
    Node create( CreateNodeParams params );

    Node update( UpdateNodeParams params );

    Node rename( RenameNodeParams params );

    Node getById( EntityId id, Workspace workspace );

    Nodes getByIds( EntityIds ids, Workspace workspace );

    Node getByPath( NodePath path, Workspace workspace );

    Nodes getByPaths( NodePaths paths, Workspace workspace);

    Nodes getByParent( NodePath parent, Workspace workspace );

    Node deleteById( EntityId id, Workspace workspace );

    Node deleteByPath( NodePath path, Workspace workspace );
}
