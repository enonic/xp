package com.enonic.wem.export.serializer;

import java.time.Instant;

import org.junit.Test;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.export.xml.mapper.XmlNodeMapper;
import com.enonic.wem.export.xml.model.XmlNode;
import com.enonic.wem.export.xml.serializer.XmlSerializers;

import static junit.framework.Assert.assertEquals;

public class XmlNodeSerializerTest
    extends BaseXmlSerializerTest
{
    @Test
    public void test_to_xml()
        throws Exception
    {
        final Node node = createSampleNode();

        final XmlNode serializedNode = XmlNodeMapper.toXml( node );

        final String result = XmlSerializers.node().serialize( serializedNode );

        assertXml( "node.xml", result );
    }

    @Test
    public void test_from_xml()
        throws Exception
    {
        final String xml = readFromFile( "node.xml" );

        final Node.Builder builder = Node.newNode();

        final XmlNode xmlObject = com.enonic.wem.export.xml.serializer.XmlSerializers.node().parse( xml );
        XmlNodeMapper.fromXml( xmlObject, builder );

        final Node deserializedNode = builder.build();

        assertEquals( createSampleNode(), deserializedNode );
    }

    private Node createSampleNode()
    {
        final Instant instant = Instant.parse( "2014-10-08T12:35:40Z" );

        return Node.newNode().
            id( NodeId.from( "my-node-id" ) ).
            parent( NodePath.ROOT ).
            name( NodeName.from( "my-node-name" ) ).
            creator( PrincipalKey.from( "user:system:rmy" ) ).
            modifier( PrincipalKey.from( "user:system:rmy" ) ).
            createdTime( instant ).
            modifiedTime( instant ).
            childOrder( ChildOrder.manualOrder() ).
            build();
    }


}
