package com.enonic.xp.core.node;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.enonic.xp.core.AbstractNodeTest;
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
                                                                 .returnFields( NodeIndexPath.PATH, NodeIndexPath.NAME,
                                                                                NodeIndexPath.NODE_TYPE, NodeIndexPath.TIMESTAMP,
                                                                                NodeIndexPath.VERSION )
                                                                 .build() );

        final NodeHit hit = result.getNodeHits().first();
        final FieldValues fields = hit.getFields();

        assertEquals( List.of( "/my-node" ), fields.getValues( NodeIndexPath.PATH ) );
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
        // a fetched node shows neither of these, so a query cannot ask for them either
        assertThrows( IllegalArgumentException.class, () -> builder.returnFields( NodeIndexPath.PARENT_PATH ) );
        assertThrows( IllegalArgumentException.class, () -> builder.returnFields( NodeIndexPath.REFERENCE ) );
    }

    @Test
    void absent_field_is_not_present()
    {
        nodeService.refresh( RefreshMode.ALL );

        // the root node is the one node indexed without a name, so the field is absent for it rather than empty
        final FindNodesByQueryResult result = doFindByQuery( NodeQuery.create()
                                                                 .query( QueryParser.parse( "_path = '/'" ) )
                                                                 .returnFields( NodeIndexPath.PATH, NodeIndexPath.NAME )
                                                                 .build() );

        final FieldValues fields = result.getNodeHits().first().getFields();
        assertEquals( Set.of( "_path" ), fields.getFields() );
        assertTrue( fields.getValues( NodeIndexPath.NAME ).isEmpty() );
    }
}
