package com.enonic.wem.core.entity;

import org.junit.Test;
import org.mockito.Mockito;

import com.enonic.wem.api.context.Context;
import com.enonic.wem.api.context.ContextBuilder;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.repository.Repository;
import com.enonic.wem.api.repository.RepositoryId;
import com.enonic.wem.api.workspace.Workspace;
import com.enonic.wem.core.entity.dao.NodeDao;
import com.enonic.wem.core.workspace.WorkspaceService;

import static org.junit.Assert.*;

public class NodeChildOrderResolverTest
{

    private final NodeDao nodeDao = Mockito.mock( NodeDao.class );

    private final WorkspaceService workspaceService = Mockito.mock( WorkspaceService.class );

    @Test
    public void given_child_order_as_param()
        throws Exception
    {
        final ChildOrder childOrder = ChildOrder.from( "myField DESC" );

        final ChildOrder resolvedOrder = NodeChildOrderResolver.create().
            nodeDao( this.nodeDao ).
            workspaceService( this.workspaceService ).
            nodePath( NodePath.newPath( "myPath" ).build() ).
            childOrder( childOrder ).
            build().
            resolve();

        assertEquals( childOrder, resolvedOrder );
    }

    @Test
    public void root_use_workspace_order()
        throws Exception
    {
        final ChildOrder childOrder = ChildOrder.from( "myField DESC" );

        final Context myContext = ContextBuilder.create().
            object( Workspace.create().
                name( "myWorkspace" ).
                childOrder( childOrder ).
                build() ).
            object( Repository.create().id( RepositoryId.from( "myRepository" ) ) ).
            build();

        final ChildOrder resolvedOrder = myContext.runWith( () -> {
            return NodeChildOrderResolver.create().
                nodeDao( this.nodeDao ).
                workspaceService( this.workspaceService ).
                nodePath( NodePath.ROOT ).
                build().
                resolve();
        } );

        assertEquals( childOrder, resolvedOrder );
    }

}