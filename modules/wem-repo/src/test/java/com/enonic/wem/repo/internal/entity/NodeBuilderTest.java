package com.enonic.wem.repo.internal.entity;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.junit.Test;

import com.enonic.wem.api.index.PatternIndexConfigDocument;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.api.support.illegaledit.IllegalEditException;

import static org.junit.Assert.*;

public class NodeBuilderTest
{
    @Test
    public void build_given_no_properties_then_rootDataSet_not_null()
        throws Exception
    {
        final Node myNode = Node.newNode().name( NodeName.from( "my-node" ) ).parent( NodePath.ROOT ).build();
        assertNotNull( myNode.data() );
    }


    @Test
    public void build_given_index_config()
    {
        final Node myNode = Node.newNode().
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( "myAnalyzer" ).
                build() ).
            build();

        assertNotNull( myNode.getIndexConfigDocument() );
        assertEquals( "myAnalyzer", myNode.getIndexConfigDocument().getAnalyzer() );

    }

    @Test
    public void build_given_path()
    {
        final Node myNode = Node.newNode().
            name( NodeName.from( "my-name" ) ).
            parent( NodePath.ROOT ).
            path( "test" ).
            build();

        assertEquals( "test/my-name", myNode.path().toString() );
    }

    @Test
    public void build_given_all_builder_properties()
        throws Exception
    {

        LocalDateTime localDateTime = LocalDateTime.of( 2013, 10, 25, 10, 43 );

        final Node myNode = Node.newNode().
            name( NodeName.from( "my-name" ) ).
            parent( NodePath.ROOT ).
            modifiedTime( localDateTime.toInstant( ZoneOffset.UTC ) ).
            createdTime( localDateTime.toInstant( ZoneOffset.UTC ) ).
            creator( PrincipalKey.from( "user:test:creator" ) ).
            modifier( PrincipalKey.from( "user:test:modifier" ) ).
            modifiedTime( localDateTime.toInstant( ZoneOffset.UTC ) ).
            path( "test" ).
            build();

        assertNotNull( myNode.name() );
        assertNotNull( myNode.parent() );
        assertNotNull( myNode.getModifiedTime() );
        assertNotNull( myNode.modifier() );
        assertNotNull( myNode.getCreatedTime() );
        assertNotNull( myNode.creator() );
        assertNotNull( myNode.path() );
    }

    @Test(expected = IllegalEditException.class)
    public void checkIllegalEdit_given_changed_parent_then_illegal()
        throws Exception
    {

        final Node myNode = Node.newNode( NodeId.from( "myid" ) ).
            name( NodeName.from( "my-name" ) ).
            parent( NodePath.ROOT ).
            build();

        final Node myEditedNode = Node.newNode( NodeId.from( "myid" ) ).
            name( NodeName.from( "my-name" ) ).
            parent( NodePath.newPath( "test" ).build() ).
            build();

        myNode.checkIllegalEdit( myEditedNode );
    }

    @Test(expected = IllegalEditException.class)
    public void checkIllegalEdit_given_changed_modifier_then_illegal()
        throws Exception
    {

        final Node myNode = Node.newNode( NodeId.from( "myid" ) ).
            name( NodeName.from( "my-name" ) ).
            parent( NodePath.ROOT ).
            build();

        final Node myEditedNode = Node.newNode( NodeId.from( "myid" ) ).
            name( NodeName.from( "my-name" ) ).
            parent( NodePath.ROOT ).
            modifier( PrincipalKey.from( "user:test:modifier" ) ).
            build();

        myNode.checkIllegalEdit( myEditedNode );
    }

}
