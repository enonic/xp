package com.enonic.wem.repo.internal.entity.json;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.Test;

import com.fasterxml.jackson.databind.JsonNode;

import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.data.Value;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.IndexPath;
import com.enonic.xp.index.IndexValueProcessor;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.query.expr.FieldOrderExpr;
import com.enonic.xp.query.expr.OrderExpr;
import com.enonic.xp.security.PrincipalKey;
import com.enonic.xp.security.UserStoreKey;
import com.enonic.xp.security.acl.AccessControlEntry;
import com.enonic.xp.security.acl.AccessControlList;
import com.enonic.xp.security.acl.Permission;
import com.enonic.xp.util.BinaryReference;

import static org.junit.Assert.*;

public class NodeJsonSerializerTest
{
    private final NodeJsonSerializer serializer;

    public NodeJsonSerializerTest()
    {
        this.serializer = NodeJsonSerializer.create( true );
    }

    @Test
    public void serialize_deserialize()
        throws Exception
    {
        PropertyTree nodeData = new PropertyTree();
        nodeData.setDouble( "a.b.c", 2.0 );
        nodeData.setLocalDate( "b", LocalDate.of( 2013, 1, 2 ) );
        nodeData.setString( "c", "runar" );
        nodeData.setLocalDateTime( "d", LocalDateTime.of( 2013, 1, 2, 3, 4, 5, 0 ) );
        nodeData.setBinaryReference( "e", BinaryReference.from( "myImage1" ) );
        nodeData.setBinaryReference( "f", BinaryReference.from( "myImage2" ) );

        final AccessControlEntry entry1 = AccessControlEntry.create().
            principal( PrincipalKey.ofAnonymous() ).
            allow( Permission.READ ).
            deny( Permission.DELETE ).
            build();
        final AccessControlEntry entry2 = AccessControlEntry.create().
            principal( PrincipalKey.ofUser( UserStoreKey.system(), "user1" ) ).
            allow( Permission.MODIFY ).
            deny( Permission.PUBLISH ).
            build();
        AccessControlList acl = AccessControlList.create().add( entry1 ).add( entry2 ).build();

        IndexValueProcessor indexValueProcessor = new IndexValueProcessor()
        {
            @Override
            public Value process( final Value value )
            {
                return value;
            }

            @Override
            public String getName()
            {
                return "indexValueProcessor";
            }
        };

        IndexConfig indexConfig = IndexConfig.create().
            enabled( true ).
            fulltext( true ).
            nGram( true ).
            decideByType( false ).
            includeInAllText( true ).
            addIndexValueProcessor( indexValueProcessor ).
            addIndexValueProcessor( indexValueProcessor ).
            build();

        Node node = Node.create().
            id( NodeId.from( "myId" ) ).
            parentPath( NodePath.ROOT ).
            name( NodeName.from( "my-name" ) ).
            indexConfigDocument( PatternIndexConfigDocument.create().
                analyzer( "myAnalyzer" ).
                defaultConfig( IndexConfig.MINIMAL ).
                add( "myPath", indexConfig ).
                build() ).
            data( nodeData ).
            childOrder( ChildOrder.create().
                add( FieldOrderExpr.create( IndexPath.from( "modifiedTime" ), OrderExpr.Direction.ASC ) ).
                add( FieldOrderExpr.create( IndexPath.from( "displayName" ), OrderExpr.Direction.DESC ) ).
                build() ).
            permissions( acl ).
            nodeType( NodeType.from( "myNodeType" ) ).
            attachedBinaries( AttachedBinaries.create().
                add( new AttachedBinary( BinaryReference.from( "myImage1" ), "a" ) ).
                add( new AttachedBinary( BinaryReference.from( "myImage2" ), "b" ) ).
                build() ).
            build();

        final String expectedStr = readJson( "serialized-node.json" );

        final String serializedNode = this.serializer.toString( node );
        System.out.println( expectedStr );
        assertEquals( expectedStr, serializedNode );

        final Node deSerializedNode = this.serializer.toNode( expectedStr );

        assertEquals( node, Node.create( deSerializedNode ).
            name( node.name() ).
            parentPath( node.parentPath() ).
            build() );
    }

    private String readJson( final String name )
        throws Exception
    {
        final URL url = getClass().getResource( name );
        final JsonNode node = this.serializer.mapper.readTree( url );
        return this.serializer.mapper.writeValueAsString( node );
    }
}
