package com.enonic.wem.api.entity;

import com.enonic.wem.api.command.entity.CreateNodeResult;
import com.enonic.wem.api.command.entity.UpdateNodeResult;

public interface NodeService
{
    CreateNodeResult create( CreateNodeParams params );

    UpdateNodeResult update( UpdateNodeParams params );

    boolean rename( RenameNodeParams params );

    Node getById( GetNodeByIdParams params );

    Nodes getByIds( GetNodesByIdsParams params );

    Node getByPath( GetNodeByPathParams params );

    Nodes getByPaths( GetNodesByPathsParams params );

    Nodes getByParent( GetNodesByParentParams params );

    Node deleteById( DeleteNodeByIdParams params );

    Node deleteByPath( DeleteNodeByPathParams params );
}
