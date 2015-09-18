package com.enonic.wem.repo.internal.entity;

import java.time.LocalDateTime;

import org.junit.Test;

import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;

import static org.junit.Assert.*;

public class NodeBuilderTest
{
    @Test
    public void build_given_no_properties_then_rootDataSet_not_null()
        throws Exception
    {
        final Node myNode = Node.create().name( NodeName.from( "my-node" ) ).parentPath( NodePath.ROOT ).build();
        assertNotNull( myNode.data() );
    }


    @Test
    public void build_given_index_config()
    {
        final Node myNode = Node.create().
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
        final Node myNode = Node.create().
            name( NodeName.from( "my-name" ) ).
            parentPath( NodePath.ROOT ).
            path( "test" ).
            build();

        assertEquals( "test/my-name", myNode.path().toString() );
    }

    @Test
    public void build_given_all_builder_properties()
        throws Exception
    {

        LocalDateTime localDateTime = LocalDateTime.of( 2013, 10, 25, 10, 43 );

        final Node myNode = Node.create().
            name( NodeName.from( "my-name" ) ).
            parentPath( NodePath.ROOT ).
            path( "test" ).
            build();

        assertNotNull( myNode.name() );
        assertNotNull( myNode.parentPath() );
        assertNotNull( myNode.path() );
    }
}
