package com.enonic.wem.api.xml.mapper;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.xml.model.XmlRelationshipType;

public final class XmlRelationshipTypeMapper
{
    public static XmlRelationshipType toXml( final RelationshipType object )
    {
        XmlRelationshipType result = new XmlRelationshipType();
        result.setDescription( object.getDescription() );
        result.setFromSemantic( object.getFromSemantic() );
        result.setToSemantic( object.getToSemantic() );
        result.setAllowedFromTypes( toAllowedFromTypes( object.getAllowedFromTypes() ) );
        result.setAllowedToTypes( toAllowedToTypes( object.getAllowedToTypes() ) );
        return result;
    }

    public static void fromXml( final XmlRelationshipType xml, final RelationshipType.Builder builder )
    {
        builder.description( xml.getDescription() );
        builder.fromSemantic( xml.getFromSemantic() );
        builder.toSemantic( xml.getToSemantic() );
        for ( String ctyName : xml.getAllowedFromTypes().getContentType() )
        {
            builder.addAllowedFromType( ContentTypeName.from( ctyName ) );
        }
        for ( String ctyName : xml.getAllowedToTypes().getContentType() )
        {
            builder.addAllowedToType( ContentTypeName.from( ctyName ) );
        }
    }

    private static XmlRelationshipType.AllowedFromTypes toAllowedFromTypes( final ContentTypeNames names )
    {
        final XmlRelationshipType.AllowedFromTypes result = new XmlRelationshipType.AllowedFromTypes();
        result.getContentType().addAll( toXml( names ) );
        return result;
    }

    private static XmlRelationshipType.AllowedToTypes toAllowedToTypes( final ContentTypeNames names )
    {
        final XmlRelationshipType.AllowedToTypes result = new XmlRelationshipType.AllowedToTypes();
        result.getContentType().addAll( toXml( names ) );
        return result;
    }

    private static List<String> toXml( final ContentTypeNames allowedFromTypes )
    {
        final List<String> result = Lists.newArrayList();
        for ( final ContentTypeName ctyName : allowedFromTypes )
        {
            result.add( ctyName.toString() );
        }
        return result;
    }
}
