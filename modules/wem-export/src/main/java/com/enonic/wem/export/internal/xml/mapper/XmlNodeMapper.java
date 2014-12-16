package com.enonic.wem.export.internal.xml.mapper;

import com.enonic.wem.api.export.ExportNodeException;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.export.internal.xml.XmlNode;

public class XmlNodeMapper
{
    public static XmlNode toXml( final Node node )
    {
        try
        {
            return doSerializeNode( node );
        }
        catch ( Exception e )
        {
            throw new ExportNodeException( "Failed to serialize node to xml", e );
        }
    }

    private static XmlNode doSerializeNode( final Node node )
    {
        final XmlNode xml = new XmlNode();

        xml.setChildOrder( getAsStringOrNull( node.getChildOrder() ) );
        xml.setNodeType( node.getNodeType().getName() );
        xml.setAttachedBinaries( XmlAttachedBinariesMapper.toXml( node.getAttachedBinaries() ) );

        if ( node.data() != null )
        {
            xml.setProperties( XmlPropertyTreeMapper.toXml( node.data() ) );
        }

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
