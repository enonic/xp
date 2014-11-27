package com.enonic.wem.export.xml.mapper;

import com.enonic.wem.api.index.ChildOrder;
import com.enonic.wem.api.node.Node;
import com.enonic.wem.api.node.NodeId;
import com.enonic.wem.api.node.NodeName;
import com.enonic.wem.api.node.NodePath;
import com.enonic.wem.api.security.PrincipalKey;
import com.enonic.wem.export.ExportNodeException;
import com.enonic.wem.export.xml.model.XmlNode;
import com.enonic.wem.export.xml.util.InstantConverter;


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

        xml.setId( node.id().toString() );
        xml.setParent( node.parent().toString() );
        xml.setName( node.name().toString() );
        xml.setCreator( node.creator().toString() );
        xml.setModifier( node.modifier().toString() );
        xml.setCreatedTime( InstantConverter.convertToXmlSerializable( node.getCreatedTime() ) );
        xml.setModifiedTime( InstantConverter.convertToXmlSerializable( node.getCreatedTime() ) );
        xml.setChildOrder( getAsStringOrNull( node.getChildOrder() ) );

        return xml;
    }

    public static void fromXml( final XmlNode xml, final Node.Builder builder )
    {
        try
        {
            doDeserializeNode( xml, builder );
        }
        catch ( Exception e )
        {
            throw new ExportNodeException( "Failed to deserialize node from xml", e );
        }
    }

    private static void doDeserializeNode( final XmlNode xml, final Node.Builder builder )
    {
        builder.name( NodeName.from( xml.getName() ) ).
            parent( NodePath.newPath( xml.getParent() ).build() ).
            id( NodeId.from( xml.getId() ) ).
            childOrder( ChildOrder.from( xml.getChildOrder() ) ).
            createdTime( InstantConverter.convertToInstant( xml.getCreatedTime() ) ).
            modifiedTime( InstantConverter.convertToInstant( xml.getModifiedTime() ) ).
            creator( PrincipalKey.from( xml.getCreator() ) ).
            modifier( PrincipalKey.from( xml.getModifier() ) );
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
