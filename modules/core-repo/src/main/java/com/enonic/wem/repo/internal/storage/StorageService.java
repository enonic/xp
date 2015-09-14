package com.enonic.wem.repo.internal.storage;


import com.enonic.wem.repo.internal.InternalContext;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodePaths;
import com.enonic.xp.node.Nodes;

public interface StorageService
{
    Node store( final Node node, final InternalContext context );

    Node updateMetadata( final Node node, final InternalContext context );

    boolean delete( final NodeId nodeId, final InternalContext context );

    Node getById( final NodeId nodeId, final InternalContext context );

    Node getByPath( final NodePath nodePath, final InternalContext context );

    Nodes getByIds( final NodeIds nodeIds, final InternalContext context );

    Nodes getByPaths( final NodePaths nodePaths, final InternalContext context );


}
