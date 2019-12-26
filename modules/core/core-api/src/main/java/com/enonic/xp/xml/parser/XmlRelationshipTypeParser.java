package com.enonic.xp.xml.parser;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.enonic.xp.annotation.PublicApi;
import com.enonic.xp.app.ApplicationRelativeResolver;
import com.enonic.xp.schema.content.ContentTypeName;
import com.enonic.xp.schema.relationship.RelationshipType;
import com.enonic.xp.xml.DomElement;

@PublicApi
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

        final ApplicationRelativeResolver resolver = new ApplicationRelativeResolver( this.currentApplication );
        return types.getChildren( "content-type" ).stream().map( child -> resolver.toContentTypeName( child.getValue() ) ).collect(
            Collectors.toList() );
    }
}
