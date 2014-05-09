package com.enonic.wem.core.entity.dao;

import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.EntityIds;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodePaths;
import com.enonic.wem.api.entity.Nodes;

public interface NodeDao
{
    public Node create( final CreateNodeArguments createNodeArguments );

    public Node update( final UpdateNodeArgs updateNodeArguments );

    public boolean move( final MoveNodeArguments moveNodeArguments );

    public Node getById( final EntityId entityId );

    public Nodes getByIds( final EntityIds entityIds );

    public Node getByPath( final NodePath path );

    public Nodes getByPaths( final NodePaths paths );

    public Nodes getByParent( final NodePath parent );

    public Node deleteById( final EntityId entityId );

    public Node deleteByPath( final NodePath nodePath );


}
