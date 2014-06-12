package com.enonic.wem.core.entity.dao;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.Nodes;
import com.enonic.wem.api.entity.Workspace;

public interface NodeDao
{
    public Node create( CreateNodeArguments createNodeArguments, Workspace workspace );

    public Node update( UpdateNodeArgs updateNodeArguments, Workspace workspace );

    public boolean move( MoveNodeArguments moveNodeArguments, Workspace workspace );

    public Node push( PushNodeArguments pushNodeArguments, Workspace workspace );

    public Node getById( EntityId entityId, Workspace workspace );

    public Nodes getByIds( EntityIds entityIds, Workspace workspace );

    public Node getByPath( NodePath path, Workspace workspace );

    public Nodes getByPaths( NodePaths paths, Workspace workspace );

    public Nodes getByParent( NodePath parent, Workspace workspace );

    public Node deleteById( EntityId entityId, Workspace workspace );

}
