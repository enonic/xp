package com.enonic.xp.xml.parser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.w3c.dom.Element;

import com.enonic.xp.module.ModuleRelativeResolver;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.xml.DomHelper;

public final class XmlRelationshipTypeParser
    extends XmlModelParser<XmlRelationshipTypeParser>
{
    private RelationshipType.Builder builder;

    public XmlRelationshipTypeParser builder( final RelationshipType.Builder builder )
    {
        this.builder = builder;
        return this;
    }

    @Override
    protected void doParse( final Element root )
        throws Exception
    {
        assertTagName( root, "relationship-type" );
        this.builder.description( DomHelper.getChildElementValueByTagName( root, "description" ) );
        this.builder.fromSemantic( DomHelper.getChildElementValueByTagName( root, "from-semantic" ) );
        this.builder.toSemantic( DomHelper.getChildElementValueByTagName( root, "to-semantic" ) );
        this.builder.setAllowedFromTypes( parseTypes( root, "allowed-from-types" ) );
        this.builder.setAllowedToTypes( parseTypes( root, "allowed-to-types" ) );
    }

    private List<ContentTypeName> parseTypes( final Element root, final String name )
    {
        final Element types = DomHelper.getChildElementByTagName( root, name );
        if ( types == null )
        {
            return Collections.emptyList();
        }

        final ModuleRelativeResolver resolver = new ModuleRelativeResolver( this.currentModule );
        return DomHelper.getChildElementsByTagName( types, "content-type" ).stream().map(
            child -> resolver.toContentTypeName( DomHelper.getTextValue( child ) ) ).collect( Collectors.toList() );
    }
}
