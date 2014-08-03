package com.enonic.wem.api.xml.mapper;

import com.enonic.wem.api.schema.content.ContentTypeName;
import com.enonic.wem.api.schema.content.ContentTypeNames;
import com.enonic.wem.api.schema.relationship.RelationshipType;
import com.enonic.wem.api.xml.model.XmlAllowedTypesDescriptor;
import com.enonic.wem.api.xml.model.XmlRelationshipType;

public final class XmlRelationshipTypeMapper
{
    public static XmlRelationshipType toXml( final RelationshipType object )
    {
        XmlRelationshipType result = new XmlRelationshipType();
        result.setDescription( object.getDescription() );
        result.setFromSemantic( object.getFromSemantic() );
        result.setToSemantic( object.getToSemantic() );
        result.setAllowedFromTypes( toXml( object.getAllowedFromTypes() ) );
        result.setAllowedToTypes( toXml( object.getAllowedToTypes() ) );
        return result;
    }

    public static void fromXml( final XmlRelationshipType xml, final RelationshipType.Builder builder )
    {
        builder.description( xml.getDescription() );
        builder.fromSemantic( xml.getFromSemantic() );
        builder.toSemantic( xml.getToSemantic() );
        for ( String ctyName : xml.getAllowedFromTypes().getList() )
        {
            builder.addAllowedFromType( ContentTypeName.from( ctyName ) );
        }
        for ( String ctyName : xml.getAllowedToTypes().getList() )
        {
            builder.addAllowedToType( ContentTypeName.from( ctyName ) );
        }
    }

    private static XmlAllowedTypesDescriptor toXml( final ContentTypeNames allowedFromTypes )
    {
        XmlAllowedTypesDescriptor result = new XmlAllowedTypesDescriptor();
        for ( final ContentTypeName ctyName : allowedFromTypes )
        {
            result.getList().add( ctyName.getContentTypeName() );
        }
        return result;
    }
}
