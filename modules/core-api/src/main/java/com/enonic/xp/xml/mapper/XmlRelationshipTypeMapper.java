package com.enonic.xp.xml.mapper;

import java.util.List;

import com.google.common.collect.Lists;

import com.enonic.xp.module.ModuleKey;
import com.enonic.xp.module.ModuleRelativeResolver;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.content.ContentTypeNames;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.xml.model.XmlRelationshipType;

public final class XmlRelationshipTypeMapper
{
    private final ModuleKey currentModule;

    public XmlRelationshipTypeMapper( final ModuleKey currentModule )
    {
        this.currentModule = currentModule;
    }

    public XmlRelationshipType toXml( final RelationshipType object )
    {
        XmlRelationshipType result = new XmlRelationshipType();
        result.setDescription( object.getDescription() );
        result.setFromSemantic( object.getFromSemantic() );
        result.setToSemantic( object.getToSemantic() );
        result.setAllowedFromTypes( toAllowedFromTypes( object.getAllowedFromTypes() ) );
        result.setAllowedToTypes( toAllowedToTypes( object.getAllowedToTypes() ) );
        return result;
    }

    public void fromXml( final XmlRelationshipType xml, final RelationshipType.Builder builder )
    {
        builder.description( xml.getDescription() );
        builder.fromSemantic( xml.getFromSemantic() );
        builder.toSemantic( xml.getToSemantic() );

        final ModuleRelativeResolver resolver = new ModuleRelativeResolver( currentModule );
        for ( final String ctyName : xml.getAllowedFromTypes().getContentType() )
        {
            builder.addAllowedFromType( resolver.toContentTypeName( ctyName ) );
        }

        for ( final String ctyName : xml.getAllowedToTypes().getContentType() )
        {
            builder.addAllowedToType( resolver.toContentTypeName( ctyName ) );
        }
    }

    private XmlRelationshipType.AllowedFromTypes toAllowedFromTypes( final ContentTypeNames names )
    {
        final XmlRelationshipType.AllowedFromTypes result = new XmlRelationshipType.AllowedFromTypes();
        result.getContentType().addAll( toXml( names ) );
        return result;
    }

    private XmlRelationshipType.AllowedToTypes toAllowedToTypes( final ContentTypeNames names )
    {
        final XmlRelationshipType.AllowedToTypes result = new XmlRelationshipType.AllowedToTypes();
        result.getContentType().addAll( toXml( names ) );
        return result;
    }

    private List<String> toXml( final ContentTypeNames allowedFromTypes )
    {
        final List<String> result = Lists.newArrayList();
        for ( final ContentTypeName ctyName : allowedFromTypes )
        {
            result.add( ctyName.toString() );
        }
        return result;
    }
}
