package com.enonic.wem.core.entity;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.NodeVersionIds;
import com.enonic.wem.api.entity.RenameNodeParams;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;
import com.enonic.wem.core.workspace.query.WorkspaceParentQuery;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


public class RenameNodeCommandTest
    extends AbstractNodeCommandTest
{

    @Test
    public void rename_no_children()
        throws Exception
    {
        final EntityId nodeId = EntityId.from( "myid" );
        final NodeVersionId nodeVersionId = NodeVersionId.from( "versionid" );

        final Node nodeToBeRenamed = Node.newNode().
            id( EntityId.from( "rename-node-id" ) ).
            name( NodeName.from( "my-node-name" ) ).
            parent( NodePath.ROOT ).
            creator( UserKey.superUser() ).
            build();

        final NodeName newNodeName = NodeName.from( "new-nodename" );

        final RenameNodeParams params = RenameNodeParams.create().
            entityId( nodeId ).
            nodeName( newNodeName ).
            build();

        final RenameNodeCommand command = createCommand( params );

        final NodeVersionId renamedNodeVersionId = NodeVersionId.from( "new-node-version-id" );

        // Mock the fetching of the node to be renamed
        when( this.workspaceService.getCurrentVersion( new WorkspaceIdQuery( TEST_CONTEXT.getWorkspace(), nodeId ) ) ).
            thenReturn( nodeVersionId );
        when( this.nodeDao.getByVersionId( nodeVersionId ) ).
            thenReturn( nodeToBeRenamed );
        when( nodeDao.store( Mockito.isA( Node.class ) ) ).
            thenReturn( renamedNodeVersionId );

        // Mock no children of the node to be renamed
        when( workspaceService.findByParent( new WorkspaceParentQuery( TEST_CONTEXT.getWorkspace(), nodeToBeRenamed.path() ) ) ).
            thenReturn( NodeVersionIds.empty() );

        // Exercise
        final Node renamedNode = command.execute();

        assertEquals( newNodeName, renamedNode.name() );
        final NodePath expectedNewPath = NodePath.newNodePath( NodePath.ROOT, newNodeName.toString() ).build();
        assertEquals( expectedNewPath, renamedNode.path() );
    }

    private RenameNodeCommand createCommand( final RenameNodeParams params )
    {
        return RenameNodeCommand.create( TEST_CONTEXT ).
            indexService( this.indexService ).
            versionService( this.versionService ).
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            params( params ).
            build();
    }

}