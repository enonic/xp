package com.enonic.xp.repo.impl.node;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeAccessException;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodeNotFoundException;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.RenameNodeParams;
import com.enonic.xp.node.UpdateNodeParams;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.Assert.*;

public class SetActiveVersionCommandTest
    extends AbstractNodeTest
{

    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        this.createDefaultRootNode();
    }

    @Test
    public void version_data_updated()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "property1", "ver1" );

        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            data( data ).
            name( "node1" ).
            build() );

        updateNode( UpdateNodeParams.create().
            id( node1.id() ).
            editor( toBeEdited -> toBeEdited.data.setString( "property1", "ver2" ) ).
            build() );

        SetActiveVersionCommand.create().
            nodeId( node1.id() ).
            nodeVersionId( node1.getNodeVersionId() ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final Node storedNode = getNodeById( node1.id() );

        assertEquals( node1.getNodeVersionId(), storedNode.getNodeVersionId() );
        assertEquals( node1.data().getString( "property1" ), storedNode.data().getString( "property1" ) );
    }

    @Test
    public void metadata_not_updated()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            name( "originalName" ).
            build() );

        updateNode( UpdateNodeParams.create().
            id( node1.id() ).
            editor( toBeEdited -> toBeEdited.data.setString( "property1", "ver2" ) ).
            build() );

        RenameNodeCommand.create().
            params( RenameNodeParams.create().
                nodeId( node1.id() ).
                nodeName( NodeName.from( "renamedNode" ) ).
                build() ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final Node movedNode = getNodeById( node1.id() );
        assertEquals( "renamedNode", movedNode.name().toString() );

        SetActiveVersionCommand.create().
            nodeId( node1.id() ).
            nodeVersionId( node1.getNodeVersionId() ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();

        final Node rollbackedNode = getNodeById( node1.id() );

        assertEquals( node1.getNodeVersionId(), rollbackedNode.getNodeVersionId() );
        assertEquals( movedNode.name(), rollbackedNode.name() );
    }


    @Test(expected = NodeAccessException.class)
    public void require_modify_access()
        throws Exception
    {
        final PropertyTree data = new PropertyTree();
        data.setString( "property1", "ver1" );

        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            data( data ).
            name( "node1" ).
            permissions( AccessControlList.of( AccessControlEntry.create().
                principal( TEST_DEFAULT_USER.getKey() ).
                deny( Permission.MODIFY ).
                allow( Permission.READ ).
                build() ) ).
            build() );

        SetActiveVersionCommand.create().
            nodeId( node1.id() ).
            nodeVersionId( node1.getNodeVersionId() ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }

    @Test(expected = NodeNotFoundException.class)
    public void must_be_version_of_node()
        throws Exception
    {
        final Node node1 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node1" ) ).
            parent( NodePath.ROOT ).
            name( "node1" ).
            build() );

        final Node node2 = createNode( CreateNodeParams.create().
            setNodeId( NodeId.from( "node2" ) ).
            parent( NodePath.ROOT ).
            name( "node2" ).
            build() );

        SetActiveVersionCommand.create().
            nodeId( node1.id() ).
            nodeVersionId( node2.getNodeVersionId() ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            indexServiceInternal( this.indexServiceInternal ).
            build().
            execute();
    }


}