package com.enonic.wem.export.internal.xml.serializer;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.TimeZone;

import org.junit.Test;

import com.google.common.io.ByteSource;

import com.enonic.wem.api.blob.BlobKey;
import com.enonic.wem.api.data.Property;
import com.enonic.wem.api.data.PropertySet;
import com.enonic.wem.api.data.PropertyTree;
import com.enonic.wem.api.data.PropertyVisitor;
import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.AttachedBinaries;
import com.enonic.wem.api.node.AttachedBinary;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.node.NodeType;
import com.enonic.wem.api.util.BinaryReference;
import com.enonic.wem.api.util.GeoPoint;
import com.enonic.wem.api.util.Link;
import com.enonic.wem.api.util.Reference;
import com.enonic.wem.export.internal.builder.PropertyTreeXmlBuilder;
import com.enonic.wem.export.internal.xml.XmlNode;
import com.enonic.wem.export.internal.xml.mapper.XmlNodeMapper;

import static org.junit.Assert.*;

public class XmlNodeSerializerTest
    extends BaseXmlSerializerTest
{
    private final TimeZone defaultTimezone = TimeZone.getDefault();

    @Test
    public void test_all_propertytypes_to_xml_and_from_xml()
        throws Exception
    {
        final Instant instant = Instant.parse( "2014-11-28T14:16:00Z" );

        final Node node = doCreateNode( instant );

        final XmlNode serializedXmlNode = XmlNodeMapper.toXml( node, false );

        XmlNodeSerializer serializer = new XmlNodeSerializer();

        final String result = serializer.serialize( serializedXmlNode );

        assertXml( "node.xml", result );

        XmlNode parsedXmlNode = serializer.parse( ByteSource.wrap( result.getBytes() ) );
        PropertyTree parsedNodeData = PropertyTreeXmlBuilder.build( parsedXmlNode.getData() );

        final PropertyTree expectedTree = node.data();
        assertEquals( expectedTree.getTotalSize(), parsedNodeData.getTotalSize() );
        assertEquals( expectedTree.toString(), parsedNodeData.toString() );

        final PropertyVisitor visitor = new PropertyVisitor()
        {
            @Override
            public void visit( final Property property )
            {
                final Property actualProperty = parsedNodeData.getProperty( property.getPath() );
                assertEquals( property.getValue(), actualProperty.getValue() );
            }
        };

        visitor.traverse( expectedTree );
    }

    @Test
    public void test_all_propertytypes_to_xml_another_timezone()
        throws Exception
    {
        TimeZone.setDefault( TimeZone.getTimeZone( "GMT-12:00" ) );

        final Instant instant = Instant.parse( "2014-11-28T14:16:00Z" );

        final Node node = doCreateNode( instant );

        final XmlNode xml = XmlNodeMapper.toXml( node, false );

        XmlNodeSerializer serializer = new XmlNodeSerializer();

        final String result = serializer.serialize( xml );

        assertXml( "node.xml", result );

        TimeZone.setDefault( defaultTimezone );
    }

    private Node doCreateNode( final Instant instant )
    {
        final PropertyTree propertyTree = new PropertyTree( new PropertyTree.PredictivePropertyIdProvider() );

        propertyTree.addString( "myString", "myStringValue" );
        propertyTree.addString( "myString", "myStringValue2" );
        propertyTree.addBoolean( "myBoolean", true );
        propertyTree.addDouble( "myDouble", 123.1 );
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

        return Node.newNode().
            id( NodeId.from( "abc" ) ).
            name( NodeName.from( "my-node-name" ) ).
            parentPath( NodePath.ROOT ).
            childOrder( ChildOrder.manualOrder() ).
            nodeType( NodeType.from( "content" ) ).
            data( propertyTree ).
            attachedBinaries( AttachedBinaries.create().
                add( new AttachedBinary( BinaryReference.from( "image.jpg" ), new BlobKey( "a" ) ) ).
                add( new AttachedBinary( BinaryReference.from( "image2.jpg" ), new BlobKey( "b" ) ) ).
                build() ).
            build();
    }
}