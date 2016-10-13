package com.enonic.xp.lib.node;

import java.util.UUID;

import com.enonic.xp.lib.node.mapper.NodeMapper;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RenameNodeParams;

public final class MoveNodeHandler
    extends BaseNodeHandler
{
    private String source;

    private String target;

    @Override
    protected Object doExecute()
    {
        return convert( executeMove() );
    }

    private Node executeMove()

    {
        //Retrieves the node id and path
        final NodeId sourceId;
        final NodePath sourcePath;
        if ( this.source.startsWith( "/" ) )
        {
            sourcePath = NodePath.create( this.source ).build();
            final Node sourceNode = nodeService.getByPath( sourcePath );
            sourceId = sourceNode.id();
        }
        else
        {
            sourceId = NodeId.from( this.source );
            final Node sourceNode = nodeService.getById( sourceId );
            sourcePath = sourceNode.path();
        }

        //If the target ends with /
        if ( target.endsWith( "/" ) )
        {
            //Moves the node to the target parent path
            return move( sourceId, NodePath.create( target ).absolute( true ).build() );
        }
        else if ( !target.startsWith( "/" ) )
        {
            //Else if the target is not a path, renames the node to the target name
            return rename( sourceId, target );
        }
        else
        {
            //Else
            final NodePath targetPath = NodePath.create( target ).build();
            final NodePath targetParent = targetPath.getParentPath();

            //If there target parent is different fromt the current parent
            if ( !targetParent.equals( sourcePath.getParentPath() ) )
            {
                // First rename the node to a temporary unique name to avoid clashing with siblings with target name in source parent or with siblings with source name in target parent
                rename( sourceId, uniqueName() );

                // Moves the node
                move( sourceId, targetParent );
            }

            //Renames the node
            return rename( sourceId, targetPath.getName() );
        }
    }

    private NodeMapper convert( final Node node )
    {
        return node == null ? null : new NodeMapper( node );
    }

    private Node move( final NodeId sourceId, final NodePath newPath )
    {
        return nodeService.move( sourceId, newPath );
    }

    private Node rename( final NodeId sourceId, final String newName )
    {
        final NodeName newNodeName = NodeName.from( newName );
        final RenameNodeParams renameParams = RenameNodeParams.create().
            nodeId( sourceId ).
            nodeName( newNodeName ).
            build();
        return nodeService.rename( renameParams );
    }

    private String uniqueName()
    {
        return UUID.randomUUID().toString();
    }

    public void setSource( final String source )
    {
        this.source = source;
    }

    public void setTarget( final String target )
    {
        this.target = target;
    }
}
