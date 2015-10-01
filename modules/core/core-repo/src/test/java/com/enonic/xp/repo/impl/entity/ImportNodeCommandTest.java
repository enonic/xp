package com.enonic.xp.repo.impl.entity;

import java.time.Instant;

import org.junit.Before;
import org.junit.Test;

import com.enonic.xp.content.CompareStatus;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeComparison;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;

import static org.junit.Assert.*;

public class ImportNodeCommandTest
    extends AbstractNodeTest
{
    @Override
    @Before
    public void setUp()
        throws Exception
    {
        super.setUp();
        CTX_DEFAULT.callWith( this::createDefaultRootNode );
        CTX_OTHER.callWith( this::createDefaultRootNode );
    }

    @Test
    public void no_timestamp()
        throws Exception
    {
        final Node importNode = Node.create().
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            build();

        final Node createdNode = importNode( importNode );

        assertNotNull( createdNode.getTimestamp() );
    }

    @Test
    public void created_nodes_with_id_and_timestamp_should_be_equal()
        throws Exception
    {
        CTX_DEFAULT.callWith( () -> importNode( Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            timestamp( Instant.parse( "2014-01-01T10:00:00Z" ) ).
            build() ) );

        CTX_OTHER.callWith( () -> importNode( Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            timestamp( Instant.parse( "2014-01-01T10:00:00Z" ) ).
            build() ) );

        final NodeComparison comparison = CompareNodeCommand.create().
            nodeId( NodeId.from( "abc" ) ).
            target( CTX_OTHER.getBranch() ).
            storageService( this.storageService ).
            build().
            execute();

        assertEquals( CompareStatus.EQUAL, comparison.getCompareStatus() );
    }

    @Test
    public void import_with_id()
        throws Exception
    {
        final Node importNode = Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            build();

        importNode( importNode );

        final Node abc = getNodeById( NodeId.from( "abc" ) );

        assertNotNull( abc );
    }


    @Test
    public void keep_permissions()
        throws Exception
    {
        final AccessControlList aclList = AccessControlList.create().
            add( AccessControlEntry.create().
                principal( PrincipalKey.ofAnonymous() ).
                allowAll().
                deny( Permission.DELETE ).
                build() ).
            add( AccessControlEntry.create().
                principal( TEST_DEFAULT_USER.getKey() ).
                allowAll().
                deny( Permission.DELETE ).
                build() ).
            build();

        final Node importNode = Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( new PropertyTree() ).
            permissions( aclList ).
            build();

        importNode( importNode );

        final Node abc = getNodeById( NodeId.from( "abc" ) );

        assertNotNull( abc );

        assertEquals( aclList, abc.getPermissions() );
    }

    @Test
    public void import_existing_node()
    {
        PropertyTree data = new PropertyTree();
        data.addString( "name", "value" );

        final Node importNode = Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( data ).
            build();

        importNode( importNode );
        final Node abc = getNodeById( NodeId.from( "abc" ) );
        assertNotNull( abc );
        assertEquals( data, abc.data() );

        PropertyTree data2 = new PropertyTree();
        data2.addString( "name", "valueUpdated" );

        final Node importNode2 = Node.create().
            id( NodeId.from( "abc" ) ).
            name( "myNode" ).
            parentPath( NodePath.ROOT ).
            data( data2 ).
            build();

        importNode( importNode2 );
        final Node abc2 = getNodeById( NodeId.from( "abc" ) );
        assertNotNull( abc2 );
        assertEquals( data2, abc2.data() );
    }


    private Node importNode( final Node importNode )
    {
        return ImportNodeCommand.create().
            importNode( importNode ).
            binaryBlobStore( this.binaryBlobStore ).
            indexServiceInternal( this.indexServiceInternal ).
            storageService( this.storageService ).
            searchService( this.searchService ).
            build().
            execute();
    }
}