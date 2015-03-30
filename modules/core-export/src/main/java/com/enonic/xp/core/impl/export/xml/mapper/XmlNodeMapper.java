package com.enonic.xp.core.impl.export.xml.mapper;

import com.enonic.xp.core.impl.export.builder.IndexConfigDocumentXmlBuilder;
import com.enonic.xp.core.impl.export.builder.PropertyTreeXmlBuilder;
import com.enonic.xp.core.impl.export.xml.XmlNode;
import com.enonic.xp.export.ExportNodeException;
import com.enonic.xp.index.ChildOrder;
import com.enonic.xp.node.Node;
import com.enonic.xp.node.NodeId;
import com.enonic.xp.node.NodeType;

public class XmlNodeMapper
{
    public static Node build( final XmlNode xml )
    {
        final Node.Builder builder = Node.newNode();

        if ( xml.getId() != null )
        {
            builder.id( NodeId.from( xml.getId() ) );
        }

        builder.childOrder( ChildOrder.from( xml.getChildOrder() ) );
        builder.nodeType( NodeType.from( xml.getNodeType() ) );

        if ( xml.getData() != null )
        {
            builder.data( PropertyTreeXmlBuilder.build( xml.getData() ) );
        }

        if ( xml.getIndexConfigs() != null )
        {
            builder.indexConfigDocument( IndexConfigDocumentXmlBuilder.build( xml.getIndexConfigs() ) );
        }

        return builder.build();
    }

    public static XmlNode toXml( final Node node, final boolean exportNodeIds )
    {
        try
        {
            return doSerializeNode( node, exportNodeIds );
        }
        catch ( Exception e )
        {
            throw new ExportNodeException( "Failed to serialize node to xml", e );
        }
    }

    private static XmlNode doSerializeNode( final Node node, final boolean exportNodeIds )
    {
        final XmlNode xml = new XmlNode();

        if ( exportNodeIds )
        {
            xml.setId( node.id().toString() );
        }
        xml.setChildOrder( getAsStringOrNull( node.getChildOrder() ) );
        xml.setNodeType( node.getNodeType().getName() );

        if ( node.data() != null )
        {
            xml.setData( XmlPropertyTreeMapper.toXml( node.data() ) );
        }

        xml.setIndexConfigs( XmlIndexConfigsMapper.toXml( node.getIndexConfigDocument() ) );

        return xml;
    }

    private static String getAsStringOrNull( final Object object )
    {
        if ( object == null )
        {
            return null;
        }

        return object.toString();
    }

}
