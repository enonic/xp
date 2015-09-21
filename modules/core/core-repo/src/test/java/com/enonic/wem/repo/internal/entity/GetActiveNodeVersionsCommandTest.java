package com.enonic.wem.repo.internal.entity;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeIds;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersion;
import com.enonic.xp.node.UpdateNodeParams;

import static org.junit.Assert.*;

public class GetActiveNodeVersionsCommandTest
    extends AbstractNodeTest
{
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void get_active_versions()
        throws Exception
    {

        final PropertyTree data = new PropertyTree();
        data.addString( "myString", "value" );

        final Node node = createNode( CreateNodeParams.create().
            name( "myNode" ).
            parent( NodePath.ROOT ).
            data( data ).
            build() );

        pushNodes( NodeIds.from( node.id() ), WS_OTHER );

        final GetActiveNodeVersionsResult result = GetActiveNodeVersionsCommand.create().
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            nodeId( node.id() ).
            branches( Branches.from( WS_DEFAULT, WS_OTHER ) ).
            build().
            execute();

        NodeVersion draft = result.getNodeVersions().get( WS_DEFAULT );
        NodeVersion master = result.getNodeVersions().get( WS_OTHER );

        assertEquals( draft, master );

        updateNode( node, CTX_DEFAULT );

        final GetActiveNodeVersionsResult result2 = GetActiveNodeVersionsCommand.create().
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            queryService( this.queryService ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            nodeId( node.id() ).
            branches( Branches.from( WS_DEFAULT, WS_OTHER ) ).
            build().
            execute();

        draft = result2.getNodeVersions().get( WS_DEFAULT );
        master = result2.getNodeVersions().get( WS_OTHER );

        assertTrue( !draft.equals( master ) );
        assertTrue( draft.getTimestamp().isAfter( master.getTimestamp() ) );
    }

    private void updateNode( final Node node, Context context )
    {
        UpdateNodeParams updateNodeParams = UpdateNodeParams.create().
            id( node.id() ).
            editor( toBeEdited -> toBeEdited.data.setString( "myString", "edit" ) ).
            build();

        context.runWith( () -> UpdateNodeCommand.create().
            params( updateNodeParams ).
            indexServiceInternal( this.indexServiceInternal ).
            queryService( this.queryService ).
            branchService( this.branchService ).
            nodeDao( this.nodeDao ).
            binaryBlobStore( this.binaryBlobStore ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute() );
    }
}



