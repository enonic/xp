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
    public Node create( final CreateNodeArguments createNodeArguments );

    public Node update( final UpdateNodeArgs updateNodeArguments );

    public boolean move( final MoveNodeArguments moveNodeArguments );

    public Node getById( final EntityId entityId, final Workspace workspace );

    public Nodes getByIds( final EntityIds entityIds, final Workspace workspace );

    public Node getByPath( final NodePath path, final Workspace workspace );

    public Nodes getByPaths( final NodePaths paths, final Workspace workspace );

    public Nodes getByParent( final NodePath parent, final Workspace workspace );

    public Node deleteById( final EntityId entityId, final Workspace workspace );

    public Node deleteByPath( final NodePath nodePath, final Workspace workspace );


}
