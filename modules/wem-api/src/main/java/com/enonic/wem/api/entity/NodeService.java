package com.enonic.wem.api.entity;

import com.enonic.wem.api.command.entity.CreateNodeResult;
import com.enonic.wem.api.command.entity.UpdateNodeResult;

public interface NodeService
{
    CreateNodeResult create( CreateNodeParams params )
        throws Exception;

    UpdateNodeResult update( UpdateNodeParams params )
        throws Exception;

    boolean rename( RenameNodeParams params )
        throws Exception;

    Node getById( GetNodeByIdParams params )
        throws Exception;

    Nodes getByIds( GetNodesByIdsParams params )
        throws Exception;

    Node getByPath( GetNodeByPathParams params )
        throws Exception;

    Nodes getByPaths( GetNodesByPathsParams params )
        throws Exception;

    Nodes getByParent( GetNodesByParentParams params )
        throws Exception;

    Node deleteById( DeleteNodeByIdParams params )
        throws Exception;

    Node deleteByPath( DeleteNodeByPathParams params )
        throws Exception;
}
