package com.enonic.xp.node;

import org.junit.jupiter.api.Test;

import com.enonic.xp.index.PatternIndexConfigDocument;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class NodeBuilderTest
{
    @Test
    void build_given_no_properties_then_rootDataSet_not_null()
    {
        final Node myNode = Node.create().name( NodeName.from( "my-node" ) ).parentPath( NodePath.ROOT ).build();
        assertNotNull( myNode.data() );
    }


    @Test
    void build_given_index_config()
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
    void build_given_path()
    {
        final Node myNode = Node.create().
            name( NodeName.from( "my-name" ) ).
            parentPath( new NodePath("/test") ).
            build();

        assertEquals( "/test/my-name", myNode.path().toString() );
    }

    @Test
    void build_given_all_builder_properties()
    {
        final Node myNode = Node.create().
            name( NodeName.from( "my-name" ) ).
            parentPath( new NodePath("/test") ).
            build();

        assertNotNull( myNode.name() );
        assertNotNull( myNode.parentPath() );
        assertNotNull( myNode.path() );
    }
}
