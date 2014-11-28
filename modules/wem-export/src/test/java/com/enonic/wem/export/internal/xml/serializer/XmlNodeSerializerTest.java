package com.enonic.wem.export.internal.xml.serializer;

import org.junit.Test;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.export.internal.xml.XmlNode;
import com.enonic.wem.export.internal.xml.mapper.XmlNodeMapper;

public class XmlNodeSerializerTest
    extends BaseXmlSerializerTest
{
    @Test
    public void test_to_xml()
        throws Exception
    {
        final Node node = Node.newNode().
            id( NodeId.from( "abc" ) ).
            name( NodeName.from( "my-node-name" ) ).
            parent( NodePath.ROOT ).
            childOrder( ChildOrder.manualOrder() ).
            build();

        final XmlNode xml = XmlNodeMapper.toXml( node );

        XmlNodeSerializer serializer = new XmlNodeSerializer();

        final String result = serializer.serialize( xml );

        assertXml( "node.xml", result );
    }

}