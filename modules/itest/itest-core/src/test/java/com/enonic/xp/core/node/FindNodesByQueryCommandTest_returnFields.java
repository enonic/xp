package com.enonic.xp.core.node;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.node.CreateNodeParams;
import com.enonic.xp.node.FieldValues;
import com.enonic.xp.node.FindNodesByQueryResult;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeHit;
import com.enonic.xp.node.NodeIndexPath;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeQuery;
import com.enonic.xp.node.RefreshMode;
import com.enonic.xp.query.parser.QueryParser;
import com.enonic.xp.util.Reference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FindNodesByQueryCommandTest_returnFields
    extends AbstractNodeTest
{
    @BeforeEach
    void setUp()
    {
        this.createDefaultRootNode();
    }

    @Test
    void no_fields_requested()
    {
        createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result =
            doFindByQuery( NodeQuery.create().query( QueryParser.parse( "_name = 'my-node'" ) ).build() );

        final NodeHit hit = result.getNodeHits().first();
        assertTrue( hit.getFields().isEmpty() );
        assertNull( hit.getNodePath() );
    }

    @Test
    void system_fields()
    {
        final Node node = createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result = doFindByQuery( NodeQuery.create()
                                                                 .query( QueryParser.parse( "_name = 'my-node'" ) )
                                                                 .returnFields( NodeIndexPath.PATH, NodeIndexPath.PARENT_PATH,
                                                                                NodeIndexPath.NAME, NodeIndexPath.NODE_TYPE,
                                                                                NodeIndexPath.TIMESTAMP, NodeIndexPath.VERSION )
                                                                 .build() );

        final NodeHit hit = result.getNodeHits().first();
        final FieldValues fields = hit.getFields();

        assertEquals( List.of( "/my-node" ), fields.getValues( NodeIndexPath.PATH ) );
        assertEquals( List.of( "/" ), fields.getValues( NodeIndexPath.PARENT_PATH ) );
        assertEquals( List.of( "my-node" ), fields.getValues( NodeIndexPath.NAME ) );
        assertEquals( List.of( node.getNodeType().getName() ), fields.getValues( NodeIndexPath.NODE_TYPE ) );
        assertEquals( node.getTimestamp(), Instant.parse( (String) fields.getSingleValue( NodeIndexPath.TIMESTAMP ).orElseThrow() ) );
        assertEquals( List.of( node.getNodeVersionId().toString() ), fields.getValues( NodeIndexPath.VERSION ) );
        // requesting the path also populates the typed accessor
        assertEquals( node.path(), hit.getNodePath() );
    }

    @Test
    void fields_outside_the_supported_set_are_rejected()
    {
        final NodeQuery.Builder builder = NodeQuery.create();

        assertEquals( "unsupported return field: displayname",
                      assertThrows( IllegalArgumentException.class,
                                    () -> builder.returnFields( IndexPath.from( "displayName" ) ) ).getMessage() );
        // typed index variants are internal layout, not API
        assertThrows( IllegalArgumentException.class, () -> builder.returnFields( IndexPath.from( "_ts._datetime" ) ) );
        assertThrows( IllegalArgumentException.class, () -> builder.returnFields( IndexPath.from( "_name._orderby" ) ) );
    }

    @Test
    void multi_valued_field()
    {
        final Node target1 = createNode( CreateNodeParams.create().name( "target-1" ).parent( NodePath.ROOT ).build() );
        final Node target2 = createNode( CreateNodeParams.create().name( "target-2" ).parent( NodePath.ROOT ).build() );

        final PropertyTree data = new PropertyTree();
        data.addReference( "refs", Reference.from( target1.id().toString() ) );
        data.addReference( "refs", Reference.from( target2.id().toString() ) );

        createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).data( data ).build() );
        nodeService.refresh( RefreshMode.ALL );

        final FindNodesByQueryResult result = doFindByQuery( NodeQuery.create()
                                                                 .query( QueryParser.parse( "_name = 'my-node'" ) )
                                                                 .returnFields( NodeIndexPath.REFERENCE )
                                                                 .build() );

        assertThat( result.getNodeHits().first().getFields().getValues( NodeIndexPath.REFERENCE ) ).containsExactlyInAnyOrder(
            target1.id().toString(), target2.id().toString() );
    }

    @Test
    void absent_field_is_not_present()
    {
        createNode( CreateNodeParams.create().name( "my-node" ).parent( NodePath.ROOT ).build() );
        nodeService.refresh( RefreshMode.ALL );

        // a node without references holds nothing for _references, so the field is absent rather than empty
        final FindNodesByQueryResult result = doFindByQuery( NodeQuery.create()
                                                                 .query( QueryParser.parse( "_name = 'my-node'" ) )
                                                                 .returnFields( NodeIndexPath.REFERENCE, NodeIndexPath.NAME )
                                                                 .build() );

        final FieldValues fields = result.getNodeHits().first().getFields();
        assertEquals( java.util.Set.of( "_name" ), fields.getFields() );
        assertTrue( fields.getValues( NodeIndexPath.REFERENCE ).isEmpty() );
    }
}
