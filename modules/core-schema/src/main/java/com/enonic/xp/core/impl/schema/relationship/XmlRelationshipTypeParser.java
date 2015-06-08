package com.enonic.xp.core.impl.schema.relationship;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.annotations.Beta;

import com.enonic.xp.module.ModuleRelativeResolver;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.xml.DomElement;
import com.enonic.xp.xml.parser.XmlModelParser;

@Beta
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
    protected void doParse( final DomElement root )
        throws Exception
    {
        assertTagName( root, "relationship-type" );
        this.builder.description( root.getChildValue( "description" ) );
        this.builder.fromSemantic( root.getChildValue( "from-semantic" ) );
        this.builder.toSemantic( root.getChildValue( "to-semantic" ) );
        this.builder.setAllowedFromTypes( parseTypes( root, "allowed-from-types" ) );
        this.builder.setAllowedToTypes( parseTypes( root, "allowed-to-types" ) );
    }

    private List<ContentTypeName> parseTypes( final DomElement root, final String name )
    {
        final DomElement types = root.getChild( name );
        if ( types == null )
        {
            return Collections.emptyList();
        }

        final ModuleRelativeResolver resolver = new ModuleRelativeResolver( this.currentModule );
        return types.getChildren( "content-type" ).stream().map( child -> resolver.toContentTypeName( child.getValue() ) ).collect(
            Collectors.toList() );
    }
}
