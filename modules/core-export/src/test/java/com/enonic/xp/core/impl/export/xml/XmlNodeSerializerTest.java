package com.enonic.xp.core.impl.export.xml;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.Test;

import com.enonic.xp.blob.BlobKey;
import com.enonic.xp.data.PropertySet;
import com.enonic.xp.data.PropertyTree;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.index.IndexConfig;
import com.enonic.xp.index.PatternIndexConfigDocument;
import com.enonic.xp.node.AttachedBinaries;
import com.enonic.xp.node.AttachedBinary;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeName;
import com.enonic.xp.node.NodePath;
import com.enonic.xp.node.NodeType;
import com.enonic.xp.util.BinaryReference;
import com.enonic.xp.util.GeoPoint;
import com.enonic.xp.util.Link;
import com.enonic.xp.util.Reference;

public class XmlNodeSerializerTest
    extends BaseXmlSerializerTest
{
    @Test
    public void testSerialize()
        throws Exception
    {
        final Instant instant = Instant.parse( "2014-11-28T14:16:00Z" );
        final Node node = doCreateNode( instant );

        final XmlNodeSerializer serializer = new XmlNodeSerializer();
        serializer.node( node ).exportNodeIds( true );

        final String result = serializer.serialize();

        assertXml( "node.xml", result );
    }

    private Node doCreateNode( final Instant instant )
    {
        final PropertyTree propertyTree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        propertyTree.addString( "myString", "myStringValue" );
        propertyTree.addString( "myString", "myStringValue2" );
        propertyTree.addBoolean( "myBoolean", true );
        propertyTree.addDouble( "myDouble", 123.1 );
        propertyTree.addLong( "myLong", 111L );
        propertyTree.addHtmlPart( "myHtmlPart", "<h1>This is the title</h1><h2>This is the subheading</h2>" );
        propertyTree.addXml( "myXml", "<car><color>Arctic Grey<color><car>" );
        propertyTree.addGeoPoint( "myGeoPoint", GeoPoint.from( "8,4" ) );

        // Date & Time
        propertyTree.addInstant( "myInstant", instant );
        propertyTree.addLocalTime( "myLocalTime", LocalTime.of( 21, 42, 0 ) );
        propertyTree.addLocalDate( "myLocalDate", LocalDate.of( 2014, 11, 28 ) );
        propertyTree.addLocalDateTime( "myLocalDateTime", LocalDateTime.of( 2014, 11, 28, 21, 0, 0, 0 ) );

        // Links and ref
        propertyTree.addReference( "myRef", Reference.from( "abcd" ) );
        propertyTree.addLink( "myLink", Link.from( "/root/parent/child" ) );

        // Binary refs
        propertyTree.addBinaryReference( "myBinaryRef1", BinaryReference.from( "image.jpg" ) );
        propertyTree.addBinaryReference( "myBinaryRef2", BinaryReference.from( "image2.jpg" ) );

        // Property-set
        final PropertySet mySubset = propertyTree.addSet( "mySet" );
        mySubset.setString( "myString", "myStringValue" );
        mySubset.setBoolean( "myBoolean", true );

        // Property-set in set
        final PropertySet mySubSubset = mySubset.addSet( "mySet" );
        mySubSubset.setString( "myString", "myStringValue" );
        mySubSubset.setBoolean( "myBoolean", true );

        // Nullable values
        propertyTree.addString( "myString", null );
        propertyTree.addHtmlPart( "myHtmlPart", null );
        propertyTree.addXml( "myXml", null );
        propertyTree.addSet( "nullSet", null );

        // Index configs
        final PatternIndexConfigDocument.Builder indexConfig = PatternIndexConfigDocument.create();
        indexConfig.analyzer( "no" );
        indexConfig.add( "mydata", IndexConfig.FULLTEXT );

        return Node.newNode().
            id( NodeId.from( "abc" ) ).
            name( NodeName.from( "my-node-name" ) ).
            parentPath( NodePath.ROOT ).
            childOrder( ChildOrder.manualOrder() ).
            nodeType( NodeType.from( "content" ) ).
            data( propertyTree ).
            indexConfigDocument( indexConfig.build() ).
            attachedBinaries( AttachedBinaries.create().
                add( new AttachedBinary( BinaryReference.from( "image.jpg" ), new BlobKey( "a" ) ) ).
                add( new AttachedBinary( BinaryReference.from( "image2.jpg" ), new BlobKey( "b" ) ) ).
                build() ).
            build();
    }
}
