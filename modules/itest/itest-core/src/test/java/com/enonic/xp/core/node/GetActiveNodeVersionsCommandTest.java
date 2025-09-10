package com.enonic.xp.core.node;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.branch.Branches;
import com.enonic.xp.context.Context;
import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.GetActiveNodeVersionsResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeVersionMetadata;
import com.enonic.xp.node.PatchNodeParams;
import com.enonic.xp.repo.impl.node.GetActiveNodeVersionsCommand;
import com.enonic.xp.repo.impl.node.PatchNodeCommand;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class GetActiveNodeVersionsCommandTest
    extends AbstractNodeTest
{
    @BeforeEach
    public void setUp()
        throws Exception
    {
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

        pushNodes( WS_OTHER, node.id() );

        final GetActiveNodeVersionsResult result = GetActiveNodeVersionsCommand.create().
            storageService( this.storageService ).
            searchService( this.searchService ).
            nodeId( node.id() ).
            branches( Branches.from( WS_DEFAULT, WS_OTHER ) ).
            build().
            execute();

        NodeVersionMetadata draft = result.getNodeVersions().get( WS_DEFAULT );
        NodeVersionMetadata master = result.getNodeVersions().get( WS_OTHER );

        assertEquals( draft, master );

        updateNode( node, ctxDefault() );

        final GetActiveNodeVersionsResult result2 = GetActiveNodeVersionsCommand.create().
            storageService( this.storageService ).
            searchService( this.searchService ).
            nodeId( node.id() ).
            branches( Branches.from( WS_DEFAULT, WS_OTHER ) ).
            build().
            execute();

        assertEquals( 2, result2.getNodeVersions().size() );

        draft = result2.getNodeVersions().get( WS_DEFAULT );
        master = result2.getNodeVersions().get( WS_OTHER );

        assertFalse( draft.equals( master ) );
        assertTrue( draft.getTimestamp().isAfter( master.getTimestamp() ) );
    }

    private void updateNode( final Node node, Context context )
    {
        PatchNodeParams updateNodeParams = PatchNodeParams.create().
            id( node.id() ).
            editor( toBeEdited -> toBeEdited.data.setString( "myString", "edit" ) ).
            build();

        context.runWith( () -> PatchNodeCommand.create().
            params( updateNodeParams ).binaryService( this.binaryService ).indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute() );
    }
}



