package com.enonic.wem.core.entity;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.account.UserKey;
import com.enonic.wem.api.entity.EntityId;
import com.enonic.wem.api.entity.Node;
import com.enonic.wem.api.entity.NodeName;
import com.enonic.wem.api.entity.NodePath;
import com.enonic.wem.api.entity.NodeVersionId;
import com.enonic.wem.api.entity.Workspace;
import com.enonic.wem.core.workspace.WorkspaceDocument;
import com.enonic.wem.core.workspace.query.WorkspaceIdQuery;

public class PushNodeCommandTest
    extends AbstractNodeCommandTest
{

    @Test
    public void push()
        throws Exception
    {
        final NodeVersionId currentVersion = NodeVersionId.from( "a" );
        final EntityId nodeId = EntityId.from( "mynode" );
        final Workspace targetWorkspace = Workspace.from( "prod" );

        final PushNodeCommand command = PushNodeCommand.create( TEST_CONTEXT ).
            nodeDao( nodeDao ).
            workspaceService( workspaceService ).
            versionService( versionService ).
            indexService( indexService ).
            id( nodeId ).
            target( targetWorkspace ).
            build();

        Mockito.when( workspaceService.getCurrentVersion( Mockito.isA( WorkspaceIdQuery.class ) ) ).
            thenReturn( currentVersion );

        final Node publishedNode = Node.newNode().
            name( NodeName.from( "myname" ) ).
            id( nodeId ).
            parent( NodePath.ROOT ).
            creator( UserKey.superUser() ).
            build();
        Mockito.when( nodeDao.getByVersionId( currentVersion ) ).
            thenReturn( publishedNode );

        command.execute();

        Mockito.verify( this.workspaceService ).store( Mockito.eq( WorkspaceDocument.create().
            workspace( targetWorkspace ).
            id( nodeId ).
            nodeVersionId( currentVersion ).
            parentPath( NodePath.ROOT ).
            path( NodePath.newNodePath( NodePath.ROOT, "myname" ).build() ).
            build() ) );

        Mockito.verify( this.indexService ).index( publishedNode, targetWorkspace );
    }

}
