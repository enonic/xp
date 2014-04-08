package com.enonic.wem.api.entity;

public interface NodeService
{
    Node create( CreateNodeParams params );

    Node update( UpdateNodeParams params );

    Node rename( RenameNodeParams params );

    Node getById( EntityId id );

    Nodes getByIds( EntityIds ids );

    Node getByPath( NodePath path );

    Nodes getByPaths( NodePaths paths );

    Nodes getByParent( NodePath parent );

    Node deleteById( EntityId id );

    Node deleteByPath( NodePath path );
}
